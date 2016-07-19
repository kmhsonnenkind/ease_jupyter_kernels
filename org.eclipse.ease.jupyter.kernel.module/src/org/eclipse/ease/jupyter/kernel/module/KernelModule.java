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

package org.eclipse.ease.jupyter.kernel.module;

import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.Dispatcher;
import org.eclipse.ease.modules.AbstractScriptModule;
import org.eclipse.ease.modules.ScriptParameter;
import org.eclipse.ease.modules.WrapToScript;

public class KernelModule extends AbstractScriptModule {
	public static final String MODULE_NAME = "Jupyter Kernel Utility";

	/**
	 * Starts a new Jupyter Kernel {@link Dispatcher} for the given
	 * {@link IScriptEngine}.
	 * 
	 * @param engine	Script engine to be used by kernel.
	 * @return	<code>true</code> if successful.
	 * @throws Throwable
	 */
	@WrapToScript
	public boolean startKernel(@ScriptParameter(defaultValue = ScriptParameter.NULL) final IScriptEngine engine)
			throws Throwable {
		
		// Check if engine given
		if (engine == null) {
			return false;
		}

		// Start the dispatcher
		final Dispatcher dispatcher = new Dispatcher(engine, "localhost", 54321);
		final Thread thread = new Thread(dispatcher);
		thread.start();

		return true;
	}
}
