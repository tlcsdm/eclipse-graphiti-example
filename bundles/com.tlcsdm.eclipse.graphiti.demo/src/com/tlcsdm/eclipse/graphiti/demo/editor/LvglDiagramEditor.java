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
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
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
 */
public class LvglDiagramEditor extends DiagramEditor {

	/** The LVGL screen model */
	private LvglScreen screen;

	/** The diagram file */
	private IFile diagramFile;

	public LvglDiagramEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Handle IFileEditorInput by converting to DiagramEditorInput
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			this.diagramFile = fileInput.getFile();

			// Load the screen model from the file
			loadScreen();

			// Create a DiagramEditorInput from the file
			input = createDiagramEditorInput(fileInput);
		}

		super.init(site, input);
		setPartName(input.getName());

		// Register the context menu provider
		registerContextMenu();
	}

	/**
	 * Loads the screen model from the diagram file.
	 */
	private void loadScreen() {
		if (diagramFile == null || !diagramFile.exists()) {
			screen = LvglXmlSerializer.createDefaultScreen();
			return;
		}

		try (InputStream is = diagramFile.getContents()) {
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
	 * Creates a DiagramEditorInput from an IFileEditorInput.
	 * The diagram is stored in a separate .diagram file alongside the .graphxml file.
	 */
	private DiagramEditorInput createDiagramEditorInput(IFileEditorInput fileInput) {
		IFile file = fileInput.getFile();
		
		// Create a diagram file path by replacing .graphxml extension with .diagram
		String diagramFileName = file.getName().replace(".graphxml", ".diagram");
		IFile diagramFile = file.getParent().getFile(new org.eclipse.core.runtime.Path(diagramFileName));
		
		URI diagramUri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);

		// Create a resource set
		ResourceSet resourceSet = new ResourceSetImpl();

		// Try to load existing diagram resource, or create a new one
		Resource resource = null;
		Diagram diagram = null;
		
		if (diagramFile.exists()) {
			try {
				resource = resourceSet.getResource(diagramUri, true);
				if (!resource.getContents().isEmpty() && resource.getContents().get(0) instanceof Diagram) {
					diagram = (Diagram) resource.getContents().get(0);
				}
			} catch (Exception e) {
				ConsoleUtil.printError("Failed to load existing diagram: " + e.getMessage());
			}
		}
		
		if (diagram == null) {
			// Create a new diagram and resource
			resource = resourceSet.createResource(diagramUri);
			diagram = PictogramsFactory.eINSTANCE.createDiagram();
			diagram.setDiagramTypeId(LvglDiagramTypeProvider.DIAGRAM_TYPE_ID);
			diagram.setName(file.getName());
			diagram.setSnapToGrid(true);
			diagram.setGridUnit(10);

			// Add to resource
			resource.getContents().add(diagram);
			
			// Save the diagram resource
			try {
				resource.save(null);
			} catch (Exception e) {
				ConsoleUtil.printError("Failed to save diagram resource: " + e.getMessage());
			}
		}

		// Create the editor input with correct signature (Diagram, String providerId)
		return DiagramEditorInput.createEditorInput(diagram, LvglDiagramTypeProvider.DIAGRAM_TYPE_ID);
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
		// First call parent to handle Graphiti diagram saving
		super.doSave(monitor);

		// Then save our custom XML model
		if (screen == null || diagramFile == null) {
			return;
		}

		try {
			// Serialize the screen to XML
			LvglXmlSerializer serializer = new LvglXmlSerializer();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serializer.save(screen, baos);

			// Write to file
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			if (diagramFile.exists()) {
				diagramFile.setContents(bais, true, true, monitor);
			} else {
				diagramFile.create(bais, true, monitor);
			}
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
	 * Gets the diagram file.
	 */
	public IFile getDiagramFile() {
		return diagramFile;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class type) {
		if (type == IFile.class) {
			return diagramFile;
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
