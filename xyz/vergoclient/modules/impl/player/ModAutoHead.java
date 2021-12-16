package xyz.vergoclient.modules.impl.player;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.InventoryUtils;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class ModAutoHead extends Module implements OnEventInterface {

	public ModAutoHead() {
		super("AutoHead", Category.PLAYER);
	}
	
	public NumberSetting delaySetting = new NumberSetting("Delay", 750, 0, 3000, 50),
			eatAtPercentSetting = new NumberSetting("Percent to eat", 50, 1, 99, 1);
	
	@Override
	public void loadSettings() {
		addSettings(eatAtPercentSetting, delaySetting);
	}
	
	public static transient TimerUtil timer = new TimerUtil();
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			
			if ((mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth()) * 100 <= eatAtPercentSetting.getValueAsDouble() && timer.hasTimeElapsed((long) delaySetting.getValueAsDouble(), false)) {
				
				for (short i = 0; i < 45; i++) {
					
					if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
						ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
						
						if (is.getItem() instanceof ItemSkull && is.getDisplayName().equalsIgnoreCase("�6Golden Head")) {
							int heldItemBeforeThrow = mc.thePlayer.inventory.currentItem;
							if (i - 36 < 0) {
								
								InventoryUtils.swap(i, 8);
								
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C09PacketHeldItemChange(8));
								
							}else {
								
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C09PacketHeldItemChange(i - 36));
								
							}
							
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
							mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C09PacketHeldItemChange(heldItemBeforeThrow));
							timer.reset();
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
