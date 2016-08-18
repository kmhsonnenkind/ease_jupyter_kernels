/*******************************************************************************
 * Copyright (c) 2016 Martin Kloesch and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Kloesch - initial API and implementation
 *     Tobias Verbeke - original session implementation in Japyter project
 *******************************************************************************/
package org.eclipse.ease.jupyter.kernel;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

/**
 * Class storing state and information about a ZMQ session.
 * 
 * This includes the ZMQ context, a list of running channels and the ZMQ session
 * id.
 * 
 * Largely based on Japyter project.
 */
public class Session implements Closeable {
	/**
	 * Random session ID used for Jupyter messages.
	 */
	private final String fID;

	/**
	 * Protocol handling the actual message parsing.
	 */
	private final Protocol fProtocol;

	/**
	 * Receive timeout for ZMQ sockets in milliseconds.
	 */
	private final int fReceiveTimeoutMillis;

	/**
	 * ZMQ context necessary for creating sockets.
	 */
	private final ZContext fZmqContext;

	/**
	 * Set of running {@link AbstractChannel} objects. Used to cleanly shut down
	 * all sockets.
	 */
	private final Set<AbstractChannel> fChannels;

	/**
	 * Constructor initializes all data-structures and stores parameters to
	 * members.
	 * 
	 * @param protocol
	 *            Protocol to handle actual message parsing.
	 * @param receiveTimeoutMillis
	 *            Receive timeout in milliseconds for ZMQ sockets.
	 * @param zmqIoThreads
	 *            Number of IO threads available to the ZMQ sockets.
	 */
	public Session(final Protocol protocol, final int receiveTimeoutMillis, final int zmqIoThreads) {
		fID = UUID.randomUUID().toString();

		this.fProtocol = protocol;

		this.fReceiveTimeoutMillis = receiveTimeoutMillis;
		this.fZmqContext = new ZContext(zmqIoThreads);
		this.fChannels = new HashSet<AbstractChannel>();
	}

	/**
	 * Returns the {@link Protocol} currently in use.
	 * 
	 * @return {@link Protocol} in use.
	 */
	public Protocol getProtocol() {
		return fProtocol;
	}

	/**
	 * Returns the receive timeout for the ZMQ sockets (in milliseconds).
	 * 
	 * @return Receive timeout in milliseconds.
	 */
	public int getReceiveTimeoutMillis() {
		return fReceiveTimeoutMillis;
	}

	/**
	 * Binds the given {@link AbstractChannel} to its address, effectively
	 * starting the Socket.
	 * 
	 * @param channel
	 *            {@link AbstractChannel} to be bound to its address.
	 * @return Actual ZMQ {@link Socket} to be used by {@link AbstractChannel}.
	 */
	public Socket bind(final AbstractChannel channel) {
		final Socket zmqSocket = fZmqContext.createSocket(channel.getZmqSocketType());
		zmqSocket.setLinger(1000L);
		zmqSocket.setReceiveTimeOut(fReceiveTimeoutMillis);
		zmqSocket.bind(channel.getAddress());

		fChannels.add(channel);

		return zmqSocket;
	}

	/**
	 * Closes the given {@link AbstractChannel}.
	 * 
	 * @param channel
	 *            {@link AbstractChannel} to be closed.
	 */
	public void closeChannel(final AbstractChannel channel) {
		fZmqContext.destroySocket(channel.getZmqSocket());
	}

	/**
	 * Closes all running channels and tries to gracefully shutdown all running
	 * tasks.
	 */
	@Override
	public void close() throws IOException {
		// Close all running channels
		for (final AbstractChannel channel : fChannels) {
			try {
				channel.close();
			} catch (IOException e) {
				// Ignore
			}
		}

		// Actually close ZMQ session
		fZmqContext.destroy();

	}

	/**
	 * Sends the given {@link Message} to given ZMQ socket.
	 * 
	 * @param message
	 *            {@link Message} to be send.
	 * @param zmqSocket
	 *            ZMQ socket to send data to.
	 * @throws IOException
	 *             If data could not be send.
	 */
	public void send(final Message message, final Socket zmqSocket) throws IOException {
		// Set session ID for message
		message.getHeader().setSession(fID);

		// Split message to byte arrays according to Wire Protocol
		final List<byte[]> frames = fProtocol.toFrames(message);
		final int nrFrames = frames.size();

		// Send data one frame at a time
		for (int i = 0; i < nrFrames; i++) {
			final boolean lastFrame = (i == (nrFrames - 1));

			final byte[] frame = frames.get(i);

			// Last frame needs to be send differently
			if (lastFrame) {
				if (!zmqSocket.send(frame)) {
					throw new IOException("Failed to send frame " + i + " of message " + message);
				}
			} else {
				if (!zmqSocket.sendMore(frame)) {
					throw new IOException("Failed to send frame " + i + " of message " + message);
				}
			}
		}
	}

	/**
	 * Reads data over given ZMQ socket and parses the data to {@link Message}
	 * object.
	 * 
	 * Throws {@link IOException} if no data received (see
	 * {@link #getReceiveTimeoutMillis()}.
	 * 
	 * Use {@link #poll(Socket)} if you do not want to get exception when no
	 * data was received.
	 * 
	 * @param zmqSocket
	 *            ZMQ socket to read from.
	 * @return Received data parsed to {@link Message}.
	 * @throws IOException
	 *             If no data was received or error occurred during
	 *             communication.
	 */
	public Message receive(final Socket zmqSocket) throws IOException {
		return receive(zmqSocket, true);
	}

	/**
	 * Tries to read data over given ZMQ socket and to parse data to
	 * {@link Message}.
	 * 
	 * If no data received, <code>null</code> is returned.
	 * 
	 * @param zmqSocket
	 *            ZMQ socket to read from.
	 * @return Received data parsed to {@link Message} or <code>null</code> if
	 *         no data received.
	 * @throws IOException
	 *             If communication error occurred.
	 */
	public Message poll(final Socket zmqSocket) throws IOException {
		return receive(zmqSocket, false);
	}

	/**
	 * Actual method reading data from given ZMQ socket. Data is parsed to
	 * {@link Message} object and returned.
	 * 
	 * Depending on parameter <code>failOnNull</code> either {@link IOException}
	 * is raised or <code>null</code> returned.
	 * 
	 * Users should rather use {@link #receive(Socket)} or {@link #poll(Socket)}
	 * as both only wrap to this method.
	 * 
	 * @param zmqSocket
	 *            ZMQ socket to read from.
	 * @param failOnNull
	 *            Flag to signalize how read timeout should be handled.
	 * @return {@link Message} based on received data, <code>null</code> if no
	 *         data received.
	 * @throws IOException
	 *             If communication error occured or no data received.
	 */
	private Message receive(final Socket zmqSocket, final boolean failOnNull) throws IOException {
		// Try to read first frame from socket
		byte[] frame = zmqSocket.recv();

		// Check if data received
		if (frame == null) {
			if (failOnNull) {
				throw new IOException("Received null first frame after waiting " + fReceiveTimeoutMillis + "ms");
			} else {
				return null;
			}
		}

		// Read all other frames
		final List<byte[]> frames = new ArrayList<byte[]>();
		do {
			frames.add(frame);
		} while (zmqSocket.hasReceiveMore() && ((frame = zmqSocket.recv()) != null));

		// Parse frames to actual Message object.
		return fProtocol.fromFrames(frames);
	}
}
