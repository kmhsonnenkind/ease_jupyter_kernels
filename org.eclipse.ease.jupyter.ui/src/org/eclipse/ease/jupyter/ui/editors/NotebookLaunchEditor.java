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

package org.eclipse.ease.jupyter.ui.editors;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.Dispatcher;
import org.eclipse.ease.jupyter.ui.Activator;
import org.eclipse.ease.service.EngineDescription;
import org.eclipse.ease.service.IScriptService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom editor for Jupyter notebook files.
 * <p>
 * Used to display EASE ipynb files and handling all the necessary work for
 * starting a new Jupyter notebook instance with the selected file.
 *
 */
public class NotebookLaunchEditor extends EditorPart {
	/**
	 * {@link File} object for notebook in use.
	 */
	private IFile fNotebookFile;
	/**
	 * {@link Process} running the actual Jupyter notebook server.
	 */
	private Process fJupyterProcess;

	/**
	 * Port the Jupyter notebook process is running on.
	 */
	private int fJupyterPort = -1;

	/**
	 * {@link Dispatcher} taking requests from Jupyter notebook server.
	 */
	private Dispatcher fDispatcher;

	/**
	 * Port {@link #fDispatcher} is listening on.
	 */
	private int fDispatcherPort = -1;

	/**
	 * {@link Thread} running {@link #fDispatcher}.
	 */
	private Thread fDispatcherThread;

	/**
	 * Temporary directory containing EASE kernel to be used for the notebook.
	 */
	private File fDispatcherDir;

	/**
	 * Number of retries for deleting temporary directory.
	 * <p>
	 * Jupyter subprocesses take too long to shut down so we need to retry.
	 */
	private static final int DELETION_RETRIES = 5;

