package xyz.vergoclient.modules.impl.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.RotationUtils;
import xyz.vergoclient.util.ServerUtils;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;

public class ModSumoBot extends Module implements OnEventInterface {

	public ModSumoBot() {
		super("SumoBot", Category.PLAYER);
	}

	public static TimerUtil cpsTimer = new TimerUtil();

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.ticksExisted > 60) {

			setInfo("Hypixel");

			// Places cage for reasons
			for (double x = -5; x < 5; x++)
				for (double y = -5; y < 5; y++)
					for (double z = -5; z < 5; z++) {
						BlockPos block = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y,
								mc.thePlayer.posZ + z);
						if (block.y < 64)
							continue;
						for (double i = block.y + 1; i > 0; i -= 0.5) {

							if (block.y < 64) {
								for (double addY = ((int) mc.thePlayer.posY) - 10; addY < ((int) mc.thePlayer.posY)
										+ 10; addY++)
									mc.theWorld.setBlockState(
											new BlockPos(mc.thePlayer.posX + x, addY, mc.thePlayer.posZ + z),
											addY > 64 ? Blocks.oak_fence.getDefaultState()
													: Blocks.barrier.getDefaultState());
								continue;
							}

							if (!mc.theWorld.isAirBlock(block)) {
								continue;
							}
							block = block.add(0, -0.5, 0);

						}
					}

			// Anti knockback
			if (MovementUtils.isOnGround(2) && !MovementUtils.isOnGround(1.1))
				MovementUtils.setMotion(-MovementUtils.getSpeed() * 0.7);

			if (MovementUtils.isOnGround(3) && !MovementUtils.isOnGround(2))
				MovementUtils.setMotion(0);

			// Look at opponent and walk foward
			mc.gameSettings.keyBindForward.pressed = false;
			mc.gameSettings.keyBindBack.pressed = false;
			EntityLivingBase target;

			List<EntityLivingBase> potentialTargets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream()
					.filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
			potentialTargets = potentialTargets.stream()
					.filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < 20 && entity != mc.thePlayer
							&& (ServerUtils.isOnMineplex() ? true : (!entity.isDead && entity.getHealth() > 0)))
					.collect(Collectors.toList());

			// Sorts them
			potentialTargets.sort(Comparator
					.comparingDouble(entity -> ((EntityLivingBase) entity).getDistanceToEntity(mc.thePlayer)));

			// Sorts them even more
			ArrayList<EntityLivingBase> targets = new ArrayList<>();
			for (EntityLivingBase f : potentialTargets) {

				if (f instanceof EntityPlayer)
					// Antibot
					if (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) f)))
						// Add target
						targets.add(f);

			}

			// If there are no targets that fit the specified criteria then set the target
			// to null
			if (targets.isEmpty()) {
				target = null;
				return;
			}

			// Get target
			target = targets.get(0);

			float[] rots = RotationUtils.getRotations(target);
			float fixedYaw = RotationUtils.updateRotation(mc.thePlayer.rotationYaw, rots[0], 360);
			mc.thePlayer.rotationYaw = fixedYaw;
			mc.thePlayer.rotationPitch = rots[1];

			// Don't get too close to them
			if (mc.thePlayer.getDistanceToEntity(target) > 3.11)
				mc.gameSettings.keyBindForward.pressed = true;

			if (mc.thePlayer.getDistanceToEntity(target) < 3.1 && MovementUtils.isOnGround(0.0001))
				mc.gameSettings.keyBindBack.pressed = true;

			// Hit player
			if (mc.thePlayer.getDistanceToEntity(target) <= 3.65 && cpsTimer.hasTimeElapsed(1000 / 14, true)) {
				mc.thePlayer.swingItem();
				mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, Action.ATTACK));
			}

		} else if (e instanceof EventReceivePacket) {

			if (e.isPre()) {

				EventReceivePacket event = (EventReceivePacket) e;

				if (event.packet instanceof S02PacketChat) {

					S02PacketChat packet = (S02PacketChat) event.packet;

					String message = packet.getChatComponent().getUnformattedText();

					if (message == null) {
						return;
					}

					// if((message.toLowerCase().contains("you won! want to play again? click
					// here!") || message.toLowerCase().contains("coins! (win)")) ||
					// message.toLowerCase().contains("experience! (win)")){
					if ((message.toLowerCase().contains("queued! use the bed to return to lobby!"))
							|| message.toLowerCase().contains("coins! (win)")
							|| message.toLowerCase().contains("experience! (win)")
							|| message.toLowerCase().contains("you won! want to play again? click here!")
							|| message.toLowerCase().contains("you died! want to play again? click here!")) {
						mc.getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C01PacketChatMessage("/play duels_sumo_duel"));
					}
					return;
				}

			}

		}

	}

}
