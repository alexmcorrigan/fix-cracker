package com.mcorrigal.fixCracker;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import quickfix.ConfigError;
import quickfix.InvalidMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mcorrigal.fixCracker.CommonConstants.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class MessageReaderTest {

	private static final String Q_MARK = "?";
	
	private static final String COMPLETE_VALID_FIX_STRING = "8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
	private static final String FIX_STRING_WITH_CHECK_SUM_NO_LAST_DELIMITER = "8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
	private static final String FIX_STRING_WITH_NO_CHECK_SUM_WITH_LAST_DELIMITER = "8=FIX.4.49=3035=D49=SEND56=TARGET11=123";
	private static final String FIX_STRING_WITH_NO_CHECK_SUM_WITH_NO_LAST_DELIMITER = "8=FIX.4.49=3035=D49=SEND56=TARGET11=123";
	
	private MessageReader fixViewer;
	
	@Before
	public void setUp() throws ConfigError {
		fixViewer = new MessageReader("FIX44.xml");
	}
	
	@Test
	public void canParseCompleteFixStringWithDifferentDelimiters() throws Exception {
		assertFixStringParsed(SOH);
		assertFixStringParsed(PIPE);
		assertFixStringParsed(COMMA);
	}
	
	@Test
	public void canNotParseFixStringDelimitedByUnknowCharacter() throws InvalidMessage, ConfigError {
		try {
			assertFixStringParsed(Q_MARK);
		} catch (Exception e) {
			assertEquals("FIX String not delimited by recognised character", e.getMessage());
		}
	}

	private void assertFixStringParsed(String delimiter) throws Exception {
		List<String> baseFixStrings = new ArrayList<String>();
		baseFixStrings.add(COMPLETE_VALID_FIX_STRING);
		baseFixStrings.add(FIX_STRING_WITH_CHECK_SUM_NO_LAST_DELIMITER);
		baseFixStrings.add(FIX_STRING_WITH_NO_CHECK_SUM_WITH_LAST_DELIMITER);
		baseFixStrings.add(FIX_STRING_WITH_NO_CHECK_SUM_WITH_NO_LAST_DELIMITER);
		
		for (String baseFixString : baseFixStrings) {
			Map<Integer, String> fixMessageMap = fixViewer.parseFixString(StringUtils.replace(baseFixString, SOH, delimiter));
			assertEquals(7, fixMessageMap.size());
			assertTrue(fixMessageMap.get(8).equals("FIX.4.4"));
			assertTrue(fixMessageMap.get(9).equals("30"));
			assertTrue(fixMessageMap.get(35).equals("D"));
			assertTrue(fixMessageMap.get(49).equals("SEND"));
			assertTrue(fixMessageMap.get(56).equals("TARGET"));
			assertTrue(fixMessageMap.get(11).equals("123"));
			assertTrue(fixMessageMap.get(10).equals("096"));
		}
	}
	
	@Test
	public void invalidFixStringThrowsInvalidMessge() {
		String fixMessageString = "NER NER NER 8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096 NER NER NER";
		try {
			fixViewer.parseFixString(fixMessageString);
		} catch (Exception e) {
			assertEquals(InvalidMessage.class.getName(), e.getClass().getName());
		}
	}
	
	@Test
	public void fieldTagsCanBeInterpreted() {
		int fieldTag = 8;
		assertEquals("BeginString (8)", fixViewer.fieldNameForTag(fieldTag));
	}
	
	@Test
	public void fieldValuesCanBeInterpretedIfEnumerated() {
		int enumeratedFieldTag = 35;
		String enumeratedFieldValue = "D";
		int nonEnumeratedFieldTag = 49;
		String nonEnumeratedFieldValue = "SEND";
		assertEquals("D (NewOrderSingle)", fixViewer.meaningfulFieldValue(enumeratedFieldTag, enumeratedFieldValue));
		assertEquals("SEND", fixViewer.meaningfulFieldValue(nonEnumeratedFieldTag, nonEnumeratedFieldValue));
	}
	
	@Test
	public void logFileReaderTest() throws IOException, InvalidMessage, ConfigError {
		BufferedReader logFileReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("fixLogs/shortFixLog.txt")));
		List<String> fixLogRecords = fixViewer.parseFixLogFile(logFileReader);
		assertEquals(10, fixLogRecords.size());
		for (String fixLogRow : fixLogRecords) {
			assertTrue(fixLogRow.contains("8=FIX.4.4"));
		}
	}
	
	@Test
	public void stripFixStringFromLogRecordSplitWithSpaces() {
		String fixLogRecord = "[some text at front] 8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096 [text at end]";
		String expectedFixString = "8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
		assertEquals(expectedFixString, fixViewer.extractFixString(fixLogRecord));
	}
	
	@Test
	public void canNotDetectAFixString() {
		List<String> badStrings = new ArrayList<String>();
		badStrings.add("[some text at front][text at end]");
		badStrings.add("[some text at front]8=FIX.4.4[text at end]");
		badStrings.add("[some text at front]" + SOH + "[text at end]");
		for (String badFixString : badStrings) {
			try {
				fixViewer.extractFixString(badFixString);
			} catch (RuntimeException e) {
				assertEquals("Can not detect fix string in: " + badFixString, e.getMessage());
			}
		}
	}
	
	@Test
	public void stripFixStringFromLogRecordSplitNoSpaces() {
		String fixLogRecord = "[some text at front]8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096[text at end]";
		String expectedFixString = "8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
		assertEquals(expectedFixString, fixViewer.extractFixString(fixLogRecord));
	}
	
}
