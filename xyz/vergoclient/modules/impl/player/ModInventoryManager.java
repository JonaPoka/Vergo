package xyz.vergoclient.modules.impl.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.InventoryUtils;
import xyz.vergoclient.util.MovementUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;

public class 	ModInventoryManager extends Module implements OnEventInterface{

	public ModInventoryManager() {
		super("InventoryManager", Category.PLAYER);
	}
	
	public static transient boolean hasMovedItem = false;
	
	public ModeSetting mode = new ModeSetting("Mode", "Open inv", "Silent", "Open inv");
	public NumberSetting tickDelay = new NumberSetting("Tick delay", 1, 1, 10, 1);
	
	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Silent", "Open inv"));
		addSettings(tickDelay, mode);
	}
	
	@Override
	public void onEnable() {
		hasMovedItem = false;
	}
	
	@Override
	public void onDisable() {
		hasMovedItem = false;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPost()) {
			setInfo("Hypixel");

			if (mc.thePlayer.ticksExisted % 60 == 0) {
//				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
			}
		}
		else if (e instanceof EventTick && e.isPre() && mc.thePlayer.ticksExisted % tickDelay.getValueAsInt() == 0) {
			
			if (!mode.is("Silent") || !MovementUtils.isMoving()) {
				moveOrDropLoop(e);
			}
			else if (mode.is("Silent") && hasMovedItem) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
				hasMovedItem = false;
			}
			
		}
		
	}
	
	private void moveOrDropLoop(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			
//			if ((mc.currentScreen == null || (!(mc.currentScreen instanceof GuiChest)) && !(mc.currentScreen instanceof GuiContainerCreative)) && mc.thePlayer.ticksExisted % 4 == 0) {
			if (mode.is("Silent") ? ((mc.currentScreen == null || (!(mc.currentScreen instanceof GuiChest)) && !(mc.currentScreen instanceof GuiContainerCreative))) : (mc.currentScreen != null && mc.currentScreen instanceof GuiInventory)) {
				
				List<Slot> inventorySlots = Lists.<Slot>newArrayList();
				inventorySlots.addAll(mc.thePlayer.inventoryContainer.inventorySlots);
				Collections.shuffle(inventorySlots);
				for (Slot slot : inventorySlots) {
					ItemStack item = slot.getStack();
					if (item != null) {
						int windowId = mc.thePlayer.inventoryContainer.windowId;
						if (moveOrDropItem(slot, windowId)) {
							if (mc.currentScreen == null) {
//								mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
							}
							if (mode.is("Silent")) {
								hasMovedItem = true;
							}
							return;
						}
						
					}
					
				}
				
				if (mode.is("Silent") && hasMovedItem) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
					hasMovedItem = false;
				}
				
			}
			else if (mode.is("Silent") && hasMovedItem && MovementUtils.isMoving()) {
//				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
				hasMovedItem = false;
			}
		}
	}
	
	private static int swordSlot = 0, pickaxeSlot = 1, axeSlot = 2;
	
	private static boolean moveOrDropItem(Slot slot, int windowId) {
		
		ItemStack stack = slot.getStack();
		
		if (stack == null || stack.getItem() == null)
			return false;
		
		if (stack.getItem() instanceof ItemSword) {
			if (InventoryUtils.getBestSwordSlot() != null && InventoryUtils.getBestSwordSlot() == slot) {
				if (slot.slotNumber == swordSlot)
					return false;
				if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
				}
				InventoryUtils.swap(slot.slotNumber, swordSlot, windowId);
			}else {
				if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
				}
				InventoryUtils.drop(slot.slotNumber, windowId);
			}
			return true;
		}
		else if (stack.getItem() instanceof ItemPickaxe) {
			if (InventoryUtils.getBestPickaxeSlot() != null && InventoryUtils.getBestPickaxeSlot() == slot) {
				if (slot.slotNumber == pickaxeSlot)
					return false;
				if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
				}
				InventoryUtils.swap(slot.slotNumber, pickaxeSlot, windowId);
			}else {
				if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
				}
				InventoryUtils.drop(slot.slotNumber, windowId);
			}
			return true;
		}
		else if (stack.getItem() instanceof ItemAxe) {
			if (InventoryUtils.getBestAxeSlot() != null && InventoryUtils.getBestAxeSlot() == slot) {
				if (slot.slotNumber == axeSlot)
					return false;
				if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
				}
				InventoryUtils.swap(slot.slotNumber, axeSlot, windowId);
			}else {
				if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
				}
				InventoryUtils.drop(slot.slotNumber, windowId);
			}
			return true;
		}
		else if (stack.getItem() instanceof ItemSpade || stack.getItem() instanceof ItemHoe) {
			if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
			}
			InventoryUtils.drop(slot.slotNumber, windowId);
			return true;
		}
		else if (stack.getItem() instanceof ItemArmor) {
			
			// Those slot numbers are for the currently equipped armor
			if (slot.slotNumber >= 5 && slot.slotNumber <= 8)
				return false;
			
			for (int type = 1; type < 5; type++) {
				ItemStack currentArmor = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
				if (ModAutoArmor.isBestArmor(stack, type) && (currentArmor == null || !ModAutoArmor.isBestArmor(currentArmor, type))) {
					return false;
				}
			}
			
			if (!hasMovedItem && Vergo.config.modInventoryManager.mode.is("Silent")) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.OPEN_INVENTORY));
			}
			InventoryUtils.drop(slot.slotNumber, windowId);
			return true;
		}
		
		return false;
		
	}
	
}
