/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.diagram;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

/**
 * Image provider for LVGL UI diagrams.
 * Provides icons for the palette and diagram elements.
 */
public class LvglImageProvider extends AbstractImageProvider implements IImageProvider {

	public static final String IMG_BUTTON = "icons/button.png";
	public static final String IMG_LABEL = "icons/label.png";
	public static final String IMG_SLIDER = "icons/slider.png";
	public static final String IMG_SWITCH = "icons/switch.png";
	public static final String IMG_CHECKBOX = "icons/checkbox.png";
	public static final String IMG_DROPDOWN = "icons/dropdown.png";
	public static final String IMG_TEXTAREA = "icons/textarea.png";
	public static final String IMG_IMAGE = "icons/image.png";
	public static final String IMG_ARC = "icons/arc.png";
	public static final String IMG_BAR = "icons/bar.png";
	public static final String IMG_CONTAINER = "icons/container.png";
	public static final String IMG_CHART = "icons/chart.png";
	public static final String IMG_TABLE = "icons/table.png";
	public static final String IMG_LIST = "icons/list.png";
	public static final String IMG_SPINNER = "icons/spinner.png";
	public static final String IMG_LED = "icons/led.png";
	public static final String IMG_CALENDAR = "icons/calendar.png";
	public static final String IMG_KEYBOARD = "icons/keyboard.png";
	public static final String IMG_ROLLER = "icons/roller.png";
	public static final String IMG_MSGBOX = "icons/msgbox.png";
	public static final String IMG_WIN = "icons/window.png";
	public static final String IMG_TABVIEW = "icons/tabview.png";
	public static final String IMG_SAMPLE = "icons/sample.png";

	private static final String PREFIX = "com.tlcsdm.eclipse.graphiti.demo.";

	public LvglImageProvider() {
		super();
	}

	@Override
	protected void addAvailableImages() {
		addImageFilePath(PREFIX + "button", IMG_BUTTON);
		addImageFilePath(PREFIX + "label", IMG_LABEL);
		addImageFilePath(PREFIX + "slider", IMG_SLIDER);
		addImageFilePath(PREFIX + "switch", IMG_SWITCH);
		addImageFilePath(PREFIX + "checkbox", IMG_CHECKBOX);
		addImageFilePath(PREFIX + "dropdown", IMG_DROPDOWN);
		addImageFilePath(PREFIX + "textarea", IMG_TEXTAREA);
		addImageFilePath(PREFIX + "image", IMG_IMAGE);
		addImageFilePath(PREFIX + "arc", IMG_ARC);
		addImageFilePath(PREFIX + "bar", IMG_BAR);
		addImageFilePath(PREFIX + "container", IMG_CONTAINER);
		addImageFilePath(PREFIX + "chart", IMG_CHART);
		addImageFilePath(PREFIX + "table", IMG_TABLE);
		addImageFilePath(PREFIX + "list", IMG_LIST);
		addImageFilePath(PREFIX + "spinner", IMG_SPINNER);
		addImageFilePath(PREFIX + "led", IMG_LED);
		addImageFilePath(PREFIX + "calendar", IMG_CALENDAR);
		addImageFilePath(PREFIX + "keyboard", IMG_KEYBOARD);
		addImageFilePath(PREFIX + "roller", IMG_ROLLER);
		addImageFilePath(PREFIX + "msgbox", IMG_MSGBOX);
		addImageFilePath(PREFIX + "window", IMG_WIN);
		addImageFilePath(PREFIX + "tabview", IMG_TABVIEW);
		addImageFilePath(PREFIX + "sample", IMG_SAMPLE);
	}
}
