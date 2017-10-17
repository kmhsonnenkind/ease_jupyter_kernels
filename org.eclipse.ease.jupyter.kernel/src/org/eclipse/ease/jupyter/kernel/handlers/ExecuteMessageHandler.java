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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.ScriptResult;
import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.channels.ChannelPrintStream;
import org.eclipse.ease.jupyter.kernel.channels.IOPubChannel;
import org.eclipse.ease.jupyter.kernel.handlers.HistoryMessageHandler.History;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteInput;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteReply;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteReply.Status;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.eclipse.ease.jupyter.kernel.messages.Status.ExecutionState;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.ease.jupyter.kernel.messages.ExecuteRequest;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteResult;
import org.eclipse.ease.jupyter.kernel.messages.Header;

/**
 * Custom message handler for {@link ExecuteRequest} messages.
 */
public class ExecuteMessageHandler implements IMessageHandler {
	public static final String REQUEST_NAME = "execute_request";
	private static final String REPLY_NAME = "execute_reply";
	private static final String EXECUTE_INPUT = "execute_input";
	private static final String EXECUTE_RESULT = "execute_result";
	private static final String STATUS_TYPE = "status";

	/**
	 * {@link IMessageHandlerFactory} for creating {@link ExecuteMessageHandler}
	 * objects.
	 *
	 */
	public static class Factory implements IMessageHandlerFactory {
		/**
		 * {@link AbstractChannel} the message handler is running for.
		 */
		private final AbstractChannel fRequestChannel;

		/**
		 * {@link IOPubChannel} to send data to clients.
		 */
		private final IOPubChannel fIoPub;

		/**
		 * {@link IScriptEngine} to execute code on.
		 */
		private final IScriptEngine fEngine;

		/**
		 * Constructor only stores parameters to members.
		 * 
		 * @param channel
		 *            {@link AbstractChannel} the message handler is running
		 *            for.
		 * @param ioPub
		 *            {@link IOPubChannel} to send data to clients.
		 * @param engine
		 *            {@link IScriptEngine} to execute code on.
		 */
		public Factory(final AbstractChannel channel, IOPubChannel ioPub, IScriptEngine engine) {
			fRequestChannel = channel;
			fIoPub = ioPub;
			fEngine = engine;
		}

		/**
		 * Creates a new {@link ExecuteMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new ExecuteMessageHandler(fRequestChannel, fIoPub, fEngine);
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
	 * {@link IOPubChannel} to send data to clients.
	 */
	private final IOPubChannel fIoPub;

	/**
	 * {@link IScriptEngine} to execute code on.
	 */
	private final IScriptEngine fEngine;

	/**
	 * Overall execution count set by constructor. Actual counter handled in
	 * {@link Factory}.
	 */
	private static int fExecutionCount;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param replyChannel
	 *            {@link AbstractChannel} to send replies to.
	 * @param ioPub
	 *            {@link IOPubChannel} to send data to clients.
	 * @param engine
	 *            {@link IScriptEngine} to execute code on.
	 */
	public ExecuteMessageHandler(AbstractChannel replyChannel, IOPubChannel ioPub, IScriptEngine engine) {
		fReplyChannel = replyChannel;
		fIoPub = ioPub;
		fEngine = engine;
	}

	/**
	 * Broadcasts the code input to let other clients know what is currently
	 * being executed.
	 * 
	 * @param code
	 *            Code being executed.
	 */
	private void broadcastInput(String code, Header parentHeader) {
		Message codeInputMessage = new Message().withParentHeader(parentHeader);
		codeInputMessage.getHeader().withMsgType(EXECUTE_INPUT).withMsgId(Message.randomId())
				.withSession(parentHeader.getSession());
		ExecuteInput codeInputContent = new ExecuteInput().withCode(code).withExecutionCount(fExecutionCount++);
		codeInputMessage.withContent(codeInputContent);
		fIoPub.send(codeInputMessage);
	}

	/**
	 * Broadcasts the execution state for the given session to be
	 * {@link ExecutionState#BUSY}.
	 * 
	 * @param parentHeader
	 *            Message header to broadcast the execution state for.
	 * @see #broadcastState(ExecutionState, Header)
	 */
	private void broadcastStart(Header parentHeader) {
		broadcastState(ExecutionState.BUSY, parentHeader);
	}

	/**
	 * Broadcasts the execution state for the given session to be
	 * {@link ExecutionState#IDLE}.
	 * 
	 * @param parentHeader
	 *            Message header to broadcast the execution state for.
	 * @see #broadcastState(ExecutionState, Header)
	 */
	private void broadcastStop(Header parentHeader) {
		broadcastState(ExecutionState.IDLE, parentHeader);
	}

	/**
	 * Broadcast the kernel's execution state for the given session.
	 * 
	 * @param state
	 *            {@link ExecutionState} to be broadcasted.
	 * @param parentHeader
	 *            Message header to broadcast the execution state for.
	 */
	private void broadcastState(ExecutionState state, Header parentHeader) {
		Message statusMessage = new Message().withParentHeader(parentHeader);
		statusMessage.getHeader().withMsgType(STATUS_TYPE).withMsgId(Message.randomId())
				.withSession(parentHeader.getSession());
		statusMessage.withContent(new org.eclipse.ease.jupyter.kernel.messages.Status().withExecutionState(state));
		fIoPub.send(statusMessage);
	}

