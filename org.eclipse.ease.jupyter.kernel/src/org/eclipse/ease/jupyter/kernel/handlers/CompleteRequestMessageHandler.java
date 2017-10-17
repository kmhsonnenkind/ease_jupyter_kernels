/*******************************************************************************
 * Copyright (c) 2017 Martin Kloesch and others.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.CompleteReply;
import org.eclipse.ease.jupyter.kernel.messages.CompleteRequest;

import org.eclipse.ease.jupyter.kernel.messages.CompleteReply.Status;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.eclipse.ease.ui.completion.CodeCompletionAggregator;
import org.eclipse.ease.ui.completion.ScriptCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom message handler for {@link CompleteRequest} messages.
 */
public class CompleteRequestMessageHandler implements IMessageHandler {
	public static final String REQUEST_NAME = "complete_request";
	private static final String REPLY_NAME = "complete_reply";

	/**
	 * {@link IMessageHandlerFactory} for creating
	 * {@link CompleteRequestMessageHandler} objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private final AbstractChannel fRequestChannel;

		/**
		 * {@link IScriptEngine} to be used for code completion.
		 */
		private final IScriptEngine fEngine;

		/**
		 * Constructor only stores parameters to members.
		 * 
		 * @param channel
		 *            {@link AbstractChannel} the message handler is running
		 *            for.
		 * @param engine
		 *            {@link IScriptEngine} to be used for code completion.
		 */
		public Factory(final AbstractChannel channel, IScriptEngine engine) {
			fRequestChannel = channel;
			fEngine = engine;
		}

		/**
		 * Creates a new {@link CompleteRequestMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new CompleteRequestMessageHandler(fRequestChannel, fEngine);
		}

	}

	/**
	 * {@link ObjectMapper} to create {@link CompleteRequest} from dictionary.
	 */
	private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * {@link AbstractChannel} for sending replies.
	 */
	private final AbstractChannel fReplyChannel;

	/**
	 * {@link IScriptEngine} to be used for code completion.
	 */
	private final IScriptEngine fEngine;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param replyChannel
	 *            {@link AbstractChannel} to send replies to.
	 * @param engine
	 *            {@link IScriptEngine} to be used for code completion.
	 */
	public CompleteRequestMessageHandler(AbstractChannel replyChannel, IScriptEngine engine) {
		fReplyChannel = replyChannel;
		fEngine = engine;
	}

	/**
	 * Handles the message by using {@link CodeCompletionAggregator} to get
	 * completion proposals and returning the matches.
	 */
	@Override
	public void handle(Message message) {
		// Parse request to more easily usable format.
		CompleteRequest request;
		try {
			request = JSON_OBJECT_MAPPER.convertValue(message.getContent(), CompleteRequest.class);
			request.validate();
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}

		String code = request.getCode().substring(0, request.getCursorPos());

		synchronized (ExecuteMessageHandler.getLock(fEngine)) {
			// Already create default reply
			Message reply = message.createReply();
			reply.getHeader().withMsgType(REPLY_NAME);

			// Assume that everything worked correctly, otherwise update later
			Set<String> matches = new HashSet<>();
			Integer cursorStart = code.length();
			Integer cursorEnd = code.length();
			Map<String, Object> metadata = new HashMap<>();
			Status status = Status.OK;

			// Use completion aggregator and extensions
			CodeCompletionAggregator completer = new CodeCompletionAggregator();
			completer.setScriptEngine(fEngine);

			// Convert to more usable format
			List<ICompletionProposal> proposals = completer.getCompletionProposals(null, code, code.length(), 0, null);
			for (ICompletionProposal proposal : proposals) {

				// ScriptCompletionProposals can be handled more elegantly
				if (proposal instanceof ScriptCompletionProposal) {
					ScriptCompletionProposal scp = (ScriptCompletionProposal) proposal;
					matches.add(scp.getReplacementString());
					cursorStart = scp.getCursorStartPosition();
				} else {
					String completion = proposal.getDisplayString();
					matches.add(completion);

					// Update cursor start position
					cursorStart = code.length();
					for (int i = 1; i < completion.length(); i++) {
						if (code.endsWith(completion.substring(0, i))) {
							cursorStart = cursorStart - i;
							break;
						}
					}
				}
			}

			// Use list instead of set for matches
			List<String> matchList = new ArrayList<>();
			matchList.addAll(matches);

			// Create reply content
			CompleteReply content = new CompleteReply().withMatches(matchList).withCursorStart(cursorStart)
					.withCursorEnd(cursorEnd).withMetadata(metadata).withStatus(status);
			reply = reply.withContent(content);

			// Actually send the reply
			try {
				fReplyChannel.send(reply);
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
