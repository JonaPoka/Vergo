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

	public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel");

	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Hypixel"));
		addSettings(mode);
	}

	@Override
	public void onEnable() {
		//ChatUtils.addChatMessage("Module Detected. Proceed with caution.");
	}

	@Override
	public void onEvent(Event e) {
		if (mode.is("Hypixel"))
			onNoFallHypixelEvent(e);
	}

	private void onNoFallHypixelEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo("Hypixel");
		}

		else if (e instanceof EventUpdate && e.isPre()) {
			if (mc.thePlayer.fallDistance > 3) {
				mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
			}
			if (mc.thePlayer.fallDistance > 4) {
				mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ);
			}
		}
	}
}
