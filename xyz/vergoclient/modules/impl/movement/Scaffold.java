package xyz.vergoclient.modules.impl.movement;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.*;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.modules.impl.combat.KillAura;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.animations.OpacityAnimation;
import xyz.vergoclient.util.animations.ScaleAnimation;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Scaffold extends Module implements OnEventInterface, OnSettingChangeInterface {

	Timer boostTiming;

	public Scaffold() {
		super("Scaffold", Category.MOVEMENT);
		this.boostTiming = new Timer();
	}

	@Override
	public void onSettingChange(SettingChangeEvent e) {

		if (e.setting.equals(keepYSetting)) {

			if (settings.contains(overrideKeepYSetting)) {
				settings.remove(overrideKeepYSetting);
			}

			if (keepYSetting.isEnabled()) {
				settings.add(overrideKeepYSetting);
			}

		}
		else if (e.setting == itemSwitchDelay) {

			if (settings.contains(itemSwitchTicks)) {
				settings.remove(itemSwitchTicks);
			}

			if (itemSwitchDelay.isEnabled()) {
				settings.add(itemSwitchTicks);
			}

		}

	}

	public NumberSetting forwardExtendSetting = new NumberSetting("", 0.0, 0.0, 5, 0.1),
			sidewaysExtendSetting = new NumberSetting("", 0.0, 0.0, 5, 0.1),
			maxBlocksPlacedPerTickSetting = new NumberSetting("", 1, 1, 25, 1),
			timerBoostSetting = new NumberSetting("", 1, 1, 2, 0.01),
			itemSwitchTicks = new NumberSetting("", 1, 1, 20, 1),
			blinkBlaster = new NumberSetting("", 230, 100, 1000, 10);
	public BooleanSetting keepYSetting = new BooleanSetting("", false),
			sprintSetting = new BooleanSetting("", false),
	//			towerSetting = new BooleanSetting("Tower", false),
	legitSetting = new BooleanSetting("", false),
			timerSlow = new BooleanSetting("", false),
			overrideKeepYSetting = new BooleanSetting("", false),
			viewRotations = new BooleanSetting("", false),
			fourDirectionalSpeed = new BooleanSetting("", false),
			oneDirectionalSpeed = new BooleanSetting("", false),
			toggleBlink = new BooleanSetting("", false),
			itemSwitchDelay = new BooleanSetting("", false),
			clientSideBlockPicker = new BooleanSetting("", false),
			hitVecFixer = new BooleanSetting("", true),
			noRotate = new BooleanSetting("", false),
			fakeMissPackets = new BooleanSetting("", false),
			placeBlockAsync = new BooleanSetting("", true),
			swaggyPaggyBoost = new BooleanSetting("", false);
	public ModeSetting rotationMode = new ModeSetting("", "Hypixel Slow", "Hypixel Slow"),
			towerMode = new ModeSetting("", "None", "None"/*, "NCP", "Test"*/);

	@Override
	public void loadSettings() {
		rotationMode.modes.clear();
		rotationMode.modes.addAll(Arrays.asList("Hypixel Slow"));
		forwardExtendSetting.minimum = 0;
		forwardExtendSetting.name = "";
		addSettings(/*forwardExtendSetting, swaggyPaggyBoost, sidewaysExtendSetting, maxBlocksPlacedPerTickSetting, blinkBlaster, timerBoostSetting,
				keepYSetting, sprintSetting, legitSetting, overrideKeepYSetting, viewRotations, rotationMode,
				fourDirectionalSpeed,, oneDirectionalSpeed, toggleBlink, itemSwitchDelay, clientSideBlockPicker,
				hitVecFixer, noRotate, fakeMissPackets, towerMode, placeBlockAsync,*/ timerSlow);
	}

	private double scafStartY = 0;
	private boolean flagFold = false;
	private boolean enabledBefore = true;

	private static transient BlockPos lastPlace = null;
	private static transient double keepPosY = 0;
	private static transient TimerUtil legitTimer = new TimerUtil();
	private static transient TimerUtil boostTimer = new TimerUtil();
	private static transient float oneDirectionalSpeedYaw = 0;
	private static transient int itemSwitchDelayTicks = 0;

	public void onEnable() {

		scafStartY = mc.thePlayer.posY;

		mc.thePlayer.setSprinting(false);

		if (mc.thePlayer.isSprinting()) {
			//mc.gameSettings.keyBindSprint.pressed = false;
			//mc.thePlayer.setSprinting(false);
		}

		boxOpacity.setOpacity(0);
		numOpacity.setOpacity(0);
		blockOpacity.setOpacity(0);


		this.boostTiming.reset();

		if(!rotationMode.is("Hypixel Slow")) {
			rotationMode.setMode("Hypixel Slow");
		}

		if(rotationMode.is("Hypixel Slow") || rotationMode.is("Hypixel Sprint")) {

			if (mc.thePlayer.isSprinting()) {
				//mc.gameSettings.keyBindSprint.pressed = false;
				//mc.thePlayer.setSprinting(false);
			}

			if (timerSlow.isEnabled()) {
				if (timerBoostSetting.getValueAsInt() != 1) {
					timerBoostSetting.value = 1;
				}
				mc.timer.timerSpeed = 0.8f;
			} else {
				mc.timer.timerSpeed = 1f;
			}
		}

		oneDirectionalSpeedYaw = mc.thePlayer.rotationYaw;
		itemSwitchDelayTicks = 0;

		if (Vergo.config.modBlink.isEnabled()) {
			Vergo.config.modBlink.toggle();
		}

		lastRandX = 0;
		lastRandZ = 0;

		if (lastBlockPos != null && lastFacing != null) {
			getRotations(lastBlockPos, lastFacing, false);
		}

		float[] rots = new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

		lastYaw = rots[0];
		lastPitch = rots[1];

//		lastYaw = mc.thePlayer.rotationYaw;
//		lastPitch = mc.thePlayer.rotationPitch;
		lastSlot = -1;
		keepPosY = mc.thePlayer.posY - 1;

		BlockPos block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);

		for (double i = mc.thePlayer.posY - 1; i > mc.thePlayer.posY - 5; i -= 0.5) {

			try {
				if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {

					keepPosY = block.getY();
					break;

				}
			} catch (Exception e) {

			}

			block = block.add(0, -1, 0);

		}

	}

	public void onDisable() {

		scaleAnim.setValues(20, 30);
		boxOpacity.setOpacity(0);
		numOpacity.setOpacity(0);
		blockOpacity.setOpacity(0);

		if(flagFold) {
			scafStartY = 0;
			mc.timer.timerSpeed = 1f;
			flagFold = false;
		}

		if (Vergo.config.modBlink.isEnabled()) {
			Vergo.config.modBlink.toggle();
		}

		if (lastSlot != mc.thePlayer.inventory.currentItem) {
			mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		}
		lastSlot = -1;
		mc.timer.timerSpeed = 1f;
		if (legitSetting.isEnabled() && mc.thePlayer.isSneaking())
			mc.gameSettings.keyBindSneak.pressed = false;
	}

	public static transient float lastYaw = 0, lastPitch = 0, lastRandX = 0, lastRandY = 0, lastRandZ = 0;
	public static transient BlockPos lastBlockPos = null;
	public static transient BlockPos offsets = BlockPos.ORIGIN;
	public static transient EnumFacing lastFacing = null;
	public static transient TimerUtil timer = new TimerUtil(), towerTimer = new TimerUtil(), blinkTimer = new TimerUtil();
	public static transient int lastSlot = -1;
	public static transient EnumFacing facing = null;
	public static transient long lastPlacedBlockTime = System.currentTimeMillis();


	private OpacityAnimation boxOpacity = new OpacityAnimation(0), blockOpacity = new OpacityAnimation(0), numOpacity = new OpacityAnimation(0);

	private ScaleAnimation scaleAnim = new ScaleAnimation(GuiScreen.width / 2 - 12f, GuiScreen.height / 2 + 18, 15, 20);

	public void onEvent(Event e) {

		if (e instanceof EventRenderGUI && e.isPre()) {

			int blocksLeft = 0;

			for (short g = 0; g < 9; g++) {

				if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
						&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBlock
						&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
						&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
						.getLocalizedName().toLowerCase().contains("chest")
						&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
						.getLocalizedName().toLowerCase().contains("table")) {
					blocksLeft += mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize;
				}

			}

			DecimalFormat decimalFormat = new DecimalFormat("###");
			String left = decimalFormat.format(blocksLeft);

			JelloFontRenderer jfr = FontUtil.comfortaaSmall;
			GlStateManager.pushMatrix();



			scaleAnim.interpolate(25, 30, 12);
			boxOpacity.interp(200, 12);
			numOpacity.interp(200, 12);
			blockOpacity.interp(200, 12);

			if (blocksLeft > 0) {

				RenderUtils.drawAlphaRoundedRect(GuiScreen.width / 2 - 12f, GuiScreen.height / 2 + 18, 25, 30, 3f, getColor(10, 10, 10, (int) boxOpacity.getOpacity()));
				if(blocksLeft <= 99 && blocksLeft > 9) {
					jfr.drawString("0" + left, GuiScreen.width / 2 - 5, GuiScreen.height / 2 + 40, getColor(255, 255, 255, (int) numOpacity.getOpacity()));
				} else if(blocksLeft <= 9) {
					jfr.drawString("00" + left, GuiScreen.width / 2 - 5, GuiScreen.height / 2 + 40, getColor(255, 255, 255, (int) numOpacity.getOpacity()));
				} else {
					jfr.drawString(left, GuiScreen.width / 2 - 5, GuiScreen.height / 2 + 40, getColor(255, 255, 255, (int) numOpacity.getOpacity()));
				}
				GlStateManager.resetColor();
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.color(1, 1, 1, blockOpacity.getOpacity());
				mc.getRenderItem().renderItemAndEffectIntoGUI(setStackToPlace(), GuiScreen.width / 2 - 7.5f, GuiScreen.height / 2 + 20);

			} else {
				RenderUtils.drawAlphaRoundedRect(GuiScreen.width / 2 - 12f, GuiScreen.height / 2 + 18, 25, 30, 3f, getColor(10, 10, 10, (int) boxOpacity.getOpacity()));
				RenderHelper.enableGUIStandardItemLighting();
				jfr.drawString("000", GuiScreen.width / 2 - 5, GuiScreen.height / 2 + 40, getColor(191, 9, 29, (int) numOpacity.getOpacity()));
			}

			GlStateManager.popMatrix();

		}

		if (e instanceof EventSneaking) {

			if (e.isPre()) {

				EventSneaking sneak = (EventSneaking) e;

				if (sneak.entity instanceof EntityPlayer && ((EntityPlayer)sneak.entity).isUser() && MovementUtils.isOnGround(0.0001, ((EntityPlayer)sneak.entity))) {
					if (rotationMode.is("Hypixel Slow")) {
//						sneak.hitboxExpand = -0.1;
					}
					sneak.sneaking = true;
				}else {
					sneak.sneaking = false;
				}
				sneak.offset = -1D;
				sneak.revertFlagAfter = !legitSetting.isEnabled();

			}

		}

		if (e instanceof EventReceivePacket && e.isPre()) {

			if (((EventReceivePacket)e).packet instanceof S2FPacketSetSlot) {
				lastSlot = ((S2FPacketSetSlot)((EventReceivePacket)e).packet).slot;
			}

		}

		if (e instanceof EventSendPacket & e.isPre()) {
			if (((EventSendPacket)e).packet instanceof C09PacketHeldItemChange) {
				lastSlot = ((C09PacketHeldItemChange)((EventSendPacket)e).packet).getSlotId();
			}

		}

		/*if (e instanceof EventRender3D && e.isPre()) {

			BlockPos below = lastPlace;

			if (below == null) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();

			GlStateManager.depthMask(false);

			GL11.glEnable(32823);
			GL11.glPolygonOffset(1.0f, -1100000.0f);

			//RenderUtils.drawColoredBox(below.getX() - 0.0001, below.getY() - 0.0001, below.getZ() - 0.0001, below.getX() + 1.0001, below.getY() + 1.0001, below.getZ() + 1.0001, 0x50C74D8E);

			RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX() + 1, below.getY(), below.getZ());
			RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ());
			RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX(), below.getY(), below.getZ() + 1);
			RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX(), below.getY() + 1, below.getZ() + 1);
			RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX(), below.getY() + 1, below.getZ());
			RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX(), below.getY() + 1, below.getZ());
			RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ());
			RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ());
			RenderUtils.drawLine(below.getX(), below.getY(), below.getZ() + 1, below.getX(), below.getY() + 1, below.getZ() + 1);
			RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ() + 1, below.getX(), below.getY() + 1, below.getZ() + 1);
			RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ() + 1, below.getX(), below.getY(), below.getZ() + 1);
			RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ() + 1, below.getX(), below.getY() + 1, below.getZ() + 1);
			RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ() + 1, below.getX() + 1, below.getY() + 1, below.getZ() + 1);
			RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ() + 1);
			RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ(), below.getX() + 1, below.getY(), below.getZ() + 1);

			GL11.glDisable(32823);
			GL11.glPolygonOffset(1.0f, 1100000.0f);

			GlStateManager.depthMask(true);

			GlStateManager.popAttrib();
			GlStateManager.popMatrix();

		}*/

		if (e instanceof EventMove && e.isPre() && fourDirectionalSpeed.isEnabled()) {

			mc.gameSettings.keyBindLeft.pressed = false;
			mc.gameSettings.keyBindRight.pressed = false;
			EventMove event = (EventMove) e;
			double motionX = event.x;
			double motionZ = event.z;
			MovementUtils.strafe();
			double realMotX = event.x * (event.x < 0 ? -1 : 1), realMotZ = event.z * (event.z < 0 ? -1 : 1);
			if (realMotX > realMotZ)
				event.z = 0;
			else
				event.x = 0;
			mc.thePlayer.motionX = motionX;
			mc.thePlayer.motionZ = motionZ;
		}
		if (e instanceof EventMove && e.isPre() && oneDirectionalSpeed.isEnabled()) {
			mc.gameSettings.keyBindLeft.pressed = false;
			mc.gameSettings.keyBindRight.pressed = false;
			EventMove event = (EventMove) e;
			double motionX = event.x;
			double motionZ = event.z;
			event.setSpeed(MovementUtils.getSpeed(), oneDirectionalSpeedYaw);
			mc.thePlayer.motionX = motionX;
			mc.thePlayer.motionZ = motionZ;
		}

		if (e instanceof EventReceivePacket && noRotate.isEnabled()) {
			if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook) {
				if (System.currentTimeMillis() < lastPlacedBlockTime + 750) {
					S08PacketPlayerPosLook packet = ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet);
					packet.setYaw(mc.thePlayer.rotationYaw);
					packet.setPitch(mc.thePlayer.rotationPitch);
				}
			}
		}

		if (e instanceof EventUpdate && e.isPre()) {
			for (int i = 0; i < maxBlocksPlacedPerTickSetting.getValueAsInt(); i++) {
				attemptBlockPlace(((EventUpdate)e));
			}
		}

	}

	private void renderItemStack(ItemStack stack, int x, int y) {
		GlStateManager.pushMatrix();

		GlStateManager.disableAlpha();
		this.mc.getRenderItem().zLevel = -150.0F;

		GlStateManager.disableCull();

		this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack, x, y);

		GlStateManager.enableCull();

		this.mc.getRenderItem().zLevel = 0;

		GlStateManager.disableBlend();

		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		GlStateManager.disableDepth();
		GlStateManager.disableLighting();

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();

		GlStateManager.scale(2.0F, 2.0F, 2.0F);

		GlStateManager.enableAlpha();

		GlStateManager.popMatrix();
	}

	public 	boolean wasOnBefore;

	private void insanityBoost(EventMove e) {
		if(swaggyPaggyBoost.isEnabled()) {

			if(fourDirectionalSpeed.isDisabled()) {
				fourDirectionalSpeed.setEnabled(true);
				wasOnBefore = false;
			} else {
				wasOnBefore = true;
			}

			/*if(this.boostTiming.delay(1L)) {
				mc.thePlayer.motionX = mc.thePlayer.motionX * 1.9;
				mc.thePlayer.motionZ = mc.thePlayer.motionZ * 1.9;
			}
			if(this.boostTiming.delay(300L)) {
				MovementUtils.setMotion(1.3f);
				if(!wasOnBefore) {
					fourDirectionalSpeed.setEnabled(false);
				}
			}
			if(this.boostTiming.delay(1000L)) {
				this.boostTiming.reset();
			}*/

		}
	}

	public float rotYawYaw;

	public void attemptBlockPlace(EventUpdate e) {
		EventUpdate event = (EventUpdate) e;

		if (!sprintSetting.isEnabled() && mc.thePlayer.isSprinting()) {

		} else if (sprintSetting.isEnabled() && !mc.thePlayer.isSprinting()) {
		}

		/*if (timerBoostSetting.getValueAsDouble() >= 1.000001)
			mc.timer.timerSpeed = MovementUtils.isMoving() ? ((float) timerBoostSetting.getValueAsDouble()) : 1f;*/

		/*if (rotationMode.is("Hypixel Slow")) {
			if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
				mc.thePlayer.motionX *= 0.6f;
				mc.thePlayer.motionZ *= 0.6f;
			}
		}*/

		// KeepY
		if (MovementUtils.isOnGround(0.00001)) {
			keepPosY = ((int) mc.thePlayer.posY) - 1;
		}

		// Keep rotations
		if (lastBlockPos != null && lastFacing != null) {
			if (mc.thePlayer.ticksExisted % 2 == 0) {
			}
			float[] keepRots = getBlockRotations(lastBlockPos, lastFacing);
			if (keepRots != null) {
				if (rotationMode.is("Hypixel Slow") || rotationMode.is("Hypixel Sprint")) {
					//lastYaw = keepRots[0];
					lastPitch = keepRots[1];
				} else if (rotationMode.is("AAC")) {

				} else {
					//lastYaw = keepRots[0];
					lastYaw = keepRots[0];
					lastPitch = keepRots[1];
				}
			}
		}

		if(mc.thePlayer.posY > scafStartY) {
			scafStartY = mc.thePlayer.posY;
			flagFold = false;
		}

		// Blink toggle
		if (toggleBlink.isEnabled()) {

			if (blinkTimer.hasTimeElapsed(blinkBlaster.getValueAsInt(), true)) {
				Vergo.config.modBlink.silentToggle();
			}

		}

		if (Vergo.config.modKillAura.isEnabled() && KillAura.target != null) {
			return;
		} else {

			event.setYaw(lastYaw);
			event.setPitch(lastPitch);



			RenderUtils.setCustomYaw(event.getYaw());
			RenderUtils.setCustomPitch(event.getPitch());

		}

		// Finds the block that the player will break
		BlockPos targetPos = null;

		// No extend
		if (forwardExtendSetting.getValueAsDouble() == 0 && sidewaysExtendSetting.getValueAsDouble() == 0) {

			targetPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if (keepYSetting.isEnabled() && !(mc.thePlayer.posY - 1 < keepPosY)
					&& !(overrideKeepYSetting.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown())) {
				targetPos.y = (int) keepPosY;
			}

			if (mc.theWorld.getBlockState(targetPos).getBlock() != Blocks.air) {
				targetPos = null;
			}

		}
		// Extend
		else {

			boolean breakLoops = false;
			for (double forwardExtend = 0; forwardExtend <= forwardExtendSetting.getValueAsDouble(); forwardExtend += 0.1) {
				if (breakLoops) {
					break;
				}
				for (double sidewaysExtend = 0; sidewaysExtend <= sidewaysExtendSetting.getValueAsDouble(); sidewaysExtend += 0.1) {
					if (breakLoops) {
						break;
					}
					for (int i = 0; i <= 1; i++) {
						if (breakLoops) {
							break;
						}
						BlockPos temp = WorldUtils
								.getForwardBlockFromMovement(forwardExtend, sidewaysExtend * (i == 0 ? 1 : -1))
								.add(0, -1, 0);

						if (keepYSetting.isEnabled() && !(mc.thePlayer.posY - 1 < keepPosY)
								&& !(overrideKeepYSetting.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown())) {
							temp.y = (int) keepPosY;
						}

						if (mc.theWorld.getBlockState(temp).getBlock() == Blocks.air) {
							targetPos = temp;
							breakLoops = true;
							break;
						}
						if (!MovementUtils.isMoving()) {
							breakLoops = true;
							break;
						}
					}
				}
			}

		}

		// Checks how many blocks you have
		int blocksLeft = 0;

		for (short g = 0; g < 9; g++) {

			if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
					&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBlock
					&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
					&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
					.getLocalizedName().toLowerCase().contains("chest")
					&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
					.getLocalizedName().toLowerCase().contains("table")) {
				blocksLeft += mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize;
			}

		}

		// Sets the item to hold
		int switchItemDelaySlot = lastSlot;
		ItemStack block = setStackToPlace();
		if (itemSwitchDelay.isEnabled() && lastSlot != switchItemDelaySlot) {
			itemSwitchDelayTicks = itemSwitchTicks.getValueAsInt();
		}

		// To prevent bans on some anticheats
		if (itemSwitchDelayTicks > 0 && itemSwitchDelay.isEnabled()) {
			itemSwitchDelayTicks--;
			if (mc.thePlayer.ticksExisted % 4 == 0 && fakeMissPackets.isEnabled())
				mc.getNetHandler().getNetworkManager()
						.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, block, 0, 0, 0));
			return;
		}

		// If it shouldn't place a block then don't try to
		if (targetPos == null || blocksLeft == 0) {
			legitTimer.reset();
			if (mc.thePlayer.ticksExisted % 4 == 0 && fakeMissPackets.isEnabled())
				mc.getNetHandler().getNetworkManager()
						.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, block, 0, 0, 0));
			return;
		}

		// Finds a block to place on
		BlockInfo info = findFacingAndBlockPosForBlock(targetPos);

		// Returns if it cannot find a block to place on
		if (info == null || (Vergo.config.modKillAura.isEnabled() && KillAura.target != null && false)) {
			return;
		}

		if (isTowering()) {
			if (towerMode.is("NCP")) {
				MovementUtils.setMotion(0);
				if (MovementUtils.isOnGround(0.0001)) {
					if (MovementUtils.isOnGround(0.76) && !MovementUtils.isOnGround(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
						mc.thePlayer.motionY = Math.round(mc.thePlayer.posY) - mc.thePlayer.posY;
					}
					if (MovementUtils.isOnGround(1.0E-4)) {
						mc.thePlayer.motionY = 0.41999998688697815;
					} else if (mc.thePlayer.posY >= Math.round(mc.thePlayer.posY) - 1.0E-4 && mc.thePlayer.posY <= Math.round(mc.thePlayer.posY) + 1.0E-4 && towerTimer.hasTimeElapsed(120, true)) {
						mc.thePlayer.motionY = 0.0;
						towerTimer.reset();
					}
				} else if (mc.theWorld.getBlockState(targetPos).getBlock().getMaterial().isReplaceable() && info != null && towerTimer.hasTimeElapsed(120, true)) {
					mc.thePlayer.motionY = 0.41955;
					towerTimer.reset();
				}
			}
			else if (towerMode.is("Hypixel")) {
				MovementUtils.setMotion(0);
				mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ);
				if (mc.thePlayer.onGround) {
					if (MovementUtils.isOnGround(0.76) && !MovementUtils.isOnGround(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
						mc.thePlayer.motionY = Math.round(mc.thePlayer.posY) - mc.thePlayer.posY;
					}
					if (MovementUtils.isOnGround(0.00000001)) {
						mc.thePlayer.motionY = 0.40099998688697815;
					} else if (mc.thePlayer.posY >= Math.round(mc.thePlayer.posY) - 1.0E-4 && mc.thePlayer.posY <= Math.round(mc.thePlayer.posY) + 1.0E-4 && towerTimer.hasTimeElapsed(210, false)) {
						mc.thePlayer.motionY = 0;
						towerTimer.reset();
					}
				} else if (mc.theWorld.getBlockState(targetPos).getBlock().getMaterial().isReplaceable() && info != null && towerTimer.hasTimeElapsed(210, false)) {
					mc.thePlayer.motionY = 0.40955;
					towerTimer.reset();
				}
			}
		}

		if (info.facing == EnumFacing.UP && mc.thePlayer.posY - info.pos.getY() > 0
				&& mc.thePlayer.posY - info.pos.getY() <= 2.1 && !MovementUtils.isOnGround(0.0001)) {
			return;
		}

		// Places the block and sets the rots
		if (mc.thePlayer.isSprinting()) {
			//mc.gameSettings.keyBindSprint.pressed = false;
			//mc.thePlayer.setSprinting(false);
		}

		float[] rotations = getBlockRotations(info.pos, info.facing);
		float[] rots = getRotations(info.pos, info.facing,
				(!legitSetting.isEnabled() || legitTimer.hasTimeElapsed(RandomUtils.nextInt(50, 75) * 2, false)));
		if (rotations == null) {
			return;
		}
		event.setYaw(rotations[0]);
		event.setPitch(rotations[1] + RandomUtils.nextFloat(0.02f, 0.2f));
		RenderUtils.setCustomYaw(event.yaw);
		RenderUtils.setCustomPitch(event.pitch);
		if (viewRotations.isEnabled()) {
			mc.thePlayer.rotationYaw = event.getYaw();
			mc.thePlayer.rotationPitch = event.getPitch();
		}

		if (legitSetting.isDisabled() || legitTimer.hasTimeElapsed(RandomUtils.nextInt(50, 75) * 2, true)) {
			// For rendering
			lastPlace = targetPos;

			if (placeBlockAsync.isEnabled()) {
				new Thread(() -> {
					placeBlock(event, info, block);
				}).start();
			}else {
				placeBlock(event, info, block);
			}

		} else if (legitSetting.isEnabled() && !mc.thePlayer.isSneaking())
			mc.gameSettings.keyBindSneak.pressed = MovementUtils.isOnGround(0.0001);

		if (Vergo.config.modSprint.isEnabled() && sprintSetting.isEnabled())
			mc.thePlayer.setSprinting(false);
	}


	private float[] getBlockRotations(BlockPos blockPos, EnumFacing enumFacing) {
		if (blockPos == null && enumFacing == null) {
			return null;
		}
		Vec3 positionEyes = mc.thePlayer.getPositionEyes(RandomUtils.nextFloat(2.0f, 2.05f));
		Vec3 add = new Vec3((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
		double n = add.xCoord - positionEyes.xCoord;
		double n2 = add.yCoord - positionEyes.yCoord;
		double n3 = add.zCoord - positionEyes.zCoord;
		return new float[]{(float)(Math.atan2(n3, n) * 180.0 / Math.PI - 89.5), -((float)(Math.atan2(n2, (float)Math.hypot(n, n3)) * 180.0 / Math.PI))};
	}

	private void placeBlock(EventUpdate event, BlockInfo info, ItemStack block) {
		try {
			Vec3 badVec = RotationUtils.getVectorForRotation(event.pitch, event.yaw);
			if (hitVecFixer.isEnabled()) {
				badVec.normalize();
				badVec.xCoord += info.pos.getX();
				badVec.yCoord += info.pos.getY();
				badVec.zCoord += info.pos.getZ();
			}
			mc.playerController.onPlayerRightClickNoSync(mc.thePlayer, mc.theWorld, block, info.pos, info.facing,
					badVec, hitVecFixer.isEnabled());
			if (clientSideBlockPicker.isEnabled()) {
				mc.thePlayer.swingItem();
			} else {
				mc.thePlayer.swingItem();
				mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
//				mc.thePlayer.swingItem();
			}

			mc.gameSettings.keyBindSneak.pressed = false;
			lastPlacedBlockTime = System.currentTimeMillis();
			lastYaw = event.yaw;
			lastPitch = event.pitch;
			itemSwitchDelayTicks = itemSwitchTicks.getValueAsInt();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public float[] getRotations(BlockPos paramBlockPos, EnumFacing paramEnumFacing, boolean newBlock) {

		if (rotationMode.is("None")) {
			return new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
		}

		if (rotationMode.is("Hypixel Slow")) {
			lastRandY = 0.4f;

			if (!newBlock) {
				//lastRandX += RandomUtils.nextFloat(0.01f, 0.05f);
				//lastRandZ = lastRandX;
			}
		}
		else if (rotationMode.is("Hypixel Sprint")) {
			//lastRandY = 0.4f;

			//lastRandX = 0.6f;
			//lastRandZ = lastRandX;

			if (!newBlock) {
				//lastRandX = 0.2f;
				//lastRandZ = lastRandX;
			}
		}
		else if (rotationMode.is("AAC")) {
			lastRandX = 0.5f;
			lastRandY = 0.2f;
			lastRandZ = 0.5f;
			if (newBlock) {
				lastRandZ = 0.4f;
				mc.gameSettings.keyBindSneak.pressed = true;
			}
			else {
				if (mc.thePlayer.isSneaking()) {
					mc.gameSettings.keyBindSneak.pressed = false;
				}
			}
		}
		else {
			lastRandX = 0.5f;
			lastRandY = 0.5f;
			lastRandZ = 0.5f;
		}

		if (lastRandX > 2) {
			lastRandX -= 2;
		}

		if (lastRandZ > 2) {
			lastRandZ -= 2;
		}

		if (lastRandX < 0) {
			lastRandX = 0;
		}

		if (lastRandZ < 0) {
			lastRandZ = 0;
		}

		double offsetX = 0, offsetZ = 0;

		double aimOffset1 = 1;
		double aimOffset2 = 0.000001;

		offsetX = (double) paramEnumFacing.getFrontOffsetX() * aimOffset1;
		offsetZ = (double) paramEnumFacing.getFrontOffsetZ() * aimOffset1;

		if (paramEnumFacing.getFrontOffsetX() == 0 && paramEnumFacing.getFrontOffsetZ() == -1) {
			offsetZ = aimOffset2;
		} else if (paramEnumFacing.getFrontOffsetX() == -1 && paramEnumFacing.getFrontOffsetZ() == 0) {
			offsetX = aimOffset2;
		}

		lastBlockPos = paramBlockPos;
		lastFacing = paramEnumFacing;

		double randX = lastRandX;
		double randY = lastRandY;
		double randZ = lastRandZ;

		if (rotationMode.is("Hypixel Slow")) {
			//if (randX >= 1.8)
			//	randX = 1.8;
			//if (randX <= 0.2)
			//	randX = 0.2;
			//if (randX >= 0.8 && randX <= 1.0)
			//	randX = 0.8;
			//if (randX >= 1.0 && randX <= 1.2)
			//	randX = 1.2;
//
			//if (randZ >= 1.8)
			//	randZ = 1.8;
			//if (randZ <= 0.2)
			//	randZ = 0.2;
			//if (randZ >= 0.8 && randX <= 1.0)
			//	randZ = 0.8;
			//if (randZ >= 1.0 && randX <= 1.2)
			//	randZ = 1.2;
		}

		if (offsetX != 0) {
			randX = 0;
		}
		if (offsetZ != 0) {
			randZ = 0;
		}

		if (randX > 1) {
			randX = 2 - randX;
		}

		if (randZ > 1) {
			randZ = 2 - randZ;
		}

		double d1 = (double) paramBlockPos.getX() - mc.thePlayer.posX + offsetX + randX;
		double d2 = (double) paramBlockPos.getZ() - mc.thePlayer.posZ + offsetZ + randZ;
		double d3 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - ((double) paramBlockPos.getY() + randY);
		double d4 = (double) MathHelper.sqrt_double(d1 * d1 + d2 * d2);

		float f1 = (float) (Math.atan2(d2, d1) * 180.0D / 3.24159/*2653589793D*/) - 87.0F;
		float f2 = (float) (Math.atan2(d3, d4) * 180.0D / 3.24159/*2653589793D*/);

		if (f2 > 90)
			f2 = 89;

		if (f2 < -90)
			f2 = -89;

//        mc.thePlayer.rotationYaw = f1;
//        mc.thePlayer.rotationPitch = f2;

		if (rotationMode.is("90 snap")) {
			float yaw = 0;
			switch (paramEnumFacing) {
				case NORTH:
					yaw = 360;
					break;
				case EAST:
					yaw = 90;
					break;
				case SOUTH:
					yaw = 180;
					break;
				case WEST:
					yaw = 270;
					break;
				default:
					yaw = 0;
					break;
			}
			return new float[] {yaw, RandomUtils.nextFloat(89, 90)};
		}
		else if (rotationMode.is("yaw - 180")) {
//			return new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
			return new float[] {mc.thePlayer.rotationYaw - 180, RandomUtils.nextFloat(89, 90)};
		}
		else if (rotationMode.is("AAC")) {
			return new float[] {oneDirectionalSpeedYaw - 180, f2};
		}
		else if (rotationMode.is("Hypixel Slow")) {

			if (!newBlock) {
				lastYaw = RotationUtils.updateRotation(lastYaw, f1, RandomUtils.nextFloat(20, 30));
				if (((f1 - lastYaw) * (f1 - lastYaw < 0 ? -1 : 1)) % 360 > 5) {
					lastPitch = f2;
					return null;
				}
			}
			if (newBlock) {
//				return new float[] {lastYaw, f2};
			}
			return new float[] {f1, f2};

		}
		else if (rotationMode.is("Hypixel Sprint")) {

			if (!newBlock) {
				lastYaw = RotationUtils.updateRotation(lastYaw, f1, RandomUtils.nextFloat(20, 30));
				if (((f1 - lastYaw) * (f1 - lastYaw < 0 ? -1 : 1)) % 360 > 10) {
					lastPitch = f2;
					return null;
				}
			}
			if (newBlock) {
//				return new float[] {lastYaw, f2};
			}
			return new float[] {f1, f2};

		}

		return new float[] {f1, f2};

	}

	private BlockInfo findFacingAndBlockPosForBlock(BlockPos input) {

		BlockInfo output = new BlockInfo();
		output.pos = input;

		// One block
		for (EnumFacing face : EnumFacing.VALUES) {

			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() != Blocks.air && shouldCancelCheck(face)) {

				output.pos = output.pos.offset(face);
				output.facing = face.getOpposite();
				output.targetPos = new BlockPos(input.getX(), input.getY(), input.getZ());
				if (keepYSetting.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
					output.pos.y = (int) keepPosY;
					output.targetPos.y = (int) keepPosY;
				}
				return output;

			}

		}

		// Two blocks
		for (EnumFacing face : EnumFacing.VALUES) {

			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() == Blocks.air) {

				for (EnumFacing face1 : EnumFacing.VALUES) {

					if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face1)).getBlock() != Blocks.air && shouldCancelCheck(face1)) {

						output.pos = output.pos.offset(face).offset(face1);
						output.facing = face.getOpposite();
						output.targetPos = output.pos.offset(face);
						if (keepYSetting.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
							output.pos.y = (int) keepPosY;
							output.targetPos.y = (int) keepPosY;
						}
						return output;

					}

				}

			}

		}

		// Three blocks
		for (EnumFacing face2 : EnumFacing.VALUES) {

			for (EnumFacing face : EnumFacing.VALUES) {

				if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face2)).getBlock() == Blocks.air) {

					for (EnumFacing face1 : EnumFacing.VALUES) {

						if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face1).offset(face2))
								.getBlock() != Blocks.air && shouldCancelCheck(face1)) {

							output.pos = output.pos.offset(face).offset(face1).offset(face2);
							output.facing = face2.getOpposite();
							output.targetPos = output.pos.offset(face).offset(face2);
							if (keepYSetting.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
								output.pos.y = (int) keepPosY;
								output.targetPos.y = (int) keepPosY;
							}
							return output;

						}

					}

				}

			}

		}

		// Four blocks
		for (EnumFacing face3 : EnumFacing.VALUES) {

			for (EnumFacing face2 : EnumFacing.VALUES) {

				for (EnumFacing face : EnumFacing.VALUES) {

					if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face2).offset(face3))
							.getBlock() == Blocks.air) {

						for (EnumFacing face1 : EnumFacing.VALUES) {

							if (mc.theWorld
									.getBlockState(output.pos.offset(face).offset(face1).offset(face2).offset(face3))
									.getBlock() != Blocks.air && shouldCancelCheck(face1)) {

								output.pos = output.pos.offset(face).offset(face1).offset(face2).offset(face3);
								output.facing = face3.getOpposite();
								output.targetPos = output.pos.offset(face).offset(face2).offset(face3);
								if (keepYSetting.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
									output.pos.y = (int) keepPosY;
									output.targetPos.y = (int) keepPosY;
								}
								return output;

							}

						}

					}

				}

			}

		}

		return null;

	}

	public ItemStack setStackToPlace() {

		ItemStack block = mc.thePlayer.getCurrentEquippedItem();

		if (block != null && block.getItem() != null && !(block.getItem() instanceof ItemBlock)) {
			block = null;
		}

		int slot = mc.thePlayer.inventory.currentItem;

		for (short g = 0; g < 9; g++) {

			if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
					&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBlock
					&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
					&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
					.getLocalizedName().toLowerCase().contains("chest")
					&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
					.getLocalizedName().toLowerCase().contains("table")
					&& (block == null
					|| (block.getItem() instanceof ItemBlock && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize >= block.stackSize))) {

				//mc.thePlayer.inventory.currentItem = g;
				slot = g;
				block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();

			}

		}
		if (lastSlot + (lastSlot >= 36 ? -36 : 0) != slot) {
//			ChatUtils.addChatMessage("test " + slot + " " + (lastSlot + (lastSlot >= 36 ? -36 : 0)));
			mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slot));
			if (clientSideBlockPicker.isEnabled()) {
				mc.thePlayer.inventory.currentItem = slot;
			}
			lastSlot = slot;
		}
		return block;
	}

	public boolean isTowering() {
//		return !towerMode.is("None") && mc.gameSettings.keyBindJump.isKeyDown() && !MovementUtils.isMoving();
		return !towerMode.is("None") && mc.gameSettings.keyBindJump.isKeyDown();
	}

	public boolean shouldCancelCheck(EnumFacing face) {

		if (keepYSetting.isEnabled() && overrideKeepYSetting.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
			keepPosY = mc.thePlayer.posY;
			return true;
		}

		if (mc.thePlayer.posY - keepPosY > 4)
			return true;

		if (keepYSetting.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
			return !(face == EnumFacing.UP);
		}else {
			return true;
		}
	}

	private class BlockInfo {

		BlockPos pos, targetPos;
		EnumFacing facing;

	}

	public static int getColor(Color color) {
		return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static int getColor(int brightness) {
		return getColor(brightness, brightness, brightness, 255);
	}

	public static int getColor(int brightness, int alpha) {
		return getColor(brightness, brightness, brightness, alpha);
	}

	public static int getColor(int red, int green, int blue) {
		return getColor(red, green, blue, 255);
	}

	public static int getColor(int red, int green, int blue, int alpha) {
		int color = 0;
		color |= alpha << 24;
		color |= red << 16;
		color |= green << 8;
		color |= blue;
		return color;
	}

}