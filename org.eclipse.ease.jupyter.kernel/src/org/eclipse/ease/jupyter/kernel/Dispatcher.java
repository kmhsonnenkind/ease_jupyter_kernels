/*******************************************************************************
 * Copyright (c) 2016 Martin Kloesch and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Kloesch - initial API and implementation
 *******************************************************************************/

package org.eclipse.ease.jupyter.kernel;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.ease.IScriptEngine;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Server receiving connections from kernel launchers, parsing the received
 * Jupyter connection file and setting up the actual kernels.
 */
public class Dispatcher implements Runnable {
	/**
	 * Buffer size for incoming and outgoing data.
	 * 
	 * Connection file usually about 300 bytes so this buffer should be more
	 * than large enough.
	 */
	private static final int BUFFER_SIZE = 8192;

	/**
	 * Simple flag to see if {@link Dispatcher} is still running.
	 */
	private AtomicBoolean fRunning = new AtomicBoolean(true);

	/**
	 * Selector for non-blocking IO.
	 */
	private Selector fSelector;

	/**
	 * Actual non-blocking channel for IO.
	 */
	private ServerSocketChannel fChannel;

	/**
	 * Address to bind the server to.
	 */
	private InetSocketAddress fAddress;

	/**
	 * {@link IScriptEngine} for code execution.
	 */
	private IScriptEngine fEngine;

	/**
	 * Set of started kernels to be terminated on shutdown.
	 */
	private final Set<Kernel> fKernels = new HashSet<Kernel>();

	/**
	 * Constructor only stores information about address to run the server on.
	 * 
	 * @param host
	 *            Host to run the server on.
	 * @param port
	 *            Port to run the server on.
	 */
	public Dispatcher(IScriptEngine engine, String host, int port) {
		fEngine = engine;
		fAddress = new InetSocketAddress(host, port);
	}

	/**
	 * Sets up the server by initializing the {@link Selector} and correctly
	 * configuring the {@link ServerSocketChannel}.
	 * 
	 * @return {@link Selector} to be used by server.
	 * @throws IOException
	 *             If server channel could not be bound.
	 */
	protected Selector setupServer() throws IOException {
		// Create selector for non-blocking IO
		Selector selector = SelectorProvider.provider().openSelector();

		// Set up server for non-blocking IO
		fChannel = ServerSocketChannel.open();
		fChannel.configureBlocking(false);
		fChannel.bind(fAddress);

		// Tell selector that we have an interest in accepting new connections.
		fChannel.register(selector, SelectionKey.OP_ACCEPT);

		return selector;

	}

	/**
	 * Callback triggered when a connection is ready to be accepted.
	 * 
	 * @param key
	 *            {@link SelectionKey} with information about the connection to
	 *            be accepted.
	 */
	protected void accept(SelectionKey key) {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Get connection to client
		SocketChannel socketChannel = null;
		SelectionKey clientKey = null;
		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);

			// Tell selector that we are interested in reading from client.
			clientKey = socketChannel.register(fSelector, SelectionKey.OP_READ);

		} catch (IOException e) {
			e.printStackTrace();
			// If opened, then close SocketChannel
			if (socketChannel != null) {
				try {
					socketChannel.close();
				} catch (IOException e2) {
					// Ignore
				}
			}
			return;
		}

		// Attach a byte buffer for sending back data.
		clientKey.attach(ByteBuffer.allocate(BUFFER_SIZE));

	}

	/**
	 * Callback triggered when data is available from client.
	 * 
	 * @param selectionKey
	 *            {@link SelectionKey} with information about the connection we
	 *            can read from.
	 * @throws IOException
	 *             If data could not be read.
	 */
	protected void read(SelectionKey selectionKey) {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

		// Allocate buffer and try to read data.
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		int bytesRead = -1;
		try {
			bytesRead = socketChannel.read(buffer);
		} catch (IOException e) {
			// Ignore because of fall-through to next check
		}

		// Check if connection is closed
		if (bytesRead <= 0) {
			closeConncection(selectionKey);
			return;
		}

		// Parse data
		Config config = null;
		try {
			byte[] configBytes = buffer.array();
			config = parseConfig(configBytes);
		} catch (IOException e) {
			// Invalid data received, close connection
			e.printStackTrace();
			closeConncection(selectionKey);
			return;
		}

		// Actually build the kernel
		Kernel kernel = new Kernel(config, fEngine);

		// Start the kernel
		try {
			kernel.start();

			// Add kernel to internal list
			synchronized (fKernels) {
				fKernels.add(kernel);
			}
		} catch (IOError e) {
			e.printStackTrace();
			// Close connection
			closeConncection(selectionKey);
		}
	}

	/**
	 * Parses the given bytes representing a {@link Config} object to an actual
	 * object.
	 * <p>
	 * Performs validation using {@link Config#validate()}.
	 * 
	 * @param configBytes
	 *            byte representation of {@link Config} object.
	 * @return {@link Config} object based on bytes.
	 * @throws IOException
	 *             If invalid data received.
	 */
	protected static Config parseConfig(byte[] configBytes) throws IOException {
		Config config = new ObjectMapper().readValue(configBytes, Config.class);
		config.validate();
		return config;
	}

	/**
	 * Closes the connection specified by the given {@link SelectionKey}.
	 * 
	 * @param selectionKey
	 *            {@link SelectionKey} with information about connection to be
	 *            closed.
	 */
	protected void closeConncection(SelectionKey selectionKey) {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		try {
			socketChannel.close();
		} catch (IOException e) {
			// ignore
		}
		selectionKey.cancel();
	}

	/**
	 * Runs the server.
	 * 
	 * Waits for connection from kernel launchers, then reads connection file
	 * and finally starts kernel based on received configuration.
	 */
	public void run() {
		// Set up the server
		try {
			fSelector = setupServer();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Actually perform IO
		while (fRunning.get()) {
			try {
				// Non-blocking wait until event is ready
				fSelector.select();

				// Information about all events
				Iterator<SelectionKey> selectedKeys = fSelector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = selectedKeys.next();

					// Remove event, otherwise it will be re-triggered
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Trigger callback depending on state
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

		// Close all started kernels
		synchronized (fKernels) {
			for (Kernel kernel : fKernels) {
				kernel.stop();
			}
		}
	}

	/**
	 * Stops the servers IO-loop.
	 */
	public void stop() {
		fRunning.set(false);
		fSelector.wakeup();
	}
}
