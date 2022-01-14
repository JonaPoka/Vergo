package xyz.vergoclient.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.core.jmx.Server;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class Hud implements OnEventInterface {

	protected static Minecraft mc;
	
	public static void init() {
		ModuleManager.eventListeners.add(new Hud());
	}
	
	public static ResourceLocation test = null;

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {

			if(Vergo.config.modHud.theFunny.isEnabled()) {
				Display.setTitle("PAWG (Phat Ass White Girls)");
			} else {
				DisplayUtils.setTitle("1");
			}

			if (Vergo.config.modHud.bpsMode.is("Always On")) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				FontUtil.comfortaaNormal.drawStringWithShadow(Math.round(MovementUtils.getBlocksPerSecond()) + " BPS", (double) GuiScreen.width - GuiScreen.width + 2, GuiScreen.height - 20, new Color(0xFFFFFF).getRGB());
			} else if (Vergo.config.modHud.bpsMode.is("Speed Only")) {
				if (Vergo.config.modSpeed.isEnabled()) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					FontUtil.comfortaaNormal.drawStringWithShadow(Math.round(MovementUtils.getBlocksPerSecond()) + " BPS", (double) GuiScreen.width - GuiScreen.width + 2, GuiScreen.height - 20, new Color(0xFFFFFF).getRGB());
				}
			} else {
				return;
			}

			// Draws the watermark in the corner
			GlStateManager.pushMatrix();

			GlStateManager.enableBlend();
			if(Vergo.config.modHud.waterMark.is("Planet")) {
				GlStateManager.scale(1.5f, 1.5f, 1);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				RenderUtils.drawImg(new ResourceLocation("Vergo/logo/512x512clear.png"), 0, 0, 30, 30);
			} else if(Vergo.config.modHud.waterMark.is("Rounded")) {
				GlStateManager.scale(1.5f, 1.5f, 1);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				RenderUtils.drawImg(new ResourceLocation("Vergo/logo/512x512-transparent-round.png"), 0, 0, 32, 32);
			} else if(Vergo.config.modHud.waterMark.is("vergosense")) {
				//NetworkPlayerInfo you = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
				//String ping = "Ping: \247f" + (you == null ? "0" : you.responseTime);

				String s1 = "vergo           -";
				String s12 = "vergo";
				String s2 = " sense";
				String s3 = "hypixel.net";
				//String s4 = mc.thePlayer.getName();

				float width1 = 54;
				float width12 = 107;

				float width2 = 52;
				float width22 = 105;

				float width3 = 48;
				float width32 = 101;

				float width4 = 46;
				float width42 = 99;

				if(ServerUtils.isOnHypixel()) {
					RenderUtils2.drawRect(2, 2, width12, 18, new Color(0x434343).getRGB());
					RenderUtils2.drawRect(3f, 3f, width22, 16, new Color(0x434343).darker().getRGB());
					RenderUtils2.drawRect(5, 5, width32, 12, new Color(0x434343).getRGB());
					RenderUtils2.drawRect(6f, 6f, width42, 10, new Color(0x303030).darker().getRGB());
					RenderUtils2.drawRect(6, 15f, 99, 1, ColorUtils.fadeColorHorizontal(new Color(10, 193, 0),(int) 8, 10).getRGB());
				} else {
					RenderUtils2.drawRect(2, 2, width1, 18, new Color(0x434343).getRGB());
					RenderUtils2.drawRect(3f, 3f, width2, 16, new Color(0x434343).darker().getRGB());
					RenderUtils2.drawRect(5, 5, width3, 12, new Color(0x434343).getRGB());
					RenderUtils2.drawRect(6f, 6f, width4, 10, new Color(0x303030).darker().getRGB());
					RenderUtils2.drawRect(6, 15f, 46, 1, ColorUtils.fadeColorHorizontal(new Color(10, 193, 0),(int) 8, 10).getRGB());
				}

				if(ServerUtils.isOnHypixel()) {
					FontUtil.comfortaaSmall.drawString(s1, 8f, 9.5f, new Color(0xffffff).getRGB());
				} else {
					FontUtil.comfortaaSmall.drawString(s12, 8f, 9.5f, new Color(0xffffff).getRGB());
				}
				FontUtil.comfortaaSmall.drawString(s2, 27f, 9.5f, new Color(0x5AFF00).getRGB());
				if(ServerUtils.isOnHypixel()) {
					FontUtil.comfortaaSmall.drawString(s3, 62, 10f, new Color(0xffffff).getRGB());
				} else {

				}
				//FontUtil.comfortaaSmall.drawString(s4, 24.5f, 6, new Color(0xffffff).getRGB());

			}
			GlStateManager.disableBlend();

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
		}
		
	}
	
	public static transient TimerUtil arrayListToggleMovement = new TimerUtil();
	public static int arrayListRainbow = 0;
	public static int arrayListColor = -1;

	public static float align = 4.5f;

	public Color waveColor = null;

	public void drawArrayList() {
		
		arrayListRainbow = 0;
		arrayListColor = -1;
			
		if(Vergo.config.modHud.hudMode.is("Vergo")) {
			arrayListColor++;

			JelloFontRenderer fr = Vergo.config.modHud.arrayListFont.is("Neurial") ? FontUtil.neurialGrotesk : Vergo.config.modHud.arrayListFont.is("Jura") ? FontUtil.juraNormal : FontUtil.arialMedium;

			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

			ArrayList<Module> modules = new ArrayList<>();
			ModuleManager.modules.forEach(module -> {
				if (module.arrayListAnimation > 0.01 || module.isEnabled()) modules.add(module);
			});
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
						} else {
							module.arrayListAnimation -= module.arrayListAnimation / 3;
							if (module.arrayListAnimation < 0)
								module.arrayListAnimation = 0;
						}
					}

					GlStateManager.pushMatrix();

					double squeeze = module.arrayListAnimation * 2;
					if (squeeze > 1)
						squeeze = 1;

					GlStateManager.translate((float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 0, 0);

					GlStateManager.scale(1, squeeze, 1);

					GlStateManager.translate(-(float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), -((float) (offset * (fr.FONT_HEIGHT + 4)) + 0), 0);

					if (Vergo.config.modHud.vergoColor.is("Burgundy")) {

						waveColor = ColorUtils.fadeColor(new Color(196, 0, 69), (int) offset, 30);

					} else if (Vergo.config.modHud.vergoColor.is("Sea Blue")) {

						waveColor = ColorUtils.fadeColor(new Color(4, 120, 219), (int) offset, 30);

					} else if (Vergo.config.modHud.vergoColor.is("Nuclear Green")) {

						waveColor = ColorUtils.fadeColor(new Color(60, 213, 69), (int) offset, 30);

					} else {

						waveColor = new Color(250, 250, 250);

					}

					if (Vergo.config.modHud.barDirection.is("Right")) {
						if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
							Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x70000000);
						}
						align = 4.5f;
						Gui.drawRect(sr.getScaledWidth() - 2, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), waveColor.getRGB());
					} else if (Vergo.config.modHud.barDirection.is("Left")) {
						if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
							Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x70000000);
						}
						align = 2.7f;
						Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6, (offset) * (fr.FONT_HEIGHT + 4), waveColor.getRGB());
					} else {
						if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
							Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x20000000);
						}
					}


					GlStateManager.colorState.alpha = 1;

					fr.drawString(textToRender, (float) (sr.getScaledWidth() - fr.getStringWidth(textToRender) - align), (float) (offset * (fr.FONT_HEIGHT + 4)) + 3f, waveColor.getRGB());

					GlStateManager.popMatrix();
					offset++;
					if (squeeze != 1) {
						offset--;
						offset += squeeze;
					}

			}
		}else {

			if(Vergo.config.modHud.hudMode.is("Young")) {
				arrayListColor++;

				JelloFontRenderer fr = Vergo.config.modHud.arrayListFont.is("Helvetica Neue") ? FontUtil.jelloFontAddAlt3 : Vergo.config.modHud.arrayListFont.is("Helvetica Neue Bold") ? FontUtil.jelloFontBoldSmall : Vergo.config.modHud.arrayListFont.is("Jura") ? FontUtil.juraNormal : FontUtil.arialSlightlyLargerThanRegular;
//			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
				ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

				ArrayList<Module> modules = new ArrayList<>();
				ModuleManager.modules.forEach(module -> {
					if (module.arrayListAnimation > 0.01 || module.isEnabled()) modules.add(module);
				});
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
						} else {
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

					if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
						Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 4, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x90000000);
					} else {

					}

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
		}
		
		arrayListRainbow = 0;
		arrayListColor = -1;
		
	}
	
}
