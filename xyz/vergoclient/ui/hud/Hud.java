package xyz.vergoclient.ui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.ui.hud.elements.Watermark;
import xyz.vergoclient.ui.hud.elements.arrayList.VergoTheme;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.Gl.BloomUtil;
import xyz.vergoclient.util.Gl.BlurUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Hud implements OnEventInterface {

	protected static Minecraft mc;
	
	public static void init() {
		ModuleManager.eventListeners.add(new Hud());
	}
	
	public static ResourceLocation test = null;

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {

			// The epic funny name.
			if(Vergo.config.modHud.theFunny.isEnabled()) {
				Display.setTitle("PAWG (Phat Ass White Girls)");
			} else {
				DisplayUtils.setTitle();
			}

			// Speed counter
			if (Vergo.config.modHud.bpsMode.is("Always On")) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				FontUtil.comfortaaNormal.drawStringWithShadow(Math.round(MovementUtils.getBlocksPerSecond()) + " BPS", (double) GuiScreen.width - GuiScreen.width + 2, GuiScreen.height - 20, new Color(0xFFFFFF).getRGB());
			} else if (Vergo.config.modHud.bpsMode.is("Speed Only")) {
				if (Vergo.config.modSpeed.isEnabled()) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					FontUtil.comfortaaNormal.drawStringWithShadow(Math.round(MovementUtils.getBlocksPerSecond()) + " BPS", (double) GuiScreen.width - GuiScreen.width + 2, GuiScreen.height - 20, new Color(0xFFFFFF).getRGB());
				}
			} else {
				// Do Nothing.
			}

			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

			// Draws the arraylist
			VergoTheme.drawArrayList();
			// Draws the watermark.
			Watermark.drawWatermark();

			// Renders all the cached images
			for (ResourceLocation cachedIcon : Vergo.cachedIcons) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(cachedIcon);
				int imageWidth = 1, imageHeight = 1;
				Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() + 10, sr.getScaledHeight() + 10, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
			}
		}
		
	}
	
}