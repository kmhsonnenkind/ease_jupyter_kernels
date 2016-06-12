package org.eclipse.ease.jupyter.kernel.launcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple command line tool reading a Jupyter connection file, connecting to a
 * running Eclipse instance and passing the content of the file on to Eclipse.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class Launcher {
	/**
	 * Main function for kernel launcher.
	 * 
	 * args parameter must consist of three values:
	 * <ul>
	 * 	<li>Launch configuration file (created by Jupyter).</li>
	 *  <li>Host Eclipse is listening on.</li>
	 *  <li>Port Eclipse is listening on.</li>
	 * </ul>
	 * 
	 * @param args	Command line parameters.
	 * @returns	0 if successfull, other other values mean error.
	 */
	public static void main(String[] args) {
		// Check command line arguments
		if (args.length != 3) {
			System.err
					.println("Invalid number of command line arguments received.");
			System.exit(-1);
		}

		// Parse command line arguments
		String connectionFile = args[0];
		String host = args[1];
		int port;
		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.err
					.println("Invalid port received, cannot be cast to integer.");
			System.exit(-2);
			// FIXME: Bug in Java compiler not detecting System.exit()
			throw new RuntimeException("Dead code.");
		}

		// Read connection file
		Path connectionPath = Paths.get(connectionFile);
		byte[] connectionBytes;
		try {
			connectionBytes = Files.readAllBytes(connectionPath);
		} catch (IOException e) {
			System.err.println("Could not read connection file.");
			System.exit(-3);
			// FIXME: Bug in Java compiler not detecting System.exit()
			throw new RuntimeException("Dead code.");
		}

		// Connect to socket
		Socket socket = null;
		try {
			try {
				socket = new Socket(host, port);
				socket.setSoTimeout(10000);
			} catch (IOException e) {
				System.err.println("Could not connect to Eclipse socket.");
				System.exit(-2);
				// FIXME: Bug in Java compiler not detecting System.exit()
				throw new RuntimeException("Dead code.");
			}

			// Send data
			try {
				DataOutputStream outputStream = new DataOutputStream(
						socket.getOutputStream());
				outputStream.write(connectionBytes);
			} catch (IOException e) {
				System.err.println("Could not send data to Eclipse.");
				System.exit(-4);
			}

			try {
				// Read back exit code for launcher
				DataInputStream inputStream = new DataInputStream(
						socket.getInputStream());
				System.exit(inputStream.readInt());
			} catch (IOException e) {
				System.err.println("Eclipse took too long to respond.");
				System.exit(-4);
			}
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}
}
