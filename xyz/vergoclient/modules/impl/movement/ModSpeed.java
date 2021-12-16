package xyz.vergoclient.modules.impl.movement;

import java.util.Arrays;

import xyz.vergoclient.util.*;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.TimerUtil;
import xyz.vergoclient.util.WorldUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ModSpeed extends Module implements OnEventInterface {

	public ModSpeed() {
		super("Speed", Category.MOVEMENT);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "SmoothHypixel", "JitterHypixel", "SmoothHypixel", "Mineplex", "Verus lowhop", "Skidded anticheat 1");
	
	public static transient float hypixelYaw = 0;

	public static transient TimerUtil hypixelTimer = new TimerUtil();
	
	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("JitterHypixel", "SmoothHypixel", "Mineplex", "Verus lowhop", "Skidded anticheat 1"));
		addSettings(mode);
	}
	
	@Override
	public void onEnable() {
		hypixelYaw = mc.thePlayer.rotationYaw;
		if (mode.is("JitterHypixel") || mode.is("SmoothHypixel")) {
			mc.timer.timerSpeed = 1;
			mc.timer.ticksPerSecond = 20;
		}
		else if (mode.is("Mineplex")) {
			MovementUtils.setMotion(0);
		}
	}

	@Override
	public void onDisable() {
		if(mode.is("JitterHypixel") || mode.is("SmoothHypixel")) {
			mc.timer.timerSpeed = 1;
			mc.timer.ticksPerSecond = 20;
		}
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (mode.is("JitterHypixel")) {
			onHypixelEvent(e);
		} else if(mode.is("SmoothHypixel")) {
			onHypixelEvent(e);
		}
		else if (mode.is("Mineplex")) {
			onMineplexEvent(e);
		}
		else if (mode.is("Skidded anticheat 1")) {
			onSkiddedSpeedCheck1Event(e);
		}
		else if (mode.is("Verus lowhop")) {
			if (e instanceof EventTick && e.isPre()) {
				if (Vergo.config.modFly.isEnabled() && Vergo.config.modFly.mode.is("Verus infinite")) {
					toggle();
					return;
				}
				setInfo("Verus lowhop");
			}
			if (e instanceof EventTick && e.isPre()) {
				
				if (player.isLastLastOnground() && !player.isLastOnGround() && !player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
//					for (double forward = 0; forward <= 3; forward += 0.1) {
//						if (mc.theWorld.getBlockState(WorldUtils.getForwardBlockFromMovement(forward)).getBlock() != Blocks.air && mc.theWorld.getBlockState(WorldUtils.getForwardBlockFromMovement(forward).add(0, 1, 0)).getBlock() == Blocks.air) {
//							return;
//						}
//					}
					for (double forward = 0; forward <= 3; forward += 0.1) {
						if (!mc.theWorld.getBlockState(WorldUtils.getForwardBlockFromMovement(forward)).getBlock().isReplaceable(mc.theWorld, WorldUtils.getForwardBlockFromMovement(forward)) && mc.theWorld.getBlockState(WorldUtils.getForwardBlockFromMovement(forward).add(0, 1, 0)).getBlock().isReplaceable(mc.theWorld, WorldUtils.getForwardBlockFromMovement(forward).add(0, 1, 0))) {
							return;
						}
					}
					mc.thePlayer.motionY = -0.0784000015258789;
					MovementUtils.setMotion(MovementUtils.getSpeed() * 1);
				}
				
				if (player.isOnGround() && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.thePlayer.onGround = true;
					mc.thePlayer.jump();
					MovementUtils.setMotion(0.272935);
//					MovementUtils.setMotion(MovementUtils.getSpeed() - 0.08);
				}
				
				MovementUtils.strafe();
				
			}
		}
	}
	
	private static transient double testSpeed = 0, testLastDist = 0;
	private static transient boolean testLastOnGround = false;
	
	// To bypass skidded speed checks (https://www.youtube.com/watch?v=QXukRdPlXn4&ab_channel=Jonhan)
	private void onSkiddedSpeedCheck1Event(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			setInfo("Skidded anticheat 1");
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			double distX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
			double distZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
			double dist = (distX * distX) + (distZ * distZ);
			double lastDist = testLastDist;
			testLastDist = dist;
			
			boolean lastOnGround = testLastOnGround;
			boolean onGround = MovementUtils.isOnGround(0.0001);
			testLastOnGround = onGround;
			
			float friction = 0.91f;
			double shiftedLastDist = lastDist * friction;
			double equalness = dist - shiftedLastDist;
//			double scaledEqualness = equalness * 138;
			double scaledEqualness = equalness * 138;
			
			if (!onGround && !lastOnGround) {
				if (scaledEqualness >= 1.0) {
//					ChatUtils.addChatMessage("You would flag " + scaledEqualness);
					MovementUtils.setMotion(0);
				}
			}
			
			if ((onGround || lastOnGround) && MovementUtils.isMoving()) {
				mc.thePlayer.jump();
				if (onGround)
					MovementUtils.setMotion(1);
			}
			
			MovementUtils.setMotion(MovementUtils.getSpeed());
			
			if (MovementUtils.isOnGround(0.7) && mc.thePlayer.motionY <= 0) {
//				MovementUtils.setMotion(MovementUtils.getBaseMoveSpeed());
			}
			
		}
		
	}
	
	public static transient int hypixelJump = 0;
	
	private void onHypixelEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {

			if(mode.is("JitterHypixel")) {
				setInfo("WatchDoggyDog");
			} else if(mode.is("SmoothHypixel")) {
				setInfo("SmoothDoggyDog");
			}

		} else if (e instanceof EventUpdate && e.isPre()) {
			if(mode.is("JitterHypixel")) {
				if (MovementUtils.isMoving()) {

//					player.setFallSpeed(0.10);

					jitterHypixelBypass();

				}
			} else if(mode.is("SmoothHypixel")) {
				smoothHypixelSpeed();
			}
			
		}
		
	}

	private void jitterHypixelBypass() {

		if(mc.gameSettings.keyBindJump.isKeyDown()) {
			return;
		}
		if(!mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(true);
		}


		if(MovementUtils.isOnGround(0.0001)) {
			mc.timer.timerSpeed = 8.0f;
			mc.timer.ticksPerSecond = 22.0f;
			mc.thePlayer.jump();
		} else {
			mc.timer.timerSpeed = 1.0f;
			mc.timer.ticksPerSecond = 20f;
		}
	}

	private void smoothHypixelSpeed() {

		if (MovementUtils.isMoving()) {
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				return;
			}
			if (!mc.thePlayer.isSprinting()) {
				mc.thePlayer.setSprinting(true);
			}
			if (MovementUtils.isOnGround(0.00001)) {
				mc.timer.timerSpeed = 1.5f;
				mc.timer.ticksPerSecond = 21f;
				mc.thePlayer.jump();
			} else {
				mc.timer.timerSpeed = 1.1f;
				mc.timer.ticksPerSecond = 20f;
			}
		}
	}
	
	private void onMineplexEvent(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			
			setInfo("Mineplex");
			
			boolean isOverVoid = true;
			BlockPos block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			
			for (double i = mc.thePlayer.posY + 1; i > 0; i -= 0.5) {
				
				if (isOverVoid) {
					
					try {
						if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
							
							isOverVoid = false;
							break;
							
						}
					} catch (Exception e1) {
						
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
			
			if (isOverVoid) {
				return;
			}
			
			if (Vergo.config.modScaffold.isDisabled())
				mc.thePlayer.jumpMovementFactor = 0.0385f;
			else
				mc.thePlayer.jumpMovementFactor = 0.02f;
			
			if (MovementUtils.isOnGround(0.0001)) {
				MovementUtils.setMotion(MovementUtils.getSpeed() / 3);
				if (!mc.gameSettings.keyBindJump.pressed && MovementUtils.isMoving()) {
					if (Vergo.config.modScaffold.isDisabled())
						mc.thePlayer.jumpMovementFactor = 0.08f;
					mc.thePlayer.jump();
				}
			}
			MovementUtils.strafe();
//			ChatUtils.addChatMessage(mc.thePlayer.motionY);
//			[17:17:32] [Client thread/INFO]: [CHAT] [ Hummus ] 0.41999998688697815
//			[17:17:32] [Client thread/INFO]: [CHAT] [ Hummus ] 0.33319999363422365
//			[17:17:32] [Client thread/INFO]: [CHAT] [ Hummus ] 0.24813599859094576
			if (mc.thePlayer.motionY == 0.33319999363422365) {
				double motionY = mc.thePlayer.motionY;
				mc.thePlayer.jump();
//				mc.thePlayer.motionY = 0.4;
				mc.thePlayer.motionY = motionY + 0.02;
//				mc.thePlayer.motionY += 0.02;
				if (Vergo.config.modScaffold.isDisabled()) {
//					mc.thePlayer.motionY *= 1.1;
					mc.thePlayer.jumpMovementFactor = 0.08f;
				}
				MovementUtils.setMotion(MovementUtils.getSpeed());
			}
			
//			[01:31:37] [Client thread/INFO]: [CHAT] [ Hummus ] -0.02465478544517497
//			[01:31:37] [Client thread/INFO]: [CHAT] [ Hummus ] -0.10256169173240309
//			[01:31:37] [Client thread/INFO]: [CHAT] [ Hummus ] -0.17891046137984298
//			[01:31:37] [Client thread/INFO]: [CHAT] [ Hummus ] -0.25373225709057123
//			[01:31:38] [Client thread/INFO]: [CHAT] [ Hummus ] -0.32705761831419744
//			[01:31:38] [Client thread/INFO]: [CHAT] [ Hummus ] -0.3989164737119214
			if (mc.thePlayer.motionY == -0.1552320045166016) {
//				mc.thePlayer.motionY = -1;
//				mc.thePlayer.lastTickPosY = mc.thePlayer.posY;
//				MovementUtils.setMotion(MovementUtils.getSpeed() + 0.05);
			}
		}
	}
	
}
