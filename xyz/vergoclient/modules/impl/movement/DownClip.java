package xyz.vergoclient.modules.impl.movement;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class DownClip extends Module {
	
	public DownClip() {
		super("DownClip", Category.MOVEMENT);
	}
	
	public NumberSetting maxDown = new NumberSetting("Max down", 4, 2, 10, 0.5);
	
	@Override
	public void loadSettings() {
		addSettings(maxDown);
	}
	
	@Override
	public void onEnable() {
		
		boolean foundSpot = false;
		for (double y = mc.thePlayer.posY - 1.5; y > mc.thePlayer.posY - maxDown.getValueAsDouble(); y -= 0.5) {
			if (foundSpot = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
				mc.thePlayer.setPosition(mc.thePlayer.posX, y - 1.5, mc.thePlayer.posZ);
				break;
			}
		}
		
		if (!foundSpot)
			ChatUtils.addChatMessage("No free space below you!");
		
		toggle();
		
	}
	
}
