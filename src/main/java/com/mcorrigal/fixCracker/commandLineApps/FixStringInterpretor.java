package com.mcorrigal.fixCracker.commandLineApps;

import com.mcorrigal.fixCracker.MessageReader;
import quickfix.ConfigError;
import quickfix.InvalidMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class FixStringInterpretor {

    private static String fixDictionaryLocation = "FIX44.xml";

	public static void main(String[] args) throws Exception {
		System.out.println("Paste FIX message up to last SOH before checkSum field (pipe delimited):");
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String fixMessageString = input.readLine();

        if (args.length == 1) fixDictionaryLocation = args[0];

		try {
			Map<Integer, String> fixFieldMap = parseString(fixMessageString);
			displayFixFieldMap(fixFieldMap);
		} catch (InvalidMessage e) {
			System.out.println("Fix string invalid: " + e.getMessage());
		} catch (ConfigError e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static Map<Integer, String> parseString(String fixString) throws Exception {
		MessageReader reader = new MessageReader(fixDictionaryLocation);
		return reader.parseFixString(fixString);
	}
	
	private static void displayFixFieldMap(Map<Integer, String> fixFieldMap) throws ConfigError {
		MessageReader reader = new MessageReader(fixDictionaryLocation);
		for (Integer tag : fixFieldMap.keySet()) {
			String tagName = reader.fieldNameForTag(tag);
			String fieldValue = reader.meaningfulFieldValue(tag, fixFieldMap.get(tag));
			String outputRow = String.format("%-10s %-20s %-20s", tag, tagName, fieldValue);
			System.out.println(outputRow);
		}
	}
	
}
