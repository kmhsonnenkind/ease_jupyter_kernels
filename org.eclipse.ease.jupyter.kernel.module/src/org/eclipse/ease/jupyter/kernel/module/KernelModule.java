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

	private static final String DEFAULT_HOST = "localhost";
	private static final Integer DEFAULT_PORT = new Integer(54321);

	/**
	 * Starts a new Jupyter Kernel {@link Dispatcher} for the given
	 * {@link IScriptEngine}.
	 * 
	 * @param engine
	 *            Script engine to be used by kernel.
	 * @param host
	 *            Host the {@link Dispatcher} should listen on.
	 * @param port
	 *            Port the {@link Dispatcher} should listen on.
	 * @return <code>true</code> if successful.
	 * @throws Throwable
	 */
	@WrapToScript
	public boolean startKernel(@ScriptParameter(defaultValue = ScriptParameter.NULL) final IScriptEngine engine,
			@ScriptParameter(defaultValue = ScriptParameter.NULL) String host,
			@ScriptParameter(defaultValue = ScriptParameter.NULL) Integer port) throws Throwable {

		// Check if engine given
		if (engine == null) {
			return false;
		}

		if (host == null) {
			host = DEFAULT_HOST;
		}

		if (port == null) {
			port = DEFAULT_PORT;
		}

		// Start the dispatcher
		final Dispatcher dispatcher = new Dispatcher(engine, host, port);
		final Thread thread = new Thread(dispatcher);
		thread.start();

		return true;
	}

}
