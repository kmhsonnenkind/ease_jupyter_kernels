/*******************************************************************************
 * Copyright (c) 2016 Martin Kloesch and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Kloesch - initial API and implementation
 *     Tobias Verbeke - original protocol implementation in Japyter project
 *******************************************************************************/

package org.eclipse.ease.jupyter.kernel.channels;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.ease.jupyter.kernel.Session;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.zeromq.ZMQ.Socket;

/**
 * Base class for all Channels used by Jupyter.
 */
public abstract class AbstractChannel implements Closeable {
	/**
	 * Address the channel is running on.
	 */
	private final String fAddress;

	/**
	 * Link to the {@link Session} necessary for sending ZMQ messages.
	 */
	protected final Session fSession;

	/**
	 * Actual ZMQ socket to send and receive data.
	 */
	private final Socket fZmqSocket;

	/**
	 * Constructor stores parameters to members and initializes socket.
	 * 
	 * @param address
	 *            Address to run the socket on.
	 * @param session
	 *            Session to have ZMQ data exchange available.
	 */
	public AbstractChannel(final String address, final Session session) {
		fAddress = address;
		fSession = session;
		fZmqSocket = session.bind(this);
	}

	/**
	 * Initialize the ZMQ socket using {@link #fSession}.
	 * 
	 * This abstract method is necessary because server sockets need to bind the
	 * address while clients only need to connect.
	 * 
	 * @return ZMQ socket to be used.
	 */
	protected abstract Socket createSocket();

	/**
	 * Sends the given message over the ZMQ socket using {@link #fSession}.
	 * 
	 * @param message
	 *            {@link Message} to be send.
	 * @throws IOException
	 *             If communication error occurred.
	 */
	public void send(Message message) throws IOException {
		getSession().send(message, getZmqSocket());
	}

	/**
	 * Closes the channel using {@link #fSession}.
	 */
	@Override
	public void close() throws IOException {
		fSession.closeChannel(this);
	}

	/**
	 * Returns the ZMQ socket type necessary for socket creation.
	 * 
	 * @return ZMQ socket type
	 */
	public abstract int getZmqSocketType();

	/**
	 * Returns the ZMQ socket used by channel.
	 * 
	 * @return ZMQ socket for channel.
	 */
	public Socket getZmqSocket() {
		return fZmqSocket;
	}

	/**
	 * Returns the {@link Session} for the channel. Used to have convenience
	 * methods for sending and receiving data.
	 * 
	 * @return
	 */
	protected Session getSession() {
		return fSession;
	}

	/**
	 * Returns the address the ZMQ socket is running on.
	 * 
	 * @return Address of ZMQ socket.
	 */
	public String getAddress() {
		return fAddress;
	}
}
