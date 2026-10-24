package com.hurricane.model;

/** Person in a victim's household */
public class FamilyMember {
	private String name;
	private int age;
	//Empty if they have no medical needs
	private String medicalNote;

	/** Makes a family member */
	public FamilyMember(String name, int age, String medicalNote) {
		this.name = name;
		this.age = age;
		this.medicalNote = medicalNote;
	}

	/** Their name */
	public String getName() {
		return name;
	}

	/** Their age */
	public int getAge() {
		return age;
	}

	/** Medical needs, or empty if none */
	public String getMedicalNote() {
		return medicalNote;
	}
}
