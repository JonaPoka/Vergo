package xyz.vergoclient.modules.impl.miscellaneous;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.*;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.network.Packet;

public class ModDisabler extends Module implements OnEventInterface {

	public ModDisabler() {
		super("Disabler", Category.MISCELLANEOUS);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Test", "Test");
	
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();

	@Override
	public void loadSettings() {


		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Test"));
		addSettings(mode);
		
	}
	
	@Override
	public void onEnable() {

	}
	
	@Override
	public void onDisable() {


	}

	@Override
	public void onEvent(Event e) {
		if (mode.is("Test")) {
			if (mc.thePlayer.ticksExisted % 69 == 0) {
				// credit to spec da savag yt
				// credit him if u use it
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0CPacketInput());
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.getEntityId()));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(0, -91, true));
			}
		}
	}
	
}
