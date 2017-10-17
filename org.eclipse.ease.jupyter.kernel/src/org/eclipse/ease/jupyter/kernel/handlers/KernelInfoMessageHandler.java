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

package org.eclipse.ease.jupyter.kernel.handlers;

import java.io.IOException;

import org.eclipse.ease.jupyter.kernel.Kernel;
import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.KernelInfoReply;
import org.eclipse.ease.jupyter.kernel.messages.LanguageInfo;
import org.eclipse.ease.jupyter.kernel.messages.Message;

/**
 * Custom message handler for handling kernel info messages.
 */
public class KernelInfoMessageHandler implements IMessageHandler {
	public static final String REQUEST_NAME = "kernel_info_request";
	private static final String REPLY_NAME = "kernel_info_reply";

	/**
	 * {@link IMessageHandlerFactory} for creating
	 * {@link KernelInfoMessageHandler} objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private final AbstractChannel fChannel;

		/**
		 * {@link Kernel} object to query information from.
		 */
		private final Kernel fKernel;

		/**
		 * Constructor only stores parameters to members.
		 * 
		 * @param channel
		 *            {@link AbstractChannel} the message handler is running
		 *            for.
		 * @param kernel
		 *            {@link Kernel} object to query information from.
		 */
		public Factory(AbstractChannel channel, Kernel kernel) {
			fChannel = channel;
			fKernel = kernel;
		}

		/**
		 * Creates a new {@link KernelInfoMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new KernelInfoMessageHandler(fChannel, fKernel);
		}

	}

	/**
	 * Abstract channel for sending replies.
	 */
	private final AbstractChannel fReplyChannel;

	/**
	 * {@link Kernel} object to query information from.
	 */
	private final Kernel fKernel;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param channel
	 *            {@link AbstractChannel} the message handler is running for.
	 * @param kernel
	 *            {@link Kernel} object to query information from.
	 */
	public KernelInfoMessageHandler(AbstractChannel channel, Kernel kernel) {
		fReplyChannel = channel;
		fKernel = kernel;
	}

	/**
	 * Handles the given message by creating and sending back
	 * {@link KernelInfoReply} message.
	 */
	@Override
	public void handle(Message message) {
		Message reply = message.createReply();
		reply.getHeader().withMsgType(REPLY_NAME);

		// FIXME: Query kernel rather than hardcoding data
		KernelInfoReply content = new KernelInfoReply().withProtocolVersion("5.0").withImplementation("ease")
				.withImplementationVersion("0.0.1").withBanner("EASE Test Kernel")
				.withLanguageInfo(new LanguageInfo().withMimetype("text/x-python3").withFileExtension(".py")
						.withName("python").withVersion("0.0.0").withPygmentsLexer("py3").withNbconvertExporter("python"));

		reply.withContent(content);
		try {
			fReplyChannel.send(reply);
		} catch (IOException e) {
			// ignore
		}
	}
}
