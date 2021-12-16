package xyz.vergoclient.modules.impl.combat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.modules.impl.miscellaneous.ModTeams;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.RotationUtils;
import xyz.vergoclient.util.ServerUtils;
import xyz.vergoclient.util.TimerUtil;
import xyz.vergoclient.util.pathfinding.AStarPathFinder;
import xyz.vergoclient.util.pathfinding.PathFinder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class ModTPAura extends Module implements OnEventInterface, OnSettingChangeInterface {

	public ModTPAura() {
		super("TpAura", Category.COMBAT);
	}
	
	public BooleanSetting throughWallsSetting = new BooleanSetting("Through walls", false),
			brokenlensSetting = new BooleanSetting("Brokenlens", false),
			runInThread = new BooleanSetting("Run in thread", true);
	public NumberSetting rangeSetting = new NumberSetting("Range", 50, 5, 150, 1),
			minApsSetting = new NumberSetting("Min aps", 10, 0.1, 20, 0.1),
			maxApsSetting = new NumberSetting("Max aps", 14, 0.1, 20, 0.1),
			targetCountSetting = new NumberSetting("Target count", 1, 1, 20, 1),
			distanceBetweenPacketsSetting = new NumberSetting("Distance between packets", 4, 0.1, 6, 0.1);
	public ModeSetting targetSortingSetting = new ModeSetting("Target sorting", "Health", "Health", "Distance");
	
	@Override
	public void loadSettings() {
		addSettings(rangeSetting, minApsSetting, maxApsSetting, targetCountSetting, distanceBetweenPacketsSetting, targetSortingSetting, throughWallsSetting, brokenlensSetting, runInThread);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		if (e.setting == minApsSetting) {
			if (currentAps < minApsSetting.getValueAsDouble())
				currentAps = minApsSetting.getValueAsDouble();
			if (maxApsSetting.getValueAsDouble() < minApsSetting.getValueAsDouble()) {
				maxApsSetting.setValue(minApsSetting.getValueAsDouble());
			}
		}
		else if (e.setting == maxApsSetting) {
			if (currentAps > maxApsSetting.getValueAsDouble())
				currentAps = maxApsSetting.getValueAsDouble();
			if (minApsSetting.getValueAsDouble() > maxApsSetting.getValueAsDouble()) {
				minApsSetting.setValue(maxApsSetting.getValueAsDouble());
			}
		}
	}
	
	private static CopyOnWriteArrayList<Object> pathfindersToRender = new CopyOnWriteArrayList<>();
	public static EntityLivingBase target = null;
	private static ArrayList<EntityLivingBase> targets = new ArrayList<>();
	private static double currentAps = 10;
	private static TimerUtil apsTimer = new TimerUtil();
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventReceivePacket && e.isPre() && ((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook && ServerUtils.isOnBrokenlens()) {
			e.setCanceled(true);
		}
		
		if (e instanceof EventRender3D && e.isPre())
			for (Object pathFinder : pathfindersToRender)
				if (pathFinder instanceof AStarPathFinder)
					((AStarPathFinder)pathFinder).renderPath();
				else if (pathFinder instanceof PathFinder)
					((PathFinder)pathFinder).renderPath();
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			setInfo(new DecimalFormat("0.00").format(currentAps) + " APS");
			
			// Sets the target
			setTargets();
			
			// If there are no targets then return
			if (targets.isEmpty())
				return;
			
			// Hits at the aps that the user set
			if (apsTimer.hasTimeElapsed((long) (1000 / currentAps), true)) {
				
				// For the randomness in the aps
				if (minApsSetting.getValueAsDouble() != maxApsSetting.getValueAsDouble()) {
					currentAps = RandomUtils.nextDouble(minApsSetting.getValueAsDouble(), maxApsSetting.getValueAsDouble());
				}else {
					currentAps = maxApsSetting.getValueAsDouble();
				}
				
				pathfindersToRender.clear();
				
				// Send packets
				
				Runnable hit = new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < targetCountSetting.getValueAsDouble(); i++) {
							
							if (targets.size() < i) {
								break;
							}
							
							try {
								if (mc.thePlayer.getDistance(targets.get(i).posX, targets.get(i).posY, targets.get(i).posZ) <= 5) {
									BlockPos pos1 = mc.thePlayer.getRealPosition();
									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(pos1.getX(), pos1.getY(), pos1.getZ(), RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[0], RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[1], true));
									mc.thePlayer.swingItem();
									mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(targets.get(i), Action.ATTACK));
									continue;
								}
							} catch (Exception e2) {
								e2.printStackTrace();
								continue;
							}
							
							try {
								if (!throughWallsSetting.isEnabled()) {
									AStarPathFinder pathFinder = new AStarPathFinder(250, throughWallsSetting.isEnabled());
									pathFinder.createPath(mc.thePlayer.getRealPosition(), targets.get(i).getPosition(), getDistanceNumber());
									BlockPos pos1 = pathFinder.path.get(pathFinder.path.size() - 1);
//									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[0], RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[1], true));
									for (BlockPos pos : pathFinder.path) {
										mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, true));
									}
//									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[0], RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[1], true));
									mc.thePlayer.swingItem();
									mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(targets.get(i), Action.ATTACK));
									if (!brokenlensSetting.isEnabled()) {
										Collections.reverse(pathFinder.path);
										for (BlockPos pos : pathFinder.path) {
											mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY(), pos.getZ(), true));
										}
										Collections.reverse(pathFinder.path);
									}
									pathFinder.spacePath(distanceBetweenPacketsSetting.getValueAsDouble());
									pathfindersToRender.add(pathFinder);
								}else {
									PathFinder pathFinder = new PathFinder(getDistanceNumber(), false, false);
									pathFinder.createPath(mc.thePlayer.getRealPosition(), targets.get(i).getPosition());
									BlockPos pos1 = pathFinder.path.get(pathFinder.path.size() - 1);
//									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[0], RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[1], true));
									for (BlockPos pos : pathFinder.path) {
										mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, true));
									}
