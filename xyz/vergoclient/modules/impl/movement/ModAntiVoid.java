package xyz.vergoclient.modules.impl.movement;

import java.util.concurrent.CopyOnWriteArrayList;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MiscellaneousUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.TimerUtil;
import xyz.vergoclient.util.datas.DataDouble6;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class ModAntiVoid extends Module implements OnEventInterface {

	public ModAntiVoid() {
		super("Antivoid", Category.MOVEMENT);
	}
	
	public void onEnable() {
		lastOnground = null;
		antivoid = false;
		packets.clear();
	}
	
	public void onDisable() {
		packets.clear();
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Blink");
	public NumberSetting fallDistance = new NumberSetting("Fall distance", 10, 3, 30, 0.5);
	public BooleanSetting autoEnableScaffold = new BooleanSetting("Auto enable scaffold", false);
	
	@Override
	public void loadSettings() {
		addSettings(mode, fallDistance, autoEnableScaffold);
	}
	
	private static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	private static transient DataDouble6 lastOnground = null;
	private static transient boolean antivoid = false, resumeCheckingAfterFall = false;
	private static transient TimerUtil noSpam = new TimerUtil();
	private static transient CopyOnWriteArrayList<BlockPos> blockposToReset = new CopyOnWriteArrayList<>();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}
		
		if (mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.allowFlying)
			return;
		
		if (mode.is("Blink")) {
			blinkAntivoidEvent(e);
			return;
		}
		
//		if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.fallDistance > fallDistance.getValueAsDouble() && mode.is("Hypixel") && Hummus.config.modFly.isDisabled()) {
//			if (isOverVoid() && !antivoid) {
//				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + fallDistance.getValueAsDouble() + 1, mc.thePlayer.posZ, true));
//				antivoid = true;
//				mc.thePlayer.onGround = false;
//				noSpam.reset();
//				ChatUtils.addChatMessage("Antivoid saved you");
//				if (autoEnableScaffold.isEnabled() && Hummus.config.modScaffold.isDisabled())
//					Hummus.config.modScaffold.toggle();
//			}
//		}
		if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.fallDistance > fallDistance.getValueAsDouble() && mode.is("Hypixel") && Vergo.config.modFly.isDisabled()) {
			if (isOverVoid() && !antivoid) {
//				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + fallDistance.getValueAsDouble() + 1, mc.thePlayer.posZ, true));
//				((EventUpdate)e).y += fallDistance.getValueAsDouble() + 1;
				((EventUpdate)e).x = mc.thePlayer.motionX;
				((EventUpdate)e).y = -999.0D;
				((EventUpdate)e).z = mc.thePlayer.motionZ;
				((EventUpdate)e).onGround = true;
				mc.thePlayer.prevRotationYaw = Float.MIN_VALUE;
				antivoid = true;
				mc.thePlayer.onGround = false;
				noSpam.reset();
				if (autoEnableScaffold.isEnabled() && Vergo.config.modScaffold.isDisabled())
					Vergo.config.modScaffold.toggle();
			}
		}
		else if (e instanceof EventUpdate && e.isPre() && mode.is("Hypixel")) {
			if (antivoid && MovementUtils.isOnGround(0.0001) && noSpam.hasTimeElapsed(500, false)) {
				antivoid = false;
			}
		}
		
	}
	
	private void blinkAntivoidEvent(Event e) {
		if (Vergo.config.modFly.isEnabled()) {
			resumeCheckingAfterFall = Vergo.config.modFly.isEnabled();
			lastOnground = null;
			return;
		}
		
		if (resumeCheckingAfterFall) {
			lastOnground = null;
			if (e instanceof EventUpdate && e.isPre()) {
				if (MovementUtils.isOnGround(0.0001)) {
					resumeCheckingAfterFall = false;
				}
			}
			return;
		}
		
//		if (ServerUtils.isOnHypixel()) {
//			NotificationManager.getNotificationManager().createNotification("Antivoid", "Antivoid does not bypass", true, 5000, Type.WARNING, Color.RED);
//			toggle();
//		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			setInfo("Blink");
			
			if (mc.thePlayer.ticksExisted < 5) {
				blockposToReset.clear();
			}
			
			if (!isOverVoid()) {
//				lastOnground = new DataDouble6(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, mc.thePlayer.motionX, mc.thePlayer.motionY + 0.1, mc.thePlayer.motionZ);
				lastOnground = new DataDouble6(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
				for (Packet p : packets) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
				}
				packets.clear();
				antivoid = false;
			}else {
				
				if (mc.thePlayer.fallDistance >= fallDistance.getValueAsDouble() && antivoid && noSpam.hasTimeElapsed(2000, true)) {
					packets.clear();
					
					// Removes ghost blocks
					for (BlockPos reset : blockposToReset)
						mc.theWorld.setBlockToAir(reset);
					
					blockposToReset.clear();
					
					try {
						MiscellaneousUtils.setPosAndMotionWithDataDouble6(lastOnground);
					} catch (Exception e2) {
						
					}
					antivoid = false;
					resumeCheckingAfterFall = true;
					if (autoEnableScaffold.isEnabled() && Vergo.config.modScaffold.isDisabled())
						Vergo.config.modScaffold.toggle();
				}
				
			}
			
		}
		else if (e instanceof EventSendPacket && e.isPre()) {
			
			if (isOverVoid()) {
				if (((EventSendPacket)e).packet instanceof C08PacketPlayerBlockPlacement) {
					C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement)((EventSendPacket)e).packet;
					blockposToReset.add(packet.position);
				}
				packets.add(((EventSendPacket)e).packet);
				e.setCanceled(true);
				antivoid = true;
			}
			
		}
	}
	
	private boolean isOverVoid() {
		
		boolean isOverVoid = true;
		BlockPos block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
		
		for (double i = mc.thePlayer.posY + 1; i > 0; i -= 0.5) {
			
			if (isOverVoid) {
				
				try {
					if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
						
						isOverVoid = false;
						break;
						
					}
				} catch (Exception e) {
					
				}
				
			}
			
			block = block.add(0, -1, 0);
			
		}
		
		for (double i = 0; i < 10; i += 0.1) {
			if (MovementUtils.isOnGround(i) && isOverVoid) {
				isOverVoid = false;
				break;
			}
		}
		
		return isOverVoid;
	}
	
}
