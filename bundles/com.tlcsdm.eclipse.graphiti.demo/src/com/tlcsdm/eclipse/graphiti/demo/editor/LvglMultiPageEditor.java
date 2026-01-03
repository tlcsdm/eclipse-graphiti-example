/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglXmlSerializer;
import com.tlcsdm.eclipse.graphiti.demo.util.ConsoleUtil;

/**
 * Multi-page editor for LVGL UI design.
 * <p>
 * Page 0: UI Drag-and-drop editor (Graphiti graphical editor)
 * Page 1: XML text editor
 * </p>
 * <p>
 * When the XML text is edited and user switches to the UI page,
 * the UI view is refreshed to reflect the XML changes.
 * </p>
 */
public class LvglMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener {

	/** Editor ID matching the id attribute in plugin.xml. */
	public static final String ID = "com.tlcsdm.eclipse.graphiti.demo.editor.multi";

	/** Index of the UI design page */
	private static final int UI_PAGE_INDEX = 0;
	/** Index of the XML source page */
	private static final int XML_PAGE_INDEX = 1;

	/** The graphical editor for UI design */
	private LvglDiagramEditor graphicalEditor;

	/** The text editor for XML source */
	private TextEditor textEditor;

	/** Flag to track if XML was modified */
	private boolean xmlModified = false;

	/** Document listener to track XML changes */
	private IDocumentListener documentListener;

