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

package org.eclipse.ease.jupyter.ui;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ease.jupyter.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * {@link Queue} of started {@link Job} objects that need to be finished
	 * before Eclipse shuts down.
	 */
	private Queue<Job> fJobs = new LinkedBlockingQueue<Job>();

	/**
	 * Simple lock object as we cannot make #fJobs final.
	 */
	private final Object fJobsLock = new Object();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * Schedules a job that needs to be finished. Adds it to the internal list
	 * of jobs and actually schedules it.
	 * <p>
	 * Jobs will be canceled and joined during shutdown, so handle with care.
	 * <p>
	 * Jobs are not guaranteed to be run, if they are scheduled after
	 * {@link #stop(BundleContext)} was already called.
	 * 
	 * @param job
	 *            Job that needs to be scheduled and finished.
	 */
	public void schedule(Job job) {
		synchronized (this.fJobsLock) {
			if (this.fJobs != null) {
				fJobs.add(job);
				job.schedule();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		synchronized (this.fJobsLock) {
			Job job = null;
			while ((job = this.fJobs.poll()) != null) {
				job.cancel();
				try {
					job.join();
				} catch (InterruptedException e) {
					// Really not much more we can do
					e.printStackTrace();
				}
			}
			this.fJobs = null;
		}

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
