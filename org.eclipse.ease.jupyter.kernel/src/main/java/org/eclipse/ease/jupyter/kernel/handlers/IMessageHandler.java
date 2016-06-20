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
