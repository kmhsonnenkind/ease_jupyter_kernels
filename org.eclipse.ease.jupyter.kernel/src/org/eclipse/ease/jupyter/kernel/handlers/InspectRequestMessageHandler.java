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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.IEngineProvider;
import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.InspectReply;
import org.eclipse.ease.jupyter.kernel.messages.InspectReply.Status;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.eclipse.ease.ui.completion.CodeCompletionAggregator;
import org.eclipse.ease.ui.completion.ScriptCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.ease.jupyter.kernel.messages.InspectRequest;

/**
 * Custom message handler for {@link InspectRequest} messages.
 */
public class InspectRequestMessageHandler implements IMessageHandler {
	public static final String REQUEST_NAME = "inspect_request";
	private static final String REPLY_NAME = "inspect_reply";

	/**
	 * {@link IMessageHandlerFactory} for creating
	 * {@link InspectRequestMessageHandler} objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private final AbstractChannel fRequestChannel;

		/**
		 * {@link IScriptEngineProvider} to dynamically get {@link IScriptEngine} to
		 * execute code on.
		 */
		private final IEngineProvider fEngineProvider;

		/**
		 * Constructor only stores parameters to members.
		 * 
		 * @param channel
		 *            {@link AbstractChannel} the message handler is running for.
		 * @param engine
		 *            {@link IScriptEngine} to be used for code analysis.
		 */
		public Factory(final AbstractChannel channel, IEngineProvider engineProvider) {
			fRequestChannel = channel;
			fEngineProvider = engineProvider;
		}

		/**
		 * Creates a new {@link InspectRequestMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new InspectRequestMessageHandler(fRequestChannel, fEngineProvider);
		}

	}

	/**
	 * {@link ObjectMapper} to create {@link InspectRequest} from dictionary.
	 */
	private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Abstract channel for sending replies.
	 */
	private final AbstractChannel fReplyChannel;

	/**
	 * {@link IScriptEngineProvider} to dynamically get {@link IScriptEngine} to
	 * execute code on.
	 */
	private final IEngineProvider fEngineProvider;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param replyChannel
	 *            {@link AbstractChannel} to send replies to.
	 * @param engine
	 *            {@link IScriptEngine} to be used for code analysis.
	 */
	public InspectRequestMessageHandler(AbstractChannel replyChannel, IEngineProvider engineProvider) {
		fReplyChannel = replyChannel;
		fEngineProvider = engineProvider;
	}

	/**
	 * Handles the message by using {@link CodeCompletionAggregator} to get object
	 * information and returning the match.
	 */
	@Override
	public void handle(Message message) {
		// Parse request to more easily usable format.
		InspectRequest request;
		try {
			request = JSON_OBJECT_MAPPER.convertValue(message.getContent(), InspectRequest.class);
			request.validate();
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}

		String code = request.getCode().substring(0, request.getCursorPos());

		final IScriptEngine engine = fEngineProvider.getEngine();

		// Already create default reply
		Message reply = message.createReply();
		reply.getHeader().withMsgType(REPLY_NAME);

		// Assume that everything worked correctly, otherwise update later
		Map<String, Object> data = new HashMap<>();
		Status status = Status.OK;
		Map<String, Object> metadata = new HashMap<>();
		Boolean found = false;

		// Use completion aggregator and extensions
		CodeCompletionAggregator completer = new CodeCompletionAggregator();
		completer.setScriptEngine(engine);

		List<ICompletionProposal> proposals = completer.getCompletionProposals(null, code, code.length(), 0, null);
		for (ICompletionProposal proposal : proposals) {
			if (proposal.getAdditionalProposalInfo() != null) {
				found = true;

				if (proposal instanceof ScriptCompletionProposal) {
					ScriptCompletionProposal scp = (ScriptCompletionProposal) proposal;
					data.put("text/plain", scp.getHelpResolver().resolveHelp());
					data.put("text/html", scp.getHelpResolver().resolveHTMLHelp());
				} else {
					data.put("text/plain", proposal.getAdditionalProposalInfo());
				}
				break;
			}
		}

		InspectReply content = new InspectReply().withStatus(status).withFound(found).withData(data)
				.withMetadata(metadata);
		reply = reply.withContent(content);

		// Actually send the reply
		try {
			fReplyChannel.send(reply);
		} catch (IOException e) {
			// ignore
		}
	}
}
