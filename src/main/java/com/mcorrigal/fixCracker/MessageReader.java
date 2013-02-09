package com.mcorrigal.fixCracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mcorrigal.fixCracker.CommonConstants.SOH;

public class MessageReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageReader.class);
	
	private DataDictionary dataDictionary;
	
	public MessageReader(String fixDictionaryLocation) {
		try {
			dataDictionary = new DataDictionary(fixDictionaryLocation);
		} catch (ConfigError e) {
			e.printStackTrace();
		}
	}
	
	public Map<Integer, String> parseFixString(String rawFixString) throws Exception {
		LOGGER.info("Received fix string [" + rawFixString + "], normalising...");
		String normalisedFixString = FixStringNormaliser.process(rawFixString);
		Map<Integer, String> fixMessageMap = new HashMap<Integer, String>();
		Message message = null;
		try {
			message = new Message(normalisedFixString, true);
			fixMessageMap.putAll(iterateMessageParts(message.getHeader().iterator()));
			fixMessageMap.putAll(iterateMessageParts(message.iterator()));
			fixMessageMap.putAll(iterateMessageParts(message.getTrailer().iterator()));
			LOGGER.info("valid");
			return fixMessageMap;
		} catch (InvalidMessage e) {
			LOGGER.warn("failed validation");
			throw e;
		}
	}
	
	private Map<Integer, String> iterateMessageParts(Iterator<Field<?>> iterator) {
		Map<Integer, String> tagValueMap = new HashMap<Integer, String>();
		while (iterator.hasNext()) {
			Field<?> field = iterator.next();
			String fieldValue = (String) field.getObject();
			tagValueMap.put(field.getField(), fieldValue);
		}
		return tagValueMap;
	}

	public String fieldNameForTag(int fieldTag) {
		return dataDictionary.getFieldName(fieldTag) + StringUtils.bracketValue(String.valueOf(fieldTag), true);
	}

	public String meaningfulFieldValue(int fieldTag, String fieldValue) {
		String dictionaryValue = dataDictionary.getValueName(fieldTag, fieldValue);
		if (dictionaryValue == null) {
			return fieldValue;
		} else {
			return fieldValue + StringUtils.bracketValue(dictionaryValue, true);
		}
	}
	
	public List<String> parseFixLogFile(BufferedReader logFile) throws IOException, InvalidMessage, ConfigError {
		List<String> logRecords = new ArrayList<String>();
		String logRecord;
		while ((logRecord = logFile.readLine()) != null) {
			LOGGER.info("Received log record: " + logRecord);
			logRecords.add(logRecord);
		}
		return logRecords;
	}

	public String extractFixString(String fixLogRecord) {
		Integer startOfFixString = fixLogRecord.indexOf("8=FIX.4.4", 0);
		Integer endOfFixString = fixLogRecord.lastIndexOf(SOH, fixLogRecord.length() - 1);
		String fixStringCandidate = null;
		try {
			fixStringCandidate = fixLogRecord.substring(startOfFixString, endOfFixString + 1);
		} catch (Exception e) {
			throw new RuntimeException("Can not detect fix string in: " + fixLogRecord);
		}
		return fixStringCandidate;
	}
	
}
