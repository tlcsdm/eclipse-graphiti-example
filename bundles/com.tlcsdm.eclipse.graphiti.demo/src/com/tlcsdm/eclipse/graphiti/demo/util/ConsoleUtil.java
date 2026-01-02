/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.util;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Utility class for writing to the Eclipse console.
 */
public class ConsoleUtil {

	private static final String CONSOLE_NAME = "LVGL UI Designer";
	private static MessageConsole console;

	private ConsoleUtil() {
		// Prevent instantiation
	}

	/**
	 * Gets or creates the LVGL console.
	 *
	 * @return the console
	 */
	public static MessageConsole getConsole() {
		if (console == null) {
			IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			IConsole[] existing = consoleManager.getConsoles();
			for (IConsole existingConsole : existing) {
				if (CONSOLE_NAME.equals(existingConsole.getName())) {
					console = (MessageConsole) existingConsole;
					return console;
				}
			}
			console = new MessageConsole(CONSOLE_NAME, null);
			consoleManager.addConsoles(new IConsole[] { console });
		}
		return console;
	}

	/**
	 * Prints a message to the console.
	 *
	 * @param message the message to print
	 */
	public static void println(String message) {
		MessageConsoleStream stream = getConsole().newMessageStream();
		stream.println(message);
	}

	/**
	 * Prints an error message to the console.
	 *
	 * @param message the error message to print
	 */
	public static void printError(String message) {
		MessageConsoleStream stream = getConsole().newMessageStream();
		stream.println("[ERROR] " + message);
	}

	/**
	 * Clears the console.
	 */
	public static void clear() {
		getConsole().clearConsole();
	}
}
