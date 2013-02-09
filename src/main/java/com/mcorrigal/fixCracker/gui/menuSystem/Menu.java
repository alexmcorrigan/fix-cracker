package com.mcorrigal.fixCracker.gui.menuSystem;

public enum Menu {

	file("File");
	
	private String menuName;
	
	private Menu(String menuName) {
		this.menuName = menuName;
	}
	
	public String getMenuName() {
		return menuName;
	}
	
}
