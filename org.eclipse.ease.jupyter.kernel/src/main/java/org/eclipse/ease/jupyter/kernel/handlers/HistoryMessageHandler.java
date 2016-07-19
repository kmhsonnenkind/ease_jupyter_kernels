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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteRequest;
import org.eclipse.ease.jupyter.kernel.messages.HistoryReply;
import org.eclipse.ease.jupyter.kernel.messages.HistoryRequest;
import org.eclipse.ease.jupyter.kernel.messages.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom message handler for history messages.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class HistoryMessageHandler implements IMessageHandler {
	/**
	 * {@link IMessageHandlerFactory} for creating {@link HistoryMessageHandler}
	 * objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private final AbstractChannel fChannel;

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
		 * Creates a new {@link HistoryMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new HistoryMessageHandler(fChannel);
		}

	}

	/**
	 * Private class used for storing the last part of the history information
	 * tuple.
	 * 
	 * As the last entry can either be a string or a tuple, this simple helper
	 * class is used.
	 * 
	 * @author Martin Kloesch (martin.kloesch@gmail.com)
	 *
	 */
	private static class HistoryContent {
		/**
		 * Input part of history content (required)
		 */
		private final String fInput;

		/**
		 * Output part of history content (optional)
		 */
		private final String fOuput;

		/**
		 * Constructor stores parameters to members.
		 * 
		 * @param input
		 *            Input part of history content (required).
		 * @param output
		 *            Outpart of history content (optional).
		 */
		public HistoryContent(String input, String output) {
			fInput = input;
			fOuput = output;
		}

		/**
		 * Serializes the object to either a string or a tuple of strings.
		 * 
		 * @param withOutput
		 *            Flag to signalize if output should be appended as well.
		 * @return Serialized content for history tuple.
		 */
		public Object serialize(boolean withOutput) {
			// Use string if no output
			if (!withOutput || fOuput == null) {
				return fInput;
			} else {
				// Otherwise use tuple.
				List<String> serialized = new ArrayList<String>();
				serialized.add(fInput);
				serialized.add(fOuput);
				return serialized;
			}
		}
	}

	/**
	 * Data storage class for single entry in history.
	 * 
	 * @author Martin Kloesch (martin.kloesch@gmail.com)
	 *
	 */
	public static class HistoryTuple {
		/**
		 * Session the code was executed for.
		 */
		private final String fSession;

		/**
		 * Line number of code (execution count)
		 */
		private final int fLineNumber;

		/**
		 * Actual content (input + optional output)
		 */
		private final HistoryContent fContent;

		/**
		 * Constructor stores parameters to members.
		 * 
		 * @param session
		 *            Session the code was executed for.
		 * @param lineNumber
		 *            Line number of code (execution count)
		 * @param input
		 *            Code input.
		 * @param output
		 *            Code output.
		 */
		public HistoryTuple(String session, int lineNumber, String input, String output) {
			fSession = session;
			fLineNumber = lineNumber;
			fContent = new HistoryContent(input, output);
		}

		/**
		 * Overload without output.
		 * 
		 * @see HistoryTuple#HistoryTuple(String, int, String, String)
		 */
		public HistoryTuple(String session, int lineNumber, String input) {
			this(session, lineNumber, input, null);
		}

		/**
		 * Serializes the {@link HistoryTuple} to a list of objects that can be
		 * send over Jupyter socket.
		 * 
		 * @param withOutput
		 *            Flag to signalize if output should also be appended
		 *            (default <code>true</code>)
		 * @return List of objects that can be send over Jupyter socket.
		 */
		public List<Object> serialize(boolean withOutput) {
			List<Object> serialized = new ArrayList<Object>();
			serialized.add(fSession);
			serialized.add(new Integer(fLineNumber));
			serialized.add(fContent.serialize(withOutput));
			return serialized;
		}

		/**
		 * Overload of {@link #serialize(boolean)} with default parameters
		 * (withOutput = <code>true</code>).
		 * 
		 * @see #serialize(boolean)
		 */
		public List<Object> serialize() {
			return serialize(true);
		}
	}

	/**
	 * Static datastorage class for execution history.
	 * 
	 * @author Martin Kloesch (martin.kloesch@gmail.com)
	 *
	 */
	public static class History {
		/**
		 * Actual history.
		 */
		private static final List<HistoryTuple> fHistory = new ArrayList<HistoryTuple>();

		/**
		 * Appends a new history tuple to the internal list.
		 * 
		 * @param tuple
		 *            {@link HistoryTuple} to be appended.
		 */
		public static void append(HistoryTuple tuple) {
			synchronized (fHistory) {
				fHistory.add(tuple);
			}
		}

		/**
		 * Wraps parameters to {@link HistoryTuple}.
		 * 
		 * @see #append(HistoryTuple)
		 * @see HistoryTuple#HistoryTuple(String, int, String)
		 */
		public static void append(String session, int lineNumber, String input, String output) {
			HistoryTuple tuple = new HistoryTuple(session, lineNumber, input, output);
			append(tuple);
		}

		/**
		 * Wraps parameters to {@link HistoryTuple}.
		 * 
		 * @see #append(HistoryTuple)
		 * @see HistoryTuple#HistoryTuple(String, int, String, String)
		 */
		public static void append(String session, int lineNumber, String input) {
			append(session, lineNumber, input, null);
		}

		/**
		 * Returns copy of history in format that can be send over Jupyter
		 * socket.
		 * 
		 * @return History in format that can be send over Jupyter socket.
		 */
		public static List<List<Object>> getHistory(boolean withOutput) {
			List<List<Object>> history = new ArrayList<List<Object>>();
			synchronized (fHistory) {
				for (HistoryTuple tuple : fHistory) {
					history.add(tuple.serialize(withOutput));
				}

			}
			return history;
		}
	}

	/**
	 * {@link ObjectMapper} to create {@link ExecuteRequest} from dictionary.
	 */
	private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Abstract channel for sending replies.
	 */
	private final AbstractChannel fReplyChannel;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param channel
	 *            {@link AbstractChannel} the message handler is running for.
	 */
	public HistoryMessageHandler(AbstractChannel channel) {
		fReplyChannel = channel;
	}

	/**
	 * Handles the given message by getting the history for the given message.
	 */
	@Override
	public void handle(Message message) {
		// Parse request to more usable format
		HistoryRequest request = JSON_OBJECT_MAPPER.convertValue(message.getContent(), HistoryRequest.class);

		// Create reply
		Message reply = message.createReply();
		reply.getHeader().withMsgType("history_reply");

		// Set history
		// TODO: Handle filters
		HistoryReply content = new HistoryReply().withHistory(History.getHistory(request.getOutput()));
		reply.withContent(content);

		try {
			// Actually send reply
			fReplyChannel.send(reply);
		} catch (IOException e) {
			// ignore
		}
	}
}
