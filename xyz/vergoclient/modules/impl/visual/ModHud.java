package xyz.vergoclient.modules.impl.visual;

import java.util.Arrays;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;

public class ModHud extends Module {

	public ModHud() {
		super("Hud", Category.VISUAL);
	}
	
	public ModeSetting arrayListFont = new ModeSetting("ArrayList Font", "Helvetica Neue", "Arial", "Arial", "Helvetica Neue", "Helvetica Neue Bold"),
			arrayListColors = new ModeSetting("ArrayList colors", "Rainbow", "Default", "Rainbow", "United States", "Transgender"),
			scoreboardPosition = new ModeSetting("Scoreboard position", "Right", "Left", "Right");
	public BooleanSetting renderScoreboardNumbers = new BooleanSetting("Render Scoreboard Numbers", false),
			renderPumpkinOverlay = new BooleanSetting("Render pumpkin overlay", false),
			renderNauseaEffect = new BooleanSetting("Render nausea effect", false),
			renderBlindnessEffect = new BooleanSetting("Render blindness effect", false);
	
	@Override
	public void loadSettings() {
		
		arrayListFont.modes.clear();
		arrayListFont.modes.addAll(Arrays.asList("Minecraft", "Arial", "Helvetica Neue", "Helvetica Neue Bold"));
		
		addSettings(arrayListFont, arrayListColors, scoreboardPosition, renderScoreboardNumbers, renderPumpkinOverlay,
				renderNauseaEffect, renderBlindnessEffect);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean isDisabled() {
		return false;
	}
	
	@Override
	public String getInfo() {
		return arrayListFont.getMode() + " - " + arrayListColors.getMode();
	}
	
}
