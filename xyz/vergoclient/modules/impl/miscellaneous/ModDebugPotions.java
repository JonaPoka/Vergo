package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.util.ChatUtils;

public class ModDebugPotions extends Module {

	public ModDebugPotions() {
		super("DebugPotions", Category.PLAYER);
	}
	
	@Override
	public void onEnable() {
		mc.thePlayer.sendChatMessage("/effect " + mc.session.getUsername() + " minecraft:regeneration 100000 255");
		mc.thePlayer.sendChatMessage("/effect " + mc.session.getUsername() + " minecraft:resistance 100000 255");
		mc.thePlayer.sendChatMessage("/effect " + mc.session.getUsername() + " minecraft:fire_resistance 100000 255");
		mc.thePlayer.sendChatMessage("/effect " + mc.session.getUsername() + " minecraft:saturation 100000 255");
		ChatUtils.addChatMessage("Gave you the debug potion effects");
		toggle();
	}
	
}
