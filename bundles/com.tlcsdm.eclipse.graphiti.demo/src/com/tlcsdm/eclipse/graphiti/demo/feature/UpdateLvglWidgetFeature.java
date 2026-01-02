/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to update the graphical representation of an LVGL widget.
 */
public class UpdateLvglWidgetFeature extends AbstractUpdateFeature {

	public UpdateLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return bo instanceof LvglWidget;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof LvglWidget widget && pe instanceof ContainerShape containerShape) {
			// Check if text needs update
			String currentText = getCurrentText(containerShape);
			String modelText = widget.getText();
			if (currentText != null && !currentText.equals(modelText)) {
				return Reason.createTrueReason("Text is out of date");
			}

			// Check if position or size needs update
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if (ga != null) {
				if (ga.getX() != widget.getX() || ga.getY() != widget.getY()) {
					return Reason.createTrueReason("Position is out of date");
				}
				if (ga.getWidth() != widget.getWidth() || ga.getHeight() != widget.getHeight()) {
					return Reason.createTrueReason("Size is out of date");
				}
			}
		}

		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof LvglWidget widget && pe instanceof ContainerShape containerShape) {
			// Update text
			for (Shape shape : containerShape.getChildren()) {
				GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
				if (ga instanceof Text text) {
					text.setValue(widget.getText());
				}
			}

			// Update position and size
			GraphicsAlgorithm mainGa = containerShape.getGraphicsAlgorithm();
			if (mainGa != null) {
				mainGa.setX(widget.getX());
				mainGa.setY(widget.getY());
				mainGa.setWidth(widget.getWidth());
				mainGa.setHeight(widget.getHeight());

				// Update child text sizes
				for (Shape shape : containerShape.getChildren()) {
					GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
					if (ga instanceof Text) {
						ga.setWidth(widget.getWidth());
						ga.setHeight(widget.getHeight());
					}
				}
			}

			return true;
		}

		return false;
	}

	private String getCurrentText(ContainerShape containerShape) {
		for (Shape shape : containerShape.getChildren()) {
			GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
			if (ga instanceof Text text) {
				return text.getValue();
			}
		}
		return null;
	}
}
