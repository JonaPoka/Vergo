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
	
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel1", "Hypixel1", "Hypixel2", "Hypixel3");

	int ticks;

	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Hypixel1", "Hypixel2", "Hypixel3"));
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
		} else if(mode.is("Hypixel2")) {
			onHypixelEvent(e);
		} else if(mode.is("Hypixel3")) {
			onHypixelEvent(e);
		}
	}
	
	private void onHypixelEvent(Event e) {

		//ChatUtils.addChatMessage("Timer: " + mc.timer.timerSpeed);
		
		if (e instanceof EventTick && e.isPre()) {

			if(mode.is("Hypixel1")) {
				setInfo("Hypixel1");
			} else if(mode.is("Hypixel2")) {
				setInfo("Hypixel2");
			} else if (mode.is("Hypixel3")) {
				setInfo("Hypixel3");
			}

		} else if (e instanceof EventUpdate && e.isPre()) {
			if(mode.is("Hypixel1")) {
				if (MovementUtils.isMoving()) {

					jitterHypixelBypass();

				}
			} else if(mode.is("Hypixel2")) {
				smoothHypixelSpeed();
			} else if(mode.is("Hypixel3")) {
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


			if(!mc.thePlayer.isSprinting()) {
				mc.thePlayer.setSprinting(true);
			}

			//ChatUtils.addChatMessage("MotionY: " + mc.thePlayer.motionY);

			if(MovementUtils.isOnGround(0.0001) && !mc.thePlayer.isCollidedHorizontally) {
				mc.thePlayer.jump();
				//ChatUtils.addChatMessage("Timer Joke");
				mc.timer.timerSpeed = 1.2f;
				mc.thePlayer.motionY -= 0.029f;
				if(mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown()) {
					MovementUtils.setSpeed(0.455);
				} else {
					mc.timer.timerSpeed = 1.0f;
					MovementUtils.setSpeed(0.25);
				}
				if (mc.thePlayer.isCollidedVertically) {
					mc.thePlayer.motionY = 0.4;
				}
			} else {
				if(!mc.thePlayer.isCollidedHorizontally) {
					mc.timer.timerSpeed = 1.09f;
					mc.thePlayer.motionY *= 1.0001f;
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
					}
				}
			}

	}

	private void jitterHypixelBypass() {
		if(mc.thePlayer.isInLava() || mc.thePlayer.isInWater() || mc.thePlayer.isSpectator()) {
			return;
		}

		if(mc.gameSettings.keyBindJump.isKeyDown()) {

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
				if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
					mc.thePlayer.jump();
					return;
				}
				mc.thePlayer.moveStrafing *= 2;
				mc.thePlayer.jump();
				mc.thePlayer.jumpMovementFactor = 0.0243F;
				mc.thePlayer.motionX *= 1.07601F;
				mc.thePlayer.motionZ *= 1.07601F;
			}

			if (mc.thePlayer.motionY >= 0.28) {
				if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
					mc.thePlayer.jump();
					return;
				}
				//ChatUtils.addChatMessage("Triggered ++ " + mc.timer.timerSpeed + " " + mc.timer.ticksPerSecond);
				mc.timer.timerSpeed = 1.13f;
			} else if(mc.thePlayer.motionY <= 0.279) {
				if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
					mc.thePlayer.jump();
					return;
				}
				mc.timer.timerSpeed = 1.09f;
				mc.thePlayer.motionX *= 1.00011F;
				mc.thePlayer.motionZ *= 1.00011F;
				//ChatUtils.addChatMessage("Reset!");
				//mc.timer.timerSpeed = 1.09f;
				//mc.thePlayer.motionX *= 1.00110F;
				//mc.thePlayer.motionZ *= 1.00110F;
			}
		}

	}

	private void smoothHypixelSpeed() {

		if(mc.thePlayer.isInLava() || mc.thePlayer.isInWater() || mc.thePlayer.isSpectator()) {
			return;
		}

		if(mc.gameSettings.keyBindJump.isKeyDown()) {

		}

		if(!mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(true);
		}

		if (MovementUtils.isMoving()) {

			if (mc.gameSettings.keyBindJump.isKeyDown()) {

			}
			if (!mc.thePlayer.isSprinting()) {
				mc.thePlayer.setSprinting(true);
			}
			if (mc.thePlayer.onGround) {
				if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
					mc.thePlayer.jump();
				} else {
					mc.thePlayer.jump();
					mc.timer.timerSpeed = 1.09f;
					mc.thePlayer.motionX *= 1.0788F;
					mc.thePlayer.motionZ *= 1.0788F;
					mc.thePlayer.moveStrafing *= 2;
				}
			} else {
				mc.thePlayer.jumpMovementFactor = 0.0256F;
			}
		}
	}
	
}
