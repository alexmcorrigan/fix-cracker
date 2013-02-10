package com.mcorrigal.fixCracker.gui.menuSystem;

public enum MenuItem {

	interpretSingleFixString("Interpret Single Fix String", Menu.file),
	exit("Exit", Menu.file);
	
	private String itemName;
	private Menu menu;
	
	private MenuItem(String itemName, Menu menu) {
		this.itemName = itemName;
		this.menu = menu;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public Menu getMenu() {
		return menu;
	}
	
	
}
