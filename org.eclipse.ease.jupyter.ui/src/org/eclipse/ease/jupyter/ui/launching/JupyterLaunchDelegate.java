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

package org.eclipse.ease.jupyter.ui.launching;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.jupyter.kernel.Dispatcher;
import org.eclipse.ease.jupyter.ui.Activator;
import org.eclipse.ease.service.EngineDescription;
import org.eclipse.ease.service.IScriptService;
import org.eclipse.ease.tools.ResourceTools;
import org.eclipse.ease.ui.tools.AbstractLaunchDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extension of {@link AbstractLaunchDelegate} for launching Jupyter notebooks.
 */
public class JupyterLaunchDelegate extends AbstractLaunchDelegate {
	/**
	 * Type of launch configuration this delegate is used for.
	 */
	private static final String LAUNCH_CONFIGURATION_ID = "org.eclipse.ease.jupyter.launchConfigurationType";

	/**
	 * Timeout in milliseconds the Jupyter job should sleep until it checks
	 * again if the user has cancelled the job.
	 */
	private static final int PROCESS_CHECK_TIMEOUT = 500;

	/**
	 * Delay between starting jupyter server and opening browser in
	 * milliseconds.
	 */
	private static final int BROWSER_INITIAL_DELAY = 1000;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		// Get engine description and filename from configuration
		EngineDescription engineDescription = engineLookup(
				configuration.getAttribute(LaunchConstants.SCRIPT_ENGINE, "Not found"));
		String filename = configuration.getAttribute(LaunchConstants.FILE_LOCATION, (String) null);

		// Check if required parameters found
		if (filename == null) {
			// Should not occur as we currently only use launch shortcuts
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Missing filename in run configuration.", null));
		}

