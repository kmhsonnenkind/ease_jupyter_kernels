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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.tools.ResourceTools;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Custom wizard for creating new EASE Jupyter notebooks.
 */
public class IpynbWizard extends Wizard implements INewWizard {
	/**
	 * {@link IStructuredSelection} used by {@link #fNewFilePage} for root
	 * directory.
	 */
	private IStructuredSelection selection = null;

	/**
	 * New file creation wizard page used for actually creating new file.
	 */
	private WizardNewFileCreationPage fNewFilePage = null;

	/**
	 * {@link IWizardPage} used for querying for {@link IScriptEngine}.
	 */
	private IpynbWizardEnginePage fEnginePage = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		// Add file creation page for *.ipynb file
		fNewFilePage = new IpynbWizardNewFileCreationPage(this.selection);
		addPage(fNewFilePage);

		// Add wizard selection page
		fEnginePage = new IpynbWizardEnginePage();
		addPage(fEnginePage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// Get file to create
		IFile file = fNewFilePage.createNewFile();
		if (file != null) {
			// Read skeleton content
			String skeleton = ResourceTools.resourceToString(file);
			if (skeleton == null) {
				return false;
			}

			// Set actual values in template
			// TODO: use template engine
			String patched = skeleton.replace("@@scriptengine@@", fEnginePage.getEngineId());
			patched = patched.replace("@@displayname@@", fEnginePage.getEngineName());
			patched = patched.replace("@@name@@", fEnginePage.getEngineId());

			try {

				file.setContents(new ByteArrayInputStream(patched.getBytes(StandardCharsets.UTF_8)), false, false,
						null);
				return true;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
