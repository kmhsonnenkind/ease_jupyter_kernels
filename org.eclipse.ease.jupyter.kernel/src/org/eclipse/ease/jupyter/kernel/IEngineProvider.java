/*******************************************************************************
 * Copyright (c) 2018 Martin Kloesch and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Kloesch - initial API and implementation
 *******************************************************************************/

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
