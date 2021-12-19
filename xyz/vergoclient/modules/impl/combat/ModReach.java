package xyz.vergoclient.modules.impl.combat;

import xyz.vergoclient.modules.Module;
import xyz.vergoclient.settings.NumberSetting;

public class ModReach extends Module {

	public ModReach() {
		super("Reach", Category.COMBAT);
	}
	
	public NumberSetting combatReach = new NumberSetting("Combat reach", 3, 3, 7, 0.1),
			blockReach = new NumberSetting("Block reach", 4.5, 4.5, 6, 0.1);
	
	@Override
	public void loadSettings() {
		addSettings(combatReach, blockReach);
	}
	
}
