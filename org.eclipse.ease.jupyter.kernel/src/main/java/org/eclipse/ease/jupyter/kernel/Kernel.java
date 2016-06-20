package org.eclipse.ease.jupyter.kernel;

import java.io.IOException;

import org.eclipse.ease.jupyter.kernel.channels.ControlChannel;
import org.eclipse.ease.jupyter.kernel.channels.HeartbeatChannel;
import org.eclipse.ease.jupyter.kernel.channels.IOPubChannel;
import org.eclipse.ease.jupyter.kernel.channels.ShellChannel;
import org.eclipse.ease.jupyter.kernel.channels.StdinChannel;
import org.eclipse.ease.jupyter.kernel.Protocol;
import org.eclipse.ease.jupyter.kernel.Session;

/**
 * Jupyter kernel for EASE script engines.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class Kernel {
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
	 * Control channel used for incoming requests.
	 */
	protected final ControlChannel fControl;

	/**
	 * Shell channel same as {@link #fControl} but for requests with higher
	 * priority.
	 */
	protected final ShellChannel fShell;

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
	 */
	public Kernel(final Config config) {
		// Create session
		final Protocol protocol = new Protocol(config.getKey(),
				config.getSignatureScheme());
		fSession = new Session(protocol, 3000, 16);

		// Create channels
		fHeartBeat = new HeartbeatChannel(getChannelAddress(config.getHbPort(),
				config), fSession);
		fIoPub = new IOPubChannel(getChannelAddress(config.getIopubPort(),
				config), fSession);
		fStdin = new StdinChannel(getChannelAddress(config.getStdinPort(),
				config), fSession);
		fControl = new ControlChannel(getChannelAddress(
				config.getControlPort(), config), fSession, this, fIoPub,
				fStdin);
		fShell = new ShellChannel(getChannelAddress(config.getShellPort(),
				config), fSession, this, fIoPub, fStdin);

	}

	/**
	 * Overload of {@link #Kernel(Config)} parsing giving parameters to
	 * {@link Config} object.
	 * 
	 * @see #Kernel(Config)
	 * @see Config
	 */
	public Kernel(String userName, String ip, int stdinPort, int controlPort,
			int shellPort, int hbPort, int ioPubPort, String transport,
			String signatureScheme, String key) {
		this(new Config().withControlPort(controlPort).withHbPort(hbPort)
				.withIopubPort(ioPubPort).withIp(ip).withKey(key)
				.withShellPort(shellPort).withSignatureScheme(signatureScheme)
				.withStdinPort(stdinPort).withTransport(transport));
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
	private static String getChannelAddress(final Integer channelPort,
			Config config) {
		return String.format("%s://%s:%d", config.getTransport(),
				config.getIp(), channelPort);
	}

	/**
	 * Starts the kernel (by starting all channels).
	 */
	public void start() {
		// Start all sockets
		fHeartBeat.start();
		fControl.start();
		fShell.start();
		fIoPub.start();
	}

	/**
	 * Stops the kernel (By stopping all channels).
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
	}
}
