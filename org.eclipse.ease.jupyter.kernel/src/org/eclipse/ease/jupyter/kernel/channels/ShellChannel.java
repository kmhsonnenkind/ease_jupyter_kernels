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

package org.eclipse.ease.jupyter.kernel.channels;

import java.io.IOException;

import org.zeromq.ZMQ;
import org.eclipse.ease.jupyter.kernel.Kernel;
import org.eclipse.ease.jupyter.kernel.Session;
import org.eclipse.ease.jupyter.kernel.handlers.AbstractMessageHandlerFactory;
import org.eclipse.ease.jupyter.kernel.handlers.IMessageHandler;
import org.eclipse.ease.jupyter.kernel.handlers.IMessageHandlerFactory;
import org.eclipse.ease.jupyter.kernel.messages.Message;

/**
 * Jupyter kernel channel running request handler.
 */
public class ShellChannel extends AbstractRunningServerChannel {
	/**
	 * Custom {@link Runnable} reading {@link Request} objects from channel and
	 * calling methods depending on received request.
	 */
	private class RequestReader implements Runnable {
		@Override
		public void run() {
			Message message;

			while (isRunning()) {
				try {
					// Read request
					message = getSession().poll(getZmqSocket());
				} catch (IOException e) {
					// Ignore exceptions, client might have disconnected
					continue;
				}

				// Check if message received (might have timed out)
				if (message == null) {
					continue;
				}

				// Actually handle message
				handleMessage(message);
			}
		}
	}

	/**
	 * Message handler factory to create new handlers for incoming requests.
	 */
	protected final AbstractMessageHandlerFactory fMessageHandlerFactory;

	/**
	 * Thread running the {@link RequestReader}.
	 */
	protected Thread fRequestReaderThread;

	/**
	 * Constructor initializes members.
	 * 
	 * @param address
	 *            Address to be used by ShellChannel.
	 * @param session
	 *            {@link Session} for creating sockets, etc...
	 * @param kernel
	 *            {@link Kernel} object necessary for callbacks.
	 * @param ioPub
	 *            {@link IOPubChannel} for informing clients about results, output,
	 *            etc.
	 */
	public ShellChannel(final String address, final Session session, Kernel kernel, IOPubChannel ioPub) {
		super(address, session);

		// Create message handler factory.
		fMessageHandlerFactory = new AbstractMessageHandlerFactory(kernel, this, ioPub);
	}

	/**
	 * {@link ShellChannel} needs to be of ZMQ dealer type.
	 */
	@Override
	public int getZmqSocketType() {
		return ZMQ.DEALER;
	}

	/**
	 * Starts the request reader thead.
	 */
	@Override
	public void start() {
		super.start();
		fRequestReaderThread = new Thread(new RequestReader());
		fRequestReaderThread.start();
	}

	/**
	 * Stops the reader thread.
	 */
	@Override
	public void stop() {
		super.stop();
		try {
			fRequestReaderThread.join();
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Handles the given method by trying to create a new {@link IMessageHandler}
	 * based on type and executing it.
	 * 
	 * @param message
	 *            {@link Message} to be handled.
	 */
	protected void handleMessage(Message message) {
		// Parse message type from header
		String messageType = message.getHeader().getMsgType();

		// Create message handler
		IMessageHandlerFactory factory = fMessageHandlerFactory.getHandlerFactory(messageType);

		if (factory != null) {
			// Actually handle message.
			IMessageHandler handler = factory.create();
			handler.handle(message);
		}
	}

}
