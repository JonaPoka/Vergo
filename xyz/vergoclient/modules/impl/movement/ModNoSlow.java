package xyz.vergoclient.modules.impl.movement;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventSlowdown;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;

public class ModNoSlow extends Module implements OnEventInterface {

	public ModNoSlow() {
		super("NoSlow", Category.MOVEMENT);
	}

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventSlowdown && e.isPre()) {
			e.setCanceled(true);
		}

		if(mc.gameSettings.keyBindSprint.isPressed()) {
			mc.thePlayer.setSprinting(true);
		}

		if (e instanceof EventSendPacket && e.isPre()) {
			EventSendPacket event = (EventSendPacket)e;
			if (event.packet instanceof C08PacketPlayerBlockPlacement) {
				C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement)event.packet;
				if (packet.position == BlockPos.ORIGIN)
					packet.position = new BlockPos(-1, -1, -1);
			}
		}

	}

}