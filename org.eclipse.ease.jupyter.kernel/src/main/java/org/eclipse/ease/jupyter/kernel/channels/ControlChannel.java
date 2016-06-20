package org.eclipse.ease.jupyter.kernel.channels;

import org.eclipse.ease.jupyter.kernel.Kernel;
import org.eclipse.ease.jupyter.kernel.Session;

/**
 * Jupyter kernel {@link ShellChannel} with higher priority because less
 * messages will be received.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class ControlChannel extends ShellChannel {
	/**
	 * @see ShellChannel#ShellChannel(String, Session, Kernel, IOPubChannel,
	 *      StdinChannel)
	 */
	public ControlChannel(final String address, final Session session,
			Kernel kernel, IOPubChannel ioPub, StdinChannel stdin) {
		super(address, session, kernel, ioPub, stdin);
	}
}
