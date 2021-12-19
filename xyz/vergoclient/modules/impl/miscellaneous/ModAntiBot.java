package xyz.vergoclient.modules.impl.miscellaneous;

import java.util.concurrent.CopyOnWriteArrayList;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.ServerUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

public class ModAntiBot extends Module implements OnEventInterface {

	public ModAntiBot() {
		super("Antibot", Category.MISCELLANEOUS);
	}
	
	private static CopyOnWriteArrayList<PotentialBot> players = new CopyOnWriteArrayList<>();
	
	@Override
	public void onEvent(Event e) {
		if (ServerUtils.isOnHypixel())
			hypixelBotChecker(e);
//		else if (ServerUtils.isOnMineplex())
		else
			mineplexBotChecker(e);
	}
	
	public void mineplexBotChecker(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			setInfo("GWEN");
		}
		else if (e instanceof EventReceivePacket && e.isPre()) {
			EventReceivePacket event = (EventReceivePacket)e;
			if (event.packet instanceof S0CPacketSpawnPlayer && !mc.isSingleplayer()) {
				
				S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) event.packet;
				
				new Thread() {
					public void run() {
						try {
							Thread.sleep(50);
							Entity entity = mc.theWorld.getEntityByID(p.getEntityID());
							if (entity instanceof EntityPlayer) {
								
								// Makes the entity into an EntityPlayer object so I can handle it as the EntityPlayer
								EntityPlayer entityPlayer = (EntityPlayer)entity;
								double oldDistanceToPlayer = mc.thePlayer.getDistanceToEntity(entityPlayer);
								Thread.sleep(150);
								
								if (entityPlayer.isInvisible()) {
			                        mc.theWorld.removeEntity(entityPlayer);
									ChatUtils.addChatMessage("Removed a bot from your game (" + entityPlayer.getDisplayName().getFormattedText() + ")");
									return;
			                    }
								
								for (EntityPlayer player : mc.theWorld.playerEntities) {
									if (player.getName().equals(entityPlayer.getName()) && player != entityPlayer) {
										if (player.ticksExisted > entityPlayer.ticksExisted) {
					                        mc.theWorld.removeEntity(entityPlayer);
											ChatUtils.addChatMessage("Removed a bot from your game (" + entityPlayer.getDisplayName().getFormattedText() + ")");
											return;
										}
									}
								}
								
								double currentDistanceToPlayer = mc.thePlayer.getDistanceToEntity(entityPlayer);
								if (oldDistanceToPlayer - currentDistanceToPlayer > 20) {
			                        mc.theWorld.removeEntity(entityPlayer);
									ChatUtils.addChatMessage("Removed a bot from your game (" + entityPlayer.getDisplayName().getFormattedText() + ")");
									return;
								}
								
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}.start();
				
			}
		}
	}
	
	public void hypixelBotChecker(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			
			setInfo("WatchBot");
			
			if (mc.thePlayer.ticksExisted == 0) {
				players.clear();
			}
			
			for (PotentialBot potentialBot : players) {
				if (potentialBot.isBot) {
					new Thread(() -> {
						if (System.currentTimeMillis() >= potentialBot.nextCheck) {
							potentialBot.isBot = isBot(potentialBot);
							potentialBot.nextCheck = System.currentTimeMillis() + 333;
							if (potentialBot.isBot && potentialBot.player.isInvisible()) {
								players.remove(potentialBot);
							}
						}
					}).start();
				}
			}
			
		}
		else if (e instanceof EventReceivePacket && e.isPre()) {
			EventReceivePacket event = (EventReceivePacket)e;
			if (event.packet instanceof S0CPacketSpawnPlayer && !mc.isSingleplayer()) {
				
				S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) event.packet;
				Entity entity = mc.theWorld.getEntityByID(p.getEntityID());
				
				if (entity instanceof EntityPlayer) {
					
					EntityPlayer player = (EntityPlayer)entity;
					PotentialBot potentialBot = new PotentialBot();
					potentialBot.isBot = false;
					potentialBot.nextCheck = System.currentTimeMillis();
					potentialBot.player = player;
					players.add(potentialBot);
					
				}
				
			}
		}
	}
	
	private static boolean isBot(PotentialBot potentialBot) {
		boolean isBot = false;
		NetworkPlayerInfo npi = mc.getNetHandler().getPlayerInfo(potentialBot.player.getUniqueID());
		isBot = npi == null || npi.getGameProfile() == null || npi.responseTime != 1;
		return isBot;
	}
	
	public static boolean isBot(EntityPlayer player) {
		
		if (!ServerUtils.isOnHypixel())
			return false;
		
		for (PotentialBot bot : players) {
			if (bot.player == player) {
				return isBot(bot);
			}
		}
		
		// If the player is not being checked start a checker for them
		PotentialBot potentialBot = new PotentialBot();
		potentialBot.isBot = false;
		potentialBot.nextCheck = System.currentTimeMillis();
		potentialBot.player = player;
		players.add(potentialBot);
		return false;
		
	}
	
	private static class PotentialBot{
		public EntityPlayer player;
		public boolean isBot = true;
		public long nextCheck = System.currentTimeMillis() - 1000;
	}
	
}
