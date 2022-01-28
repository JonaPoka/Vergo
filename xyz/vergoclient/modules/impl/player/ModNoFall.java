package xyz.vergoclient.modules.impl.player;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
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
		if (mode.is("Hypixel")) {
			onNoFallHypixelEvent(e);
		}
	}


	private void onNoFallHypixelEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo("Hypixel");
		}

		else if (e instanceof EventUpdate && e.isPre()) {

			/* double x = mc.thePlayer.posX;
			double y = mc.thePlayer.posY;
			double z = mc.thePlayer.posZ;

			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 6D, z, false));

			for (int i =0; i < 15; ++i) {
				Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.5D, z, false));
				Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.2D, z, false));
			} */

			if (mc.thePlayer.fallDistance > 3 && !isOverVoid() && !mc.thePlayer.isSpectator()) {
				mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
			}
			if (mc.thePlayer.fallDistance > 4 && !isOverVoid() && !mc.thePlayer.isSpectator()) {
				mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ);
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
