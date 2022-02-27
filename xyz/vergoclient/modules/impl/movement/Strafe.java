package xyz.vergoclient.modules.impl.movement;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;

public class Strafe extends Module implements OnEventInterface {

	public Strafe() {
		super("Strafe", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		ChatUtils.addChatMessage("\"you need a strafe disabler for Hypixel :lolxd:\"");
	}

	/*@Override
	public void loadSettings() {
		addSettings(autoJumpSetting);
	}*/
	
	//public BooleanSetting autoJumpSetting = new BooleanSetting("Auto jump", false);
	
	@Override
	public void onEvent(Event e) {

		if (!mc.thePlayer.onGround) {
			if (mc.gameSettings.keyBindJump.pressed) {
				if ((mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindLeft.pressed)) {
					MovementUtils.strafe(MovementUtils.getSpeed() * 0.85);
				} else {
					MovementUtils.strafe();
				}
			}
		}

	}

	// Unused fucking shit event
	/*public void doStrafe(EventMove eventMove) {
		eventMove.setSpeed(1.0f);
	}*/
	
}
