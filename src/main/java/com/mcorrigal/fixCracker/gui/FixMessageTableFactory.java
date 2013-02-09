package com.mcorrigal.fixCracker.gui;

import static com.mcorrigal.fixCracker.CommonConstants.FIXED_WIDTH_FONT;

import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.mcorrigal.fixCracker.MessageReader;

public class FixMessageTableFactory {

	private static final String[] TABLE_HEADER = {"Tag", "Value"};
	
	private MessageReader messageReader;
	
	public FixMessageTableFactory(MessageReader messageReader) {
		this.messageReader = messageReader;
	}
	
	public JTable create(Map<Integer, String> fixMessageMap) {
		return buildTableForFixMessageMap(fixMessageMap);
	}
	
	private JTable buildTableForFixMessageMap(Map<Integer, String> fixMessageMap) {
		Object[][] tagValuePairs = new Object[fixMessageMap.size()][2];
		
		int tagCounter = 0;
		for (Integer tagId : fixMessageMap.keySet()) {
			tagValuePairs[tagCounter] = new Object[]{
					messageReader.fieldNameForTag(tagId), 
					messageReader.meaningfulFieldValue(tagId, fixMessageMap.get(tagId))};
			tagCounter++;
		}
		
		TableModel tm = new DefaultTableModel(tagValuePairs, TABLE_HEADER);
		JTable table = new JTable(tm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFont(FIXED_WIDTH_FONT);
		return table;
	}
	
	
}
