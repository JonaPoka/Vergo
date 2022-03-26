package xyz.vergoclient.modules.impl.visual;

import java.util.Arrays;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;

public class Hud extends Module {

	public Hud() {
		super("Hud", Category.VISUAL);
	}

	public ModeSetting currentTheme = new ModeSetting("Theme", "Default", "Default"),
						waterMark = new ModeSetting("Watermark", "Simple", "Simple", "vergosense", "Text", "Planet"),
						bpsMode = new ModeSetting("BPS Count", "Always On", "Speed Only", "Never"),
						vergoColor = new ModeSetting("Colours", "Burgundy", "Burgundy", "Sea Blue", "Nuclear Green");

	public BooleanSetting theFunny = new BooleanSetting("TheFunnyName", false), blurToggle = new BooleanSetting("ArrayBlur", true);

	@Override
	public void loadSettings() {
		currentTheme.modes.addAll(Arrays.asList("Default"));

		waterMark.modes.addAll(Arrays.asList("Simple", "vergosense", "Text", "Planet"));

		addSettings(currentTheme, waterMark, bpsMode, theFunny, blurToggle);

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
