package com.hurricane.model;

/** Loads and saves data */
public abstract class DataManager implements DataLoader, DataWriter {
	protected String filePath;

	/** Makes a data manager for that path */
	public DataManager(String filePath) {
		this.filePath = filePath;
	}
}
