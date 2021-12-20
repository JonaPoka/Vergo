package xyz.vergoclient.modules.impl.visual;

import java.util.Arrays;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;

public class ModHud extends Module {

	public ModHud() {
		super("Hud", Category.VISUAL);
	}
	
	public ModeSetting waterMark = new ModeSetting("Watermark", "Rounded", "Rounded", "Planet", "None"),
			arrayListFont = new ModeSetting("ArrayList Font", "Helvetica Neue Bold", "Arial", "Arial", "Helvetica Neue", "Helvetica Neue Bold", "Jura"),
			arrayListColors = new ModeSetting("ArrayList colors", "Rainbow", "Default", "Rainbow");
	public BooleanSetting renderScoreboardNumbers = new BooleanSetting("Render Scoreboard Numbers", false),
						  arrayListBackground = new BooleanSetting("ArrayList Background", true);
	
	@Override
	public void loadSettings() {
		
		arrayListFont.modes.clear();
		arrayListFont.modes.addAll(Arrays.asList("Minecraft", "Arial", "Helvetica Neue", "Helvetica Neue Bold", "Jura"));
		
		addSettings(waterMark, arrayListFont, arrayListColors, arrayListBackground, renderScoreboardNumbers);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean isDisabled() {
		return false;
	}
	
}