	/**
	 * Broadcasts the result of a code execution to all clients.
	 * 
	 * @param result
	 *            Result to be broadcasted.
	 */
	private void broadcastResult(ScriptResult result, Header parentHeader) {
		// Check if valid result given.
		if (result == null || result.getException() != null || result.getResult() == null) {
			return;
		}

		Object resultObject = result.getResult();
		IJupyterPublishable publishableResult = null;
		// Check if the result already has correct format
		if (resultObject instanceof IJupyterPublishable) {
			publishableResult = (IJupyterPublishable) resultObject;
		} else {
			// Try to get an adapter to cast to IJupyterPublishable
			IAdapterManager manager = org.eclipse.core.runtime.Platform.getAdapterManager();
			publishableResult = manager.getAdapter(resultObject, IJupyterPublishable.class);
			if (publishableResult == null) {
				// Last fallback use string representation
				publishableResult = new StringPublishable(resultObject.toString());
			}
		}

		// Create result message
		Message resultMessage = new Message().withParentHeader(parentHeader);
		resultMessage.getHeader().withMsgId(Message.randomId()).withMsgType(EXECUTE_RESULT)
				.withSession(parentHeader.getSession());
		ExecuteResult executeResult = new ExecuteResult().withExecutionCount(fExecutionCount)
				.withData(publishableResult.toMimeTypeDict()).withMetadata(new HashMap<String, Object>());
		resultMessage.withContent(executeResult);

		// Broadcast message
		fIoPub.send(resultMessage);
	}

	/**
	 * Stores the executed code (and potentially its) result in the history.
	 * 
	 * @param request
	 *            Request with information about code execution.
	 * @param result
	 *            Object containing the result.
	 * @param session
	 *            Session ID necessary to map requests to sessions.
	 */
	private void storeHistory(ExecuteRequest request, ScriptResult result, String session) {
		// Check to see if history should be stored
		if (!request.getSilent() && request.getStoreHistory()) {
			String resultString = null;
			if (result != null && result.getException() == null) {
				if (result.getResult() == null) {
					resultString = "null";
				} else {
					resultString = result.getResult().toString();
				}
			}
			History.append(session, fExecutionCount, request.getCode(), resultString);
		}

	}

	/**
	 * Mappings from {@link IScriptEngine} to corresponding lock.
	 * <p>
	 * We cannot directly lock on {@link IScriptEngine} because code execution
	 * is happening in another thread. Therefore we need placeholder objects.
	 */
	private static final Map<IScriptEngine, Object> LOCKS = new HashMap<IScriptEngine, Object>();

	/**
	 * Gets a simple lock object for given {@link IScriptEngine}.
	 * <p>
	 * As we cannot lock on {@link IScriptEngine} directly we need placeholder
	 * objects.
	 * 
	 * @param engine
	 *            {@link IScriptEngine} to get lock for.
	 * @return Lock object for given {@link IScriptEngine}.
	 */
	public static synchronized Object getLock(IScriptEngine engine) {
		if (!LOCKS.containsKey(engine)) {
			LOCKS.put(engine, new Object());
		}
		return LOCKS.get(engine);
	}

	/**
	 * Handles the message by executing the code and sending the result back.
	 */
	@Override
	public void handle(Message message) {
		// Parse request to more easily usable format.
		ExecuteRequest request;
		try {
			request = JSON_OBJECT_MAPPER.convertValue(message.getContent(), ExecuteRequest.class);
			request.validate();
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
		String code = request.getCode();

		synchronized (getLock(fEngine)) {
			// Set message header for print streams
			ChannelPrintStream stdout = (ChannelPrintStream) fEngine.getOutputStream();
			stdout.setParentHeader(message.getHeader());
			ChannelPrintStream stderr = (ChannelPrintStream) fEngine.getErrorStream();
			stderr.setParentHeader(message.getHeader());

			// Broadcast the execution state to be busy
			broadcastStart(message.getHeader());

			// Broadcast code request to let other clients know what is
			// happening
			if (!request.getSilent()) {
				broadcastInput(code, message.getHeader());
			}

			// Already create default reply
			Message reply = message.createReply();
			reply.getHeader().withMsgType(REPLY_NAME);

			// Assume that everything worked correctly, otherwise update later
			ExecuteReply content = new ExecuteReply().withExecutionCount(fExecutionCount).withStatus(Status.OK);
			ScriptResult result = null;
			try {
				// Synchronously execute code using IScriptEngine
				result = fEngine.executeSync(code);

				// Flush output stream just to be sure
				stdout.flush();
				stderr.flush();

				// Also reset the header field just to be sure
				stdout.setParentHeader(null);
				stderr.setParentHeader(null);

				// Check if exception occurred
				Throwable exception = result.getException();
				if (exception != null) {
					// Patch stacktrace to suitable format
					List<String> stackTrace = new ArrayList<String>();
					for (StackTraceElement ste : exception.getStackTrace()) {
						stackTrace.add(ste.toString());
					}

					// Actual set exception
					content = content.withStatus(Status.ERROR).withEname(exception.getClass().getName())
							.withEvalue(exception.getMessage()).withTraceback(stackTrace);
				}
			} catch (InterruptedException e) {
				// Set status to aborted
				content = content.withStatus(Status.ABORT);
			}
			reply = reply.withContent(content);

			// Broadcast result
			if (!request.getSilent()) {
				broadcastResult(result, message.getHeader());
			}

			// Broadcast the execution state to be idle again
			broadcastStop(message.getHeader());

			// (Potentially) Store history
			storeHistory(request, result, message.getHeader().getSession());

			// Actually send the reply
			try {
				fReplyChannel.send(reply);
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
