package xyz.vergoclient.commands.impl;

import xyz.vergoclient.commands.CommandManager;
import xyz.vergoclient.commands.OnCommandInterface;
import xyz.vergoclient.util.ChatUtils;

public class CommandSetSpawn implements OnCommandInterface {

	@Override
	public void onCommand(String... args) {
		ChatUtils.addChatMessage("Set your spawnpoint");
	}

	@Override
	public String getName() {
		return "setSpawn";
	}

	@Override
	public String getUsage() {
		return CommandManager.prefix + getName();
	}

	@Override
	public String getDescription() {
		return "Sets your spawn for the death tp module";
	}
}
