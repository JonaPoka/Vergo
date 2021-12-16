package xyz.vergoclient.commands.impl;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.commands.CommandManager;
import xyz.vergoclient.commands.OnCommandInterface;
import xyz.vergoclient.util.ChatUtils;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public class CommandHelp implements OnCommandInterface {

	@Override
	public void onCommand(String... args) {
		ChatUtils.addChatMessage(" ");
		for (OnCommandInterface command : Vergo.commandManager.commands) {
			ChatStyle style = new ChatStyle();
			style.setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText("Usage: " + command.getUsage())));
			ChatUtils.addChatMessage(CommandManager.prefix + command.getName() + " | " + command.getDescription(), style);
		}
		ChatUtils.addChatMessage(" ");
		ChatUtils.addChatMessage("Hover over the commands to show their usage");
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "Figure it out stupid";
	}

	@Override
	public String getDescription() {
		return "help";
	}

}
