package org.eclipse.ease.jupyter.kernel.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ease.jupyter.kernel.Kernel;
import org.eclipse.ease.jupyter.kernel.channels.AbstractServerChannel;
import org.eclipse.ease.jupyter.kernel.channels.IOPubChannel;
import org.eclipse.ease.jupyter.kernel.channels.StdinChannel;

/**
 * Abstract Factory for creating {@link IMessageHandlerFactory} objects.
 * 
 * This allows decoupling of the calling code and the implementation of the
 * {@link IMessageHandler} objects.
 * 
 * Users do not need to know about the available {@link IMessageHandler}s but
 * only need to call this method to get the corresponding factory method.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class AbstractMessageHandlerFactory {
	/**
	 * Lookup table from message type to {@link IMessageHandlerFactory} to
	 * create new {@link IMessageHandler} objects.
	 */
	private final Map<String, IMessageHandlerFactory> fFactoryMethods = new HashMap<String, IMessageHandlerFactory>();

	/**
	 * Constructor initializes members and populates message handler array.
	 * 
	 * @param kernel
	 *            {@link Kernel} necessary because handlers might need to
	 *            perform callbacks.
	 * @param channel
	 *            {@link AbstractServerChannel} to be able to send back data.
	 * @param ioPub
	 *            {@link IOPubChannel} for publishing data.
	 * @param stdin
	 *            {@link StdinChannel} for querying data from user.
	 */
	public AbstractMessageHandlerFactory(Kernel kernel,
			AbstractServerChannel channel, IOPubChannel ioPub,
			StdinChannel stdin) {
		fFactoryMethods.put("kernel_info_request",
				new KernelInfoMessageHandler.Factory(channel, kernel));
		fFactoryMethods.put("execute_request",
				new ExecuteMessageHandler.Factory(channel, ioPub, stdin));
		fFactoryMethods.put("is_complete_request",
				new IsCompleteMessageHandler.Factory(channel));
	}

	/**
	 * Return {@link IMessageHandlerFactory} for given message type.
	 * 
	 * @param messageType
	 *            Message type to get {@link IMessageHandlerFactory} for.
	 * @return {@link IMessageHandlerFactory} if implemented message type,
	 *         <code>null</code> otherwise.
	 */
	public IMessageHandlerFactory getHandlerFactory(String messageType) {
		return fFactoryMethods.get(messageType);

	}
}
