/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Abstract base class for all model elements.
 * Provides property change support for the MVC pattern.
 */
public abstract class ModelElement implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Property name for layout changes */
	public static final String PROPERTY_LAYOUT = "layout";
	/** Property name for adding elements */
	public static final String PROPERTY_ADD = "add";
	/** Property name for removing elements */
	public static final String PROPERTY_REMOVE = "remove";
	/** Property name for name changes */
	public static final String PROPERTY_NAME = "name";
	/** Property name for connection changes */
	public static final String PROPERTY_CONNECTION = "connection";

	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

	/**
	 * Adds a property change listener.
	 *
	 * @param listener the listener to add
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener cannot be null");
		}
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	/**
	 * Removes a property change listener.
	 *
	 * @param listener the listener to remove
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			getPropertyChangeSupport().removePropertyChangeListener(listener);
		}
	}

	/**
	 * Fires a property change event.
	 *
	 * @param property the property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if (getPropertyChangeSupport().hasListeners(property)) {
			getPropertyChangeSupport().firePropertyChange(property, oldValue, newValue);
		}
	}

	private PropertyChangeSupport getPropertyChangeSupport() {
		if (pcsDelegate == null) {
			pcsDelegate = new PropertyChangeSupport(this);
		}
		return pcsDelegate;
	}
}
