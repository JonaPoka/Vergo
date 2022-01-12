package xyz.vergoclient.modules.impl.movement;

import net.minecraft.potion.Potion;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.Timer;
import xyz.vergoclient.util.TimerUtil;

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
	
	public ModeSetting mode = new ModeSetting("Mode", "SmoothHypixel", "JitterHypixel", "SmoothHypixel", "Hypixel LoFi");

	public NumberSetting motionY = new NumberSetting("MotionY", 1.0, 1.0, 3, 0.01);

	public BooleanSetting toggleBPS = new BooleanSetting("Toggle BPS", false), overrideFOV = new BooleanSetting("Override FOV", true);
	
	public static transient float hypixelYaw = 0;

	public static transient TimerUtil hypixelTimer = new TimerUtil();

	int ticks;

	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("JitterHypixel", "SmoothHypixel", "Hypixel LoFi"));
		addSettings(mode, motionY);
	}

	@Override
	public void onEnable() {
		hypixelYaw = mc.thePlayer.rotationYaw;
		if (mode.is("JitterHypixel") || mode.is("SmoothHypixel") || mode.is("Hypixel LoFi")) {
			mc.timer.timerSpeed = 1;
			mc.timer.ticksPerSecond = 20;
		}

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
		
		if (mode.is("JitterHypixel")) {
			onHypixelEvent(e);
		} else if(mode.is("SmoothHypixel")) {
			onHypixelEvent(e);
		} else if(mode.is("Hypixel LoFi")) {
			onHypixelEvent(e);
		}
	}
	
	private void onHypixelEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {

			if(mode.is("JitterHypixel")) {
				setInfo("WatchDoggyDog");
			} else if(mode.is("SmoothHypixel")) {
				setInfo("SmoothDoggyDog");
			} else if (mode.is("Hypixel LoFi")) {
				setInfo("ButtaDowg");
			}

		} else if (e instanceof EventUpdate && e.isPre()) {
			if(mode.is("JitterHypixel")) {
				if (MovementUtils.isMoving()) {

					jitterHypixelBypass();

				}
			} else if(mode.is("SmoothHypixel")) {
				smoothHypixelSpeed();
			} else if(mode.is("Hypixel LoFi")) {
				if(MovementUtils.isMoving()) {
					this.packTimer.reset();
					hypixelLoFi(e);
				}
			}
			
		}
		
	}

	public static TimerUtil blinkTimer = new TimerUtil();


	private void hypixelLoFi(Event event) {
		if(mc.thePlayer.isInLava() || mc.thePlayer.isInWater() || mc.thePlayer.isSpectator()) {
			return;
		}

		if(mc.gameSettings.keyBindJump.isKeyDown()) {

		}


		if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			mc.thePlayer.jump();
			return;
		}

		if(!mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(true);
		}

		//ChatUtils.addChatMessage("MotionY: " + mc.thePlayer.motionY);

		if (MovementUtils.isMoving()) {

				if (mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.gameSettings.keyBindJump.pressed = false;
				}


				if (!mc.thePlayer.isSprinting()) {
					mc.thePlayer.setSprinting(true);
				}

				if(mc.thePlayer.onGround) {
					mc.thePlayer.moveStrafing *= 2;
					mc.thePlayer.jump();
					mc.thePlayer.jumpMovementFactor = 0.0243F;
					mc.thePlayer.motionX *= 1.07601F;
					mc.thePlayer.motionZ *= 1.07601F;
				}

				if (mc.thePlayer.motionY >= 0.28) {
					//ChatUtils.addChatMessage("Triggered ++ " + mc.timer.timerSpeed + " " + mc.timer.ticksPerSecond);
					mc.timer.timerSpeed = 1.13f;
				} else if(mc.thePlayer.motionY <= 0.279) {
					//ChatUtils.addChatMessage("Reset!");
					mc.timer.timerSpeed = 1.09f;
					mc.thePlayer.motionX *= 1.00110F;
					mc.thePlayer.motionZ *= 1.00110F;
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
