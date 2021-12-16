package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.FileSetting;
import xyz.vergoclient.settings.NumberSetting;

public class ModAutoSaveConfig extends Module implements OnEventInterface {

	public ModAutoSaveConfig() {
		super("AutoSaveConfig", Category.MISCELLANEOUS);
	}
	
	public FileSetting configSetting = new FileSetting("Config", FileManager.configDir);
	public NumberSetting saveDelay = new NumberSetting("Save tick delay", 100, 10, 600, 1);
	
	@Override
	public void loadSettings() {
		addSettings(configSetting, saveDelay);
	}
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventTick && e.isPre() && mc.thePlayer.ticksExisted % saveDelay.getValueAsInt() == 0) {
			Vergo.config.save(configSetting.getFile().getName().substring(0, configSetting.getFile().getName().length() - 5));
		}
	}
	
}
