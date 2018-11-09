package com.zhonghong.model;

public class Contact {

	private String name;
	private String[] numbers;
	
	public Contact(String name,String[] number){
		this.name = name;
		this.numbers = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getNumbers() {
		return numbers;
	}

	public void setNumbers(String[] numbers) {
		this.numbers = numbers;
	}

}
