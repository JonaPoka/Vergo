package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.ui.guis.GuiClickGui;

public class ModClickgui extends Module {

	public ModClickgui() {
		super("Clickgui", Category.MISCELLANEOUS);
		
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Old", "Old", "Old");
	
	@Override
	public void loadSettings() {
		addSettings(mode);
	}
	
	@Override
	public boolean isEnabled() {
		return mc.currentScreen instanceof GuiClickGui;
	}
	
	@Override
	public boolean isDisabled() {
		return !(mc.currentScreen instanceof GuiClickGui);
	}
	
	@Override
	public void onEnable() {
		if (mc.currentScreen instanceof GuiClickGui)
			mc.displayGuiScreen(null);
		else {
			if (mode.is("Old")) {
				mc.displayGuiScreen(GuiClickGui.getClickGui());
			}
		}
		silentToggle();
	}
	
}
