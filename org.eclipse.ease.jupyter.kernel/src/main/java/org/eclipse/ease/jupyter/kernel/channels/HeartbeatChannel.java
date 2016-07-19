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

import org.zeromq.ZMQ;

import org.eclipse.ease.jupyter.kernel.Session;

/**
 * Custom Jupyter kernel channel receiving heartbeat messages that are simply
 * returned back.
 */
public class HeartbeatChannel extends AbstractRunningServerChannel {
	/**
	 * Custom {@link Runnable} echoing back all received data.
	 * 
	 * Simplest form of Jupyter kernel channel.
	 */
	private class HeartbeatEchoer implements Runnable {
		@Override
		public void run() {
			String recvd = null;
			while (isRunning()) {
				// Read data
				recvd = getZmqSocket().recvStr();

				// Ignore if noone is connected
				if (recvd == null) {
					continue;
				}

				// Send back message
				getZmqSocket().send(recvd);
			}
		}
	}

	/**
	 * Thread running the {@link HeartbeatEchoer}.
	 */
	protected Thread fEchoThread;

	/**
	 * Constructor only wraps to parent constructor.
	 * 
	 * @see AbstractRunningChannel#AbstractRunningChannel(String, Session,
	 *      boolean);
	 */
	public HeartbeatChannel(final String address, final Session session) {
		super(address, session);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.openanalytics.japyter.client.AbstractRunningChannel#start()
	 */
	@Override
	public void start() {
		super.start();

		// Start the echo thread (stop handled in parent)
		fEchoThread = new Thread(new HeartbeatEchoer());
		fEchoThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.openanalytics.japyter.client.AbstractChannel#getZmqSocketType()
	 */
	@Override
	public int getZmqSocketType() {
		// Heartbeat needs to be REP socket
		return ZMQ.REP;
	}
}
