package xyz.vergoclient.modules.impl.miscellaneous;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C18PacketSpectate;

public class ModDisabler extends Module implements OnEventInterface {

	public ModDisabler() {
		super("Disabler", Category.MISCELLANEOUS);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Brokenlens", "Brokenlens", "Cancel C00 & C0F", "Mineplex Combat", "Hypixel timer blink", "Test");
	
	public static transient CopyOnWriteArrayList<Packet> testPackets = new CopyOnWriteArrayList<Packet>();
	public static transient TimerUtil testTimer = new TimerUtil();
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Brokenlens", "Cancel C00 & C0F", "Mineplex Combat", "Hypixel timer blink", "Test"));
		
		addSettings(mode);
		
	}
	
	@Override
	public void onEnable() {
		testPackets.clear();
		testTimer.reset();
	}
	
	@Override
	public void onDisable() {
		if (mode.is("Hypixel timer blink") && Vergo.config.modBlink.isEnabled()) {
			Vergo.config.modBlink.toggle();
		}
		testPackets.forEach(p -> {
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
		});
		testPackets.clear();
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre() && mode.is("Brokenlens"))
			brokenLensDisabler((EventTick) e);
		
		if (e instanceof EventSendPacket && e.isPre() && mode.is("Cancel C00 & C0F"))
			cancelC00AndC0F((EventSendPacket) e);
		
		if (mode.is("Test")) {
			
			if (e instanceof EventSendPacket && e.isPre()) {
				EventSendPacket event = (EventSendPacket)e;
				if (event.packet instanceof C0FPacketConfirmTransaction && mc.thePlayer.ticksExisted > 50) {
					e.setCanceled(true);
				}
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C18PacketSpectate(UUID.randomUUID()));
			}
			else if (e instanceof EventTick && e.isPre() && testTimer.hasTimeElapsed(150, true)) {
//				ChatUtils.addChatMessage("Lagged - " + testPackets.size());
				testPackets.forEach(p -> {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
				});
				testPackets.clear();
			}
			else if (e instanceof EventTick && e.isPre()) {
				setInfo("test - " + testPackets.size());
			}
			
		}
		else if (mode.is("Hypixel timer blink")) {
			if (e instanceof EventTick && e.isPre()) {
				setInfo("Hypixel timer blink");
				if (testTimer.hasTimeElapsed(230, true)) {
					Vergo.config.modBlink.toggle();
				}
			}
			else if (e instanceof EventSendPacket && e.isPre()) {
				if (((EventSendPacket)e).packet instanceof C00PacketKeepAlive) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(((EventSendPacket)e).packet);
					e.setCanceled(true);
				}
			}
		}
	}
	
	private void cancelC00AndC0F(EventSendPacket e){
		setInfo("Cancel C00 & C0F");
		if (e.packet instanceof C00PacketKeepAlive || e.packet instanceof C0FPacketConfirmTransaction)
			e.setCanceled(true);
	}
	
	private void brokenLensDisabler(EventTick e) {
		if (mc.thePlayer == null)
			return;
		setInfo("Brokenlens");
		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + -12E-10, -12E-10, mc.thePlayer.posZ + -12E-10, true));
		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 12E-10, 12E-10, mc.thePlayer.posZ + 12E-10, true));
		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
//		if (mc.thePlayer.ticksExisted % 2 == 0) {
//			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ, new Random().nextBoolean()));
//		}else {
//			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, new Random().nextBoolean()));
//		}
	}
	
}
