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
import java.io.OutputStream;

import org.eclipse.ease.jupyter.kernel.messages.Header;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.eclipse.ease.jupyter.kernel.messages.Stream;

/**
 * Custom {@link OutputStream} for redirecting STDOUT and STDERR.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class ChannelOutputStream extends OutputStream {
	/**
	 * {@link StringBuffer} containing the data to be send.
	 */
	private final StringBuffer fDataBuffer = new StringBuffer();

	/**
	 * Stream name necessary to differentiate between different output types
	 * (STDOUT, STDERR).
	 */
	private final String fStreamName;

	/**
	 * {@link IOPubChannel} actually sending the data.
	 */
	private final IOPubChannel fChannel;

	/**
	 * {@link Header} currently in use for information about session.
	 */
	private Header fHeader;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param streamName
	 *            Name of stream (stdout or stderr)
	 * @param channel
	 *            {@link IOPubChannel} for actually sending data.
	 */
	public ChannelOutputStream(String streamName, IOPubChannel channel) {
		fStreamName = streamName;
		fChannel = channel;
	}

	/**
	 * Sets the parent header to be used.
	 * 
	 * @param parentHeader
	 *            Necessary because Jupyter notebook keeps track of origins for
	 *            stream messages.
	 */
	public void setParentHeader(Header header) {
		fHeader = header;
	}

	/**
	 * Appends the given value to the internal buffer (as char).
	 * 
	 * @see OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		synchronized (this) {
			fDataBuffer.append((char) b);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		super.flush();

		// Get data and reset buffer
		String data;
		synchronized (this) {
			if (fDataBuffer.length() == 0) {
				return;
			}
			data = fDataBuffer.toString();
			fDataBuffer.setLength(0);
		}

		// Create message to be send
		Message message = new Message();
		if (fHeader != null) {
			message = message.withParentHeader(fHeader);
		}
		message.getHeader().withMsgType("stream").withMsgId(Message.randomId());
		message.withContent(new Stream().withName(fStreamName).withText(data));

		fChannel.send(message);

	}

}
