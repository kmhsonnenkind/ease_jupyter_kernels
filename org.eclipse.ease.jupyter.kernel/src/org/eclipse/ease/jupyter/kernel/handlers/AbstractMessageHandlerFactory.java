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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ease.IScriptEngine;
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
	public AbstractMessageHandlerFactory(Kernel kernel, AbstractServerChannel channel, IOPubChannel ioPub,
			IScriptEngine engine) {
		// Add all message handler factories
		fFactoryMethods.put(KernelInfoMessageHandler.REQUEST_NAME,
				new KernelInfoMessageHandler.Factory(channel, kernel));
		fFactoryMethods.put(ExecuteMessageHandler.REQUEST_NAME,
				new ExecuteMessageHandler.Factory(channel, ioPub, engine));
		fFactoryMethods.put(IsCompleteMessageHandler.REQUEST_NAME, new IsCompleteMessageHandler.Factory(channel));
		fFactoryMethods.put(HistoryMessageHandler.REQUEST_NAME, new HistoryMessageHandler.Factory(channel));
		fFactoryMethods.put(CompleteRequestMessageHandler.REQUEST_NAME, new CompleteRequestMessageHandler.Factory(channel, engine));
		fFactoryMethods.put(InspectRequestMessageHandler.REQUEST_NAME, new InspectRequestMessageHandler.Factory(channel, engine));

		// TODO: find race condition in shutdown request
		// fFactoryMethods.put(ShutdownMessageHandler.REQUEST_NAME, new
		// ShutdownMessageHandler.Factory(channel, kernel));
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
