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

import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteRequest;
import org.eclipse.ease.jupyter.kernel.messages.IsCompleteReply;
import org.eclipse.ease.jupyter.kernel.messages.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.ease.jupyter.kernel.messages.IsCompleteRequest;

/**
 * Custom message handler for {@link IsCompleteRequest} messages.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class IsCompleteMessageHandler implements IMessageHandler {

	/**
	 * {@link IMessageHandlerFactory} for creating
	 * {@link IsCompleteMessageHandler} objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private AbstractChannel fChannel;

		/**
		 * Constructor only stores parameters to members.
		 * 
		 * @param channel
		 *            {@link AbstractChannel} the message handler is running
		 *            for.
		 */
		public Factory(AbstractChannel channel) {
			fChannel = channel;
		}

		/**
		 * Creates a new {@link IsCompleteMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new IsCompleteMessageHandler(fChannel);
		}

	}

	/**
	 * {@link ObjectMapper} to create {@link ExecuteRequest} from dictionary.
	 */
	private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Abstract channel for sending replies.
	 */
	private AbstractChannel fReplyChannel;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param replyChannel
	 *            {@link AbstractChannel} to send replies to.
	 */
	public IsCompleteMessageHandler(AbstractChannel replyChannel) {
		fReplyChannel = replyChannel;
	}

	/**
	 * Handles the message by checking if the code is complete and sending
	 * result back.
	 */
	@Override
	public void handle(Message message) {
		IsCompleteRequest request = JSON_OBJECT_MAPPER.convertValue(
				message.getContent(), IsCompleteRequest.class);

		// TODO: actually check if request is complete.

		Message reply = message.createReply();
		reply.getHeader().withMsgType("is_complete_reply");

		IsCompleteReply content = new IsCompleteReply()
				.withIndent("")
				.withStatus(
						org.eclipse.ease.jupyter.kernel.messages.IsCompleteReply.Status.COMPLETE);
		reply.withContent(content);
		try {
			fReplyChannel.send(reply);
		} catch (IOException e) {
			// ignore
		}
	}
}
