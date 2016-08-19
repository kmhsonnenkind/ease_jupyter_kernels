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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.service.EngineDescription;
import org.eclipse.ease.service.IScriptService;
import org.eclipse.ease.service.ScriptService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

/**
 * Custom wizard page for selecting {@link IScriptEngine} to be used by Jupyter
 * notebook file.
 */
public class IpynbWizardEnginePage extends WizardPage {
	/**
	 * {@link Combo} to select {@link IScriptEngine}.
	 */
	private Combo fEngineSelect;

	/**
	 * Checkbox to create "plain" Jupyter notebook file.
	 */
	private Button fStandardNotebookButton;

	/**
	 * Constructor sets title, description and state.
	 */
	public IpynbWizardEnginePage() {
		super("Jupyter Notebook");
		setTitle("Jupyter Notebook");
		setDescription("Information about Jupyter Notebook");

		// User must select engine at least once
		setPageComplete(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		// EASE script engine select
		Label engineSelectLabel = new Label(comp, SWT.NONE);
		engineSelectLabel.setText("Select Script Engine for Launch: ");
		GridDataFactory.swtDefaults().applyTo(engineSelectLabel);

		List<String> engines = new ArrayList<String>();
		for (EngineDescription engine : ScriptService.getService().getEngines()) {
			engines.add(engine.getName());

		}
		fEngineSelect = new Combo(comp, SWT.DROP_DOWN);
		fEngineSelect.setItems(engines.toArray(new String[engines.size()]));
		fEngineSelect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Set completed
				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		GridDataFactory.swtDefaults().applyTo(fEngineSelect);

		// Checkbox to see if normal Jupyter file
		fStandardNotebookButton = new Button(comp, SWT.CHECK);
		fStandardNotebookButton.setText("Create Jupyter notebook file without EASE kernel");
		fStandardNotebookButton.setSelection(false);
		fStandardNotebookButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fStandardNotebookButton.getSelection()) {
					setPageComplete(true);
				} else {
					if (fEngineSelect.getSelectionIndex() == -1) {
						setPageComplete(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		GridDataFactory.swtDefaults().span(2, 1).applyTo(fStandardNotebookButton);

	}

	/**
	 * Checks if the user wants to create a standard Jupyter notebook file
	 * without an EASE kernel.
	 * 
	 * @return <code>true</code> if standard notebook file should be created.
	 */
	public boolean createStandardNotebook() {
		return fStandardNotebookButton.getSelection();
	}

	/**
	 * Returns the name of the currently selected {@link IScriptEngine}.
	 * 
	 * @return name of the currently selected {@link IScriptEngine}.
	 */
	public String getEngineName() {
		return fEngineSelect.getText();
	}

	/**
	 * Returns the ID of the currently selected {@link IScriptEngine}.
	 * 
	 * @return name of the currently selected {@link IScriptEngine}.
	 */
	public String getEngineId() {
		final IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench()
				.getService(IScriptService.class);
		for (EngineDescription engine : scriptService.getEngines()) {
			if (engine.getName().equals(fEngineSelect.getText())) {
				return engine.getID();
			}
		}

		return fEngineSelect.getText();
	}
}
