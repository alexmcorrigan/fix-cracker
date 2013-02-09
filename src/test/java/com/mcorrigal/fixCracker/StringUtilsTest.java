package com.mcorrigal.fixCracker;

import static org.junit.Assert.assertEquals;

import com.mcorrigal.fixCracker.StringUtils;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void bracketValue() {
		String value = "35";
		assertEquals("(35)", StringUtils.bracketValue(value, false));
		assertEquals(" (35)", StringUtils.bracketValue(value, true));
	}
	
}
