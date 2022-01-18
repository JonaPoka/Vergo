package xyz.vergoclient.modules.impl.movement;

import net.minecraft.util.BlockPos;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.Timer;

import java.util.Arrays;


public class ModFly extends Module implements OnEventInterface {

	Timer timer;

	public ModFly() {
		super("Fly", Category.MOVEMENT);
		this.timer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Vanilla", "Test");

	public NumberSetting scale = new NumberSetting("TheFunny", 5, 0, 100, 0.1);

	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Hypixel", "Vanilla", "Test"));
		
		addSettings(mode, scale);
	}

	public static BlockPos position = null;
	
	@Override
	public void onEnable() {

	}
	
	@Override
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
	}
	
	@Override
	public void onEvent(Event e) {

		if(e instanceof EventMove) {

			if(mode.is("Hypixel")) {
				doTheFunnyFly(((EventMove) e));
			}

			else if(mode.is("Vanilla")) {
				if (!mc.thePlayer.capabilities.isFlying) {
					mc.thePlayer.capabilities.isFlying = true;
				}
			}

			else if(mode.is("Test")) {
				doTheFunnyTest();
			}

		}

	}

	private void doTheFunnyFly(EventMove eventMove) {

		if(this.timer.delay(1200L)) {
			ChatUtils.addChatMessage("Teleported!");
			this.HClip(scale.getValueAsFloat(), eventMove);
			this.timer.reset();
		}else {
			eventMove.setX(0.0);
			eventMove.setY(0.0);
			eventMove.setZ(0.0);
		}
	}

	private void HClip(final double horizontal , EventMove eventMove) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);

		position = mc.thePlayer.getPosition();

		ChatUtils.addChatMessage("BLOCKPLACE + " + position);

		position.x = (int) (position.getX() + horizontal * -Math.sin(playerYaw));
		position.y = (int) (position.getY() - 2.0);
		position.z = (int) (position.getZ() + horizontal * Math.cos(playerYaw));
		mc.thePlayer.setPosition(position.getX(), position.getY(), position.getZ());
	}

	public void doTheFunnyTest() {
		mc.thePlayer.cameraYaw = 0.09090909086F * 2;

		MovementUtils.setSpeed(0.2);

		mc.thePlayer.motionY = 0;

		mc.timer.timerSpeed = 1f;

		if(mc.thePlayer.isInvisible()) {
			mc.timer.timerSpeed = 1f;
		}
	}
	
}
