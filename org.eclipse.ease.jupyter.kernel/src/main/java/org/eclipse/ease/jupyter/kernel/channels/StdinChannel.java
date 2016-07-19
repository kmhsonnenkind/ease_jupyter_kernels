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
import org.zeromq.ZMQ;

/**
 * Custom Jupyter kernel channel to query data from clients.
 */
public class StdinChannel extends AbstractServerChannel {
	/**
	 * @see AbstractServerChannel#AbstractServerChannel(String, Session).
	 */
	public StdinChannel(final String address, final Session session) {
		super(address, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ease.jupyter.kernel.channels.AbstractChannel#getZmqSocketType
	 * ()
	 */
	@Override
	public int getZmqSocketType() {
		return ZMQ.ROUTER;
	}
}
