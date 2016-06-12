package org.eclipse.ease.jupyter.kernel;

/**
 * Jupyter kernel for EASE script engines.
 * 
 * @author Martin Kloesch (martin.kloesch@gmail.com)
 *
 */
public class Kernel {
	
	public Kernel(String ip, int stdinPort, int controlPort, int shellPort,
			int hbPort, int ioPubPort, String transport,
			String signatureScheme, String key) {
	}

	public void start() {
		System.out.println("Starting Jupyter kernel.");
	}
	
	public void stop() {
		System.out.println("Stopping Jupyter kernel.");
	}
}
