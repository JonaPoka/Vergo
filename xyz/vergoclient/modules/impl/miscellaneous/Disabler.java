package xyz.vergoclient.modules.impl.miscellaneous;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.*;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.*;
import net.minecraft.network.Packet;

public class Disabler extends Module implements OnEventInterface {

	Timer timer;

	public Disabler() {
		super("Disabler", Category.MISCELLANEOUS);
		this.timer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Disabler", "Watchdog", "Watchdog");
	
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();

	@Override
	public void loadSettings() {


		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Watchdog"));
		addSettings(mode);
		
	}
	
	@Override
	public void onEnable() {

		if(mode.is("Watchdog")) {
			setInfo("Watchdawg");
		}

	}
	
	@Override
	public void onDisable() {


	}

	@Override
	public void onEvent(Event e) {

		if(e instanceof EventSendPacket) {
			EventSendPacket event = (EventSendPacket) e;
			if (event.packet instanceof C03PacketPlayer || event.packet instanceof C03PacketPlayer.C04PacketPlayerPosition || event.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
				if (mc.thePlayer.ticksExisted < 50) {
					event.setCanceled(true);
				}
			}
		}

	}
	
}
