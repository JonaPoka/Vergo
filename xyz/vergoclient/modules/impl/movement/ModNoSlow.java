package xyz.vergoclient.modules.impl.movement;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomUtils;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventSlowdown;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.util.MovementUtils;

public class ModNoSlow extends Module implements OnEventInterface {

	public ModNoSlow() {
		super("NoSlow", Category.MOVEMENT);
	}

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventSlowdown && e.isPre()) {
			e.setCanceled(true);
		}

		if (mc.gameSettings.keyBindSprint.isPressed()) {
			mc.thePlayer.setSprinting(true);
		}

		if (e instanceof EventSendPacket && e.isPre()) {
			EventSendPacket event = (EventSendPacket) e;
			if (event.packet instanceof C08PacketPlayerBlockPlacement) {
				C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) event.packet;
				if (shouldNoSlow() && isAirClick(packet)) {
					//KillAura.blocking = true;
					event.packet = new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0);
				}
			}
			if (e instanceof EventMove) {
					if (mc.thePlayer.isBlocking() && MovementUtils.isMoving() && MovementUtils.isOnGround(0.42) && Vergo.config.modKillAura.target == null) {
						if (e.isPre()) {
							mc.getNetHandler().getNetworkManager().sendPacket((
									new C07PacketPlayerDigging(
											C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
											new BlockPos(RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)),
											EnumFacing.DOWN)));
						} else {
							mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
						}
					}
				}
			}
		}
		public boolean shouldNoSlow () {
			return mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemBow ||
					mc.thePlayer.getHeldItem().getItem() instanceof ItemFood);
		}
		public boolean isAirClick (C08PacketPlayerBlockPlacement packet){
			BlockPos pos = packet.getPosition();
			return pos.getX() == -1 && pos.getY() == -1 && pos.getZ() == -1;
		}

}