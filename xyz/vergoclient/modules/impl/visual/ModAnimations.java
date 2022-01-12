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
import net.minecraft.util.MathHelper;

public class ModAnimations extends Module implements OnEventInterface {

	public ModAnimations() {
		super("Animations", Category.VISUAL);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "1.7", "1.7", "Jiggle", "Wand", "Tap", "Smooth", "Poke");
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("1.7", "Jiggle", "Wand", "Tap", "Smooth", "Poke"));
		
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
			float swingProgressReversed = 1.0f - swingProgress;
			
			if (mode.is("1.7")) {
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
				ir.transformFirstPersonItem(f, swingProgress);
				ir.func_178103_d();
			}
			else if (mode.is("Jiggle")) {
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
		        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
		        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		        float f1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
		        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
		        GlStateManager.rotate(f1 * -20.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f2 * 10.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.rotate(f2 * -80.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(0.4F, 0.4F, 0.4F);
				ir.func_178103_d();
			}
			else if (mode.is("Wand")) {
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
		        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
		        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		        float f1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
		        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
		        GlStateManager.rotate(-10.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(-10.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.rotate(f1 * -20.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
//		        GlStateManager.rotate(f2 * -80.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(0.4F, 0.4F, 0.4F);
				ir.func_178103_d();
			}
			else if (mode.is("Tap")) {
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
		        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
		        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		        float f1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
		        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
		        GlStateManager.rotate(f1 * -20.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.rotate(f2 * -10.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(0.4F, 0.4F, 0.4F);
				ir.func_178103_d();
			}
			else if (mode.is("Smooth")) {
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
		        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
		        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		        float f1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
		        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress <= 0.5 ? swingProgressReversed : swingProgress) * (float)Math.PI);
		        GlStateManager.rotate(f1 * -20.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.rotate(f2 * -40.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(0.4F, 0.4F, 0.4F);
				ir.func_178103_d();
			}
			else if (mode.is("Poke")) {
		        float f1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
		        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
		        GlStateManager.rotate(f2 * -6, 1, 0, 0);
		        GlStateManager.rotate(f2 * -6, 0, 1, 0);
		        GlStateManager.rotate(f2 * -6, 0, 0, 1);
				GlStateManager.translate(-0.15f, 0.15f, -0.2f);
		        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
		        GlStateManager.rotate(10.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f1 * -2.0F, 0.0F, 1.0F, 0.0F);
//		        GlStateManager.rotate(f2 * -30, 1, 0, 1);
//		        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
//		        GlStateManager.rotate(f2 * -80.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(0.4F, 0.4F, 0.4F);
				ir.func_178103_d();
			}
			
		}
		else if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}
		
	}

}
