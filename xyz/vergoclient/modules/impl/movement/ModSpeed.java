package xyz.vergoclient.modules.impl.movement;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
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

import java.util.Arrays;

public class ModSpeed extends Module implements OnEventInterface {

	public ModSpeed() {
		super("Speed", Category.MOVEMENT);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "SmoothHypixel", "JitterHypixel", "SmoothHypixel", "Hypixel LoFi");
	
	public static transient float hypixelYaw = 0;

	public static transient TimerUtil hypixelTimer = new TimerUtil();
	
	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("JitterHypixel", "SmoothHypixel", "Hypixel LoFi"));
		addSettings(mode);
	}
	
	@Override
	public void onEnable() {
		hypixelYaw = mc.thePlayer.rotationYaw;
		if (mode.is("JitterHypixel") || mode.is("SmoothHypixel") || mode.is("Hypixel LoFi")) {
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
	}
	
	private static transient double testSpeed = 0, testLastDist = 0;
	private static transient boolean testLastOnGround = false;
	
	public static transient int hypixelJump = 0;
	
	private void onHypixelEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {

			if(mode.is("JitterHypixel")) {
				setInfo("WatchDoggyDog");
			} else if(mode.is("SmoothHypixel")) {
				setInfo("SmoothDoggyDog");
			} else if (mode.is("Hypixel LoFi")) {
				setInfo("LowDoggyDog");
			}

		} else if (e instanceof EventUpdate && e.isPre()) {
			if(mode.is("JitterHypixel")) {
				if (MovementUtils.isMoving()) {

					jitterHypixelBypass();

				}
			} else if(mode.is("SmoothHypixel")) {
				smoothHypixelSpeed();
			}
			
		}
		
	}

	private void jitterHypixelBypass() {

		if(mc.gameSettings.keyBindJump.isKeyDown()) {

		}
		if(!mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(true);
		}

		if(MovementUtils.isOnGround(0.0001)) {
			mc.timer.timerSpeed = 15.0f;
			mc.timer.ticksPerSecond = 24.0f;
			mc.thePlayer.jump();
		} else {
			mc.timer.timerSpeed = 1.0f;
			mc.timer.ticksPerSecond = 20f;
		}
	}

	private void smoothHypixelSpeed() {

		if (MovementUtils.isMoving()) {
			if (mc.gameSettings.keyBindJump.isKeyDown()) {

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
	
}
