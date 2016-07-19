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

import org.eclipse.ease.jupyter.kernel.messages.Message;

/**
 * Message handler for handling messages received from Jupyter client.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 * 
 */
public interface IMessageHandler {
	/**
	 * Handles the given message.
	 * 
	 * @param message
	 *            {@link Message} to be handled.
	 */
	public void handle(Message message);

}