		// Check if script engine given
		if (engineDescription == null) {
			// TODO: Check if it makes sense to ask the user for the script
			// engine to use. For now only show error message
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Launch error",
							"Jupyter ipynb file was not created using eclipse and is therefore missing required launch information.");
				}
			});
			return;
		}

		// Asynchronously run Jupyter using Job
		Job job = new Job("Jupyter Notebook " + filename) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				SubMonitor subMonitor = SubMonitor.convert(monitor, 5);
				IStatus status = Status.OK_STATUS;

				// Objects that need clean-up
				Process process = null;
				Dispatcher dispatcher = null;
				Thread dispatcherThread = null;
				File dispatcherDir = null;

				try {
					// Get workspace as root of all operations
					String workspaceString = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

					// Create directory for dispatcher
					subMonitor.setTaskName("Copying kernel to temporary directory.");
					dispatcherDir = Files.createTempDirectory("ease_jupyter_kernels").toFile();
					File kernelDir = new File(new File(dispatcherDir, "kernels"), engineDescription.getID());
					URL kernelSkeletonUrl = Activator.getDefault().getBundle().getEntry("/resources/kernel_skeleton");
					File kernelSkeletonDir;
					try {
						kernelSkeletonDir = new File(FileLocator.resolve(kernelSkeletonUrl).toURI());
					} catch (URISyntaxException e) {
						return new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not create Jupyter kernel.", e);
					}
					FileUtils.copyDirectory(kernelSkeletonDir, kernelDir);
					int dispatcherPort = getFreePort(54321);
					File kernelFile = new File(kernelDir, "kernel.json");
					String skeletonContent = FileUtils.readFileToString(kernelFile);
					if (skeletonContent == null) {
						return new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not create Jupyter kernel");
					}
					// TODO: Use template engine
					File launcherFile = new File(kernelDir, "org.eclipse.ease.jupyter.kernel.launcher.jar");
					String patchedContent = skeletonContent.replace("@@launcher@@", launcherFile.getAbsolutePath())
							.replace("@@dispatcherport@@", Integer.toString(dispatcherPort))
							.replace("@@enginename@@", engineDescription.getName());
					FileUtils.writeStringToFile(kernelFile, patchedContent, false);

					// Create process for Jupyter notebook server
					int jupyterPort = getFreePort(8888);
					subMonitor.setTaskName("Starting the Jupyter notebook server.");
					ProcessBuilder builder = new ProcessBuilder("jupyter-notebook", "--no-browser",
							"--port=" + jupyterPort, "--notebook-dir=" + workspaceString);
					builder.directory(new File(workspaceString));
					builder.environment().put("JUPYTER_PATH", dispatcherDir.getAbsolutePath());
					process = builder.start();

					// Create dispatcher for the actual connection
					subMonitor.setTaskName("Creating dispatcher for receiving connections to kernel.");
					IScriptEngine engine = engineDescription.createEngine();
					dispatcher = new Dispatcher(engine, "localhost", dispatcherPort);
					dispatcherThread = new Thread(dispatcher);
					dispatcherThread.start();

					// Create URL for notebook location
					URI workspaceUri = new File(workspaceString).toURI();
					URI fileUri = new File(filename).toURI();
					URI relativeUri = workspaceUri.relativize(fileUri);
					String browserString = String.format("http://localhost:%d/notebooks/%s", jupyterPort,
							relativeUri.toString());
					URL browserURL = new URL(browserString);

					// Open browser widget showing notebook
					subMonitor.setTaskName("Running the Jupyter notebook.");
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								IWebBrowser browser = Activator.getDefault().getWorkbench().getBrowserSupport()
										.createBrowser("Jupyter");

								// FIXME: browser comes up too fast
								try {
									Thread.sleep(BROWSER_INITIAL_DELAY);
								} catch (InterruptedException e) {
									// Ignore and hope for the best
								}
								browser.openURL(browserURL);

							} catch (PartInitException e) {
								e.printStackTrace();
							}
						}
					});

					// Wait for either the process to end or cancellation
					while (process.isAlive()) {

						// Check if user cancelled
						if (monitor.isCanceled()) {
							status = Status.CANCEL_STATUS;
							break;
						}

						// Wait a bit and check again
						try {
							Thread.sleep(PROCESS_CHECK_TIMEOUT);
						} catch (InterruptedException e) {
							status = Status.CANCEL_STATUS;
							break;
						}
					}

				} catch (IOException e) {
					return new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not start Jupyter notebook.", e);
				} finally {
					// TODO: Think of way to close browser

					// If user aborted shut down process
					subMonitor.setTaskName("Shutting down Jupyter notebook server.");
					if (process != null && process.isAlive()) {
						process.destroy();
					}

					// Close the dispatcher
					subMonitor.setTaskName("Shutting down dispatcher.");
					if (dispatcher != null) {
						dispatcher.stop();
						if (dispatcherThread != null) {
							try {
								dispatcherThread.join();
							} catch (InterruptedException e) {
								// ignore
							}
						}
					}

					// Delete temporary files
					if (dispatcherDir != null && dispatcherDir.exists()) {
						try {
							FileUtils.deleteDirectory(dispatcherDir);
						} catch (IOException e) {
							return new Status(Status.ERROR, Activator.PLUGIN_ID,
									"Could not delete temporary directory " + dispatcherDir.getAbsolutePath(), e);
						}
					}
				}
				subMonitor.done();
				return status;
			}
		};
		job.schedule();

	}

	@Override
	protected ILaunchConfiguration createLaunchConfiguration(IResource file, String mode) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(LAUNCH_CONFIGURATION_ID);

		// Set file location for launch
		ILaunchConfigurationWorkingCopy configuration = type.newInstance(null, file.getName());
		configuration.setAttribute(LaunchConstants.FILE_LOCATION, file.getLocation().toString());

		// Try to parse file for IScriptEngine
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(ResourceTools.getInputStream(file), JsonNode.class);
			JsonNode engineNode = rootNode.at("/metadata/ease_info/scriptengine");
			if (engineNode != null) {
				configuration.setAttribute(LaunchConstants.SCRIPT_ENGINE, engineNode.asText());
			}
		} catch (JsonParseException | JsonMappingException e) {
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not parse Jupyter notebook file.", e));
		} catch (IOException e) {
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not read Jupyter notebook file.", e));
		}

		// TODO: think about if it makes sense to store this information
		// configuration.doSave();
		return configuration;
	}

	@Override
	protected String getFileLocation(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(LaunchConstants.FILE_LOCATION, "");
	}

	@Override
	protected String getLaunchConfigurationId() {
		return LAUNCH_CONFIGURATION_ID;
	}

	/**
	 * Utility method performing lookup for {@link EngineDescription} based on
	 * engine's name.
	 * <p>
	 * As a fallback if not engine found, the name will be assumed as the engine
	 * ID.
	 * 
	 * @param engineName
	 *            Name of the engine to be searched.
	 * @return {@link EngineDescription} matching the given input or
	 *         <code>null</code> if no engine found.
	 */
	private EngineDescription engineLookup(String engineName) {
		final IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench()
				.getService(IScriptService.class);

		// Try lookup by id
		EngineDescription description = scriptService.getEngineByID(engineName);
		if (description != null) {
			return description;
		}

		// Fallback to lookup by name
		for (EngineDescription desc : scriptService.getEngines()) {
			// Lookup by name
			if (desc.getName().equals(engineName)) {
				return scriptService.getEngineByID(desc.getID());
			}
		}

		return null;
	}

	private int getFreePort(int port) {
		ServerSocket socket = null;
		while (true) {
			try {
				socket = new ServerSocket(port);
				return port;
			} catch (IOException ignore) {
				port++;
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException ignore2) {
						// ignore
					}
				}
			}
		}
	}
}