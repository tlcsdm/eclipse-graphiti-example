/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;

import com.tlcsdm.eclipse.graphiti.demo.diagram.LvglDiagramTypeProvider;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglXmlSerializer;
import com.tlcsdm.eclipse.graphiti.demo.util.ConsoleUtil;

/**
 * Graphiti-based diagram editor for LVGL UI design.
 * This editor is embedded within the multi-page editor.
 * Uses in-memory diagram model - no .diagram file is created.
 */
public class LvglDiagramEditor extends DiagramEditor {

	/** The LVGL screen model */
	private LvglScreen screen;

	/** The .graphxml file (the only file used) */
	private IFile graphxmlFile;

	public LvglDiagramEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Handle IFileEditorInput by converting to DiagramEditorInput
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			this.graphxmlFile = fileInput.getFile();

			// Load the screen model from the .graphxml file
			loadScreen();

			// Create an in-memory DiagramEditorInput (no .diagram file needed)
			input = createInMemoryDiagramEditorInput(fileInput);
		}

		super.init(site, input);
		setPartName(input.getName());

		// Register the context menu provider
		registerContextMenu();
	}

	/**
	 * Loads the screen model from the .graphxml file.
	 */
	private void loadScreen() {
		if (graphxmlFile == null || !graphxmlFile.exists()) {
			screen = LvglXmlSerializer.createDefaultScreen();
			return;
		}

		try (InputStream is = graphxmlFile.getContents()) {
			if (is.available() > 0) {
				LvglXmlSerializer serializer = new LvglXmlSerializer();
				screen = serializer.load(is);
			} else {
				screen = LvglXmlSerializer.createDefaultScreen();
			}
		} catch (Exception e) {
			ConsoleUtil.printError("Failed to load screen: " + e.getMessage());
			screen = LvglXmlSerializer.createDefaultScreen();
		}
	}

	/**
	 * Creates an in-memory DiagramEditorInput from an IFileEditorInput.
	 * The diagram model is stored in memory only - no .diagram file is created.
	 */
	private DiagramEditorInput createInMemoryDiagramEditorInput(IFileEditorInput fileInput) {
		IFile file = fileInput.getFile();
		
		// Use a virtual URI for the in-memory diagram resource
		// This prevents any file system access for the diagram model
		URI diagramUri = URI.createURI("memory://" + file.getFullPath().toString() + ".diagram");

		// Create a resource set
		ResourceSet resourceSet = new ResourceSetImpl();

		// Create a new in-memory diagram resource
		Resource resource = resourceSet.createResource(diagramUri);
		Diagram diagram = PictogramsFactory.eINSTANCE.createDiagram();
		diagram.setDiagramTypeId(LvglDiagramTypeProvider.DIAGRAM_TYPE_ID);
		diagram.setName(file.getName());
		diagram.setSnapToGrid(true);
		diagram.setGridUnit(10);
		
		// Initialize GraphicsAlgorithm with background color (required by Graphiti GridLayer)
		IGaService gaService = Graphiti.getGaService();
		Rectangle rect = gaService.createRectangle(diagram);
		rect.setForeground(gaService.manageColor(diagram, IColorConstant.BLACK));
		rect.setBackground(gaService.manageColor(diagram, IColorConstant.WHITE));

		// Add to resource
		resource.getContents().add(diagram);

		// Create the editor input with correct signature (Diagram, String providerId)
		return DiagramEditorInput.createEditorInput(diagram, LvglDiagramTypeProvider.PROVIDER_ID);
	}

	/**
	 * Registers the context menu provider for the graphical viewer.
	 */
	private void registerContextMenu() {
		// Context menu is handled by the ToolBehaviorProvider in Graphiti
		// Additional context menu actions are added via the LvglToolBehaviorProvider
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Only save the .graphxml file - no .diagram file
		// Skip parent's doSave as it tries to save the diagram resource
		
		if (screen == null || graphxmlFile == null) {
			return;
		}

		try {
			// Serialize the screen to XML
			LvglXmlSerializer serializer = new LvglXmlSerializer();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serializer.save(screen, baos);

			// Write to .graphxml file
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			if (graphxmlFile.exists()) {
				graphxmlFile.setContents(bais, true, true, monitor);
			} else {
				graphxmlFile.create(bais, true, monitor);
			}
			
			// Mark the command stack as saved to clear the dirty flag
			getEditingDomain().getCommandStack().flush();
		} catch (Exception e) {
			ConsoleUtil.printError("Failed to save diagram: " + e.getMessage());
		}
	}

	@Override
	public void doSaveAs() {
		// Not supported
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Gets the screen model.
	 */
	public LvglScreen getScreen() {
		return screen;
	}

	/**
	 * Sets the screen model and refreshes the diagram.
	 * Used when XML content changes in the source tab.
	 */
	public void setScreen(LvglScreen newScreen) {
		this.screen = newScreen;

		// Refresh the diagram
		getDiagramBehavior().refresh();
	}

	/**
	 * Gets the .graphxml file.
	 */
	public IFile getDiagramFile() {
		return graphxmlFile;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class type) {
		if (type == IFile.class) {
			return graphxmlFile;
		}
		
		// Check if the editor is fully initialized before delegating to parent
		// The parent's getAdapter may call getEditDomain() which is only set during the editor initialization lifecycle
		if (getEditDomain() == null) {
			// Editor not fully initialized yet, return null to avoid NullPointerException
			return null;
		}
		
		return super.getAdapter(type);
	}
}
