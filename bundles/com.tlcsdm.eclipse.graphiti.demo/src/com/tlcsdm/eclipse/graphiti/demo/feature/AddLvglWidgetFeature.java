/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to add a graphical representation for an LVGL widget.
 */
public class AddLvglWidgetFeature extends AbstractAddFeature {

	// Widget colors based on type
	private static final IColorConstant BUTTON_BG = IColorConstant.LIGHT_BLUE;
	private static final IColorConstant LABEL_BG = IColorConstant.WHITE;
	private static final IColorConstant SLIDER_BG = IColorConstant.LIGHT_GRAY;
	private static final IColorConstant CONTAINER_BG = IColorConstant.WHITE;
	private static final IColorConstant TEXT_COLOR = IColorConstant.BLACK;
	private static final IColorConstant BORDER_COLOR = IColorConstant.DARK_GRAY;

	public AddLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof LvglWidget) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		LvglWidget widget = (LvglWidget) context.getNewObject();
		ContainerShape targetContainer = context.getTargetContainer();

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		// Create container shape
		ContainerShape containerShape = peCreateService.createContainerShape(targetContainer, true);

		// Get dimensions
		int width = widget.getWidth();
		int height = widget.getHeight();
		int x = context.getX();
		int y = context.getY();

		// Create main graphics algorithm based on widget type
		GraphicsAlgorithm mainGa = createWidgetGraphics(gaService, containerShape, widget, x, y, width, height);
		if (mainGa != null) {
			gaService.setLocationAndSize(mainGa, x, y, width, height);
		}

		// Add text if widget has text content
		if (widget.getText() != null && !widget.getText().isEmpty()) {
			Shape textShape = peCreateService.createShape(containerShape, false);
			Text text = gaService.createText(textShape, widget.getText());
			text.setForeground(manageColor(TEXT_COLOR));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			gaService.setLocationAndSize(text, 0, 0, width, height);
		}

		// Create the link between the pictogram element and the business object
		link(containerShape, widget);

		// Add anchors for connections
		peCreateService.createChopboxAnchor(containerShape);

		return containerShape;
	}

	private GraphicsAlgorithm createWidgetGraphics(IGaService gaService, ContainerShape containerShape,
			LvglWidget widget, int x, int y, int width, int height) {

		switch (widget.getWidgetType()) {
		case BUTTON:
			RoundedRectangle buttonRect = gaService.createRoundedRectangle(containerShape, 8, 8);
			buttonRect.setBackground(manageColor(getColorFromInt(widget.getBgColor())));
			buttonRect.setForeground(manageColor(BORDER_COLOR));
			buttonRect.setLineWidth(2);
			return buttonRect;

		case LABEL:
			Rectangle labelRect = gaService.createRectangle(containerShape);
			labelRect.setBackground(manageColor(getColorFromInt(widget.getBgColor())));
			labelRect.setForeground(manageColor(BORDER_COLOR));
			labelRect.setLineWidth(1);
			labelRect.setLineVisible(false);
			labelRect.setFilled(false);
			return labelRect;

		case SLIDER:
		case BAR:
			RoundedRectangle sliderRect = gaService.createRoundedRectangle(containerShape, 4, 4);
			sliderRect.setBackground(manageColor(SLIDER_BG));
			sliderRect.setForeground(manageColor(BORDER_COLOR));
			sliderRect.setLineWidth(1);
			return sliderRect;

		case SWITCH:
			RoundedRectangle switchRect = gaService.createRoundedRectangle(containerShape, height / 2, height / 2);
			switchRect.setBackground(manageColor(SLIDER_BG));
			switchRect.setForeground(manageColor(BORDER_COLOR));
			switchRect.setLineWidth(2);
			return switchRect;

		case CHECKBOX:
			Rectangle checkboxRect = gaService.createRectangle(containerShape);
			checkboxRect.setBackground(manageColor(CONTAINER_BG));
			checkboxRect.setForeground(manageColor(BORDER_COLOR));
			checkboxRect.setLineWidth(2);
			return checkboxRect;

		case DROPDOWN:
			RoundedRectangle dropdownRect = gaService.createRoundedRectangle(containerShape, 4, 4);
			dropdownRect.setBackground(manageColor(CONTAINER_BG));
			dropdownRect.setForeground(manageColor(BORDER_COLOR));
			dropdownRect.setLineWidth(1);
			return dropdownRect;

		case TEXTAREA:
			Rectangle textareaRect = gaService.createRectangle(containerShape);
			textareaRect.setBackground(manageColor(CONTAINER_BG));
			textareaRect.setForeground(manageColor(BORDER_COLOR));
			textareaRect.setLineWidth(1);
			return textareaRect;

		case IMAGE:
			Rectangle imageRect = gaService.createRectangle(containerShape);
			imageRect.setBackground(manageColor(SLIDER_BG));
			imageRect.setForeground(manageColor(BORDER_COLOR));
			imageRect.setLineWidth(1);
			return imageRect;

		case ARC:
			RoundedRectangle arcRect = gaService.createRoundedRectangle(containerShape, width / 2, height / 2);
			arcRect.setBackground(manageColor(CONTAINER_BG));
			arcRect.setForeground(manageColor(BORDER_COLOR));
			arcRect.setLineWidth(2);
			arcRect.setFilled(false);
			return arcRect;

		case CONTAINER:
		case TABVIEW:
		case LIST:
		case TILEVIEW:
		case MENU:
		case WIN:
			Rectangle containerRect = gaService.createRectangle(containerShape);
			containerRect.setBackground(manageColor(CONTAINER_BG));
			containerRect.setForeground(manageColor(BORDER_COLOR));
			containerRect.setLineWidth(2);
			return containerRect;

		case CHART:
		case TABLE:
		case CALENDAR:
			Rectangle chartRect = gaService.createRectangle(containerShape);
			chartRect.setBackground(manageColor(CONTAINER_BG));
			chartRect.setForeground(manageColor(BORDER_COLOR));
			chartRect.setLineWidth(1);
			return chartRect;

		case SPINNER:
			RoundedRectangle spinnerRect = gaService.createRoundedRectangle(containerShape, width / 2, height / 2);
			spinnerRect.setBackground(manageColor(SLIDER_BG));
			spinnerRect.setForeground(manageColor(BORDER_COLOR));
			spinnerRect.setLineWidth(2);
			return spinnerRect;

		case LED:
			RoundedRectangle ledRect = gaService.createRoundedRectangle(containerShape, width / 2, height / 2);
			ledRect.setBackground(manageColor(IColorConstant.GREEN));
			ledRect.setForeground(manageColor(BORDER_COLOR));
			ledRect.setLineWidth(2);
			return ledRect;

		case KEYBOARD:
		case BUTTONMATRIX:
			Rectangle keyboardRect = gaService.createRectangle(containerShape);
			keyboardRect.setBackground(manageColor(SLIDER_BG));
			keyboardRect.setForeground(manageColor(BORDER_COLOR));
			keyboardRect.setLineWidth(1);
			return keyboardRect;

		case ROLLER:
		case SPINBOX:
			RoundedRectangle rollerRect = gaService.createRoundedRectangle(containerShape, 4, 4);
			rollerRect.setBackground(manageColor(CONTAINER_BG));
			rollerRect.setForeground(manageColor(BORDER_COLOR));
			rollerRect.setLineWidth(1);
			return rollerRect;

		case MSGBOX:
			RoundedRectangle msgboxRect = gaService.createRoundedRectangle(containerShape, 8, 8);
			msgboxRect.setBackground(manageColor(CONTAINER_BG));
			msgboxRect.setForeground(manageColor(BORDER_COLOR));
			msgboxRect.setLineWidth(2);
			return msgboxRect;

		default:
			Rectangle defaultRect = gaService.createRectangle(containerShape);
			defaultRect.setBackground(manageColor(CONTAINER_BG));
			defaultRect.setForeground(manageColor(BORDER_COLOR));
			defaultRect.setLineWidth(1);
			return defaultRect;
		}
	}

	private IColorConstant getColorFromInt(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		return new IColorConstant() {
			@Override
			public int getRed() {
				return r;
			}

			@Override
			public int getGreen() {
				return g;
			}

			@Override
			public int getBlue() {
				return b;
			}
		};
	}
}
