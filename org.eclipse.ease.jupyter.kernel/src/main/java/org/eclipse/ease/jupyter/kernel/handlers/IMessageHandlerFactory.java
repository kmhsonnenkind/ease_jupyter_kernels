package org.eclipse.ease.jupyter.kernel.handlers;

/**
 * Interface for factory methods creating new {@link IMessageHandler}.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 * 
 */
public interface IMessageHandlerFactory {
	/**
	 * Create a new {@link IMessageHandler}.
	 * 
	 * @return New {@link IMessageHandler}.
	 */
	public IMessageHandler create();
}
