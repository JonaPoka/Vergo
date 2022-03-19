package xyz.vergoclient.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
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

	private static final ResourceLocation VERGOSENSE_BACKGROUND_TEXTURE;

	static {
		VERGOSENSE_BACKGROUND_TEXTURE = new ResourceLocation("fuckafriendforfree.png");
	}

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

					JelloFontRenderer fr = FontUtil.comfortaaSmall;

					String vergoStr = "vergo" + EnumChatFormatting.GREEN + "sense" + EnumChatFormatting.WHITE + " | " + ServerUtils.getServerIP() + " | " + AccountUtils.account.username + "#" + FormattingUtil.formatUID();

					RenderUtils2.drawRect(2, 2, (float) (12 + fr.getStringWidth(vergoStr)), 18, new Color(0x434343).getRGB());
					RenderUtils2.drawRect(3f, 3f, (float) (10 + fr.getStringWidth(vergoStr)), 16, new Color(0x434343).darker().getRGB());
					RenderUtils.drawImg(VERGOSENSE_BACKGROUND_TEXTURE, 3f, 3f, (float) (10 + fr.getStringWidth(vergoStr)), 16);
					RenderUtils2.drawRect(5, 5, (float) (6 + fr.getStringWidth(vergoStr)), 12, new Color(0x434343).getRGB());
					RenderUtils2.drawRect(6f, 6f, (float) (4 + fr.getStringWidth(vergoStr)), 10, new Color(0x303030).darker().getRGB());

					fr.drawString(vergoStr, 8f, 9.5f, new Color(0xffffff).getRGB());

			} else if(Vergo.config.modHud.waterMark.is("Simplistic")) {
				JelloFontRenderer fr = FontUtil.comfortaaSmall;

				JelloFontRenderer fr1 = FontUtil.comfortaaSmall;

				String clientName = "Vergo - ";

				String serverName = ServerUtils.getServerIP() + " - ";

				String userName = AccountUtils.account.username;

				BloomUtil.drawAndBloom(() -> ColorUtils.glDrawSidewaysGradientRect(3, 5, (float) (fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + fr1.getStringWidth(userName)) + 18, 1.5f, new Color(210, 8, 62).getRGB(), new Color(108, 51, 217).getRGB()));
				BlurUtil.blurArea(3, 6, (float) (fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + fr1.getStringWidth(userName)) + 18, 12f);
				RenderUtils.drawAlphaRoundedRect(3, 6, (float) (fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + fr1.getStringWidth(userName)) + 18, 12f, 0f, new Color(60, 60, 60, 100));

				fr1.drawString(clientName, 5, 11, 0xffffffff);
				fr1.drawString(serverName, fr1.getStringWidth(clientName) + 5, 11, 0xffffffff);
				fr1.drawString(userName, fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + 7, 11, 0xffffffff);

			}

			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

			GlStateManager.disableBlend();

			GlStateManager.popMatrix();
			
			// Draws the array list
			drawArrayList();
			
			// Renders all the cached images
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
	public int Rainbow = 125;

	public int waveColor2;

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

					} else if (Vergo.config.modHud.vergoColor.is("New Vergo")) {

						waveColor = ColorUtils.fadeColor(new Color(159, 0, 82), (int) offset, 25);

					} else if (Vergo.config.modHud.vergoColor.is("Nuclear Green")) {

						waveColor = ColorUtils.fadeColor(new Color(60, 213, 69), (int) offset, 30);

					} else {

						waveColor = new Color(250, 250, 250);

					}

					if (Vergo.config.modHud.barDirection.is("Right")) {
						if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
							//ChatUtils.addChatMessage(textToRender);
							BlurUtil.blurArea(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, 0, sr.getScaledWidth(), (offset + 1) * (fr.FONT_HEIGHT + 4));
							Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x40000000);
						}
						align = 4.5f;
						Gui.drawRect(sr.getScaledWidth() - 2, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), waveColor.getRGB());
					} else if (Vergo.config.modHud.barDirection.is("Left")) {
						if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
							BlurUtil.blurArea(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, 13, sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4));
							Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4), 0x40000000);
						}
						align = 2.7f;
						Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, (offset + 1) * (fr.FONT_HEIGHT + 4), sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6, (offset) * (fr.FONT_HEIGHT + 4), waveColor.getRGB());
					} else {
						if (Vergo.config.modHud.arrayListBackground.isEnabled()) {
							BlurUtil.blurArea(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6, 13, sr.getScaledWidth(), (offset) * (fr.FONT_HEIGHT + 4));
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
		}
		
		arrayListRainbow = 0;
		arrayListColor = -1;
		
	}
	
}
