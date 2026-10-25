package com.hurricane.model;

/** 2D vector */
public class Vector2 {
	protected double x;
	protected double y;

	/** Makes a vector */
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/** Sum of two vectors */
	public Vector2 add(Vector2 other) {
		return new Vector2(x + other.x, y + other.y);
	}

	/** Dot product */
	public double dot(Vector2 other) {
		return x * other.x + y * other.y;
	}

	/** X value */
	public double getX() {
		return x;
	}

	/** Y value */
	public double getY() {
		return y;
	}
}
