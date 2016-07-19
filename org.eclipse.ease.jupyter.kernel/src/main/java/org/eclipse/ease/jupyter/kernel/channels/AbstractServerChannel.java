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

import org.eclipse.ease.jupyter.kernel.Session;
import org.zeromq.ZMQ.Socket;

/**
 * Extension of {@link AbstractChannel} used by server channels.
 * 
 * Necessary because clients need to connect rather than bind the socket.
 */
public abstract class AbstractServerChannel extends AbstractChannel {
	/**
	 * @see AbstractChannel#AbstractChannel(String, Session)
	 */
	public AbstractServerChannel(final String address, final Session session) {
		super(address, session);
	}

	/**
	 * Binds the ZMQ socket using {@link Session}.
	 */
	@Override
	protected Socket createSocket() {
		return getSession().bind(this);
	}
}
