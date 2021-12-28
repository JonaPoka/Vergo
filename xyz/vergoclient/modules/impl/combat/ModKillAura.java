package xyz.vergoclient.modules.impl.combat;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.network.play.client.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventSendPacket;
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

public class ModKillAura extends Module implements OnSettingChangeInterface, OnEventInterface {

	public ModKillAura() {
		super("KillAura", Category.COMBAT);
	}

	// Timers
	public TimerUtil hypixelBlockingTimer = new TimerUtil();

	// Settings
	public NumberSetting rangeSetting = new NumberSetting("Range", 3.8, 0.5, 6, 0.1),
			minApsSetting = new NumberSetting("Min aps", 10, 0.1, 20, 0.1),
			maxApsSetting = new NumberSetting("Max aps", 14, 0.1, 20, 0.1),
					combatPacketsPerHit = new NumberSetting("Combat packets per hit", 1, 1, 10, 1),
			tickSwitchTimeSetting = new NumberSetting("Tick switch time", 20, 1, 200, 1),
			almostLegitMovementSensitivity = new NumberSetting("Movement sensitivity", 0.25, 0.05, 1, 0.05),
			almostLegitHitboxExpand = new NumberSetting("Hitbox expand", 0.25, 0.01, 0.5, 0.01),
			almostLegitSnapBack = new NumberSetting("Snap back", 0.5, 0.1, 1, 0.05),
			minRotationSpeed = new NumberSetting("Min rotation speed", 20, 0, 100, 1),
			maxRotationSpeed = new NumberSetting("Max rotation speed", 30, 1, 100, 1),
			minRotationBezierCurveSpeed = new NumberSetting("Min rotation speed", 0.05, 0, 1, 0.01),
			maxRotationBezierCurveSpeed = new NumberSetting("Max rotation speed", 0.07, 0.01, 1, 0.01);
	public BooleanSetting targetPlayersSetting = new BooleanSetting("Target players", true),
			targetAnimalsSetting = new BooleanSetting("Target animals", false),
			targetMobsSetting = new BooleanSetting("Target mobs", false),
			targetOtherSetting = new BooleanSetting("Target others", false),
			rayTraceCheck = new BooleanSetting("Raytrace", false),
			viewRotations = new BooleanSetting("View rotations", false),
			movementMatchRotation = new BooleanSetting("Movement Match Rotation", false),
			visualizeRange = new BooleanSetting("Visualize Range", false),
			visualizeTargetCircle = new BooleanSetting("Visualize Target", true);
	public ModeSetting targetSelectionSetting = new ModeSetting("Target selection", "Switch", "Switch", "Single"),
			targetSortingSetting = new ModeSetting("Target sorting", "Health", "Health", "Distance"),
			rotationSetting = new ModeSetting("Rotation", "Lock", "Smooth", "Lock", "Spin", "None", "Almost legit", "Bezier Curve"),
			autoblockSetting = new ModeSetting("AutoBlock", "Hypixel");

	@Override
	public void loadSettings() {

		rotationSetting.modes.clear();
		rotationSetting.modes.addAll(Arrays.asList("Smooth", "Lock", "Spin", "None", "Almost legit", "Bezier Curve"));

		autoblockSetting.modes.clear();
		autoblockSetting.modes.addAll(Arrays.asList("None",  "Hypixel"));

		addSettings(rangeSetting, minApsSetting, maxApsSetting, combatPacketsPerHit, targetPlayersSetting, targetAnimalsSetting,
				targetMobsSetting, targetOtherSetting, rayTraceCheck, targetSelectionSetting, targetSortingSetting,
				rotationSetting, autoblockSetting, visualizeTargetCircle, visualizeRange);

	}

