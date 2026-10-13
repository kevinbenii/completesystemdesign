package com.hurricane.model;

public class Vector2 {
	protected double x;
	protected double y;

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 add(Vector2 other) {
		return new Vector2(x + other.x, y + other.y);
	}

	public double dot(Vector2 other) {
		return x * other.x + y * other.y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
