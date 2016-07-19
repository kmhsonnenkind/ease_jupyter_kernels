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
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.eclipse.ease.jupyter.kernel.messages.ShutdownReply;

/**
 * Custom message handler for handling kernel shutdown messages.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class ShutdownMessageHandler implements IMessageHandler {
	/**
	 * {@link IMessageHandlerFactory} for creating
	 * {@link ShutdownMessageHandler} objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private final AbstractChannel fChannel;

		/**
		 * {@link Kernel} object to be able to later shut it down.
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
		 * Creates a new {@link ShutdownMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new ShutdownMessageHandler(fChannel, fKernel);
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
	public ShutdownMessageHandler(AbstractChannel channel, Kernel kernel) {
		fReplyChannel = channel;
		fKernel = kernel;
	}

	/**
	 * Handles the given message by creating and sending back
	 * {@link KernelInfoReply} message.
	 */
	@Override
	public void handle(Message message) {
		// TODO: handle restarts
		Message reply = message.createReply();
		ShutdownReply content = new ShutdownReply().withRestart(false);
		reply = reply.withContent(content);
		
		try {
			fReplyChannel.send(reply);
			fKernel.stop();

		} catch (IOException e) {
			// ignore
		}
	}
}
