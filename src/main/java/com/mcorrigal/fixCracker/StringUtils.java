package com.mcorrigal.fixCracker;

public class StringUtils {

	public static String bracketValue(String value, boolean withLeadingSpace) {
		String bracketedValue = "(" + value + ")";
		if (withLeadingSpace) {
			return CommonConstants.SPACE + bracketedValue;
		} else {
			return bracketedValue;
		}
	}
	
}
