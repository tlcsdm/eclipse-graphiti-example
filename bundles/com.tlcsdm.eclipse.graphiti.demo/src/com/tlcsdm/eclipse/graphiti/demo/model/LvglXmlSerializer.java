/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.FlexAlign;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.FlexFlow;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.LayoutType;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.WidgetType;

/**
 * XML serializer for LVGL screen and widget models.
 * Supports saving to and loading from .graphxml files.
 */
public class LvglXmlSerializer {

	private static final String ELEMENT_SCREEN = "screen";
	private static final String ELEMENT_WIDGET = "widget";
	private static final String ELEMENT_CHILDREN = "children";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_WIDTH = "width";
	private static final String ATTR_HEIGHT = "height";
	private static final String ATTR_BG_COLOR = "bgColor";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_X = "x";
	private static final String ATTR_Y = "y";
	private static final String ATTR_TEXT = "text";
	private static final String ATTR_TEXT_COLOR = "textColor";
	private static final String ATTR_BORDER_WIDTH = "borderWidth";
	private static final String ATTR_BORDER_COLOR = "borderColor";
	private static final String ATTR_RADIUS = "radius";
	private static final String ATTR_IMAGE_SOURCE = "imageSource";
	private static final String ATTR_CHECKED = "checked";
	private static final String ATTR_VALUE = "value";
	private static final String ATTR_MIN_VALUE = "minValue";
	private static final String ATTR_MAX_VALUE = "maxValue";
	private static final String ATTR_ROW_COUNT = "rowCount";
	private static final String ATTR_COLUMN_COUNT = "columnCount";
	private static final String ATTR_TABLE_DATA = "tableData";
	private static final String ATTR_LAYOUT_TYPE = "layoutType";
	private static final String ATTR_FLEX_FLOW = "flexFlow";
	private static final String ATTR_FLEX_MAIN_ALIGN = "flexMainAlign";
	private static final String ATTR_FLEX_CROSS_ALIGN = "flexCrossAlign";
	private static final String ATTR_FLEX_TRACK_ALIGN = "flexTrackAlign";
	private static final String ATTR_PAD_ROW = "padRow";
	private static final String ATTR_PAD_COLUMN = "padColumn";

	/**
	 * Saves an LvglScreen to an output stream in XML format.
	 *
	 * @param screen the screen to save
	 * @param outputStream the output stream to write to
	 * @throws IOException if an I/O error occurs
	 */
	public void save(LvglScreen screen, OutputStream outputStream) throws IOException {
		try {
			Document doc = createDocument(screen);
			writeDocument(doc, outputStream);
		} catch (ParserConfigurationException | TransformerException e) {
			throw new IOException("Failed to save screen to XML", e);
		}
	}

	/**
	 * Saves an LvglScreen to a string in XML format.
	 *
	 * @param screen the screen to save
	 * @return the XML string
	 * @throws IOException if an error occurs
	 */
	public String saveToString(LvglScreen screen) throws IOException {
		try {
			Document doc = createDocument(screen);
			return writeDocumentToString(doc);
		} catch (ParserConfigurationException | TransformerException e) {
			throw new IOException("Failed to save screen to XML string", e);
		}
	}

