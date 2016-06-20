package org.eclipse.ease.jupyter.kernel.channels;

import org.eclipse.ease.jupyter.kernel.Session;
import org.zeromq.ZMQ;

/**
 * Custom Jupyter kernel channel to query data from clients.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
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
