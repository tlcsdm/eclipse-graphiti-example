/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * Action bar contributor for the LVGL multi-page editor.
 * Provides toolbar buttons for Undo, Redo, and Delete actions.
 */
public class LvglEditorActionBarContributor extends MultiPageEditorActionBarContributor {

	public LvglEditorActionBarContributor() {
		super();
	}

	@Override
	public void setActivePage(IEditorPart activeEditor) {
		// Update actions when the active page changes
		if (activeEditor != null) {
			// The actions are automatically managed by the retarget actions
		}
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		// Add undo/redo buttons with null checks
		IAction undoAction = getAction(ActionFactory.UNDO.getId());
		if (undoAction != null) {
			toolBarManager.add(undoAction);
		}
		IAction redoAction = getAction(ActionFactory.REDO.getId());
		if (redoAction != null) {
			toolBarManager.add(redoAction);
		}
		toolBarManager.add(new Separator());
		// Add delete button with null check
		IAction deleteAction = getAction(ActionFactory.DELETE.getId());
		if (deleteAction != null) {
			toolBarManager.add(deleteAction);
		}
	}

	/**
	 * Gets a global action from the action bars.
	 * 
	 * @param actionId the action ID
	 * @return the action, or null if not registered
	 */
	private IAction getAction(String actionId) {
		return getActionBars().getGlobalActionHandler(actionId);
	}
}
