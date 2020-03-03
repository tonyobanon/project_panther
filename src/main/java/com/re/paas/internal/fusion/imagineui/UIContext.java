package com.re.paas.internal.fusion.imagineui;

import java.util.function.BiConsumer;

import com.re.paas.api.annotations.develop.BlockerTodo;

@BlockerTodo
public class UIContext {

	public static boolean confirm(String title, String body) {
		return true;
	}

	public static void showToast(String message) {

	}

	/**
	 * 
	 * This indicates to the user that a task is currently in progress.
	 * 
	 * @param message Message
	 * @param block   Indicates whether the UI interaction should be disabled while
	 *                task executes
	 * @return {@link BiConsumer} that should be repeatedly called to indicate the
	 *         current status of the task, i.e <percentage_completion, status_text>
	 */
	public static BiConsumer<Integer, String> loading(String message, boolean block) {
		return null;
	}
}
