package xyz.vergoclient.modules.impl.movement;

import java.awt.*;
import java.util.Arrays;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventSneaking;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.modules.impl.combat.ModKillAura;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
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

public class ModScaffold extends Module implements OnEventInterface, OnSettingChangeInterface {

	public ModScaffold() {
		super("Scaffold", Category.MOVEMENT);
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
	
	public NumberSetting forwardExtendSetting = new NumberSetting("Forward extend", 0.0, 0.0, 5, 0.1),
			sidewaysExtendSetting = new NumberSetting("Sideways extend", 0.0, 0.0, 5, 0.1),
			maxBlocksPlacedPerTickSetting = new NumberSetting("Max blocks placed per tick", 1, 1, 25, 1),
			timerBoostSetting = new NumberSetting("Timer Boost", 1, 1, 2, 0.01),
			itemSwitchTicks = new NumberSetting("Item switch tick delay", 1, 1, 20, 1);
	public BooleanSetting keepYSetting = new BooleanSetting("Keep Y", false),
			sprintSetting = new BooleanSetting("Sprint", false),
//			towerSetting = new BooleanSetting("Tower", false),
			legitSetting = new BooleanSetting("Legit", false),
			timerSlow = new BooleanSetting("Timer Slow", true),
			overrideKeepYSetting = new BooleanSetting("Override keep y when jump is pressed", true),
			viewRotations = new BooleanSetting("View rotations", false),
			fourDirectionalSpeed = new BooleanSetting("Four directional speed", true),
			oneDirectionalSpeed = new BooleanSetting("One directional speed", false),
			toggleBlink = new BooleanSetting("Toggle blink", false),
			itemSwitchDelay = new BooleanSetting("Switch item delay", false),
			clientSideBlockPicker = new BooleanSetting("Client side block picker", false),
			hitVecFixer = new BooleanSetting("Hit vec fixer", true),
			noRotate = new BooleanSetting("NoRotate", false),
			fakeMissPackets = new BooleanSetting("Fake miss packets", false),
			placeBlockAsync = new BooleanSetting("Async block placements", true);
	public ModeSetting rotationMode = new ModeSetting("Rotation setting", "Hypixel Sprint", "90 snap", "yaw - 180", "Hypixel Slow", "Hypixel Sprint", "None"),
			towerMode = new ModeSetting("Tower mode", "None", "None", "Hypixel", "NCP", "Test");
	
	@Override
	public void loadSettings() {
		rotationMode.modes.clear();
		rotationMode.modes.addAll(Arrays.asList("90 snap", "90 snap", "yaw - 180", "Hypixel Slow", "Hypixel Sprint", "None"));
		forwardExtendSetting.minimum = 0;
		forwardExtendSetting.name = "Forward extend";
		addSettings(forwardExtendSetting, sidewaysExtendSetting/*maxBlocksPlacedPerTickSetting*/, timerBoostSetting,
				keepYSetting, sprintSetting, legitSetting, overrideKeepYSetting, viewRotations, rotationMode,
				fourDirectionalSpeed, oneDirectionalSpeed, toggleBlink, itemSwitchDelay, clientSideBlockPicker,
				hitVecFixer, noRotate, fakeMissPackets, towerMode, placeBlockAsync, timerSlow);
	}
	
	private static transient BlockPos lastPlace = null;
	private static transient double keepPosY = 0;
	private static transient TimerUtil legitTimer = new TimerUtil(), crazyTimerTimer = new TimerUtil();
	private static transient boolean switchLook = false;
	private static transient float oneDirectionalSpeedYaw = 0;
	private static transient int itemSwitchDelayTicks = 0;
	
	public void onEnable() {

		if(rotationMode.is("Hypixel Slow") || rotationMode.is("Hypixel Sprint")) {

			if(mc.thePlayer.isSprinting()) {
				mc.thePlayer.setSprinting(false);
			}
			mc.timer.timerSpeed = 0.8f;
		}
		
		timer.reset();
		
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
			
			String left = blocksLeft + " block" + (blocksLeft != 1 ? "s" : "") + " left";
			
			if (blocksLeft > 0) {
				
				mc.fontRendererObj.drawString(left,
						((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
								- (mc.fontRendererObj.getStringWidth(left) / 2)),
						((float) (new ScaledResolution(mc).getScaledHeight_double() / 3)
								- (mc.fontRendererObj.FONT_HEIGHT - 18)),
						-1, true);

			} else {
				mc.fontRendererObj.drawString(left,
						((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
								- (mc.fontRendererObj.getStringWidth(left) / 2)),
						((float) (new ScaledResolution(mc).getScaledHeight_double() / 3)
								- (mc.fontRendererObj.FONT_HEIGHT - 18)),
						0xff2121, true);
			}
			
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
			
//			if (Hummus.config.modKillAura.isEnabled() && ModKillAura.target != null) {
//				return;
//			}
			
			if (((EventReceivePacket)e).packet instanceof S2FPacketSetSlot) {
				lastSlot = ((S2FPacketSetSlot)((EventReceivePacket)e).packet).slot;
				//e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventSendPacket & e.isPre()) {
			
//			if (Hummus.config.modKillAura.isEnabled() && ModKillAura.target != null) {
//				return;
//			}
			if (((EventSendPacket)e).packet instanceof C09PacketHeldItemChange) {
				lastSlot = ((C09PacketHeldItemChange)((EventSendPacket)e).packet).getSlotId();
			}
			
		}
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			BlockPos below = lastPlace;
			
			if (below == null) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			
			GlStateManager.depthMask(false);
			
			GL11.glEnable(32823);
			GL11.glPolygonOffset(1.0f, -1100000.0f);

			RenderUtils.drawColoredBox(below.getX() - 0.0001, below.getY() - 0.0001, below.getZ() - 0.0001, below.getX() + 1.0001, below.getY() + 1.0001, below.getZ() + 1.0001, 0x50C74D8E);

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
			
		}
		
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
	
	public void attemptBlockPlace(EventUpdate e) {
		// Event
		EventUpdate event = (EventUpdate) e;

		// Info
		setInfo("Mode: " + rotationMode.getMode());

		// prevents flags on Hypixel
		if (!sprintSetting.isEnabled() && mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(false);
		} else if (sprintSetting.isEnabled() && !mc.thePlayer.isSprinting()) {
//						mc.thePlayer.setSprinting(true);
		}

		if (timerBoostSetting.getValueAsDouble() >= 1.000001)
			mc.timer.timerSpeed = MovementUtils.isMoving() ? ((float) timerBoostSetting.getValueAsDouble()) : 1f;

		if (rotationMode.is("Hypixel Slow")) {
			if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//		                  mc.thePlayer.motionX *= 0.818f;
//		                  mc.thePlayer.motionZ *= 0.818f;
				mc.thePlayer.motionX *= 0.6f;
				mc.thePlayer.motionZ *= 0.6f;
//		                	mc.timer.timerSpeed = 0.818f;
			}
		}
		
		// KeepY
		if (MovementUtils.isOnGround(0.00001)) {
			keepPosY = ((int) mc.thePlayer.posY) - 1;
		}

		// Keep rotations
		if (lastBlockPos != null && lastFacing != null) {
			if (mc.thePlayer.ticksExisted % 2 == 0) {
//							lastBlockPos = lastBlockPos.offset(lastFacing.getOpposite());
			}
			float[] keepRots = getRotations(lastBlockPos, lastFacing, false);
			if (keepRots != null) {
				if (rotationMode.is("Hypixel Slow") || rotationMode.is("Hypixel Sprint")) {
					lastYaw = keepRots[0];
//					lastPitch = keepRots[1];
				} else if (rotationMode.is("AAC")) {

				} else {
					lastYaw = keepRots[0];
					lastPitch = keepRots[1];
				}
			}
		}

		// Blink toggle
		if (toggleBlink.isEnabled()) {

			if (blinkTimer.hasTimeElapsed(230, true)) {
				Vergo.config.modBlink.toggle();
			}

		}

		if (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
			return;
		} else {
			event.setYaw(lastYaw);
			event.setPitch(lastPitch);

			RenderUtils.setCustomYaw(lastYaw);
			RenderUtils.setCustomPitch(lastPitch);

		}

		// Finds the block that the player will break
		BlockPos targetPos = null;

		// No extend
		if (forwardExtendSetting.getValueAsDouble() == 0 && sidewaysExtendSetting.getValueAsDouble() == 0) {

			targetPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
//			targetPos = WorldUtils.getForwardBlockFromMovement(-0.125).add(0, -1, 0);
//			targetPos = WorldUtils.getForwardBlockFromMovement(-0.125).add(0, -1, 0);

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
		if (info == null || (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null && false)) {
			return;
		}
		
		if (isTowering()) {
			if (towerMode.is("NCP")) {
				MovementUtils.setMotion(0);
//				MovementUtils.setMotion(0);
//				mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ);
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
		if (mc.thePlayer.isSprinting())
			mc.thePlayer.setSprinting(false);

		float[] rots = getRotations(info.pos, info.facing,
				(!legitSetting.isEnabled() || legitTimer.hasTimeElapsed(RandomUtils.nextInt(50, 75) * 2, false)));
		if (rots == null) {
			return;
		}
		event.setYaw(rots[0]);
		event.setPitch(rots[1]);
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
			mc.thePlayer.setSprinting(true);
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
				lastRandX += RandomUtils.nextFloat(0.01f, 0.05f);
				lastRandZ = lastRandX;
			}
		}
		else if (rotationMode.is("Hypixel Sprint")) {
			lastRandY = 0.4f;
			
			lastRandX = 0.6f;
			lastRandZ = lastRandX;
			
			if (!newBlock) {
				lastRandX = 0.2f;
				lastRandZ = lastRandX;
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
			if (randX >= 1.8)
				randX = 1.8;
			if (randX <= 0.2)
				randX = 0.2;
			if (randX >= 0.8 && randX <= 1.0)
				randX = 0.8;
			if (randX >= 1.0 && randX <= 1.2)
				randX = 1.2;

			if (randZ >= 1.8)
				randZ = 1.8;
			if (randZ <= 0.2)
				randZ = 0.2;
			if (randZ >= 0.8 && randX <= 1.0)
				randZ = 0.8;
			if (randZ >= 1.0 && randX <= 1.2)
				randZ = 1.2;
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
		
		float f1 = (float) (Math.atan2(d2, d1) * 180.0D / 3.141592653589793D) - 90.0F;
		float f2 = (float) (Math.atan2(d3, d4) * 180.0D / 3.141592653589793D);
		
		if (f2 > 90)
			f2 = 90;

		if (f2 < -90)
			f2 = -90;
		
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
	
}
