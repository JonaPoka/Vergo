package xyz.vergoclient.modules.impl.player;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.util.InventoryUtils;
import xyz.vergoclient.util.TimerUtil;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.NumberSetting;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ModChestStealer extends Module implements OnEventInterface {

	public ModChestStealer() {
		super("ChestStealer", Category.PLAYER);
	}
	
	public static TimerUtil timer = new TimerUtil();
	
	public NumberSetting tickDelay = new NumberSetting("Delay", 1, 1, 10, 1);
	public BooleanSetting checkChestNames = new BooleanSetting("Verify Name", true);
	
	@Override
	public void loadSettings() {
		addSettings(tickDelay, checkChestNames);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre() && mc.thePlayer.ticksExisted % tickDelay.getValueAsInt() == 0) {
			
			if (mc.currentScreen instanceof GuiChest && !(mc.currentScreen instanceof GuiCrafting)) {
				
				if (checkChestNames.isEnabled() && !((GuiChest)mc.currentScreen).lowerChestInventory.getDisplayName().getUnformattedTextForChat().equals("Chest")) {
					return;
				}
				
				List<Slot> inventorySlots = Lists.<Slot>newArrayList();
				inventorySlots.addAll(((GuiChest)mc.currentScreen).inventorySlots.inventorySlots);
				Collections.shuffle(inventorySlots);
				
				if (InventoryUtils.isInventoryFull()) {
					mc.thePlayer.closeScreen();
					return;
				}
				
				for (Slot slot : inventorySlots) {
					ItemStack item = slot.getStack();
					if (item != null && slot.slotNumber < ((GuiChest)mc.currentScreen).inventorySlots.inventorySlots.size() - 36) {
						InventoryUtils.shiftClick(slot.slotNumber, ((GuiChest)mc.currentScreen).inventorySlots.windowId);
						return;
					}
					
				}
				
				mc.thePlayer.closeScreen();
				
			}
			
		}
		
	}

}
