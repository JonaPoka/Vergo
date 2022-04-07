package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.disabler.HypixelStrafe;
import xyz.vergoclient.modules.impl.miscellaneous.disabler.HypixelTimer;
import xyz.vergoclient.modules.impl.miscellaneous.disabler.WatchdogTest;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.util.main.ChatUtils;

import java.util.Arrays;

public class Disabler extends Module implements OnEventInterface {

	public Disabler() {
		super("Disabler", Category.MISCELLANEOUS);
	}
	
	public ModeSetting mode = new ModeSetting("Disabler", "Watchdog", "Watchdog", "HypixelTest");

	@Override
	public void loadSettings() {

		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Watchdog", "HypixelTest"));
		addSettings(mode);
		
	}

	@Override
	public void onEnable() {

		if(mode.is("HypixelTest")) {
			ChatUtils.addProtMsg("Warning! Experimental Disabler!");
		}

	}
	
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
	}

	@Override
	public void onEvent(Event e) {

		if(e instanceof EventTick) {
			if (mode.is("Watchdog")) {
				setInfo("Watchdog");
			} else if (mode.is("HypixelTest")) {
				setInfo("Experimental");
			}
		}

		if(mode.is("Watchdog")) {
			// Strafe Disabler
			HypixelStrafe hypixelStrafe = new HypixelStrafe();
			hypixelStrafe.onEvent(e);

			// Timer Disabler
			HypixelTimer hypixelTimer = new HypixelTimer();
			hypixelTimer.onEvent(e);
		} else if(mode.is("HypixelTest")) {
			WatchdogTest wdTest = new WatchdogTest();
			wdTest.onEvent(e);
		}

	}
	
}