	// Vars used in the module
	private transient static double currentAps = 10, tickSwitch = 0;
	public transient static EntityLivingBase target, lastTarget = null;
	private transient static TimerUtil apsTimer = new TimerUtil();
	private transient static boolean isBlocking = false;

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
		} else if (e.setting == rotationSetting) {
			if (rotationSetting.is("Almost legit")) {
				if (!this.settings.contains(almostLegitHitboxExpand)) {
					this.settings.add(almostLegitHitboxExpand);
				}
				if (!this.settings.contains(almostLegitMovementSensitivity)) {
					this.settings.add(almostLegitMovementSensitivity);
				}
				if (!this.settings.contains(almostLegitMovementSensitivity)) {
					this.settings.add(almostLegitMovementSensitivity);
				}
				if (!this.settings.contains(almostLegitSnapBack)) {
					this.settings.add(almostLegitSnapBack);
				}
				if (!this.settings.contains(minRotationSpeed)) {
					this.settings.add(minRotationSpeed);
				}
				if (!this.settings.contains(maxRotationSpeed)) {
					this.settings.add(maxRotationSpeed);
				}
			}
			else {
				if (this.settings.contains(almostLegitHitboxExpand)) {
					this.settings.remove(almostLegitHitboxExpand);
				}
				if (this.settings.contains(almostLegitMovementSensitivity)) {
					this.settings.remove(almostLegitMovementSensitivity);
				}
				if (this.settings.contains(almostLegitMovementSensitivity)) {
					this.settings.remove(almostLegitMovementSensitivity);
				}
				if (this.settings.contains(almostLegitSnapBack)) {
					this.settings.remove(almostLegitSnapBack);
				}
				if (this.settings.contains(minRotationSpeed)) {
					this.settings.remove(minRotationSpeed);
				}
				if (this.settings.contains(maxRotationSpeed)) {
					this.settings.remove(maxRotationSpeed);
				}
			}

			if (rotationSetting.is("Bezier Curve")) {
				if (!this.settings.contains(minRotationBezierCurveSpeed)) {
					this.settings.add(minRotationBezierCurveSpeed);
				}
				if (!this.settings.contains(maxRotationBezierCurveSpeed)) {
					this.settings.add(maxRotationBezierCurveSpeed);
				}
			}
			else if (rotationSetting.is("Smooth")) {
				if (!this.settings.contains(minRotationBezierCurveSpeed)) {
					this.settings.add(minRotationBezierCurveSpeed);
				}
				if (!this.settings.contains(maxRotationBezierCurveSpeed)) {
					this.settings.add(maxRotationBezierCurveSpeed);
				}
			}
			else {
				if (this.settings.contains(minRotationBezierCurveSpeed)) {
					this.settings.remove(minRotationBezierCurveSpeed);
				}
				if (this.settings.contains(maxRotationBezierCurveSpeed)) {
					this.settings.remove(maxRotationBezierCurveSpeed);
				}
			}

		} else if (e.setting == minRotationSpeed) {
			if (maxRotationSpeed.getValueAsDouble() < minRotationSpeed.getValueAsDouble()) {
				maxRotationSpeed.setValue(minRotationSpeed.getValueAsDouble());
			}
		} else if (e.setting == maxRotationSpeed) {
			if (minRotationSpeed.getValueAsDouble() > maxRotationSpeed.getValueAsDouble()) {
				minRotationSpeed.setValue(maxRotationSpeed.getValueAsDouble());
			}
		} else if (e.setting == minRotationBezierCurveSpeed) {
			if (maxRotationBezierCurveSpeed.getValueAsDouble() < minRotationBezierCurveSpeed.getValueAsDouble()) {
				maxRotationBezierCurveSpeed.setValue(minRotationBezierCurveSpeed.getValueAsDouble());
			}
		} else if (e.setting == maxRotationBezierCurveSpeed) {
			if (minRotationBezierCurveSpeed.getValueAsDouble() > maxRotationBezierCurveSpeed.getValueAsDouble()) {
				minRotationBezierCurveSpeed.setValue(maxRotationBezierCurveSpeed.getValueAsDouble());
			}
		}

	}

	@Override
	public void onEnable() {
		lastYaw = mc.thePlayer.rotationYaw;
		lastPitch = mc.thePlayer.rotationPitch;
		legitStartingYaw = mc.thePlayer.rotationYaw;
		legitStartingPitch = mc.thePlayer.rotationPitch;
		bezierCurveHelper.clearPoints();
		bezierCurveHelper.clearProgress();
		target = null;
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


			if(visualizeRange.isEnabled()) {
				final float timer = mc.timer.renderPartialTicks;
				final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * timer;
				final double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * timer;
				final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * timer;

				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				Vec3 lastLine = null;

				for (short i = 0; i <= 360 * 2; i++) {

					float f = (float) ((mc.thePlayer.rotationYaw + i) * (Math.PI / 180));
					double x2 = x, z2 = z;
					x2 -= (double) (MathHelper.sin(f) * rangeSetting.getValueAsDouble()) * -1;
					z2 += (double) (MathHelper.cos(f) * rangeSetting.getValueAsDouble()) * -1;

					if (lastLine == null) {
						lastLine = new Vec3(x2, y, z2);
						continue;
					}

					if (i != 0) {
						RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, x2, lastLine.yCoord, z2);
					}

					lastLine.xCoord = x2;
					lastLine.zCoord = z2;

				}
				GlStateManager.popMatrix();
				GlStateManager.popAttrib();
			}

			if(visualizeTargetCircle.isEnabled() && target != null) {

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

		if (e instanceof EventRender3D && e.isPre() && target != null && rotationSetting.is("Almost legit")) {
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			double x = legitRotation.x + target.posX;
			double y = legitRotation.y + target.posY;
			double z = legitRotation.z + target.posZ;
			GL11.glEnable(32823);
			GL11.glPolygonOffset(1.0f, -1100000.0f);
			RenderUtils.drawColoredBox(x - 0.1, y - 0.1, z - 0.1, x + 0.1, y + 0.1, z + 0.1, 0xffffffff);
			GL11.glDisable(32823);
			GL11.glPolygonOffset(1.0f, 1100000.0f);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}

		// To make the movement match the killaura rotation
		if (e instanceof EventMove && e.isPre() && movementMatchRotation.isEnabled() && target != null) {
			mc.gameSettings.keyBindLeft.pressed = false;
			mc.gameSettings.keyBindRight.pressed = false;
			EventMove event = (EventMove) e;
			double motionX = event.x;
			double motionZ = event.z;
			event.setSpeed(MovementUtils.getSpeed(), lastYaw);
			mc.thePlayer.motionX = motionX;
			mc.thePlayer.motionZ = motionZ;
		} else if (e instanceof EventUpdate && e.isPre()) {
			EventUpdate event = (EventUpdate) e;

			if(rotationSetting.is("Lock")) {
				setInfo("WatchDawg");
			}

			// Sets the target
			setTarget();
			//mc.leftClickCounter = 0;

			// If there is no target return
			if (target == null) {
				legitStartingYaw = mc.thePlayer.rotationYaw;
				legitStartingPitch = mc.thePlayer.rotationPitch;
				bezierCurveHelper.clearPoints();
				if (!mc.gameSettings.keyBindUseItem.isKeyDown())
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
			if (viewRotations.isEnabled()) {
				mc.thePlayer.rotationYaw = event.getYaw();
				mc.thePlayer.rotationPitch = event.getPitch();
			}

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
				for (int i = 0; i < combatPacketsPerHit.getValueAsInt(); i++) {
					mc.thePlayer.swingItem();
					mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, Action.ATTACK));
				}

				// autoblock
				if (autoblockSetting.is("Hypixel"))
					block(true);

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

	public static transient BezierCurveHelper bezierCurveHelper = new BezierCurveHelper();

	private boolean setRotations(EventUpdate e) {

		if (rotationSetting.is("Lock")) {
			float[] rots = RotationUtils.getRotations(target);
			e.setYaw(rots[0]);
			e.setPitch(rots[1]);
			return true;
		} else if (rotationSetting.is("Smooth")) {
			DataDouble3 targetPos = MiscellaneousUtils.getClosestPointFromBoundingBox(target.getEntityBoundingBox(), mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
			float[] targetRotation = RotationUtils.getRotationFromPosition(targetPos.x, targetPos.z, targetPos.y - 0.6);

			if (bezierCurveHelper.getNumberOfPoints() == 0) {
				bezierCurveHelper.clearProgress();
			}

			bezierCurveHelper.clearPoints();
			bezierCurveHelper.addPoints(new BezierCurveHelper.Point(legitStartingYaw % 360, legitStartingPitch), new BezierCurveHelper.Point(targetRotation[0] % 360, targetRotation[1]));
//			bezierCurveHelper.createThirdPoint();

			bezierCurveHelper.addProgress(RandomUtils.nextDouble(minRotationBezierCurveSpeed.getValueAsDouble(), maxRotationBezierCurveSpeed.getValueAsDouble()));
			BezierCurveHelper.Point bezierCurvePoint = bezierCurveHelper.getPoint();
			float[] bezierCurveRotations = new float[] {(float) bezierCurvePoint.x, (float) bezierCurvePoint.y};
			float oldYaw = lastYaw;
			e.setYaw(bezierCurveRotations[0]);
			e.setPitch(bezierCurveRotations[1]);
			lastYaw = bezierCurveRotations[0];
			lastPitch = bezierCurveRotations[1];

			if (Math.abs(bezierCurveRotations[0] - targetRotation[0]) % 360 > 10) {
				return false;
			}

			return true;

		} else if (rotationSetting.is("Spin")) {
			e.setYaw(System.currentTimeMillis() % 360);
//			e.setPitch((float) (Math.cos(mc.thePlayer.ticksExisted) * 30));
			e.setPitch(90);
			return true;
		} else if (rotationSetting.is("None")) {
			return true;
		} else if (rotationSetting.is("Almost legit")) {
			boolean shouldHit = true;
			double offsetX = legitOffsets.x;
			double offsetY = legitOffsets.y;
			double offsetZ = legitOffsets.z;
			double newOffsetX = mc.thePlayer.posX - target.posX;
			double newOffsetY = (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
					- (target.posY + target.getEyeHeight());
			double newOffsetZ = mc.thePlayer.posZ - target.posZ;
			double movementSensitivity = almostLegitMovementSensitivity.getValueAsDouble();
			double movementX = (offsetX - newOffsetX) * movementSensitivity;
			double movementY = (offsetY - newOffsetY) * movementSensitivity;
			double movementZ = (offsetZ - newOffsetZ) * movementSensitivity;
			if (true) {
				double x = legitRotation.x + target.posX;
				double y = legitRotation.y + target.posY;
				double z = legitRotation.z + target.posZ;
				if (target.getEntityBoundingBox().minX <= x && x <= target.getEntityBoundingBox().maxX
						&& target.getEntityBoundingBox().minY <= y && y <= target.getEntityBoundingBox().maxY
						&& target.getEntityBoundingBox().minZ <= z && z <= target.getEntityBoundingBox().maxZ) {
					legitRotation.x -= movementX;
					legitRotation.y -= movementY;
					legitRotation.z -= movementZ;
				} else {
//					legitRotation.x += movementX * 3;
//					legitRotation.y += movementY * 3;
//					legitRotation.z += movementZ * 3;
					double expand = almostLegitHitboxExpand.getValueAsDouble();
					if (target.getEntityBoundingBox().expand(expand, expand, expand).minX <= x
							&& x <= target.getEntityBoundingBox().expand(expand, expand, expand).maxX
							&& target.getEntityBoundingBox().expand(expand, expand, expand).minY <= y
							&& y <= target.getEntityBoundingBox().expand(expand, expand, expand).maxY
							&& target.getEntityBoundingBox().expand(expand, expand, expand).minZ <= z
							&& z <= target.getEntityBoundingBox().expand(expand, expand, expand).maxZ) {
						legitRotation.x += movementX;
						legitRotation.y += movementY;
						legitRotation.z += movementZ;
					} else {
						double snapBack = almostLegitSnapBack.getValueAsDouble();
						legitOffsets = new DataDouble3(legitOffsets.x * snapBack, legitOffsets.y * snapBack,
								legitOffsets.z * snapBack);
						legitRotation = new DataDouble3(legitRotation.x * snapBack, target.getEyeHeight() / 2,
								legitRotation.z * snapBack);
						shouldHit = false;
						legitStartingYaw = lastYaw;
						legitStartingPitch = lastPitch;
						bezierCurveHelper.clearPoints();
					}
				}
			}
			legitOffsets.x = newOffsetX;
			legitOffsets.y = newOffsetY;
			legitOffsets.z = newOffsetZ;
			double x = legitRotation.x;
			double y = legitRotation.y;
			double z = legitRotation.z;

			float[] finalRots = RotationUtils.getRotationFromPosition(x + target.posX, z + target.posZ,
					y + target.posY - 0.6);

			float[] smoothRots = new float[] { lastYaw, lastPitch };

			float rotationFactor = 1;
			try {
				rotationFactor = (float) RandomUtils.nextDouble(minRotationSpeed.getValueAsDouble(), maxRotationSpeed.getValueAsDouble());
			} catch (Exception e2) {

			}

//			ChatUtils.addChatMessage(RotationUtils.getRotationChange(lastYaw, finalRots[0]) + " " + RotationUtils.getRotationChange(lastPitch + 90, finalRots[1] + 90));

			float yawChange = RotationUtils.getRotationChange(lastYaw, finalRots[0]);
			float pitchChange = RotationUtils.getRotationChange(lastPitch, finalRots[1]);

			if (pitchChange < 0) {
				pitchChange *= -1;
			}

			if (pitchChange < 0) {
				pitchChange *= -1;
			}

			// Yaw takes control
			if (yawChange > pitchChange || true) {
				smoothRots[0] = RotationUtils.updateRotation(lastYaw, finalRots[0], rotationFactor);
				float pitchMovementPercent = RotationUtils.getRotationPercent(legitStartingYaw, smoothRots[0],
						finalRots[0]);
				float pitchMovementFloat = RotationUtils.updateRotationWithPercent(lastPitch, finalRots[1],
						pitchMovementPercent);
				smoothRots[1] = pitchMovementFloat;
			}

			// Pitch takes control
			else {
				smoothRots[1] = RotationUtils.updateRotation(lastPitch, finalRots[1], rotationFactor);
				float yawMovementPercent = RotationUtils.getRotationPercent(legitStartingPitch, smoothRots[1],
						finalRots[1]);
				float yawMovementFloat = RotationUtils.updateRotationWithPercent(lastYaw, finalRots[0],
						yawMovementPercent);
				smoothRots[0] = yawMovementFloat;
			}

			lastYaw = smoothRots[0];
			lastPitch = smoothRots[1];

			e.setYaw(smoothRots[0]);
			e.setPitch(smoothRots[1]);

			if (((finalRots[0] - lastYaw) * (finalRots[0] - lastYaw < 0 ? -1 : 1)) % 360 > 10) {
				shouldHit = false;
			}

			x += target.posX;
			y += target.posY;
			z += target.posZ;

			return shouldHit;
		}
		else if (rotationSetting.is("Bezier Curve")) {

			DataDouble3 targetPos = MiscellaneousUtils.getClosestPointFromBoundingBox(target.getEntityBoundingBox().expand(-0.1), mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
			float[] targetRotation = RotationUtils.getRotationFromPosition(targetPos.x, targetPos.z, targetPos.y - 0.6);

			if (bezierCurveHelper.getNumberOfPoints() == 0) {
				bezierCurveHelper.clearProgress();
			}

			if (bezierCurveHelper.getProgress() == 1) {
				legitStartingYaw = lastYaw;
				legitStartingPitch = lastPitch;
				bezierCurveHelper.clearPoints();
				bezierCurveHelper.addPoints(new BezierCurveHelper.Point(legitStartingYaw % 360, legitStartingPitch), new BezierCurveHelper.Point(targetRotation[0] % 360, targetRotation[1]));
				bezierCurveHelper.createThirdPoint();
			}

			if (bezierCurveHelper.points.isEmpty()) {
				bezierCurveHelper.clearPoints();
				bezierCurveHelper.addPoints(new BezierCurveHelper.Point(legitStartingYaw % 360, legitStartingPitch), new BezierCurveHelper.Point(targetRotation[0] % 360, targetRotation[1]));
				bezierCurveHelper.createThirdPoint();
			}

			bezierCurveHelper.addProgress(RandomUtils.nextDouble(minRotationBezierCurveSpeed.getValueAsDouble(), maxRotationBezierCurveSpeed.getValueAsDouble()));
			BezierCurveHelper.Point bezierCurvePoint = bezierCurveHelper.getPoint();
			float[] bezierCurveRotations = new float[] {(float) bezierCurvePoint.x, (float) bezierCurvePoint.y};
			float oldYaw = lastYaw;
			e.setYaw(bezierCurveRotations[0]);
			e.setPitch(bezierCurveRotations[1]);
			lastYaw = bezierCurveRotations[0];
			lastPitch = bezierCurveRotations[1];

			if (!bezierCurveHelper.points.isEmpty()) {
				bezierCurveHelper.points.add(bezierCurveHelper.points.size() - 1, new BezierCurveHelper.Point(lastYaw % 360, lastPitch));
//				bezierCurveHelper.addPoints(new BezierCurveHelper.Point(lastYaw % 360, lastPitch));
			}

			if (Math.abs(bezierCurveRotations[0] - targetRotation[0]) % 360 > 10) {
				return false;
			}

			return true;

		}

		return false;

	}

	private void setTarget() {

		if (target != null && (target.isDead || target.getHealth() <= 0) && !ServerUtils.isOnMineplex()) {
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
						&& entity != mc.thePlayer
						&& (ServerUtils.isOnMineplex() ? true : (!entity.isDead && entity.getHealth() > 0)))
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
				if (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) e)))
					if (Vergo.config.modTeams.isDisabled() || !ModTeams.isOnSameTeam(e))
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
			bezierCurveHelper.clearPoints();
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

			mc.gameSettings.keyBindUseItem.pressed = true;

			isBlocking = true;
		}

		// Stop blocking
		else if (!shouldBlock && isBlocking) {
			if (autoblockSetting.is("Hypixel")) {
				mc.gameSettings.keyBindUseItem.pressed = false;
			}

			isBlocking = false;
		}
	}


		public static BlockPos getHypixelBlockpos(String str){
		int val = 89;
		if(str != null && str.length() > 1){
			char[] chs = str.toCharArray();

			int lenght = chs.length;
			for(int i = 0; i < lenght; i++)
				val += (int)chs[i] * str.length()* str.length() + (int)str.charAt(0) + (int)str.charAt(1);
			val/=str.length();
		}
		return new BlockPos(val, -val%255, val);
	}

}
