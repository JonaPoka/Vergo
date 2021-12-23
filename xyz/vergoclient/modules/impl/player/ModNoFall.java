package xyz.vergoclient.modules.impl.player;

import java.util.Arrays;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

public class ModNoFall extends Module implements OnEventInterface {

	public ModNoFall() {
		super("NoFall", Category.PLAYER);
	}

	public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Packet", "AAC");

	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Packet", "Packet", "AAC"));
		addSettings(mode);
	}

	@Override
	public void onEnable() {
		ChatUtils.addChatMessage("Module Detected. Proceed with caution.");
	}

	@Override
	public void onEvent(Event e) {

		if (mode.is("Packet"))
			onNoFallPacketEvent(e);
		else if (mode.is("Hypixel"))
			onNoFallHypixelEvent(e);
		else if (mode.is("AAC"))
			onAACEvent(e);
	}

	private void onNoFallPacketEvent(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			setInfo("Packet");
		}

		if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.fallDistance > 3) {
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
		}
	}

	private static transient double lastX = 0, lastY = 0, lastZ = 0;
	private static transient double lastFallDist = 0;
	private static transient boolean smooth = false;
	private static transient boolean canNegate = true;

	private void onNoFallHypixelEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo("Hypixel");
		}

		if (e instanceof EventReceivePacket && e.isPre()) {
			EventReceivePacket event = (EventReceivePacket) e;
			if (canNegate) {
				if (smooth && event.packet instanceof S08PacketPlayerPosLook) {
					S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) event.packet;
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
							s08.getX(), s08.getY(), s08.getZ(), s08.getYaw(), s08.getPitch(), false));
					s08.x = lastX;
					s08.y = lastY + 0.01;
					s08.z = lastZ;
					smooth = false;
					mc.thePlayer.setSprinting(false);
				}
			}
		} else if (e instanceof EventUpdate && e.isPre()) {
			EventUpdate event = (EventUpdate) e;
			double x = event.x;
			double y = event.y;
			double z = event.z;
			float yaw = event.getYaw();
			float pitch = event.getPitch();
			if (canNegate) {
				if (mc.thePlayer.onGround && lastFallDist > 3 && !mc.thePlayer.isPotionActive(Potion.jump)) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(
							new C03PacketPlayer.C04PacketPlayerPosition(event.x, event.y - 0.075, event.z, false));
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
							event.x, event.y - 0.08, event.z, yaw, pitch, false));
					mc.thePlayer.setSprinting(true);
					smooth = true;
					lastX = x;
					lastY = y;
					lastZ = z;
				}
				lastFallDist = mc.thePlayer.fallDistance;
			}
			if (mc.thePlayer.ticksExisted < 10) {
				canNegate = false;
			} else if (mc.thePlayer.ticksExisted > 10 && !canNegate && mc.thePlayer.onGround) {
				canNegate = true;
			}
		} else if (e instanceof EventSendPacket && e.isPre()) {
			EventSendPacket event = (EventSendPacket) e;
			if (canNegate) {
				if (mc.thePlayer.fallDistance > 2.5 && !MovementUtils.isOverVoid()
						&& !mc.thePlayer.isPotionActive(Potion.jump)) {
					if (event.packet instanceof C03PacketPlayer.C04PacketPlayerPosition.C06PacketPlayerPosLook) {
						C03PacketPlayer.C04PacketPlayerPosition.C06PacketPlayerPosLook c03c04c06 = (C03PacketPlayer.C04PacketPlayerPosition.C06PacketPlayerPosLook) event.packet;
						mc.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(c03c04c06.getPositionX(),
										c03c04c06.getPositionY(), c03c04c06.getPositionZ(), false));
						event.setCanceled(true);
					}
					if (MovementUtils.isMoving() && event.packet instanceof C0BPacketEntityAction) {
						event.setCanceled(true);
					}
				}
			}
		}

	}
	
	private void onAACEvent(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			setInfo("AAC (flags a lot)");
			if (mc.thePlayer.fallDistance >= 2) {
//				mc.thePlayer.motionY = -0.0784000015258789;
				if (mc.thePlayer.ticksExisted % 2 == 0) {
//					MovementUtils.forward(1);
//					mc.thePlayer.fallDistance -= 2;
				}
			}
		}
		else if (e instanceof EventReceivePacket && e.isPre()) {
			EventReceivePacket event = (EventReceivePacket)e;
			if (event.packet instanceof S08PacketPlayerPosLook && mc.thePlayer.fallDistance >= 2.9) {
//				mc.thePlayer.onGround = true;
//				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			}
		}
		else if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.fallDistance >= 2.9) {
			mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.posY - 0.0784000015258789, mc.thePlayer.lastTickPosZ);
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
			mc.gameSettings.keyBindForward.pressed = false;
			mc.gameSettings.keyBindBack.pressed = false;
			mc.gameSettings.keyBindLeft.pressed = false;
			mc.gameSettings.keyBindRight.pressed = false;
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
			mc.thePlayer.fallDistance = 0;
		}
		if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.fallDistance >= 2.9) {
			player.setFallSpeed(0);
			mc.thePlayer.onGround = true;
		}
		else if (e instanceof EventReceivePacket && e.isPre() && mc.thePlayer.fallDistance >= 2.9) {
			if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook) {
				mc.thePlayer.fallDistance = 0;
			}
		}
	}
	
}
