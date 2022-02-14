package xyz.vergoclient.modules.impl.movement;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.util.MovementUtils;

public class Strafe extends Module implements OnEventInterface {

	public Strafe() {
		super("Strafe", Category.MOVEMENT);
	}
	
	@Override
	public void loadSettings() {
		addSettings(autoJumpSetting);
	}
	
	public BooleanSetting autoJumpSetting = new BooleanSetting("Auto jump", false);
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (MovementUtils.isOnGround(0.0001) && autoJumpSetting.isEnabled() && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.pressed) {
				mc.thePlayer.jump();
			}
			//MovementUtils.strafe();
			//mc.thePlayer.rotationYaw = mc.thePlayer.movementInput.moveStrafe;

			if(e instanceof EventMove) {
				doStrafe(((EventMove) e));
			}
		}
		else if (e instanceof EventMove && e.isPre()) {
			EventMove event = (EventMove)e;
			event.setSpeed(MovementUtils.getSpeed());
		}
	}

	public void doStrafe(EventMove eventMove) {
		eventMove.setSpeed(1.0f);
	}
	
}
