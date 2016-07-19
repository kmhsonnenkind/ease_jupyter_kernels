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

import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.messages.Header;

/**
 * Custom {@link PrintStream} for redirecting STDOUT and STDERR. Necessary
 * because {@link IScriptEngine#getOutputStream()} returns {@link PrintStream}
 * rather than {@link OutputStream}.
 * 
 * Note that this class is <b>NOT</b> thread-safe and users should take care
 * themselves.
 */
public class ChannelPrintStream extends PrintStream {
	/**
	 * {@link ChannelOutputStream} handling the actual data buffering.
	 */
	private final ChannelOutputStream fOutputStream;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param streamName
	 *            Name of stream (stdout or stderr)
	 * @param channel
	 *            {@link IOPubChannel} for actually sending data.
	 */
	public ChannelPrintStream(ChannelOutputStream os) {
		super(os);
		fOutputStream = os;
	}

	/**
	 * Sets the parent header to be used.
	 * 
	 * @param parentHeader
	 *            Necessary because Jupyter notebook keeps track of origins for
	 *            stream messages.
	 * @see ChannelOutputStream#setParentHeader(Header)
	 */
	public void setParentHeader(Header parentHeader) {
		fOutputStream.setParentHeader(parentHeader);
	}

	@Override
	public void flush() {
		super.flush();
	}
}
