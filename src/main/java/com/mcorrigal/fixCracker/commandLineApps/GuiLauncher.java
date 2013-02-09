package com.mcorrigal.fixCracker.commandLineApps;

import com.mcorrigal.fixCracker.MessageReader;
import com.mcorrigal.fixCracker.gui.FixCrackerGui;

public class GuiLauncher {

	public static void main(String[] args) {
        String fixDictionaryLocation = "FIX44.xml";
        if (args.length == 1) {
            fixDictionaryLocation = args[0];
        }
		FixCrackerGui gui = new FixCrackerGui(new MessageReader(fixDictionaryLocation));
	}

}
