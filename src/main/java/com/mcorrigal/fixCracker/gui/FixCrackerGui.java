package com.mcorrigal.fixCracker.gui;

import com.mcorrigal.fixCracker.MessageReader;
import com.mcorrigal.fixCracker.gui.menuSystem.Menu;
import com.mcorrigal.fixCracker.gui.menuSystem.MenuItem;
import org.apache.commons.lang.StringUtils;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import static com.mcorrigal.fixCracker.CommonConstants.*;

public class FixCrackerGui implements ActionListener {

	private JFrame frame;
	private JDesktopPane desktopPane;
	private MessageReader messageReader;
	
	public FixCrackerGui(MessageReader messageReader) {
		this.messageReader = messageReader;
		initialise();
		constructFrame();
	}
	
	private void initialise() {
	}
	
	private void constructFrame() {
		frame = new JFrame();
		desktopPane = new JDesktopPane();
		desktopPane.setBackground(new Color(-16768434));
		frame.setContentPane(desktopPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 800, 600);
		frame.setJMenuBar(buildMenus());
		frame.setVisible(true);
	}
	
	private JMenuBar buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		for (Menu menuToCreate : Menu.values()) {
			JMenu menu = new JMenu(menuToCreate.getMenuName());
			menu.setFont(DISPLAY_FONT);
			
			for (MenuItem menuItemToCreate : MenuItem.values()) {
				if (menuItemToCreate.getMenu().equals(menuToCreate)) {
					JMenuItem menuItem = new JMenuItem(menuItemToCreate.getItemName());
					menuItem.addActionListener(this);
					menuItem.setFont(DISPLAY_FONT);
					menu.add(menuItem);
				}
			}
			
			menuBar.add(menu);
		}
		return menuBar;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(MenuItem.exit.getItemName())) {
			System.exit(0);
		} else if (event.getActionCommand().equals(MenuItem.interpretSingleFixString.getItemName())) {
			doInterpretSingleFixMessageString();
		}
	}
	
	private void doInterpretSingleFixMessageString() {
		String rawFixString = (String) JOptionPane.showInputDialog(
				frame, 
				"Paste your Fix string:", 
				"Single Fix String", 
				JOptionPane.PLAIN_MESSAGE, 
				null, 
				null, 
				"<message>");
        if (rawFixString != null) {
            try {
                Map<Integer, String> fixMessageMap = messageReader.parseFixString(rawFixString);
                JInternalFrame fixFrame = new JInternalFrame("New Fix Message", true, true, true, true);

                JPanel content = new JPanel();
                content.setLayout(new BorderLayout());

                JTextArea fixStringTextArea = new JTextArea(StringUtils.replace(rawFixString, SOH, PIPE));
                fixStringTextArea.setEditable(false);
                fixStringTextArea.setLineWrap(true);
                fixStringTextArea.setWrapStyleWord(false);
                fixStringTextArea.setFont(FIXED_WIDTH_FONT);

                JScrollPane fixStringSrcollPane = new JScrollPane(fixStringTextArea);
                FixMessageTableFactory fixTableFactory = new FixMessageTableFactory(messageReader);
                JScrollPane tableScrollPane = new JScrollPane(fixTableFactory.create(fixMessageMap));

                content.add(fixStringSrcollPane, BorderLayout.NORTH);
                content.add(tableScrollPane, BorderLayout.CENTER);

                fixFrame.setContentPane(content);
                fixFrame.setVisible(true);
                fixFrame.pack();
                desktopPane.add(fixFrame);
                frame.setContentPane(desktopPane);
            } catch (Exception e) {
                alertError(e);
            }
        }
	}
	
	private void alertError(Exception e) {
		JOptionPane.showMessageDialog(frame, e.getMessage(), "Problem with Fix String", JOptionPane.ERROR_MESSAGE);
	}
	
	
}
