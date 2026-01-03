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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.tlcsdm.eclipse.graphiti.demo.diagram.LvglDiagramTypeProvider;
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

		// Create initial content
		try {
			LvglScreen screen = LvglXmlSerializer.createDefaultScreen();
			screen.setName(getScreenName(diagramFile.getName()));

			LvglXmlSerializer serializer = new LvglXmlSerializer();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			serializer.save(screen, outputStream);

			InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			diagramFile.setContents(inputStream, true, true, new NullProgressMonitor());
			
			// Create the accompanying .diagram file for Graphiti
			createDiagramFile(diagramFile);

			// Open the editor
			openEditor(diagramFile);

			return true;
		} catch (IOException | CoreException e) {
			MessageDialog.openError(getShell(), "Error", "Failed to create diagram: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Creates a .diagram file alongside the .graphxml file.
	 * This file stores the Graphiti diagram model.
	 */
	private void createDiagramFile(IFile graphxmlFile) throws CoreException {
		try {
			// Create diagram filename by replacing the .graphxml extension with .diagram
			String graphxmlName = graphxmlFile.getName();
			String diagramFileName;
			String fullExtension = "." + FILE_EXTENSION;
			if (graphxmlName.endsWith(fullExtension)) {
				diagramFileName = graphxmlName.substring(0, graphxmlName.length() - fullExtension.length()) + ".diagram";
			} else {
				diagramFileName = graphxmlName + ".diagram";
			}
			
			IFile diagramFile = graphxmlFile.getParent().getFile(new Path(diagramFileName));
			
			if (!diagramFile.exists()) {
				// Create an empty diagram EMF resource
				URI diagramUri = URI.createPlatformResourceURI(
						diagramFile.getFullPath().toString(), true);
				
				ResourceSet resourceSet = new ResourceSetImpl();
				Resource resource = resourceSet.createResource(diagramUri);
				
				// Create a diagram
				Diagram diagram = PictogramsFactory.eINSTANCE.createDiagram();
				diagram.setDiagramTypeId(LvglDiagramTypeProvider.DIAGRAM_TYPE_ID);
				diagram.setName(graphxmlFile.getName());
				diagram.setSnapToGrid(true);
				diagram.setGridUnit(10);
				
				// Initialize GraphicsAlgorithm with background color (required by Graphiti GridLayer)
				IGaService gaService = Graphiti.getGaService();
				Rectangle rect = gaService.createRectangle(diagram);
				rect.setForeground(gaService.manageColor(diagram, IColorConstant.BLACK));
				rect.setBackground(gaService.manageColor(diagram, IColorConstant.WHITE));
				
				resource.getContents().add(diagram);
				
				// Save the resource
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resource.save(baos, null);
				
				// Create the file
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				diagramFile.create(bais, true, new NullProgressMonitor());
			}
		} catch (Exception e) {
			throw new CoreException(new Status(
					IStatus.ERROR,
					"com.tlcsdm.eclipse.graphiti.demo",
					"Failed to create diagram file: " + e.getMessage(),
					e));
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
