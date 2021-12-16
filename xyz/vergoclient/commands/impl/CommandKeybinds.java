package xyz.vergoclient.commands.impl;

import java.io.File;

import xyz.vergoclient.commands.CommandManager;
import xyz.vergoclient.commands.OnCommandInterface;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.files.impl.FileKeybinds;
import xyz.vergoclient.keybinds.KeyboardManager;
import xyz.vergoclient.util.ChatUtils;

public class CommandKeybinds implements OnCommandInterface {

	@Override
	public void onCommand(String... args) {
		
		if (args.length < 3) {
			argsWarn();
			return;
		}
		
		String keybindsName = args[2];
		
		if (args[1].equalsIgnoreCase("save")) {
			FileManager.writeToFile(new File(FileManager.keybindsDir, keybindsName + ".json"), KeyboardManager.keybinds);
			ChatUtils.addChatMessage("Keybinds saved");
		}
		else if (args[1].equalsIgnoreCase("load")) {
			KeyboardManager.keybinds = FileManager.readFromFile(new File(FileManager.keybindsDir, keybindsName + ".json"), new FileKeybinds());
			ChatUtils.addChatMessage("Keybinds loaded");
		}
		else
			argsWarn();
		
	}
	
	private void argsWarn() {
		ChatUtils.addChatMessage("Please use " + getUsage());
	}
	
	@Override
	public String getName() {
		return "keybinds";
	}

	@Override
	public String getUsage() {
		return CommandManager.prefix + getName() + " <save/load> <keybinds>";
	}

	@Override
	public String getDescription() {
		return "Saves or loads a config";
	}

}
