package xyz.vergoclient.modules.impl.movement;

import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventSlowdown;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.util.MovementUtils;

public class NoSlow extends Module implements OnEventInterface {

	public NoSlow() {
		super("NoSlow", Category.MOVEMENT);
	}

	@Override
	public void onEvent(Event e) {

		if(MovementUtils.isMoving()) {
			if (mc.gameSettings.keyBindSprint.isPressed()) {
				mc.thePlayer.setSprinting(true);
			}

			if(e instanceof EventSlowdown) {
				e.setCanceled(true);
			}
		}

		if(Vergo.config.modScaffold.isEnabled()) {
			return;
		}
		if(e instanceof EventSendPacket) {



			if (mc.thePlayer.isBlocking()) {
				EventSendPacket event = (EventSendPacket) e;
				if (event.packet instanceof C08PacketPlayerBlockPlacement) {
					mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
				}
			}
		}
	}

}