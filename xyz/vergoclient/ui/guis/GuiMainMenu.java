package xyz.vergoclient.ui.guis;

import java.awt.*;
import java.io.IOException;

import net.minecraft.util.ResourceLocation;
import org.apache.http.client.methods.HttpGet;

import org.lwjgl.opengl.Display;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.DisplayUtils;
import xyz.vergoclient.util.GuiUtils;
import xyz.vergoclient.util.NetworkManager;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.TimerUtil;
import xyz.vergoclient.util.datas.DataDouble5;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiMainMenu extends GuiScreen {

	@Override
	public void initGui() {

		singlePlayer.data = 0;
		multiPlayer.data = 0;
		settings.data = 0;
		language.data = 0;
		altManager.data = 0;

		serverStatusBlink = 0;

		Display.setTitle("Vergo " + Vergo.version);

	}

	public static double scrollPercent = 0, buttonWindowY = 0, serverStatusBlink = 0;

	public DataDouble5 singlePlayer = new DataDouble5(), multiPlayer = new DataDouble5(), settings = new DataDouble5(),
			language = new DataDouble5(), altManager = new DataDouble5();

	public TimerUtil checkServerStatusDelay = new TimerUtil(), serverStatusBlinkTimer = new TimerUtil();

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		// The font renderers
		JelloFontRenderer titleFontRenderer = FontUtil.fontBig;
		JelloFontRenderer buttonFontRenderer = FontUtil.fontBig;

		// Create buttons
		double buttonWidth = width * 0.15, buttonHeight = height * 0.04, widthMiddle = width * 0.5, heightMiddle = height * 0.5,
				buttonWindowWidth = width * (1.0 / 3.0), buttonWindowHeight = height * 0.6;

		singlePlayer.x1 = widthMiddle - buttonWidth;
		singlePlayer.x2 = widthMiddle + buttonWidth;
		singlePlayer.y1 = (buttonHeight * 3) - heightMiddle + buttonWindowHeight + 10 - heightMiddle;
		singlePlayer.y2 = (buttonHeight * 5) - heightMiddle + buttonWindowHeight + 10 - heightMiddle;

		multiPlayer.x1 = widthMiddle - buttonWidth;
		multiPlayer.x2 = widthMiddle + buttonWidth;
		multiPlayer.y1 = (buttonHeight * 5) - heightMiddle + buttonWindowHeight + 20 - heightMiddle;
		multiPlayer.y2 = (buttonHeight * 7) - heightMiddle + buttonWindowHeight + 20 - heightMiddle;

		settings.x1 = widthMiddle - buttonWidth;
		settings.x2 = widthMiddle + buttonWidth;
		settings.y1 = (buttonHeight * 7) - heightMiddle + buttonWindowHeight + 30 - heightMiddle;
		settings.y2 = (buttonHeight * 9) - heightMiddle + buttonWindowHeight + 30 - heightMiddle;

		language.x1 = widthMiddle - buttonWidth;
		language.x2 = widthMiddle + buttonWidth;
		language.y1 = (buttonHeight * 9) - heightMiddle + buttonWindowHeight + 40 - heightMiddle;
		language.y2 = (buttonHeight * 11) - heightMiddle + buttonWindowHeight + 40 - heightMiddle;

		altManager.x1 = widthMiddle - buttonWidth;
		altManager.x2 = widthMiddle + buttonWidth;
		altManager.y1 = (buttonHeight * 11) - heightMiddle + buttonWindowHeight + 50 - heightMiddle;
		altManager.y2 = (buttonHeight * 13) - heightMiddle + buttonWindowHeight + 50 - heightMiddle;

		buttonWindowHeight = ((buttonHeight * 14) - heightMiddle + buttonWindowHeight + 60) - heightMiddle;
		buttonWindowHeight *= 2;

		scrollPercent = buttonWindowY / heightMiddle;
		scrollPercent += (1 - scrollPercent) / 12;
		buttonWindowY = heightMiddle * scrollPercent;

		// Render
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		Gui.drawRect(0, 0, width, height, new Color(18, 18, 18).getRGB());

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/logo/120x120-transparent-round.png"), width / 2 - 60, height /2 - 80, 120, 120);

		// Draw buttons
		buttonWindowY += 10;

		RenderUtils.drawRoundedRect(singlePlayer.x1 - 10, buttonWindowY + 30, 31, 35, 3, new Color(28, 28, 28));
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/clickgui/player.png"), singlePlayer.x1 -10, buttonWindowY + 30, 32 , 32);

		RenderUtils.drawRoundedRect(multiPlayer.x1 + 30, buttonWindowY + 30, 31, 35, 3, new Color(28, 28, 28));
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/clickgui/servers.png"), multiPlayer.x1 + 30,  buttonWindowY + 30, 32, 32);

		RenderUtils.drawRoundedRect(settings.x1 + 70, buttonWindowY + 30, 31, 35, 3, new Color(28, 28, 28));
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/clickgui/settings.png"), settings.x1 + 70, buttonWindowY + 30, 32, 32);

		RenderUtils.drawRoundedRect(altManager.x1 + 110, buttonWindowY + 30, 31, 35, 3, new Color(28, 28, 28));
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/clickgui/security.png"), altManager.x1 + 110, buttonWindowY + 30, 32, 32);

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), singlePlayer)) {

		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), multiPlayer)) {

		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), settings)) {

		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), language)) {

		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), altManager)) {

		}

		//buttonFontRenderer.drawCenteredString(".", (float)singlePlayer.x1 + 50, (float)buttonWindowY, -1);
		//buttonFontRenderer.drawCenteredString(".", (float)multiPlayer.x1 + 90, (float)buttonWindowY, -1);
		//buttonFontRenderer.drawCenteredString(".", (float)settings.x1 + 130, (float)buttonWindowY, -1);
		//buttonFontRenderer.drawCenteredString(".", (float)singlePlayer.x1 + 170, (float)buttonWindowY, -1);
		buttonWindowY -= 10;
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		if (GuiUtils.isMouseOverDataDouble5((int)singlePlayer.x1 + 50, (int) (buttonWindowY), singlePlayer)) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		} else if (GuiUtils.isMouseOverDataDouble5((int) multiPlayer.x1 + 90, (int) (buttonWindowY), multiPlayer)) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		} else if (GuiUtils.isMouseOverDataDouble5((int) settings.x1 + 130, (int) (buttonWindowY), settings)) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		} else if (GuiUtils.isMouseOverDataDouble5((int) altManager.x1 + 170, (int) (buttonWindowY), altManager)) {
			mc.displayGuiScreen(GuiAltManager.getGuiAltManager());
		}

	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {

	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {

	}

}