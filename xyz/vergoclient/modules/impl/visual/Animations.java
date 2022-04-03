package xyz.vergoclient.modules.impl.visual;

import java.util.Arrays;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSwordBlockAnimation;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;

public class Animations extends Module implements OnEventInterface {

	public Animations() {
		super("Animations", Category.VISUAL);
	}
	
	public ModeSetting mode = new ModeSetting("Style", "1.7", "1.7", "Poke", "SlowPop");
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("1.7", "Poke", "SlowPop"));
		
		addSettings(mode);
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventSwordBlockAnimation && e.isPre()) {
			EventSwordBlockAnimation event = (EventSwordBlockAnimation)e;
			event.setCanceled(false);
			
			ItemRenderer ir = mc.getItemRenderer();
			float partialTicks = ir.partTicks;
			
			float f = 1.0F - (mc.getItemRenderer().prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * partialTicks);
			float swingProgress = mc.thePlayer.getSwingProgress(partialTicks);
			
			if (mode.is("1.7")) {
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
				ir.transformFirstPersonItem(f, swingProgress);
				ir.func_178103_d();
			} else if(mode.is("Poke")) {
				GlStateManager.translate(0.15f, 0.15f, -0.25f);
				ir.transformFirstPersonItem(0.2f, - swingProgress);
				ir.func_178103_d();
			} else if(mode.is("SlowPop")) {
				GlStateManager.translate(0.15f, 0.15f, -0.25f);
				ir.transformFirstPersonItem(f, 0.2f * swingProgress);
				ir.func_178103_d();
			}
			
		}
		else if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}
		
	}

}
