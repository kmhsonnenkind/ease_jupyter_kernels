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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.zeromq.ZMQ;

import org.eclipse.ease.jupyter.kernel.Session;
import org.eclipse.ease.jupyter.kernel.messages.Message;

/**
 * Custom jupyter kernel channel publishing data to all connected clients.
 */
public class IOPubChannel extends AbstractRunningServerChannel {
	/**
	 * @see AbstractRunningServerChannel#AbstractRunningServerChannel(String,
	 *      Session)
	 */
	public IOPubChannel(final String address, final Session session) {
		super(address, session);
	}

	/**
	 * Blocking queue for messages to be send to all clients.
	 */
	private final BlockingQueue<Message> fOutputQueue = new ArrayBlockingQueue<Message>(16);

	/**
	 * Adds a new {@link Message} to the output queue.
	 * 
	 * Method does not guarantee that {@link Message} will be send.
	 * 
	 * @param message
	 *            {@link Message} to be send asynchronously.
	 */
	public void send(Message message) {
		try {
			fOutputQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Custom runnable trying to take {@link Message} from internal queue and
	 * sending it to all connected clients.
	 */
	private class MessageDispatcher implements Runnable {
		@Override
		public void run() {
			Message toSend = null;
			while (isRunning()) {
				try {
					toSend = fOutputQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				try {
					getSession().send(toSend, getZmqSocket());
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * Thread running the {@link MessageDispatcher}.
	 */
	protected Thread fPubThread;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.openanalytics.japyter.client.AbstractRunningChannel#start()
	 */
	@Override
	public void start() {
		super.start();

		// Start the echo thread (stop handled in parent)
		fPubThread = new Thread(new MessageDispatcher());
		fPubThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.openanalytics.japyter.client.AbstractChannel#getZmqSocketType()
	 */
	@Override
	public int getZmqSocketType() {
		// IOPub needs to be PUB socket
		return ZMQ.PUB;
	}
}
