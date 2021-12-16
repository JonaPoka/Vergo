package xyz.vergoclient.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Hud implements OnEventInterface {
	
	public static void init() {
		ModuleManager.eventListeners.add(new Hud());
	}
	
	public static ResourceLocation test = null;
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {
			
			// Draws the watermark in the corner
			GlStateManager.pushMatrix();


			
			String clientName = "VERGO";
			GlStateManager.scale(1.5f, 1.5f, 1);
			if (Vergo.config.modRainbow.isEnabled()) {
				float rainbowBarMax = Minecraft.getMinecraft().fontRendererObj.getStringWidth(clientName) + 3f;
				int rainbowOffset = 400;
				for (int i = 1; i <= rainbowBarMax; i++) {
					rainbowOffset += 15;
					Gui.drawRect(i - 1, Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2, i, Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 3, RenderUtils.getRainbow(rainbowOffset));
				}
			}
			Minecraft.getMinecraft().fontRendererObj.drawString(clientName, 1, 2, Colors.ARRAY_LIST_MODULE_NAMES.getColor(), false);
			GlStateManager.popMatrix();
			
			// Draws the array list
			drawArrayList();
			
			// Renders all the cached images
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			for (ResourceLocation cachedIcon : Vergo.cachedIcons) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(cachedIcon);
				int imageWidth = 1, imageHeight = 1;
				Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() + 10, sr.getScaledHeight() + 10, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
			}
			
			/*Gui.drawRect(0, sr.getScaledHeight() - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2) - 4, (Minecraft.getMinecraft().fontRendererObj.getStringWidth(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id)) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(MiscellaneousUtils.getFormattedSessionTime() + " - " + new DecimalFormat("###00.00").format(MovementUtils.getBlocksPerSecond()) + " BPS") ? Minecraft.getMinecraft().fontRendererObj.getStringWidth(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id)) : Minecraft.getMinecraft().fontRendererObj.getStringWidth(MiscellaneousUtils.getFormattedSessionTime() + " - " + new DecimalFormat("###00.00").format(MovementUtils.getBlocksPerSecond()) + " BPS")) + 3f, sr.getScaledHeight(), 0x90000000);
			
			if (Hummus.config.modRainbow.isEnabled()) {
				float rainbowBarMax = (Minecraft.getMinecraft().fontRendererObj.getStringWidth(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id)) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(MiscellaneousUtils.getFormattedSessionTime() + " - " + new DecimalFormat("###00.00").format(MovementUtils.getBlocksPerSecond()) + " BPS") ? Minecraft.getMinecraft().fontRendererObj.getStringWidth(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id)) : Minecraft.getMinecraft().fontRendererObj.getStringWidth(MiscellaneousUtils.getFormattedSessionTime() + " - " + new DecimalFormat("###00.00").format(MovementUtils.getBlocksPerSecond()) + " BPS")) + 3f;
				int rainbowOffset = 400;
				for (int i = 1; i <= rainbowBarMax; i++) {
					rainbowOffset += 15;
					Gui.drawRect(i - 1, sr.getScaledHeight() - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2) - 4, i, sr.getScaledHeight() - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2) - 5, RenderUtils.getRainbow(rainbowOffset));
				}
			}
			
			// Shows the logged in user and the session time

			Minecraft.getMinecraft().fontRendererObj.drawString(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id), 1, sr.getScaledHeight() - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT - 1, Colors.ARRAY_LIST_MODULE_NAMES.getColor(), true);
			Minecraft.getMinecraft().fontRendererObj.drawString(MiscellaneousUtils.getFormattedSessionTime() + " - " + new DecimalFormat("###00.00").format(MovementUtils.getBlocksPerSecond()) + " BPS", 1, sr.getScaledHeight() - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2) - 1, Colors.ARRAY_LIST_MODULE_NAMES.getColor(), true);
			*/
		}
		
	}
	
	public static transient TimerUtil arrayListToggleMovement = new TimerUtil();
	public static int arrayListRainbow = 0;
	public static int arrayListColor = -1;
	
	public void drawArrayList() {
		
		arrayListRainbow = 0;
		arrayListColor = -1;
		
		if (Vergo.config.modHud.arrayListFont.is("Minecraft")) {
			
			arrayListColor++;
			
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			
			ArrayList<Module> modules = new ArrayList<>();
			ModuleManager.modules.forEach(module -> {if (module.arrayListAnimation > 0.01 || module.isEnabled()) modules.add(module);});
			modules.sort(Comparator.comparingDouble(module -> fr.getStringWidth(module.getName() + (module.getInfo().isEmpty() ? "" : " " + module.getInfo()))));
			Collections.reverse(modules);
			
			boolean updateToggleMovement = arrayListToggleMovement.hasTimeElapsed(1000 / 40, true);
			
			double offset = 0;
			for (Module module : modules) {
				
				arrayListRainbow += 125;
				arrayListColor++;
				
				String textToRender = module.getName() + " §7" + module.getInfo();
				if (module.getInfo().isEmpty())
					textToRender = module.getName();
				
				if (updateToggleMovement) {
					if (module.isEnabled()) {
						module.arrayListAnimation += (1 - module.arrayListAnimation) / 8;
						if (module.arrayListAnimation > 1)
							module.arrayListAnimation = 1;
					}else {
						module.arrayListAnimation -= module.arrayListAnimation / 3;
						if (module.arrayListAnimation < 0)
							module.arrayListAnimation = 0;
					}
				}
				
				GlStateManager.pushMatrix();
				
				double squeeze = module.arrayListAnimation * 2;
				if (squeeze > 1)
					squeeze = 1;
				
//				GlStateManager.translate(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4, (offset + 1) * (fr.FONT_HEIGHT * 1.5), 0);
				GlStateManager.translate((float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 0, 0);
//				GlStateManager.scale(squeeze, squeeze, 1);
				GlStateManager.scale(1, squeeze, 1);
//				GlStateManager.translate(-(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4), -((offset) * (fr.FONT_HEIGHT * 1.5)), 0);
				GlStateManager.translate(-(float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), -((float) (offset * (fr.FONT_HEIGHT + 4)) + 0), 0);
				
				Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x90000000);
				
				// Used for a jello font renderer
//				fr.drawString(textToRender, (float) (sr.getScaledWidth() - fr.getStringWidth(textToRender) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 2.5f, Colors.ARRAYLISTMODULENAMES.getColor());
				
				// Used for the minecraft font renderer
				fr.drawString(textToRender, (float) (sr.getScaledWidth() - fr.getStringWidth(textToRender) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 2.5f, Colors.ARRAY_LIST_MODULE_NAMES.getColor(), false);
				
				GlStateManager.popMatrix();
				offset++;
				if (squeeze != 1) {
					offset--;
					offset += squeeze;
				}
			}
		}else {
			
			arrayListColor++;
			
			JelloFontRenderer fr = Vergo.config.modHud.arrayListFont.is("Helvetica Neue") ? FontUtil.jelloFontAddAlt3 : Vergo.config.modHud.arrayListFont.is("Helvetica Neue Bold") ? FontUtil.jelloFontBoldSmall : FontUtil.arialSlightlyLargerThanRegular;
//			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			
			ArrayList<Module> modules = new ArrayList<>();
			ModuleManager.modules.forEach(module -> {if (module.arrayListAnimation > 0.01 || module.isEnabled()) modules.add(module);});
			modules.sort(Comparator.comparingDouble(module -> fr.getStringWidth(module.getName() + (module.getInfo().isEmpty() ? "" : " " + module.getInfo()))));
			Collections.reverse(modules);
			
			boolean updateToggleMovement = arrayListToggleMovement.hasTimeElapsed(1000 / 40, true);
			
			double offset = 0;
			for (Module module : modules) {
				
				arrayListRainbow += 125;
				arrayListColor++;
				
				String textToRender = module.getName() + " §7" + module.getInfo();
				if (module.getInfo().isEmpty())
					textToRender = module.getName();
				
				if (updateToggleMovement) {
					if (module.isEnabled()) {
						module.arrayListAnimation += (1 - module.arrayListAnimation) / 8;
						if (module.arrayListAnimation > 1)
							module.arrayListAnimation = 1;
					}else {
						module.arrayListAnimation -= module.arrayListAnimation / 3;
						if (module.arrayListAnimation < 0)
							module.arrayListAnimation = 0;
					}
				}
				
				GlStateManager.pushMatrix();
				
				double squeeze = module.arrayListAnimation * 2;
				if (squeeze > 1)
					squeeze = 1;
				
//				GlStateManager.translate(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4, (offset + 1) * (fr.FONT_HEIGHT * 1.5), 0);
				//GlStateManager.translate((float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 0, 0);
//				GlStateManager.scale(squeeze, squeeze, 1);
				GlStateManager.scale(1, squeeze, 1);
//				GlStateManager.translate(-(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4), -((offset) * (fr.FONT_HEIGHT * 1.5)), 0);
				//GlStateManager.translate(-(float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), -((float) (offset * (fr.FONT_HEIGHT + 4)) + 0), 0);
				
				Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x90000000);
				
				// Used for a jello font renderer
//				fr.drawString(textToRender, (float) (sr.getScaledWidth() - fr.getStringWidth(textToRender) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 2.5f, Colors.ARRAYLISTMODULENAMES.getColor());
				
				// Used for the minecraft font renderer
				GlStateManager.colorState.alpha = 1;
				fr.drawString(textToRender, (float) (sr.getScaledWidth() - fr.getStringWidth(textToRender) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 2.5f, Colors.ARRAY_LIST_MODULE_NAMES.getColor());
				
				GlStateManager.popMatrix();
				offset++;
				if (squeeze != 1) {
					offset--;
					offset += squeeze;
				}
			}
		}
		
		arrayListRainbow = 0;
		arrayListColor = -1;
		
	}
	
}
