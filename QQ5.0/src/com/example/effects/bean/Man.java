package com.example.effects.bean;

public class Man implements Comparable<Man>{
	private String name;
	private char letter;

	public Man(String name, char letter) {
		super();
		this.name = name;
		this.letter = letter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	@Override
	public int compareTo(Man another) {
		return letter - another.letter;
	}

}
