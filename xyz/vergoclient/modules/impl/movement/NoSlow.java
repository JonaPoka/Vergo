package xyz.vergoclient.modules.impl.movement;

import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventSlowdown;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;

public class NoSlow extends Module implements OnEventInterface {

	public NoSlow() {
		super("NoSlow", Category.MOVEMENT);
	}

	public static int ticks = 0;

	@Override
	public void onEvent(Event e) {

		if(Vergo.config.modScaffold.isEnabled() || Vergo.config.modSpeed.isEnabled()) {
			return;
		}

		if(e instanceof EventSlowdown) {
			if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking()) {
				if(!mc.thePlayer.isEating()) {
					ticks = mc.thePlayer.ticksExisted;
					e.setCanceled(true);
				} else {
					return;
				}
			}
		}

		if(e instanceof EventUpdate && e.isPost()) {

			if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() && !mc.thePlayer.isEating()) {
				mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255,
						mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
			} else {
				ticks = mc.thePlayer.ticksExisted;
			}

		}
	}

}