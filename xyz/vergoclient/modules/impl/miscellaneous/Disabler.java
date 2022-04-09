package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.disabler.HypixelTimer;
import xyz.vergoclient.modules.impl.miscellaneous.disabler.WatchdogTest;
import xyz.vergoclient.settings.ModeSetting;

import java.util.Arrays;

public class Disabler extends Module implements OnEventInterface {

	public Disabler() {
		super("Disabler", Category.MISCELLANEOUS);
	}
	
	public ModeSetting mode = new ModeSetting("Disabler", "Watchdog", "Watchdog");

	@Override
	public void loadSettings() {

		mode.modes.clear();

		mode.modes.addAll(Arrays.asList("Watchdog"));

		addSettings(mode);
		
	}

	@Override
	public void onEnable() {



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
			}
		}

		// Strafe Disabler
		WatchdogTest wdTest = new WatchdogTest();
		wdTest.onEvent(e);

		// Timer Disabler
		HypixelTimer hypixelTimer = new HypixelTimer();
		hypixelTimer.onEvent(e);

	}
	
}
