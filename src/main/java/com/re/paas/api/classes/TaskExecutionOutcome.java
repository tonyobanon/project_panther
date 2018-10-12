package com.re.paas.api.classes;

public enum TaskExecutionOutcome {

	SUCCESS(0), FAILURE(1);

	private int value;
	private Object message;

	private TaskExecutionOutcome(Integer value) {
		this.value = value;
	}

	public static TaskExecutionOutcome from(int value) {

		switch (value) {

		case 0:
			return TaskExecutionOutcome.SUCCESS;
			
		case 1:
			return TaskExecutionOutcome.FAILURE;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}

	public Object getMessage() {
		return message;
	}

	public TaskExecutionOutcome setMessage(ClientRBRef message) {
		this.message = message;
		return this;
	}
	
	public TaskExecutionOutcome setMessage(String message) {
		this.message = message;
		return this;
	}

	public TaskExecutionOutcome setValue(int value) {
		this.value = value;
		return this;
	}
}
