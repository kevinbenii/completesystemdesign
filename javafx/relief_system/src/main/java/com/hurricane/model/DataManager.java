package com.model;

public abstract class DataManager implements DataLoader, DataWriter {
	protected String filePath;

	public DataManager(String filePath) {
		this.filePath = filePath;
	}
}
