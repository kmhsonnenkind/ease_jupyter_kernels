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

package org.eclipse.ease.jupyter.kernel;

import java.io.IOException;

import org.eclipse.ease.jupyter.kernel.channels.ChannelOutputStream;
import org.eclipse.ease.jupyter.kernel.channels.ChannelPrintStream;
import org.eclipse.ease.jupyter.kernel.channels.HeartbeatChannel;
import org.eclipse.ease.jupyter.kernel.channels.IOPubChannel;
import org.eclipse.ease.jupyter.kernel.channels.ShellChannel;
import org.eclipse.ease.jupyter.kernel.channels.StdinChannel;
import org.eclipse.ease.service.EngineDescription;
import org.eclipse.ease.IReplEngine;
import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.Protocol;
import org.eclipse.ease.jupyter.kernel.Session;

/**
 * Jupyter kernel for EASE script engines.
 */
public class Kernel implements IEngineProvider {
	/**
	 * {@link EngineDescription} to dynamically create {@link IScriptEngine}.
	 */
	private final EngineDescription fEngineDescription;

	/**
	 * {@link IScriptEngine} to execute code on.
	 */
	protected IScriptEngine fEngine;

	/**
	 * Session used by kernel.
	 * 
	 * Stores information about running channels, used signature algorithm, ...
	 */
	protected final Session fSession;

	/**
	 * Heartbeat channel used by clients to check if kernel is still alive.
	 */
	protected final HeartbeatChannel fHeartBeat;

	/**
	 * Shell channel used for incoming requests.
	 */
	protected final ShellChannel fShell;

	/**
	 * Control channel used for incoming requests (same as {@link #fShell} but for
	 * requests with higher priority.
	 */
	protected final ShellChannel fControl;

	/**
	 * STDIN channel used to query clients for user input.
	 */
	protected final StdinChannel fStdin;

	/**
	 * IOPub channel used to distribute calculated data and messages to clients.
	 */
	protected final IOPubChannel fIoPub;

	/**
	 * Constructor parses config and creates members accordingly.
	 * 
	 * @param config
	 *            Config with information about ports to be used, signature
	 *            algorithm, ...
	 * @param engineDescription
	 *            {@link EngineDescription} to dynamically create
	 *            {@link IScriptEngine}.
	 */
	public Kernel(final Config config, EngineDescription engineDescription) {
		fEngineDescription = engineDescription;

		// Create session
		final Protocol protocol = new Protocol(config.getKey(), config.getSignatureScheme());
		fSession = new Session(protocol, 3000, 16);

		// Create channels
		fHeartBeat = new HeartbeatChannel(getChannelAddress(config.getHbPort(), config), fSession);
		fIoPub = new IOPubChannel(getChannelAddress(config.getIopubPort(), config), fSession);

		// Setup engine here once minimal setup is available
		setupEngine();

		// Create rest of the channels that rely on script engine
		fStdin = new StdinChannel(getChannelAddress(config.getStdinPort(), config), fSession);
		fShell = new ShellChannel(getChannelAddress(config.getShellPort(), config), fSession, this, fIoPub);
		fControl = new ShellChannel(getChannelAddress(config.getControlPort(), config), fSession, this, fIoPub);
	}

	/**
	 * Utility method parsing config for transport and IP and creating channel
	 * address based on given port.
	 * 
	 * @param channelPort
	 *            Port to be used by channel address.
	 * @param config
	 *            Config with information about transport and IP to be used.
	 * @return Channel address for given port.
	 */
	private static String getChannelAddress(final Integer channelPort, Config config) {
		return String.format("%s://%s:%d", config.getTransport(), config.getIp(), channelPort);
	}

	@Override
	public IScriptEngine getEngine() {
		return fEngine;
	}

	@Override
	public void resetEngine() {
		setupEngine();
	}

	/**
	 * Sets internal {@link IScriptEngine} based on given
	 * {@link #fEngineDescription}.
	 */
	private void setupEngine() {
		// Create and setup new script engine
		IScriptEngine engine = fEngineDescription.createEngine();
		if (engine instanceof IReplEngine) {
			((IReplEngine) engine).setTerminateOnIdle(false);
		}
		engine.setOutputStream(new ChannelPrintStream(new ChannelOutputStream("stdout", fIoPub)));
		engine.setErrorStream(new ChannelPrintStream(new ChannelOutputStream("stderr", fIoPub)));

		// Shut down old engine
		if (fEngine != null) {
			fEngine.getOutputStream().close();
			fEngine.getErrorStream().close();
			fEngine.terminate();
		}

		// Actually update engine
		fEngine = engine;
	}

	/**
	 * Starts the kernel by starting all channels and the script engine.
	 */
	public void start() {
		// Start script engine
		fEngine.schedule();

		// Start all sockets
		fHeartBeat.start();
		fControl.start();
		fShell.start();
		fIoPub.start();
	}

	/**
	 * Stops the kernel by stopping all channels and the script engine.
	 */
	public void stop() {
		// Stop all sockets
		fHeartBeat.stop();
		fControl.stop();
		fShell.stop();
		fIoPub.stop();

		// Shut down the session
		try {
			fSession.close();
		} catch (IOException e) {
			// ignore
		}

		// Stop the script engine
		fEngine.terminate();
	}
}
