/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglXmlSerializer;

/**
 * Wizard to create a new LVGL UI diagram file.
 */
public class NewLvglDiagramWizard extends Wizard implements INewWizard {

	public static final String FILE_EXTENSION = "graphxml";

	private NewLvglDiagramWizardPage mainPage;
	private IStructuredSelection selection;

	public NewLvglDiagramWizard() {
		setWindowTitle("New LVGL UI Diagram");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public void addPages() {
		mainPage = new NewLvglDiagramWizardPage(selection);
		addPage(mainPage);
	}

	@Override
	public boolean performFinish() {
		IFile diagramFile = mainPage.createNewFile();

		if (diagramFile == null) {
			return false;
		}

		// Create initial content - only create the .graphxml file
		try {
			LvglScreen screen = LvglXmlSerializer.createDefaultScreen();
			screen.setName(getScreenName(diagramFile.getName()));

			LvglXmlSerializer serializer = new LvglXmlSerializer();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			serializer.save(screen, outputStream);

			InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			diagramFile.setContents(inputStream, true, true, new NullProgressMonitor());

			// Open the editor - diagram model is created in-memory by the editor
			openEditor(diagramFile);

			return true;
		} catch (IOException | CoreException e) {
			MessageDialog.openError(getShell(), "Error", "Failed to create diagram: " + e.getMessage());
			return false;
		}
	}

	private String getScreenName(String fileName) {
		if (fileName.endsWith("." + FILE_EXTENSION)) {
			return fileName.substring(0, fileName.length() - FILE_EXTENSION.length() - 1);
		}
		return fileName;
	}

	private void openEditor(IFile file) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, file);
		} catch (PartInitException e) {
			MessageDialog.openError(getShell(), "Error", "Failed to open editor: " + e.getMessage());
		}
	}

	/**
	 * Gets the project from the selection.
	 *
	 * @return the project, or null
	 */
	public IProject getProject() {
		if (selection != null && !selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element instanceof IResource) {
				return ((IResource) element).getProject();
			}
		}
		return null;
	}

	/**
	 * Gets the folder from the selection.
	 *
	 * @return the folder, or null
	 */
	public IFolder getFolder() {
		if (selection != null && !selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element instanceof IFolder) {
				return (IFolder) element;
			}
		}
		return null;
	}
}
