package xyz.vergoclient.commands.impl;

import xyz.vergoclient.commands.CommandManager;
import xyz.vergoclient.commands.OnCommandInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class CommandSay implements OnCommandInterface {

	@Override
	public void onCommand(String... args) {
		
		String[] splitMessage = args;
		String publicMessage = "";
		
		for (String s : splitMessage) {
			publicMessage += s + " ";
		}
		
		publicMessage = publicMessage.replaceFirst(CommandManager.prefix + "say ", "");
		publicMessage = publicMessage.substring(0, publicMessage.length() - 1);
		
		Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketNoEvent(new C01PacketChatMessage(publicMessage));
		
	}

	@Override
	public String getName() {
		return "say";
	}

	@Override
	public String getUsage() {
		return CommandManager.prefix + getName() + " what to say";
	}

	@Override
	public String getDescription() {
		return "Bypass the chat commands to say stuff in chat";
	}

}
