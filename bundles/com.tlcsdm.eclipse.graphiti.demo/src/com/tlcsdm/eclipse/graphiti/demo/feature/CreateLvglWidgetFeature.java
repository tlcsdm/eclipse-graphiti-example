/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.WidgetType;

/**
 * Feature to create a new LVGL widget in the diagram.
 */
public class CreateLvglWidgetFeature extends AbstractCreateFeature {

	private final WidgetType widgetType;
	private static int widgetCounter = 0;

	public CreateLvglWidgetFeature(IFeatureProvider fp, WidgetType widgetType) {
		super(fp, widgetType.getDisplayName(), "Create a new " + widgetType.getDisplayName() + " widget");
		this.widgetType = widgetType;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// Create a unique name for the widget
		String baseName = widgetType.getLvglType().replace("lv_", "");
		String name = baseName + "_" + (++widgetCounter);

		// Create the widget model
		LvglWidget widget = new LvglWidget(name, widgetType);

		// Set position from the create context
		widget.setX(context.getX());
		widget.setY(context.getY());

		// Set default size based on widget type
		setDefaultSize(widget);

		// Add to the screen model
		Diagram diagram = (Diagram) context.getTargetContainer();
		Object bo = getBusinessObjectForPictogramElement(diagram);
		if (bo instanceof LvglScreen screen) {
			screen.addWidget(widget);
		}

		// Add the graphical representation
		addGraphicalRepresentation(context, widget);

		return new Object[] { widget };
	}

	private void setDefaultSize(LvglWidget widget) {
		switch (widgetType) {
			case BUTTON:
				widget.setWidth(120);
				widget.setHeight(40);
				widget.setText("Button");
				break;
			case LABEL:
				widget.setWidth(100);
				widget.setHeight(30);
				widget.setText("Label");
				break;
			case SLIDER:
				widget.setWidth(150);
				widget.setHeight(20);
				break;
			case SWITCH:
				widget.setWidth(60);
				widget.setHeight(30);
				break;
			case CHECKBOX:
				widget.setWidth(100);
				widget.setHeight(30);
				widget.setText("Checkbox");
				break;
			case DROPDOWN:
				widget.setWidth(120);
				widget.setHeight(35);
				widget.setText("Option 1");
				break;
			case TEXTAREA:
				widget.setWidth(200);
				widget.setHeight(100);
				break;
			case IMAGE:
				widget.setWidth(80);
				widget.setHeight(80);
				break;
			case ARC:
				widget.setWidth(100);
				widget.setHeight(100);
				break;
			case BAR:
				widget.setWidth(150);
				widget.setHeight(20);
				break;
			case CONTAINER:
				widget.setWidth(200);
				widget.setHeight(150);
				break;
			case CHART:
				widget.setWidth(200);
				widget.setHeight(150);
				break;
			case TABLE:
				widget.setWidth(200);
				widget.setHeight(150);
				break;
			case LIST:
				widget.setWidth(150);
				widget.setHeight(200);
				break;
			case TABVIEW:
				widget.setWidth(250);
				widget.setHeight(200);
				break;
			case SPINNER:
				widget.setWidth(50);
				widget.setHeight(50);
				break;
			case LED:
				widget.setWidth(40);
				widget.setHeight(40);
				break;
			case CALENDAR:
				widget.setWidth(250);
				widget.setHeight(200);
				break;
			case KEYBOARD:
				widget.setWidth(300);
				widget.setHeight(150);
				break;
			case ROLLER:
				widget.setWidth(80);
				widget.setHeight(100);
				break;
			case MSGBOX:
				widget.setWidth(200);
				widget.setHeight(150);
				widget.setText("Message");
				break;
			case WIN:
				widget.setWidth(300);
				widget.setHeight(200);
				widget.setText("Window");
				break;
			default:
				widget.setWidth(100);
				widget.setHeight(40);
				break;
		}
	}
}
