package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.ui.click.GuiClickGui;

public class ClickGui extends Module {

	public ClickGui() {
		super("Clickgui", Category.MISCELLANEOUS);
		
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
			mc.displayGuiScreen(GuiClickGui.getClickGui());
		}
		silentToggle();
	}
	
}
