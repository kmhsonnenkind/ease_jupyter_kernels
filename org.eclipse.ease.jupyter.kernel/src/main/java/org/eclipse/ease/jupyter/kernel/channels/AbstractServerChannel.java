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
