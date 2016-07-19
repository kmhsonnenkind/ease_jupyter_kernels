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
import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.Protocol;
import org.eclipse.ease.jupyter.kernel.Session;

/**
 * Jupyter kernel for EASE script engines.
 */
public class Kernel {
	/**
	 * {@link IScriptEngine} to execute code on.
	 */
	protected final IScriptEngine fEngine;

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
	 * Control channel used for incoming requests (same as {@link #fShell} but
	 * for requests with higher priority.
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
	 * @param engine
	 *            {@link IScriptEngine} to be used by kernel for execution.
	 */
	public Kernel(final Config config, IScriptEngine engine) {
		// Create copy of given script engine
		fEngine = engine.getDescription().createEngine();
		fEngine.setTerminateOnIdle(false);

		// Create session
		final Protocol protocol = new Protocol(config.getKey(), config.getSignatureScheme());
		fSession = new Session(protocol, 3000, 16);

		// Create channels
		fHeartBeat = new HeartbeatChannel(getChannelAddress(config.getHbPort(), config), fSession);
		fIoPub = new IOPubChannel(getChannelAddress(config.getIopubPort(), config), fSession);
		fStdin = new StdinChannel(getChannelAddress(config.getStdinPort(), config), fSession);
		fShell = new ShellChannel(getChannelAddress(config.getShellPort(), config), fSession, this, fIoPub, engine);
		fControl = new ShellChannel(getChannelAddress(config.getControlPort(), config), fSession, this, fIoPub, engine);

		// Patch streams for script engine
		engine.setOutputStream(new ChannelPrintStream(new ChannelOutputStream("stdout", fIoPub)));
		engine.setErrorStream(new ChannelPrintStream(new ChannelOutputStream("stderr", fIoPub)));
		// TODO: patch input stream
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
