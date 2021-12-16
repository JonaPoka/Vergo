package xyz.vergoclient.modules.impl.player;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.TimerUtil;

public class ModTimer extends Module implements OnEventInterface {

	public ModTimer() {
		super("Timer", Category.PLAYER);
	}
	
	public static TimerUtil blinkTimer = new TimerUtil();
	
	public NumberSetting ticksPerSecond = new NumberSetting("TPS", 20, 1, 100, 1),
			timerSpeed = new NumberSetting("Timer speed", 1, 0.3, 5, 0.05);
	public BooleanSetting hypixelBlinkToggle = new BooleanSetting("Hypixel blink toggle", true);
	
	@Override
	public void loadSettings() {
		addSettings(ticksPerSecond, timerSpeed, hypixelBlinkToggle);
	}
	
	@Override
	public void onEnable() {
		if (Vergo.config.modBlink.isEnabled()) {
			Vergo.config.modBlink.toggle();
		}
	}
	
	@Override
	public void onDisable() {
		if (Vergo.config.modBlink.isEnabled()) {
			Vergo.config.modBlink.toggle();
		}
		mc.timer.timerSpeed = 1;
		mc.timer.ticksPerSecond = 20;
	}
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			setInfo((ticksPerSecond.getValueAsDouble() * timerSpeed.getValueAsDouble()) + " TPS");
			mc.timer.timerSpeed = (float) timerSpeed.getValueAsDouble();
			mc.timer.ticksPerSecond = (float) ticksPerSecond.getValueAsDouble();
			
			if (hypixelBlinkToggle.isEnabled()) {
		        if (blinkTimer.hasTimeElapsed(230, true)) {
		        	Vergo.config.modBlink.toggle();
		        }
			}
		}
	}

}
