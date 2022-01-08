package xyz.vergoclient.modules.impl.visual;

import java.util.Arrays;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;

public class ModHud extends Module {

	public ModHud() {
		super("Hud", Category.VISUAL);
	}
	
	public ModeSetting hudMode = new ModeSetting("Mode", "Young", "Vergo", "Young"),
			bpsMode = new ModeSetting("BPS Mode", "Always On", "Always On", "Speed Only", "Off"),
			vergoColor = new ModeSetting("Vergo Color Scheme", "Burgundy", "Burgundy", "Sea Blue", "Nuclear Green"),
			barDirection = new ModeSetting("Align Color Bar", "Right", "Right", "Left", "None"),
			waterMark = new ModeSetting("Watermark", "Rounded", "Rounded", "Planet", "None"),
			arrayListFont = new ModeSetting("ArrayList Font", "Jura", "Arial", "Helvetica Neue", "Helvetica Neue Bold", "Jura", "Neurial"),
			arrayListColors = new ModeSetting("ArrayList colors", "Rainbow", "Default", "Rainbow");
	public BooleanSetting renderScoreboardNumbers = new BooleanSetting("Render Scoreboard Numbers", false),
						  arrayListBackground = new BooleanSetting("ArrayList Background", true),
						  theFunny = new BooleanSetting("TheFunnyName", false);
	@Override
	public void loadSettings() {

		hudMode.modes.clear();
		hudMode.modes.addAll(Arrays.asList("Young", "Vergo"));

		bpsMode.modes.clear();
		bpsMode.modes.addAll(Arrays.asList("Always On", "Speed Only", "Off"));

		vergoColor.modes.clear();
		vergoColor.modes.addAll(Arrays.asList("Burgundy", "Sea Blue", "Nuclear Green"));

		arrayListFont.modes.clear();
		arrayListFont.modes.addAll(Arrays.asList("Arial", "Helvetica Neue", "Helvetica Neue Bold", "Jura", "Neurial"));

		waterMark.modes.clear();
		waterMark.modes.addAll(Arrays.asList("Rounded", "Planet", "None"));

		barDirection.modes.clear();
		barDirection.modes.addAll(Arrays.asList("Right", "Left", "None"));

		//arrayListColors.modes.clear();
		//arrayListColors.modes.addAll(Arrays.asList("Default", "Rainbow"));
		
		addSettings(hudMode, bpsMode, vergoColor, barDirection, waterMark, arrayListFont, /*arrayListColors*/ arrayListBackground, renderScoreboardNumbers, theFunny);

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
