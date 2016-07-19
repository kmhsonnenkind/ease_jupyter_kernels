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
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.ease.jupyter.kernel.Session;

/**
 * Extension of {@link AbstractServerChannel} storing information if channel is
 * currently running.
 *
 */
public abstract class AbstractRunningServerChannel extends AbstractServerChannel {

	/**
	 * Flag to check if channel is still running.
	 */
	private final AtomicBoolean running = new AtomicBoolean(true);

	/**
	 * @see AbstractServerChannel#AbstractServerChannel(String, Session)
	 */
	public AbstractRunningServerChannel(final String address, final Session session) {
		super(address, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ease.jupyter.kernel.channels.AbstractChannel#close()
	 */
	@Override
	public void close() throws IOException {
		if (isRunning()) {
			stop();
		}

		super.close();
	}

	/**
	 * Getter to see if channel is still running.
	 * 
	 * @return <code>true</code> if channel is still running.
	 */
	public boolean isRunning() {
		return running.get();
	}

	/**
	 * Sets running flag to <code>false</code>.
	 */
	public void stop() {
		running.set(false);
	}

	/**
	 * Sets running flag to <code>true</code>
	 */
	public void start() {
		running.set(true);
	}
}