	/**
	 * Milliseconds to sleep between retrying deltion of temporary directories.
	 * <p>
	 * Jupyter subprocesses take too long to shut down so we need to retry.
	 */
	private static final int DELETION_SLEEP = 500;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);

		// Check that initiated from file
		if (!(input instanceof IFileEditorInput)) {
			// Should not occur
			throw new PartInitException("Can only launch Jupyter notebooks from files.");
		}
		// Get notebook file in more suitable format
		fNotebookFile = ((IFileEditorInput) input).getFile();
		if (!fNotebookFile.exists()) {
			throw new PartInitException(new Status(Status.ERROR, Activator.PLUGIN_ID,
					"File '" + fNotebookFile.getLocation().toString() + "' does not exist."));
		}

		// Get information about script engine to be used
		EngineDescription engineDescription = parseFileForEngine(fNotebookFile);

		if (engineDescription == null) {
			// Just launch and hope for standard Jupyter file
			try {
				fJupyterProcess = startJupyterProcess();
			} catch (IOException e) {
				throw new PartInitException(
						new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not start Jupyter process.", e));
			}
		} else {
			// Otherwise start up custom kernel

			// Create dispatcher directory from skeleton
			try {
				initKernelDir(engineDescription);
			} catch (IOException | URISyntaxException e) {
				throw new PartInitException(new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Could not create temporary Jupyter kernel directory for EASE kernel.", e));
			}

			// Launch Jupyter process
			try {
				fJupyterProcess = startJupyterProcess();
			} catch (IOException e) {
				throw new PartInitException(
						new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not start Jupyter process.", e));
			}

			// Create dispatcher for receiving connections to kernel
			startDispatcherThread(engineDescription);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// Create FillLayout to have all space available to browser
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);

		// Actually create browser
		Browser browser = new Browser(parent, SWT.None);

		// FIXME: Jupyter takes too long to start
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// ignore
		}

		// Open Jupyter URL
		browser.setUrl(getJupyterUrl());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		Job cleanupJob = new Job("Shutting down Jupyter...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// Shut down process
				if (fJupyterProcess != null && fJupyterProcess.isAlive()) {
					fJupyterProcess.destroy();
					try {
						fJupyterProcess.waitFor();
					} catch (InterruptedException e) {
						// Hope that process shut down
					}
				}

				// Close the dispatcher
				if (fDispatcher != null) {
					fDispatcher.stop();
					if (fDispatcherThread != null) {
						try {
							fDispatcherThread.join();
						} catch (InterruptedException e) {
							// ignore
						}
					}
				}

				// Delete temporary files
				if (fDispatcherDir != null && fDispatcherDir.exists()) {

					// Try a couple of time because process might still lock
					// directory
					for (int i = 0; i < DELETION_RETRIES; i++) {
						try {
							FileUtils.deleteDirectory(fDispatcherDir);
							break;
						} catch (IOException e) {
							// Sleep a bit and then retry
							try {
								Thread.sleep(DELETION_SLEEP);
							} catch (InterruptedException e2) {
								// Ignore
							}
						}
					}
				}

				// Refresh file just to be sure
				if (fNotebookFile != null && fNotebookFile.exists()) {
					try {
						fNotebookFile.refreshLocal(0, null);
					} catch (CoreException e) {
						// Ignore and ask user to refresh later
					}
				}
				return Status.OK_STATUS;
			}

		};

		// Schedule Job using plugin to assure it completes
		Activator.getDefault().schedule(cleanupJob);
	}

	/**
	 * Parses the given ipynb JSON file for the EASE {@link IScriptEngine} to be
	 * used.
	 * 
	 * @param file
	 *            *.ipynb JSON file to be parsed.
	 * @return {@link EngineDescription} for {@link IScriptEngine} from file or
	 *         <code>null</code> if error occurred.
	 */
	private static EngineDescription parseFileForEngine(IFile file) {
		try {
			// Parse JSON file to more usable format
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(file.getContents(), JsonNode.class);

			// XPath like query
			JsonNode engineNode = rootNode.at("/metadata/ease_info/scriptengine");
			if (engineNode != null) {
				// Perform script engine lookup by ID from JSON file
				final String engineID = engineNode.asText();
				IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench()
						.getService(IScriptService.class);
				if (scriptService != null) {
					return scriptService.getEngineByID(engineID);
				}
			}
		} catch (IOException | CoreException e) {
			// Swallow exception and have errorhandling outside
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Initializes a new EASE kernel directory for the given
	 * {@link EngineDescription}.
	 * 
	 * @param engineDescription
	 *            Description of {@link IScriptEngine} to be used by EASE
	 *            Jupyter kernel.
	 * @throws IOException
	 *             If EASE kernel could not be created.
	 * @throws URISyntaxException
	 *             Should not occur.
	 */
	private void initKernelDir(final EngineDescription engineDescription) throws IOException, URISyntaxException {
		// Create temporary directory
		fDispatcherDir = Files.createTempDirectory("ease_jupyter_kernel").toFile();

		// Get directory for kernel to be used
		File kernelDir = new File(new File(fDispatcherDir, "kernels"), engineDescription.getID());

		// FIXME: Necessary during development
		URL kernelSkeletonUrl = Activator.getDefault().getBundle().getEntry("/resources/kernel_skeleton");
		if (kernelSkeletonUrl == null) {
			// Load from jar rather than filesystem
			kernelSkeletonUrl = Activator.getDefault().getBundle().getEntry("/kernel_skeleton");
		}

		// Check if resource could be found
		if (kernelSkeletonUrl == null) {
			throw new IOException("Could not find Jupyter kernel skeleton in plugin.");
		}

		// Escape OS specific characters (like ':' for Windows drives)
		// See:
		// http://stackoverflow.com/questions/10144210/java-jar-file-use-resource-errors-uri-is-not-hierarchical
		kernelSkeletonUrl = FileLocator.toFileURL(kernelSkeletonUrl);
		URI kernelSkeletonUri = new URI(kernelSkeletonUrl.getProtocol(), kernelSkeletonUrl.getPath(), null);
		File kernelSkeletonDir = new File(kernelSkeletonUri);

		// Copy directory skeleton
		FileUtils.copyDirectory(kernelSkeletonDir, kernelDir);

		// Fill kernel.json template values
		fDispatcherPort = getFreePort(50000);
		File kernelFile = new File(kernelDir, "kernel.json");
		File launcherFile = new File(kernelDir, "org.eclipse.ease.jupyter.kernel.launcher.jar");
		populateKernelTempatefile(kernelFile, launcherFile, fDispatcherPort, engineDescription.getName());
	}

	/**
	 * Tries to find a new free port close to the given start port.
	 * <p>
	 * Not that there is a (unlikely but) possible race condition if the
	 * returned port is being used between calling this and actually starting a
	 * server. Handle blocked servers appropriately.
	 * 
	 * @param startPort
	 *            Lowest desired port number.
	 * @return Free port (hopefully) close to startPort.
	 */
	private static int getFreePort(int startPort) {
		ServerSocket socket = null;
		while (true) {
			try {
				// Try to create server for port number
				socket = new ServerSocket(startPort);

				// Make reusable to timeout state problems
				socket.setReuseAddress(true);

				// No exception -> port is free
				return startPort;
			} catch (IOException ignore) {
				// IOException most (!) likely because of used port
				startPort++;
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException ignore) {
						// Should not occur
					}
				}
			}
		}
	}

	/**
	 * Populates the given kernel.json template file with the given values.
	 * 
	 * @param templateFile
	 *            the "kernel.json" template file to be patched.
	 * @param launcherFile
	 *            "org.eclipse.ease.jupyter.kernel.launcher.jar" file to be
	 *            used. Must be accessible by forked processes.
	 * 
	 * @param dispatcherPort
	 *            Port the {@link Dispatcher} is listening on.
	 * @param engineName
	 *            User-friendly name of the {@link IScriptEngine} used.
	 * @throws IOException
	 */
	private static void populateKernelTempatefile(final File templateFile, final File launcherFile, int dispatcherPort,
			final String engineName) throws IOException {
		// Get template file contents
		String skeletonContent = FileUtils.readFileToString(templateFile);

		// Patch contents
		// FIXME: problem when kernel.json file contains single backslashes
		// under windows
		String patched = skeletonContent.replace("@@launcher@@", launcherFile.getCanonicalPath().replace('\\', '/'))
				.replaceAll("@@dispatcherport@@", Integer.toString(dispatcherPort))
				.replace("@@enginename@@", engineName);

		// Write back patched value
		FileUtils.writeStringToFile(templateFile, patched);
	}

	/**
	 * Starts the Jupyter notebook process matching the setup based on
	 * {@link #initKernelDir(EngineDescription)}.
	 * 
	 * @throws IOException
	 *             If Jupyter notebook process could not be created.
	 */
	private Process startJupyterProcess() throws IOException {
		// Get free port for Jupyter notebook
		fJupyterPort = getFreePort(8888);

		// Create process
		String workspaceDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		ProcessBuilder processBuilder = new ProcessBuilder("jupyter-notebook", "--no-browser", "--port=" + fJupyterPort,
				"--notebook-dir=" + workspaceDir);
		processBuilder.directory(new File(workspaceDir));
		processBuilder.inheritIO();

		// Patch environment variables for Jupyter to find kernels
		if (fDispatcherDir != null) {
			processBuilder.environment().put("JUPYTER_PATH", fDispatcherDir.getCanonicalPath());
		}

		// Actually create process
		return processBuilder.start();

	}

	/**
	 * Starts a new {@link Dispatcher} with a new {@link IScriptEngine} based on
	 * the given {@link EngineDescription}.
	 * <p>
	 * Port for dispatcher is based on value of {@link #fDispatcherPort} and
	 * host always defaults to "localhost".
	 * <p>
	 * {@link Dispatcher} is being run in on {@link Thread} (stored in
	 * {@link #fDispatcherThread}.
	 * 
	 * @param engineDescription
	 *            {@link EngineDescription} on which to base the
	 *            {@link IScriptEngine} for the {@link Dispatcher}.
	 */
	private void startDispatcherThread(EngineDescription engineDescription) {
		// Create script engine for dispatcher
		IScriptEngine engine = engineDescription.createEngine();

		// Create actual dispatcher object
		fDispatcher = new Dispatcher(engine, "localhost", fDispatcherPort);

		// Start thread for dispatcher
		fDispatcherThread = new Thread(fDispatcher);
		fDispatcherThread.start();
	}

	/**
	 * Returns URL the Jupyter notebook is running on.
	 * 
	 * @return URL for Jupyter notebook.
	 */
	private String getJupyterUrl() {
		// Create relative path for file
		URI workspaceUri = ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
		URI relative = workspaceUri.relativize(fNotebookFile.getLocationURI());

		// Return formatted URL string
		return String.format("http://localhost:%d/notebooks/%s", fJupyterPort, relative.toString());
	}

}