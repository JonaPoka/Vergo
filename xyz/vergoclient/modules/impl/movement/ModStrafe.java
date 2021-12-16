package xyz.vergoclient.modules.impl.movement;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.util.MovementUtils;

public class ModStrafe extends Module implements OnEventInterface {

	public ModStrafe() {
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
			
			setInfo(autoJumpSetting.isEnabled() ? "Jump" : "Normal");
			
			if (MovementUtils.isOnGround(0.0001) && autoJumpSetting.isEnabled() && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.pressed) {
				mc.thePlayer.jump();
			}
			MovementUtils.strafe();
		}
		else if (e instanceof EventMove && e.isPre()) {
			EventMove event = (EventMove)e;
			event.setSpeed(MovementUtils.getSpeed());
		}
	}
	
}
