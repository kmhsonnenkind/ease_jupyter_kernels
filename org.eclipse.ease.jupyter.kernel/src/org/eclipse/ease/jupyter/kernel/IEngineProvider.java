package org.eclipse.ease.jupyter.kernel;

import org.eclipse.ease.IScriptEngine;

/**
 * Simple interface for objects providing dynamic access to an
 * {@link IScriptEngine}.
 */
public interface IEngineProvider {
	/**
	 * Returns the {@link IScriptEngine} provided by the object.
	 * 
	 * @return {@link IScriptEngine} for executing code.
	 */
	public IScriptEngine getEngine();

	/**
	 * Resets the {@link IScriptEngine} provided by the object.
	 */
	public void resetEngine();
}
