package com.mcorrigal.fixCracker;

import static com.mcorrigal.fixCracker.CommonConstants.COMMA;
import static com.mcorrigal.fixCracker.CommonConstants.PIPE;
import static com.mcorrigal.fixCracker.CommonConstants.SOH;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixStringNormaliser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FixStringNormaliser.class);

	private static String stringInProgress = null;
	
	public static String process(String rawFixString) throws Exception {
		stringInProgress = rawFixString;
		forDelimiters();
		forFinalCharacter();
		forCheckSum();
		LOGGER.info("Normalised fix string: " + stringInProgress);
		return stringInProgress;
	}
	
	private static void forDelimiters() throws Exception {
		if (stringInProgress.contains(SOH)) {
			LOGGER.info("Fix string delimited by SOH");
		} else if (stringInProgress.contains(PIPE)) {
			LOGGER.info("Fix string delimited by PIPE, converting to SOH");
			stringInProgress = StringUtils.replace(stringInProgress, PIPE, SOH);
		} else if (stringInProgress.contains(COMMA)) {
			LOGGER.info("Fix string delimited by COMMA, converting to SOH");
			stringInProgress = StringUtils.replace(stringInProgress, COMMA, SOH);
		} else {
			LOGGER.info("Failed to normalise. Can not determine tag delimiter used.");
			throw new Exception("FIX String not delimited by recognised character");
		}
	}
	
	private static void forFinalCharacter() {
		if (!stringInProgress.endsWith(SOH)) {
			LOGGER.info("Fix string did not end with SOH, completing");
			stringInProgress = stringInProgress + SOH;
		} else {
			LOGGER.info("Fix string already ends with SOH");
		}
	}
	
	private static void forCheckSum() {
		if (!stringInProgress.contains(SOH + "10=")) {
			String checkSum = StringUtils.leftPad(FixUtils.calculateCheckSumFor(stringInProgress).toString(), 3, '0');
			LOGGER.info("Fix string did not have check sum, calculated it as " + checkSum + ", and adding it");
			stringInProgress = stringInProgress + "10=" + checkSum + SOH;
		} else {
			LOGGER.info("Fix string already has checkSum");
		}
	}
	
	
}
