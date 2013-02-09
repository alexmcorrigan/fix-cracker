package com.mcorrigal.fixCracker;

import org.junit.Before;
import org.junit.Test;

import quickfix.ConfigError;
import quickfix.DataDictionary;

public class DataDictinaryTest {

	private DataDictionary dataDictionary;
	
	@Before
	public void setup() {
		try {
			dataDictionary = new DataDictionary("FIX44.xml");
		} catch (ConfigError e) {
			e.printStackTrace();
		}
	}
	
	@Test
 	public void ddTest() {
		System.out.println(dataDictionary.getValueName(35, "D"));
	}
	
}
