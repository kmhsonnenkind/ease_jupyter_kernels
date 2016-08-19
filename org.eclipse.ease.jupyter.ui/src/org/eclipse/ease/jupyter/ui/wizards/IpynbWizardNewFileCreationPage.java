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

package org.eclipse.ease.jupyter.ui.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.ease.jupyter.ui.Activator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Custom {@link WizardNewFileCreationPage} for creating *.ipynb files.
 */
public class IpynbWizardNewFileCreationPage extends WizardNewFileCreationPage {
	private boolean fCreateStandardNotebook = false;

	/**
	 * Constructor sets up description and file extension.
	 * 
	 * @see WizardNewFileCreationPage#WizardNewFileCreationPage(String,
	 *      IStructuredSelection)
	 */
	public IpynbWizardNewFileCreationPage(IStructuredSelection selection) {
		super("IpynbWizardNewFileCreationPage", selection);
		setTitle("Jupyter Notebook file");
		setDescription("Creates new Jupyter notebook file for use with EASE script engine");
		setFileExtension("ipynb");
	}

	/**
	 * Simple setter to see if standard Jupyter notebook file without EASE
	 * kernel should be created.
	 * 
	 * @param standard
	 *            Flag to signalize if standard notebook file should be created.
	 *            <code>true</code> means create standard file.
	 */
	public void createStandardNotebook(boolean standard) {
		fCreateStandardNotebook = standard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	@Override
	protected InputStream getInitialContents() {
		try {
			URL skeletonUrl;

			// Standard Jupyter notebook without EASE kernel
			if (fCreateStandardNotebook) {
				// FIXME: Necessary during devlopment
				skeletonUrl = Activator.getDefault().getBundle().getEntry("/resources/standard.ipynb");
				if (skeletonUrl == null) {
					// Load from jar rather than filesystem
					skeletonUrl = Activator.getDefault().getBundle().getEntry("/standard.ipynb");
				}
			} else {
				// Custom EASE kernel
				// FIXME: Necessary during devlopment
				skeletonUrl = Activator.getDefault().getBundle().getEntry("/resources/skeleton.ipynb");
				if (skeletonUrl == null) {
					// Load from jar rather than filesystem
					skeletonUrl = Activator.getDefault().getBundle().getEntry("/skeleton.ipynb");

				}
			}
			if (skeletonUrl != null) {
				return skeletonUrl.openStream();
			}
		} catch (IOException e) {
			// let exception fall through
		}
		return null;
	}

}
