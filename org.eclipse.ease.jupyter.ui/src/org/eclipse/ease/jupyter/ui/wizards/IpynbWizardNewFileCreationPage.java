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

import org.eclipse.ease.jupyter.ui.Activator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Custom {@link WizardNewFileCreationPage} for creating *.ipynb files.
 */
public class IpynbWizardNewFileCreationPage extends WizardNewFileCreationPage {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	@Override
	protected InputStream getInitialContents() {
		try {
			return Activator.getDefault().getBundle().getEntry("/resources/skeleton.ipynb").openStream();
		} catch (IOException e) {
			return null;
		}
	}

}
