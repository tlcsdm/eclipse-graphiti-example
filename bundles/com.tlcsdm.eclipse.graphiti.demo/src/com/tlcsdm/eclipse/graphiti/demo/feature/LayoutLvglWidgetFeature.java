/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to layout an LVGL widget after resize.
 */
public class LayoutLvglWidgetFeature extends AbstractLayoutFeature {

	public LayoutLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		return bo instanceof LvglWidget;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof LvglWidget widget && pe instanceof ContainerShape containerShape) {
			GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

			if (containerGa != null) {
				int width = containerGa.getWidth();
				int height = containerGa.getHeight();

				// Update the text shape to fill the container
				for (Shape shape : containerShape.getChildren()) {
					GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
					if (ga instanceof Text) {
						ga.setX(0);
						ga.setY(0);
						ga.setWidth(width);
						ga.setHeight(height);
					}
				}

				// Update the model
				widget.setWidth(width);
				widget.setHeight(height);

				return true;
			}
		}

		return false;
	}
}
