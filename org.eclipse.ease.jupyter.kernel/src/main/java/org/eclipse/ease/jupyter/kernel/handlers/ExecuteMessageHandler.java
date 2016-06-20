package org.eclipse.ease.jupyter.kernel.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.channels.IOPubChannel;
import org.eclipse.ease.jupyter.kernel.channels.StdinChannel;
import org.eclipse.ease.jupyter.kernel.messages.Content;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteReply;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteReply.Status;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.eclipse.ease.jupyter.kernel.messages.Payload;
import org.eclipse.ease.jupyter.kernel.messages.Stream;
import org.eclipse.ease.jupyter.kernel.messages.UserExpressions;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.ease.jupyter.kernel.messages.ExecuteRequest;

/**
 * Custom message handler for {@link ExecuteRequest} messages.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class ExecuteMessageHandler implements IMessageHandler {
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
		 * {@link StdinChannel} to query data from clients.
		 */
		private final StdinChannel fStdin;

		/**
		 * Overall execution count for {@link ExecuteMessageHandler}.
		 */
		private static int fExecutionCount = 0;

		/**
		 * Constructor only stores parameters to members.
		 * 
		 * @param channel
		 *            {@link AbstractChannel} the message handler is running
		 *            for.
		 * @param ioPub
		 *            {@link IOPubChannel} to send data to clients.
		 * @param stdin
		 *            {@link StdinChannel} to query data from clients.
		 */
		public Factory(final AbstractChannel channel, IOPubChannel ioPub,
				StdinChannel stdin) {
			fRequestChannel = channel;
			fIoPub = ioPub;
			fStdin = stdin;
		}

		/**
		 * Creates a new {@link ExecuteMessageHandler} object.
		 */
		@Override
		public IMessageHandler create() {
			return new ExecuteMessageHandler(fRequestChannel, fIoPub, fStdin,
					fExecutionCount++);
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
	 * {@link StdinChannel} to query data from clients.
	 */
	private final StdinChannel fStdin;

	/**
	 * Overall execution count set by constructor. Actual counter handled in
	 * {@link Factory}.
	 */
	private final int fExecutionCount;

	/**
	 * Constructor only stores parameters to members.
	 * 
	 * @param replyChannel
	 *            {@link AbstractChannel} to send replies to.
	 * @param ioPub
	 *            {@link IOPubChannel} to send data to clients.
	 * @param stdin
	 *            {@link StdinChannel} to query data from clients.
	 * @param executionCount
	 *            Overall execution counter to be appended to the reply.
	 */
	public ExecuteMessageHandler(AbstractChannel replyChannel,
			IOPubChannel ioPub, StdinChannel stdin, int executionCount) {
		fReplyChannel = replyChannel;
		fIoPub = ioPub;
		fStdin = stdin;
		fExecutionCount = executionCount;
	}

	/**
	 * Handles the message by executing the code and sending the result back.
	 */
	@Override
	public void handle(Message message) {
		// Parse request to more easily usable format.
		ExecuteRequest request = JSON_OBJECT_MAPPER.convertValue(
				message.getContent(), ExecuteRequest.class);

		// TODO: actually execute

		// Create the reply
		Message reply = message.createReply();
		reply.getHeader().withMsgType("execute_reply");

		Content content = new ExecuteReply().withStatus(Status.OK)
				.withPayload(new ArrayList<Payload>())
				.withExecutionCount(fExecutionCount)
				.withUserExpressions(new UserExpressions());
		reply.withContent(content);

		// Actually send the reply
		try {
			fReplyChannel.send(reply);

			if (fIoPub != null) {
				// Create echo message
				Message echoMessage = new Message();
				echoMessage.getHeader().withMsgType("stream")
						.withMsgId(Message.randomId())
						.withSession(message.getHeader().getSession());

				// Create content
				Content echoContent = new Stream().withName("stdout").withText(
						request.getCode());
				echoMessage.withContent(echoContent);

				fIoPub.send(echoMessage);
			}
		} catch (IOException e) {
			// ignore
		}
	}
}
