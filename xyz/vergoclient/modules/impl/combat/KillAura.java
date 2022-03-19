package xyz.vergoclient.modules.impl.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.*;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.modules.impl.miscellaneous.AntiBot;
import xyz.vergoclient.modules.impl.miscellaneous.Teams;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.datas.DataDouble3;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class KillAura extends Module implements OnSettingChangeInterface, OnEventInterface {

	// Timers
	Timer blockTimer;

	public KillAura() {
		super("KillAura", Category.COMBAT);
		this.blockTimer = new Timer();
	}

	// Settings
	public NumberSetting rangeSetting = new NumberSetting("Range", 3.8, 0.5, 6, 0.1),
			minApsSetting = new NumberSetting("Min", 10, 0.1, 20, 0.1),
			maxApsSetting = new NumberSetting("Max", 14, 0.1, 20, 0.1),
			//combatPacketsPerHit = new NumberSetting("Combat packets per hit", 1, 1, 10, 1),
			tickSwitchTimeSetting = new NumberSetting("Switch Timer", 20, 1, 200, 1);
			//almostLegitMovementSensitivity = new NumberSetting("Movement sensitivity", 0.25, 0.05, 1, 0.05),
			//almostLegitHitboxExpand = new NumberSetting("Hitbox expand", 0.25, 0.01, 0.5, 0.01),
			//almostLegitSnapBack = new NumberSetting("Snap back", 0.5, 0.1, 1, 0.05),
			//minRotationSpeed = new NumberSetting("Min Rot Speed", 20, 0, 100, 1),
			//maxRotationSpeed = new NumberSetting("Max Rot Speed", 30, 1, 100, 1),
			//minRotationBezierCurveSpeed = new NumberSetting("Min Rot Speed", 0.05, 0, 1, 0.01),
			//maxRotationBezierCurveSpeed = new NumberSetting("Max Rot Speed", 0.07, 0.01, 1, 0.01);
	public BooleanSetting targetPlayersSetting = new BooleanSetting("Players", true),
			targetAnimalsSetting = new BooleanSetting("Animals", false),
			targetMobsSetting = new BooleanSetting("Mobs", false),
			targetOtherSetting = new BooleanSetting("Others", false),
			rayTraceCheck = new BooleanSetting("Visible Only", false),
			//viewRotations = new BooleanSetting("View rotations", false),
			//movementMatchRotation = new BooleanSetting("Movement Match Rotation", false),
			//visualizeRange = new BooleanSetting("Visualize Range", false),
			visualizeTargetCircle = new BooleanSetting("Visualize Target", true);
			//criticals = new BooleanSetting("Criticals", true);
	public ModeSetting targetSelectionSetting = new ModeSetting("Attack Mode", "Switch", "Switch", "Single"),
			targetSortingSetting = new ModeSetting("Priority", "Health", "Health", "Distance"),
			rotationSetting = new ModeSetting("Rotations", "Lock", /*"Smooth",*/ "Lock"/*, "Spin", "None", "Almost legit", "Bezier Curve"*/),
			autoblockSetting = new ModeSetting("Block Mode", "Hypixel");

	@Override
	public void loadSettings() {

		autoblockSetting.modes.clear();
		autoblockSetting.modes.addAll(Arrays.asList("None", "Hypixel"));

		addSettings(rangeSetting, minApsSetting, maxApsSetting, /*combatPacketsPerHit,*/ targetPlayersSetting, targetAnimalsSetting,
				targetMobsSetting, targetOtherSetting, rayTraceCheck, targetSelectionSetting, targetSortingSetting,
				/*rotationSetting,*/ autoblockSetting, visualizeTargetCircle /*visualizeRange, criticals*/);

	}

	// Vars used in the module
	private transient static double currentAps = 10, tickSwitch = 0;
	public transient static EntityLivingBase target, lastTarget = null;
	private transient static TimerUtil apsTimer = new TimerUtil();
	private transient static boolean isBlocking = false;

	public TimerUtil critTimer = new TimerUtil();

	@Override
	public void onSettingChange(SettingChangeEvent e) {

		if (e.setting == minApsSetting) {
			if (currentAps < minApsSetting.getValueAsDouble())
				currentAps = minApsSetting.getValueAsDouble();
			if (maxApsSetting.getValueAsDouble() < minApsSetting.getValueAsDouble()) {
				maxApsSetting.setValue(minApsSetting.getValueAsDouble());
			}
		} else if (e.setting == maxApsSetting) {
			if (currentAps > maxApsSetting.getValueAsDouble())
				currentAps = maxApsSetting.getValueAsDouble();
			if (minApsSetting.getValueAsDouble() > maxApsSetting.getValueAsDouble()) {
				minApsSetting.setValue(maxApsSetting.getValueAsDouble());
			}
		} else if (e.setting == targetSelectionSetting) {
			if (targetSelectionSetting.is("Switch")) {
				if (!settings.contains(tickSwitchTimeSetting)) {
					tickSwitch = tickSwitchTimeSetting.getValueAsDouble();
					settings.add(tickSwitchTimeSetting);
				}
			} else {
				if (settings.contains(tickSwitchTimeSetting)) {
					settings.remove(tickSwitchTimeSetting);
				}
			}
		} else if (e.setting == tickSwitchTimeSetting) {
			if (tickSwitch > tickSwitchTimeSetting.getValueAsDouble())
				tickSwitch = tickSwitchTimeSetting.getValueAsDouble();
		}

	}

	@Override
	public void onEnable() {
		this.critTimer.reset();

		lastYaw = mc.thePlayer.rotationYaw;
		lastPitch = mc.thePlayer.rotationPitch;
		legitStartingYaw = mc.thePlayer.rotationYaw;
		legitStartingPitch = mc.thePlayer.rotationPitch;
		target = null;

		this.blockTimer.reset();
	}

	@Override
	public void onDisable() {
		target = null;
		block(false);
		mc.thePlayer.clearItemInUse();
		mc.gameSettings.keyBindUseItem.pressed = false;
	}

	public float animation;

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventRender3D && e.isPre()) {

			if (visualizeTargetCircle.isEnabled() && target != null) {

				final float timer = mc.timer.renderPartialTicks;
				final double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * timer;
				final double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * timer;
				final double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * timer;

				// Animation up and down. Don't change... PLEASE

				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				Vec3 lastLine = null;

				for (short i = 0; i <= 360 * 2; i++) {

					float f = (float) ((target.rotationYaw + i) * (Math.PI / 180));
					double x2 = x, z2 = z;
					x2 -= (double) (MathHelper.sin(f) * 0.7) * -1;
					z2 += (double) (MathHelper.cos(f) * 0.7) * -1;

					if (lastLine == null) {
						lastLine = new Vec3(x2, y, z2);
						continue;
					}

					if (i != 0) {
						GL11.glColor4f(0.7f, 0.52f, 1.0f, 0.05f);
						RenderUtils.drawLine(lastLine.xCoord, y, lastLine.zCoord, x2, y + 2, z2);

						/*GL11.glColor4f(0.7f, 0.52f, 1.0f, 0.25f);
						RenderUtils.drawLine(lastLine.xCoord, y + 0.5, lastLine.zCoord, x2, y + 0.2, z2);
						GL11.glColor4f(0.7f, 0.52f, 1.0f, 0.2f);
						RenderUtils.drawLine(lastLine.xCoord, y + 0.2, lastLine.zCoord, x2, y + 0.1, z2);*/
					}

					lastLine.xCoord = x2;
					lastLine.zCoord = z2;

				}
				GlStateManager.popMatrix();
				GlStateManager.popAttrib();
			}
		}
		if (e instanceof EventUpdate && e.isPre()) {
			EventUpdate event = (EventUpdate) e;

			if (rotationSetting.is("Lock")) {
				setInfo("WatchDog");
			}

			// Sets the target
			setTarget();
			//mc.leftClickCounter = 0;

			// If there is no target return
			if (target == null) {
				if (!mc.gameSettings.keyBindUseItem.isKeyDown())
					legitStartingYaw = mc.thePlayer.rotationYaw;
					legitStartingPitch = mc.thePlayer.rotationPitch;
					mc.thePlayer.clearItemInUse();
				return;
			}

			// Autoblock
			if ((!autoblockSetting.is("Legit") || !apsTimer.hasTimeElapsed((long) (1000 / currentAps), false)))
				block(true);

			// Sets the rotations
			boolean shouldHit = setRotations(event);
			RenderUtils.setCustomYaw(event.getYaw());
			RenderUtils.setCustomPitch(event.getPitch());

			// So rotations don't ban
			lastTarget = target;

			// If the rotations haven't looked at the player yet then don't send a damage
			// packet
			if (!shouldHit)
				return;

			// Hits at the aps that the user set
			if (apsTimer.hasTimeElapsed((long) (1000 / currentAps), true)) {

				// For the randomness in the aps
				if (minApsSetting.getValueAsDouble() != maxApsSetting.getValueAsDouble()) {
					currentAps = RandomUtils.nextDouble(minApsSetting.getValueAsDouble(),
							maxApsSetting.getValueAsDouble());
				} else {
					currentAps = maxApsSetting.getValueAsDouble();
				}

				// autoblock
				block(false);

				// Send hit packet
				for (int i = 0; i < 1; i++) {
					mc.thePlayer.swingItem();
					mc.leftClickCounter = 0;
					mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, Action.ATTACK));
				}

				// autoblock
				if (autoblockSetting.is("Hypixel"))
					if (target != null) {
						block(true);
					}

			}

		}
		if (e instanceof EventSendPacket && e.isPre()) {
			EventSendPacket event = (EventSendPacket) e;
			if (event.packet instanceof C09PacketHeldItemChange) {
				if (mc.thePlayer.getCurrentEquippedItem() == null
						|| !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
					isBlocking = false;
				}
			}
		}

	}

	// Returns false if the rotation is not finished
	public static transient float lastYaw = 0, lastPitch = 0;

	// For the almost legit rotation settings
	public static transient DataDouble3 legitOffsets = new DataDouble3(0, 0, 0),
			legitRotation = new DataDouble3(0, 0, 0);
	public static transient float legitStartingYaw = 0, legitStartingPitch = 0;

	private boolean setRotations(EventUpdate e) {

		if (rotationSetting.is("Lock")) {
			float[] rots = RotationUtils.getRotationToEntity(target);
			e.setYaw(rots[0]);
			e.setPitch(rots[1]);
			return true;
		}

		return false;

	}

	private void setTarget() {

		if (target != null && (target.isDead || target.getHealth() <= 0)) {
			target = null;
		}

		// For the switch mode to not switch targets unless necessary
		if (targetSelectionSetting.is("Switch")) {
			if (tickSwitch <= 0) {
				tickSwitch = tickSwitchTimeSetting.getValueAsDouble();
			} else if (target != null) {
				if (mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) <= rangeSetting
						.getValueAsDouble()) {
					tickSwitch--;
					return;
				} else {
					tickSwitch = tickSwitchTimeSetting.getValueAsDouble();
				}
			}
		}

		// Gets potential targets
		List<EntityLivingBase> potentialTargets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream()
				.filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
		potentialTargets = potentialTargets.stream()
				.filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < rangeSetting.getValueAsDouble()
						&& entity != mc.thePlayer)
				.collect(Collectors.toList());

		// Sorts them
		if (targetSortingSetting.is("Health")) {
			potentialTargets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase) entity).getHealth()));
		} else if (targetSortingSetting.is("Distance")) {
			potentialTargets.sort(Comparator
					.comparingDouble(entity -> ((EntityLivingBase) entity).getDistanceToEntity(mc.thePlayer)));
		}

		// Sorts them even more
		ArrayList<EntityLivingBase> targets = new ArrayList<>();
		for (EntityLivingBase e : potentialTargets) {

			if (rayTraceCheck.isEnabled()) {
				if (!e.canEntityBeSeen(mc.thePlayer)) {
					continue;
				}
			}

			if (e instanceof EntityPlayer && targetPlayersSetting.isEnabled())
				// Antibot
				if (Vergo.config.modAntibot.isDisabled() || !AntiBot.isBot(((EntityPlayer) e)))
					if (Vergo.config.modTeams.isDisabled() || !Teams.isOnSameTeam(e))
						// Add target
						targets.add(e);

			if (e instanceof EntityAnimal && targetAnimalsSetting.isEnabled())
				targets.add(e);

			if (e instanceof EntityMob && targetMobsSetting.isEnabled())
				targets.add(e);

			if (!(e instanceof EntityPlayer || e instanceof EntityAnimal || e instanceof EntityMob)
					&& targetOtherSetting.isEnabled())
				targets.add(e);

		}

		// If there are no targets that fit the specified criteria then set the target
		// to null
		if (targets.isEmpty()) {
			target = null;
			return;
		}

		// Get target
		target = targets.get(0);

		// For the almost legit rotations
		if (target != lastTarget) {
			legitStartingYaw = lastYaw;
			legitStartingPitch = lastPitch;
		}

	}

	private void block(boolean shouldBlock) {

		if (mc.thePlayer.getCurrentEquippedItem() == null
				|| !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)
				|| autoblockSetting.is("None"))
			return;

		mc.gameSettings.keyBindUseItem.pressed = true;

		// Start blocking
		if (shouldBlock && !isBlocking) {
			if (this.blockTimer.delay(1L)) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255,
						null, 0, 0, 0));
			}

			isBlocking = true;
		}

		// Stop blocking
		else if (!shouldBlock && isBlocking) {
			long random = RandomUtils.nextLong(2000, 2500);
			if (autoblockSetting.is("Hypixel")) {
				if (this.blockTimer.delay(random)) {
					BlockPos debug = new BlockPos(0, 0, 0);
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
							net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, debug,
							EnumFacing.DOWN));
					this.blockTimer.reset();
				}
			}

			isBlocking = false;
		}
	}

}