	/**
	 * Creates a multi-page editor.
	 */
	public LvglMultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor,
	 * which is the UI drag-and-drop editor.
	 */
	void createUIPage() {
		try {
			graphicalEditor = new LvglDiagramEditor();
			int index = addPage(graphicalEditor, getEditorInput());
			setPageText(index, "Design");
		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating UI editor",
					null,
					e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor,
	 * which is the XML text editor.
	 */
	void createXMLPage() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "Source");

			// Add document listener to track changes
			if (textEditor.getDocumentProvider() != null) {
				IDocument document = textEditor.getDocumentProvider().getDocument(getEditorInput());
				if (document != null) {
					documentListener = new IDocumentListener() {
						@Override
						public void documentChanged(DocumentEvent event) {
							xmlModified = true;
						}

						@Override
						public void documentAboutToBeChanged(DocumentEvent event) {
							// Not needed
						}
					};
					document.addDocumentListener(documentListener);
				}
			}
		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating XML editor",
					null,
					e.getStatus());
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		createUIPage();
		createXMLPage();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 */
	@Override
	public void dispose() {
		// Remove document listener
		if (textEditor != null && documentListener != null) {
			IDocument document = textEditor.getDocumentProvider().getDocument(getEditorInput());
			if (document != null) {
				document.removeDocumentListener(documentListener);
			}
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 * When saving from Design tab, the graphical model is serialized to XML and saved.
	 * When saving from Source tab, the XML is saved and the graphical model is updated.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// Save from the currently active page
		if (getActivePage() == XML_PAGE_INDEX) {
			// Save XML changes first
			textEditor.doSave(monitor);
			// Refresh UI editor with new content
			refreshGraphicalEditor();
			// Reset the xmlModified flag since we've synchronized
			xmlModified = false;
		} else {
			// Save from graphical editor - serialize and save
			graphicalEditor.doSave(monitor);
			// Update the text editor content and sync its dirty state
			refreshTextEditorAndSave(monitor);
		}
		// Fire property change to update the dirty state indicator
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Saves the multi-page editor's document as another file.
	 * Not supported.
	 */
	@Override
	public void doSaveAs() {
		// Not supported
	}

	/**
	 * Initialize the editor.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		super.init(site, editorInput);
		setPartName(editorInput.getName());
	}

	/**
	 * Returns whether "Save As" is allowed.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Returns whether the editor is dirty.
	 * The multi-page editor is dirty if either the graphical editor or text editor has unsaved changes.
	 */
	@Override
	public boolean isDirty() {
		return (graphicalEditor != null && graphicalEditor.isDirty())
				|| (textEditor != null && textEditor.isDirty());
	}

	/**
	 * Calculates the contents of page when it is activated.
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == UI_PAGE_INDEX && xmlModified) {
			// Switching to UI page after XML was modified - refresh UI
			refreshGraphicalEditor();
			xmlModified = false;
		} else if (newPageIndex == XML_PAGE_INDEX && graphicalEditor != null && graphicalEditor.isDirty()) {
			// Switching to XML page after UI was modified - refresh XML
			refreshTextEditor();
		}
	}

	/**
	 * Refresh the graphical editor with content from XML text.
	 */
	private void refreshGraphicalEditor() {
		try {
			IDocument document = textEditor.getDocumentProvider().getDocument(getEditorInput());
			if (document != null) {
				String xmlContent = document.get();
				if (xmlContent != null && !xmlContent.trim().isEmpty()) {
					LvglXmlSerializer serializer = new LvglXmlSerializer();
					ByteArrayInputStream bais = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
					LvglScreen newScreen = serializer.load(bais);

					// Update the graphical editor's model
					graphicalEditor.setScreen(newScreen);
				}
			}
		} catch (Exception e) {
			ConsoleUtil.printError("Failed to refresh UI editor from XML: " + e.getMessage());
		}
	}

	/**
	 * Refresh the text editor with content from the graphical model.
	 */
	private void refreshTextEditor() {
		try {
			LvglScreen screen = graphicalEditor.getScreen();
			if (screen != null) {
				LvglXmlSerializer serializer = new LvglXmlSerializer();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				serializer.save(screen, baos);

				IDocument document = textEditor.getDocumentProvider().getDocument(getEditorInput());
				if (document != null) {
					// Temporarily remove listener to avoid triggering xmlModified
					if (documentListener != null) {
						document.removeDocumentListener(documentListener);
					}
					document.set(baos.toString(StandardCharsets.UTF_8));
					if (documentListener != null) {
						document.addDocumentListener(documentListener);
					}
				}
			}
		} catch (Exception e) {
			ConsoleUtil.printError("Failed to refresh XML editor from UI: " + e.getMessage());
		}
	}

	/**
	 * Refresh the text editor with content from the graphical model and save it.
	 * This ensures the text editor is synchronized and not marked dirty after save.
	 */
	private void refreshTextEditorAndSave(IProgressMonitor monitor) {
		try {
			if (textEditor == null || textEditor.getDocumentProvider() == null) {
				return;
			}
			LvglScreen screen = graphicalEditor.getScreen();
			if (screen != null) {
				LvglXmlSerializer serializer = new LvglXmlSerializer();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				serializer.save(screen, baos);

				IDocument document = textEditor.getDocumentProvider().getDocument(getEditorInput());
				if (document != null) {
					// Temporarily remove listener to avoid triggering xmlModified
					if (documentListener != null) {
						document.removeDocumentListener(documentListener);
					}
					document.set(baos.toString(StandardCharsets.UTF_8));
					if (documentListener != null) {
						document.addDocumentListener(documentListener);
					}
					// Reset the document provider by reloading content from the file.
					// Since graphicalEditor.doSave() has already saved the XML to file,
					// this reloads the saved content and clears the text editor's dirty state.
					textEditor.getDocumentProvider().resetDocument(getEditorInput());
					// Reset xmlModified since content is now synchronized
					xmlModified = false;
				}
			}
		} catch (Exception e) {
			ConsoleUtil.printError("Failed to refresh and save XML editor from UI: " + e.getMessage());
		}
	}

	/**
	 * Closes all project files on project close.
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(() -> {
				IEditorInput input = getEditorInput();
				if (input instanceof IFileEditorInput) {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((IFileEditorInput) input).getFile().getProject().equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(input);
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	/**
	 * Get the screen model from the graphical editor.
	 */
	public LvglScreen getScreen() {
		if (graphicalEditor != null) {
			return graphicalEditor.getScreen();
		}
		return null;
	}

	/**
	 * Get the diagram file.
	 */
	public IFile getDiagramFile() {
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile();
		}
		return null;
	}

	/**
	 * Get the graphical editor.
	 */
	public LvglDiagramEditor getGraphicalEditor() {
		return graphicalEditor;
	}
}
