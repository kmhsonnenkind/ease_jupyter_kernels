package org.eclipse.ease.jupyter.kernel.handlers;

import java.io.IOException;

import org.eclipse.ease.jupyter.kernel.Kernel;
import org.eclipse.ease.jupyter.kernel.channels.AbstractChannel;
import org.eclipse.ease.jupyter.kernel.messages.Content;
import org.eclipse.ease.jupyter.kernel.messages.KernelInfoReply;
import org.eclipse.ease.jupyter.kernel.messages.LanguageInfo;
import org.eclipse.ease.jupyter.kernel.messages.Message;

/**
 * Custom message handler for handling kernel info messages.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class KernelInfoMessageHandler implements IMessageHandler {
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
		reply.getHeader().withMsgType("kernel_info_reply");

		// TODO: Query kernel rather than hardcoding data
		Content content = new KernelInfoReply()
				.withProtocolVersion("5.0")
				.withImplementation("ease")
				.withImplementationVersion("0.0.1")
				.withBanner("EASE Test Kernel")
				.withLanguageInfo(
						new LanguageInfo().withMimetype("text/javascript")
								.withFileExtension(".js")
								.withName("javascript").withVersion("0.0.0"));

		reply.withContent(content);
		try {
			fReplyChannel.send(reply);
		} catch (IOException e) {
			// ignore
		}
	}
}
