package com.hurricane.model;

/** Pet that belongs to a victim */
public class Pet {
	private String name;
	//Kind of animal, e.g. "dog"
	private String type;

	/** Makes a pet */
	public Pet(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/** Its name */
	public String getName() {
		return name;
	}

	/** Kind of animal, e.g. "dog" */
	public String getType() {
		return type;
	}
}
