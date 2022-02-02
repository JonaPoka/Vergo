package xyz.vergoclient.modules.impl.movement;

import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.*;

import java.util.Arrays;

public class ModSpeed extends Module implements OnEventInterface {


	Timer jumpTimer;

	Timer packTimer;

	public TimerUtil packetFucker = new TimerUtil();

	public ModSpeed() {
		super("Speed", Category.MOVEMENT);
		this.jumpTimer = new Timer();
		this.packTimer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel1", "Hypixel1");

	int ticks;

	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Hypixel1"));
		addSettings(mode);
	}

	@Override
	public void onEnable() {
		mc.timer.timerSpeed = 1;
		mc.timer.ticksPerSecond = 20;

		packetFucker.reset();

		ticks = mc.thePlayer.ticksExisted;

		this.packTimer.reset();
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1;
		mc.timer.ticksPerSecond = 20;

		if(Vergo.config.modBlink.isEnabled()) {
			Vergo.config.modBlink.toggle();
		}
	}
	
	@Override
	public void onEvent(Event e) {

		if (mode.is("Hypixel1")) {
			onHypixelEvent(e);
		}

	}
	
	private void onHypixelEvent(Event e) {

		//ChatUtils.addChatMessage("Timer: " + mc.timer.timerSpeed);
		
		if (e instanceof EventTick && e.isPre()) {

			if(mode.is("Hypixel1")) {
				setInfo("Hypixel1");
			}

		} else if (e instanceof EventUpdate && e.isPre()) {

			if(mode.is("Hypixel1")) {
				if(MovementUtils.isMoving()) {
					hypixelOne(e);
				}
			}
			
		}
		
	}

	public static TimerUtil blinkTimer = new TimerUtil();


	private void hypixelOne(Event event) {

		if(mc.thePlayer.isInLava() || mc.thePlayer.isInWater() || mc.thePlayer.isSpectator()) {
			return;
		}

		if(mc.gameSettings.keyBindJump.isKeyDown()) {

		}


		if(!mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(true);
		}

		if(MovementUtils.isOnGround(0.0001) && !mc.thePlayer.isCollidedHorizontally) {
			mc.thePlayer.jump();
			mc.thePlayer.motionY -= 0.023f;
			if(mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown()) {
				MovementUtils.setSpeed(0.455);
			} else {
				mc.timer.timerSpeed = 1.0f;
				MovementUtils.setSpeed(0.25);
			}
			if (mc.thePlayer.isCollidedVertically) {
				mc.thePlayer.motionY = 0.4;
			}
		}

		if(mc.thePlayer.motionY > 0.2) {
			mc.timer.timerSpeed = 1.2f;
		} else if(mc.thePlayer.motionY < 0.19) {
			mc.timer.timerSpeed = 1.06f;
		}

	}
	
}