//									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[0], RotationUtils.getRotationFromPosition(pos1.getX(), pos1.getZ(), pos1.getY(), targets.get(i).posX, targets.get(i).posZ, targets.get(i).posY)[1], true));
									mc.thePlayer.swingItem();
									mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(targets.get(i), Action.ATTACK));
									if (!brokenlensSetting.isEnabled()) {
										Collections.reverse(pathFinder.path);
										for (BlockPos pos : pathFinder.path) {
											mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, true));
										}
										Collections.reverse(pathFinder.path);
									}
									pathfindersToRender.add(pathFinder);
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							
						}
					}
				};
				
				if (runInThread.isEnabled()) {
					new Thread(hit).start();
				}else {
					hit.run();
				}
				
			}
			
		}
		else if (e instanceof EventReceivePacket && e.isPre() && (ServerUtils.isOnBrokenlens() || brokenlensSetting.isEnabled()) && mc.thePlayer.ticksExisted > 5) {
			if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook) {
				e.setCanceled(true);
			}
		}
		
	}
	
	public int getDistanceNumber() {
		return distanceBetweenPacketsSetting.getValueAsInt();
	}
	
	private void setTargets() {
		
		if (target != null && target.isDead) {
			target = null;
		}
		
		// Gets potential targets
		List<EntityLivingBase> potentialTargets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
		potentialTargets = potentialTargets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < rangeSetting.getValueAsDouble() && entity != mc.thePlayer && (ServerUtils.isOnMineplex() ? true : (!entity.isDead && entity.getHealth() > 0))).collect(Collectors.toList());
		
		// Sorts them
		if (targetSortingSetting.is("Health")) {
			potentialTargets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getHealth()));
		}
		else if (targetSortingSetting.is("Distance")) {
			potentialTargets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));
		}
		
		// Sorts them even more
		ArrayList<EntityLivingBase> targets = new ArrayList<>();
		for (EntityLivingBase e : potentialTargets) {
			if (e instanceof EntityPlayer && (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) e))))
				if (Vergo.config.modTeams.isDisabled() || !ModTeams.isOnSameTeam(e))
					// Add target
					targets.add(e);
		}
		
		// If there are no targets that fit the specified criteria then set the target to null
		if (targets.size() <= 0) {
			target = null;
			return;
		}
		
		// Get target
		target = targets.get(0);
		ModTPAura.targets = targets;
		
	}
	
}