	/**
	 * Loads an LvglScreen from an input stream.
	 *
	 * @param inputStream the input stream to read from
	 * @return the loaded screen
	 * @throws IOException if an I/O error occurs
	 */
	public LvglScreen load(InputStream inputStream) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inputStream);
			return parseDocument(doc);
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException("Failed to load screen from XML", e);
		}
	}

	private Document createDocument(LvglScreen screen) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element screenElement = doc.createElement(ELEMENT_SCREEN);
		screenElement.setAttribute(ATTR_NAME, screen.getName());
		screenElement.setAttribute(ATTR_WIDTH, Integer.toString(screen.getWidth()));
		screenElement.setAttribute(ATTR_HEIGHT, Integer.toString(screen.getHeight()));
		screenElement.setAttribute(ATTR_BG_COLOR, Integer.toString(screen.getBgColor()));
		doc.appendChild(screenElement);

		for (LvglWidget widget : screen.getWidgets()) {
			Element widgetElement = createWidgetElement(doc, widget);
			screenElement.appendChild(widgetElement);
		}

		return doc;
	}

	private Element createWidgetElement(Document doc, LvglWidget widget) {
		Element element = doc.createElement(ELEMENT_WIDGET);

		// Basic properties
		element.setAttribute(ATTR_NAME, widget.getName());
		element.setAttribute(ATTR_TYPE, widget.getWidgetType().name());
		element.setAttribute(ATTR_X, Integer.toString(widget.getX()));
		element.setAttribute(ATTR_Y, Integer.toString(widget.getY()));
		element.setAttribute(ATTR_WIDTH, Integer.toString(widget.getWidth()));
		element.setAttribute(ATTR_HEIGHT, Integer.toString(widget.getHeight()));

		// Text (if not empty)
		if (widget.getText() != null && !widget.getText().isEmpty()) {
			element.setAttribute(ATTR_TEXT, widget.getText());
		}

		// Style properties
		element.setAttribute(ATTR_BG_COLOR, Integer.toString(widget.getBgColor()));
		element.setAttribute(ATTR_TEXT_COLOR, Integer.toString(widget.getTextColor()));
		if (widget.getBorderWidth() > 0) {
			element.setAttribute(ATTR_BORDER_WIDTH, Integer.toString(widget.getBorderWidth()));
			element.setAttribute(ATTR_BORDER_COLOR, Integer.toString(widget.getBorderColor()));
		}
		if (widget.getRadius() > 0) {
			element.setAttribute(ATTR_RADIUS, Integer.toString(widget.getRadius()));
		}

		// Image source (if applicable)
		if (widget.getImageSource() != null && !widget.getImageSource().isEmpty()) {
			element.setAttribute(ATTR_IMAGE_SOURCE, widget.getImageSource());
		}

		// Checkbox/Switch state
		if (widget.isChecked()) {
			element.setAttribute(ATTR_CHECKED, "true");
		}

		// Value-based properties (if not default)
		if (widget.getValue() != 0 || widget.getMinValue() != 0 || widget.getMaxValue() != 100) {
			element.setAttribute(ATTR_VALUE, Integer.toString(widget.getValue()));
			element.setAttribute(ATTR_MIN_VALUE, Integer.toString(widget.getMinValue()));
			element.setAttribute(ATTR_MAX_VALUE, Integer.toString(widget.getMaxValue()));
		}

		// Table properties (if applicable)
		if (widget.getWidgetType() == WidgetType.TABLE) {
			element.setAttribute(ATTR_ROW_COUNT, Integer.toString(widget.getRowCount()));
			element.setAttribute(ATTR_COLUMN_COUNT, Integer.toString(widget.getColumnCount()));
			if (widget.getTableData() != null && !widget.getTableData().isEmpty()) {
				element.setAttribute(ATTR_TABLE_DATA, widget.getTableData());
			}
		}

		// Layout properties (for containers)
		if (widget.isContainer() && widget.getLayoutType() != LayoutType.NONE) {
			element.setAttribute(ATTR_LAYOUT_TYPE, widget.getLayoutType().name());
			if (widget.getLayoutType() == LayoutType.FLEX) {
				element.setAttribute(ATTR_FLEX_FLOW, widget.getFlexFlow().name());
				element.setAttribute(ATTR_FLEX_MAIN_ALIGN, widget.getFlexMainAlign().name());
				element.setAttribute(ATTR_FLEX_CROSS_ALIGN, widget.getFlexCrossAlign().name());
				element.setAttribute(ATTR_FLEX_TRACK_ALIGN, widget.getFlexTrackAlign().name());
			}
			if (widget.getPadRow() > 0) {
				element.setAttribute(ATTR_PAD_ROW, Integer.toString(widget.getPadRow()));
			}
			if (widget.getPadColumn() > 0) {
				element.setAttribute(ATTR_PAD_COLUMN, Integer.toString(widget.getPadColumn()));
			}
		}

		// Add children
		if (!widget.getChildren().isEmpty()) {
			Element childrenElement = doc.createElement(ELEMENT_CHILDREN);
			for (LvglWidget child : widget.getChildren()) {
				Element childElement = createWidgetElement(doc, child);
				childrenElement.appendChild(childElement);
			}
			element.appendChild(childrenElement);
		}

		return element;
	}

	private void writeDocument(Document doc, OutputStream outputStream) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outputStream);
		transformer.transform(source, result);
	}

	private String writeDocumentToString(Document doc) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		Writer writer = new StringWriter();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		return writer.toString();
	}

	private LvglScreen parseDocument(Document doc) {
		Element screenElement = doc.getDocumentElement();
		if (!ELEMENT_SCREEN.equals(screenElement.getTagName())) {
			throw new IllegalArgumentException("Root element must be '" + ELEMENT_SCREEN + "'");
		}

		LvglScreen screen = new LvglScreen();
		screen.setName(screenElement.getAttribute(ATTR_NAME));
		screen.setWidth(parseIntAttribute(screenElement, ATTR_WIDTH, 480));
		screen.setHeight(parseIntAttribute(screenElement, ATTR_HEIGHT, 320));
		screen.setBgColor(parseIntAttribute(screenElement, ATTR_BG_COLOR, 0xFFFFFF));

		NodeList widgetNodes = screenElement.getChildNodes();
		for (int i = 0; i < widgetNodes.getLength(); i++) {
			Node node = widgetNodes.item(i);
			if (node instanceof Element element && ELEMENT_WIDGET.equals(element.getTagName())) {
				LvglWidget widget = parseWidgetElement(element);
				screen.addWidget(widget);
			}
		}

		return screen;
	}

	private LvglWidget parseWidgetElement(Element element) {
		LvglWidget widget = new LvglWidget();

		// Basic properties
		widget.setName(element.getAttribute(ATTR_NAME));
		String typeStr = element.getAttribute(ATTR_TYPE);
		if (typeStr != null && !typeStr.isEmpty()) {
			try {
				widget.setWidgetType(WidgetType.valueOf(typeStr));
			} catch (IllegalArgumentException e) {
				widget.setWidgetType(WidgetType.BUTTON);
			}
		}
		widget.setX(parseIntAttribute(element, ATTR_X, 0));
		widget.setY(parseIntAttribute(element, ATTR_Y, 0));
		widget.setWidth(parseIntAttribute(element, ATTR_WIDTH, 100));
		widget.setHeight(parseIntAttribute(element, ATTR_HEIGHT, 40));

		// Text
		String text = element.getAttribute(ATTR_TEXT);
		if (text != null) {
			widget.setText(text);
		}

		// Style properties
		widget.setBgColor(parseIntAttribute(element, ATTR_BG_COLOR, 0xFFFFFF));
		widget.setTextColor(parseIntAttribute(element, ATTR_TEXT_COLOR, 0x000000));
		widget.setBorderWidth(parseIntAttribute(element, ATTR_BORDER_WIDTH, 0));
		widget.setBorderColor(parseIntAttribute(element, ATTR_BORDER_COLOR, 0x000000));
		widget.setRadius(parseIntAttribute(element, ATTR_RADIUS, 0));

		// Image source
		String imageSource = element.getAttribute(ATTR_IMAGE_SOURCE);
		if (imageSource != null && !imageSource.isEmpty()) {
			widget.setImageSource(imageSource);
		}

		// Checkbox/Switch state
		String checked = element.getAttribute(ATTR_CHECKED);
		widget.setChecked("true".equalsIgnoreCase(checked));

		// Value-based properties
		widget.setValue(parseIntAttribute(element, ATTR_VALUE, 0));
		widget.setMinValue(parseIntAttribute(element, ATTR_MIN_VALUE, 0));
		widget.setMaxValue(parseIntAttribute(element, ATTR_MAX_VALUE, 100));

		// Table properties
		widget.setRowCount(parseIntAttribute(element, ATTR_ROW_COUNT, 3));
		widget.setColumnCount(parseIntAttribute(element, ATTR_COLUMN_COUNT, 3));
		String tableData = element.getAttribute(ATTR_TABLE_DATA);
		if (tableData != null) {
			widget.setTableData(tableData);
		}

		// Layout properties
		String layoutTypeStr = element.getAttribute(ATTR_LAYOUT_TYPE);
		if (layoutTypeStr != null && !layoutTypeStr.isEmpty()) {
			try {
				widget.setLayoutType(LayoutType.valueOf(layoutTypeStr));
			} catch (IllegalArgumentException e) {
				widget.setLayoutType(LayoutType.NONE);
			}
		}

		String flexFlowStr = element.getAttribute(ATTR_FLEX_FLOW);
		if (flexFlowStr != null && !flexFlowStr.isEmpty()) {
			try {
				widget.setFlexFlow(FlexFlow.valueOf(flexFlowStr));
			} catch (IllegalArgumentException e) {
				widget.setFlexFlow(FlexFlow.ROW);
			}
		}

		String flexMainAlignStr = element.getAttribute(ATTR_FLEX_MAIN_ALIGN);
		if (flexMainAlignStr != null && !flexMainAlignStr.isEmpty()) {
			try {
				widget.setFlexMainAlign(FlexAlign.valueOf(flexMainAlignStr));
			} catch (IllegalArgumentException e) {
				widget.setFlexMainAlign(FlexAlign.START);
			}
		}

		String flexCrossAlignStr = element.getAttribute(ATTR_FLEX_CROSS_ALIGN);
		if (flexCrossAlignStr != null && !flexCrossAlignStr.isEmpty()) {
			try {
				widget.setFlexCrossAlign(FlexAlign.valueOf(flexCrossAlignStr));
			} catch (IllegalArgumentException e) {
				widget.setFlexCrossAlign(FlexAlign.START);
			}
		}

		String flexTrackAlignStr = element.getAttribute(ATTR_FLEX_TRACK_ALIGN);
		if (flexTrackAlignStr != null && !flexTrackAlignStr.isEmpty()) {
			try {
				widget.setFlexTrackAlign(FlexAlign.valueOf(flexTrackAlignStr));
			} catch (IllegalArgumentException e) {
				widget.setFlexTrackAlign(FlexAlign.START);
			}
		}

		widget.setPadRow(parseIntAttribute(element, ATTR_PAD_ROW, 0));
		widget.setPadColumn(parseIntAttribute(element, ATTR_PAD_COLUMN, 0));

		// Parse children
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element childElement) {
				if (ELEMENT_CHILDREN.equals(childElement.getTagName())) {
					NodeList widgetNodes = childElement.getChildNodes();
					for (int j = 0; j < widgetNodes.getLength(); j++) {
						Node widgetNode = widgetNodes.item(j);
						if (widgetNode instanceof Element widgetElement
								&& ELEMENT_WIDGET.equals(widgetElement.getTagName())) {
							LvglWidget childWidget = parseWidgetElement(widgetElement);
							widget.addChild(childWidget);
						}
					}
				}
			}
		}

		return widget;
	}

	private int parseIntAttribute(Element element, String name, int defaultValue) {
		String value = element.getAttribute(name);
		if (value == null || value.isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Creates a default LvglScreen with sample widgets.
	 *
	 * @return a new screen with sample content
	 */
	public static LvglScreen createDefaultScreen() {
		LvglScreen screen = new LvglScreen("main_screen");
		screen.setWidth(480);
		screen.setHeight(320);
		screen.setBgColor(0xFFFFFF);

		// Add a label widget
		LvglWidget label = new LvglWidget("lbl_title", WidgetType.LABEL);
		label.setX(140);
		label.setY(20);
		label.setWidth(200);
		label.setHeight(40);
		label.setText("LVGL UI Designer");
		label.setTextColor(0x333333);
		screen.addWidget(label);

		// Add a button widget
		LvglWidget button = new LvglWidget("btn_ok", WidgetType.BUTTON);
		button.setX(180);
		button.setY(260);
		button.setWidth(120);
		button.setHeight(40);
		button.setText("OK");
		button.setBgColor(0x2196F3);
		button.setTextColor(0xFFFFFF);
		button.setRadius(8);
		screen.addWidget(button);

		return screen;
	}
}
