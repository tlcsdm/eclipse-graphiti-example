/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Diagram Type Provider for LVGL UI diagrams.
 * This is the main entry point for Graphiti to interact with our diagram.
 */
public class LvglDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String DIAGRAM_TYPE_ID = "com.tlcsdm.eclipse.graphiti.demo.LvglDiagram";

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public LvglDiagramTypeProvider() {
		setFeatureProvider(new LvglFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new LvglToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
}
