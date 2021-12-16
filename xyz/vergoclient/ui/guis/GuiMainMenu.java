package xyz.vergoclient.ui.guis;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;

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

		DisplayUtils.setTitle("Main menu");

	}

	public static double scrollPercent = 0, buttonWindowY = 0, serverStatusBlink = 0;

	public DataDouble5 singlePlayer = new DataDouble5(), multiPlayer = new DataDouble5(), settings = new DataDouble5(),
			language = new DataDouble5(), altManager = new DataDouble5();

	public TimerUtil checkServerStatusDelay = new TimerUtil(), serverStatusBlinkTimer = new TimerUtil();



	/* public void pingServers() {
		new Thread(() -> {
			try {
				String hummusStatus = NetworkManager.getNetworkManager().sendGet(new HttpGet("https://hummusclient.info/api/account/login"));
				hummusServers = hummusStatus.equalsIgnoreCase("An unknown error has occurred") || hummusStatus.isEmpty() ? MojangServerStatus.RED : MojangServerStatus.GREEN;
			} catch (Exception e) {
				hummusServers = MojangServerStatus.WHITE;
				e.printStackTrace();
			}
		}).start();
	} */

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		// The font renderers
		JelloFontRenderer titleFontRenderer = FontUtil.fontBig;
		JelloFontRenderer buttonFontRenderer = FontUtil.fontBig;
		JelloFontRenderer serverStatusFontRenderer = FontUtil.fontBig;
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
		Gui.drawRect(0, 0, width, height, Colors.MAIN_MENU_BACKGROUND.getColor());

		// Server statuses
		if (checkServerStatusDelay.hasTimeElapsed(60000, true)) {
			// pingServers();
		}
		// GlStateManager.color(1, 1, 1, 1);
		// GlStateManager.colorState.alpha = 1;
		/* serverStatusFontRenderer.drawString("Hummus servers: ", 5, height - (serverStatusFontRenderer.FONT_HEIGHT * 2) - 2, -1);
		if (serverStatusBlinkTimer.hasTimeElapsed(100, true)) {
			serverStatusBlink += 0.1;
			if (serverStatusBlink >= 2)
				serverStatusBlink = 0;
		} */

		// double blurSize = 6;
		// blurSize += 5 * ((serverStatusBlink > 1 ? 2 - serverStatusBlink : serverStatusBlink));
		// RenderUtils.drawPointWithBlur(5 + serverStatusFontRenderer.getStringWidth("Hummus servers: ") + 5, height - serverStatusFontRenderer.FONT_HEIGHT - 0.6f - 2.5, hummusServers.color, (int) blurSize);

		// Button window drop shadow
		String title = "Vergo";

		//let me push ffs sssssssssss

		/* Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 13, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 13,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 13, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 13,
				0x02000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 11, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 11,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 11, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 11,
				0x05000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 10, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 10,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 10, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 10,
				0x10000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 9, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 9,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 9, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 9,
				0x15000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 8, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 8,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 8, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 8,
				0x20000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 7, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 7,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 7, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 7,
				0x25000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 6, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 6,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 6, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 6,
				0x30000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 5, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 5,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 5, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 5,
				0x35000000);
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 4, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 4,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 4, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 4,
				0x40000000); */

		/* Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 12, buttonWindowY - (buttonWindowHeight / 2) - 12,
				widthMiddle + (buttonWindowWidth / 2) + 12, buttonWindowY + (buttonWindowHeight / 2) + 12,
				0x02000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 10, buttonWindowY - (buttonWindowHeight / 2) - 10,
				widthMiddle + (buttonWindowWidth / 2) + 10, buttonWindowY + (buttonWindowHeight / 2) + 10,
				0x05000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 9, buttonWindowY - (buttonWindowHeight / 2) - 9,
				widthMiddle + (buttonWindowWidth / 2) + 9, buttonWindowY + (buttonWindowHeight / 2) + 9,
				0x10000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 8, buttonWindowY - (buttonWindowHeight / 2) - 8,
				widthMiddle + (buttonWindowWidth / 2) + 8, buttonWindowY + (buttonWindowHeight / 2) + 8,
				0x15000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 7, buttonWindowY - (buttonWindowHeight / 2) - 7,
				widthMiddle + (buttonWindowWidth / 2) + 7, buttonWindowY + (buttonWindowHeight / 2) + 7,
				0x20000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 6, buttonWindowY - (buttonWindowHeight / 2) - 6,
				widthMiddle + (buttonWindowWidth / 2) + 6, buttonWindowY + (buttonWindowHeight / 2) + 6,
				0x25000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 5, buttonWindowY - (buttonWindowHeight / 2) - 5,
				widthMiddle + (buttonWindowWidth / 2) + 5, buttonWindowY + (buttonWindowHeight / 2) + 5,
				0x30000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 4, buttonWindowY - (buttonWindowHeight / 2) - 4,
				widthMiddle + (buttonWindowWidth / 2) + 4, buttonWindowY + (buttonWindowHeight / 2) + 4,
				0x35000000);
		Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 3, buttonWindowY - (buttonWindowHeight / 2) - 3,
				widthMiddle + (buttonWindowWidth / 2) + 3, buttonWindowY + (buttonWindowHeight / 2) + 3,
				0x40000000);

		 */

		// Color outline
		 double colorOffset = 0, colorAdd = 7;
		for (double x = widthMiddle - (buttonWindowWidth / 2) - 2; x < (widthMiddle + (buttonWindowWidth / 2) + 2) * 0.75; x++) {
			colorOffset += colorAdd;
			Gui.drawRect(((widthMiddle + (buttonWindowWidth / 2) - (widthMiddle - (buttonWindowWidth / 2))) / 2) + x, buttonWindowY - (buttonWindowHeight / 2) - 2,
					((widthMiddle + (buttonWindowWidth / 2) - (widthMiddle - (buttonWindowWidth / 2))) / 2) + x + 1, buttonWindowY - (buttonWindowHeight / 2),
					RenderUtils.getRainbow(colorOffset));
		}
		for (double y = buttonWindowY - (buttonWindowHeight / 2); y <= buttonWindowY + (buttonWindowHeight / 2) + 2; y++) {
			colorOffset += colorAdd;
			Gui.drawRect(widthMiddle + (buttonWindowWidth / 2), y, widthMiddle + (buttonWindowWidth / 2) + 2, y - 1,
					RenderUtils.getRainbow(colorOffset));
		}
		for (double x = widthMiddle - (buttonWindowWidth / 2) - 2; x < widthMiddle + (buttonWindowWidth / 2) + 2; x++) {
			colorOffset += colorAdd;
			Gui.drawRect((widthMiddle + (buttonWindowWidth / 2) + 2) - (x - (widthMiddle - (buttonWindowWidth / 2) - 2)), buttonWindowY + (buttonWindowHeight / 2) + 2,
					widthMiddle - (buttonWindowWidth / 2) - 2, buttonWindowY + (buttonWindowHeight / 2),
					RenderUtils.getRainbow(colorOffset));
		}
		for (double y = buttonWindowY + (buttonWindowHeight / 2); y >= buttonWindowY - (buttonWindowHeight / 2) - 2; y--) {
			colorOffset += colorAdd;
			Gui.drawRect(widthMiddle - (buttonWindowWidth / 2) - 2, y, widthMiddle - (buttonWindowWidth / 2), y + 1,
					RenderUtils.getRainbow(colorOffset));
		}
		for (double x = widthMiddle - (buttonWindowWidth / 2) - 2; x < (widthMiddle + (buttonWindowWidth / 2) + 2) * 0.75; x++) {
			colorOffset += colorAdd;
			Gui.drawRect(x, buttonWindowY - (buttonWindowHeight / 2) - 2,
					x + 1, buttonWindowY - (buttonWindowHeight / 2),
					RenderUtils.getRainbow(colorOffset));
		}

		// Main button window
		// Gui.drawRect(widthMiddle - (buttonWindowWidth / 2), buttonWindowY - (buttonWindowHeight / 2),
				// widthMiddle + (buttonWindowWidth / 2), buttonWindowY + (buttonWindowHeight / 2),
				// Colors.MAIN_MENU_BUTTON_WINDOW.getColor());
		GlStateManager.colorState.alpha = 1;
		Gui.drawRect(widthMiddle - (titleFontRenderer.getStringWidth(title) / 2) - 3, buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) - 3,
				widthMiddle + (titleFontRenderer.getStringWidth(title) / 2) + 3, buttonWindowY - (buttonWindowHeight / 2) + (titleFontRenderer.FONT_HEIGHT) + 3,
				Colors.MAIN_MENU_TITLE_BACKGROUND_COLOR.getColor());
		titleFontRenderer.drawString(title, widthMiddle - (titleFontRenderer.getStringWidth(title) / 2), (float) (buttonWindowY - (buttonWindowHeight / 2) - (titleFontRenderer.FONT_HEIGHT) + 1), -1);

		// Draw buttons
		buttonWindowY += 10;

		Gui.drawRect(singlePlayer.x1, singlePlayer.y1 + buttonWindowY, singlePlayer.x2, singlePlayer.y2 + buttonWindowY, Colors.MAIN_MENU_TITLE_BACKGROUND_COLOR.getColor());
		Gui.drawRect(multiPlayer.x1, multiPlayer.y1 + buttonWindowY, multiPlayer.x2, multiPlayer.y2 + buttonWindowY, Colors.MAIN_MENU_TITLE_BACKGROUND_COLOR.getColor());
		Gui.drawRect(settings.x1, settings.y1 + buttonWindowY, settings.x2, settings.y2 + buttonWindowY, Colors.MAIN_MENU_TITLE_BACKGROUND_COLOR.getColor());
		Gui.drawRect(language.x1, language.y1 + buttonWindowY, language.x2, language.y2 + buttonWindowY, Colors.MAIN_MENU_TITLE_BACKGROUND_COLOR.getColor());
		Gui.drawRect(altManager.x1, altManager.y1 + buttonWindowY, altManager.x2, altManager.y2 + buttonWindowY, Colors.MAIN_MENU_TITLE_BACKGROUND_COLOR.getColor());

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), singlePlayer)) {
			singlePlayer.data += (1 - singlePlayer.data) / 2;
		}else {
			singlePlayer.data -= (singlePlayer.data) / 2;
		}
		if (singlePlayer.data < 0)
			singlePlayer.data = 0;
		if (singlePlayer.data > 1)
			singlePlayer.data = 1;
		for (double x = singlePlayer.x1; x < singlePlayer.x2; x++) {
			// colorOffset += colorAdd;
			Gui.drawRect(x, singlePlayer.y2 + buttonWindowY - ((buttonHeight / 4) * singlePlayer.data), x + 1, singlePlayer.y2 + buttonWindowY, Colors.ALT_MANAGER_PURPLE.getColor());
		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), multiPlayer)) {
			multiPlayer.data += (1 - multiPlayer.data) / 2;
		}else {
			multiPlayer.data -= (multiPlayer.data) / 2;
		}
		if (multiPlayer.data < 0)
			multiPlayer.data = 0;
		if (multiPlayer.data > 1)
			multiPlayer.data = 1;
		for (double x = multiPlayer.x1; x < multiPlayer.x2; x++) {
			// colorOffset += colorAdd;
			Gui.drawRect(x, multiPlayer.y2 + buttonWindowY - ((buttonHeight / 4) * multiPlayer.data), x + 1, multiPlayer.y2 + buttonWindowY, Colors.ALT_MANAGER_PURPLE.getColor());
		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), settings)) {
			settings.data += (1 - settings.data) / 2;
		}else {
			settings.data -= (settings.data) / 2;
		}
		if (settings.data < 0)
			settings.data = 0;
		if (settings.data > 1)
			settings.data = 1;
		for (double x = settings.x1; x < settings.x2; x++) {
			// colorOffset += colorAdd;
			Gui.drawRect(x, settings.y2 + buttonWindowY - ((buttonHeight / 4) * settings.data), x + 1, settings.y2 + buttonWindowY, Colors.ALT_MANAGER_PURPLE.getColor());
		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), language)) {
			language.data += (1 - language.data) / 2;
		}else {
			language.data -= (language.data) / 2;
		}
		if (language.data < 0)
			language.data = 0;
		if (language.data > 1)
			language.data = 1;
		for (double x = language.x1; x < language.x2; x++) {
			// colorOffset += colorAdd;
			Gui.drawRect(x, language.y2 + buttonWindowY - ((buttonHeight / 4) * language.data), x + 1, language.y2 + buttonWindowY, Colors.ALT_MANAGER_PURPLE.getColor());
		}

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY), altManager)) {
			altManager.data += (1 - altManager.data) / 2;
		}else {
			altManager.data -= (altManager.data) / 2;
		}
		if (altManager.data < 0)
			altManager.data = 0;
		if (altManager.data > 1)
			altManager.data = 1;
		for (double x = altManager.x1; x < altManager.x2; x++) {
			// colorOffset += colorAdd;
			Gui.drawRect(x, altManager.y2 + buttonWindowY - ((buttonHeight / 4) * altManager.data), x + 1, altManager.y2 + buttonWindowY, Colors.ALT_MANAGER_PURPLE.getColor());
		}

		buttonFontRenderer.drawCenteredString(I18n.format("menu.singleplayer", new Object[0]), (float) (singlePlayer.x1 + ((singlePlayer.x2 - singlePlayer.x1) / 2)), (float) ((singlePlayer.y1 + ((singlePlayer.y2 - singlePlayer.y1) / 2)) - buttonFontRenderer.FONT_HEIGHT + 1.5 + buttonWindowY), -1);
		buttonFontRenderer.drawCenteredString(I18n.format("menu.multiplayer", new Object[0]), (float) (multiPlayer.x1 + ((multiPlayer.x2 - multiPlayer.x1) / 2)), (float) ((multiPlayer.y1 + ((multiPlayer.y2 - multiPlayer.y1) / 2)) - buttonFontRenderer.FONT_HEIGHT + 1.5 + buttonWindowY), -1);
		buttonFontRenderer.drawCenteredString(I18n.format("menu.options", new Object[0]).replace("...", ""), (float) (settings.x1 + ((settings.x2 - settings.x1) / 2)), (float) ((settings.y1 + ((settings.y2 - settings.y1) / 2)) - buttonFontRenderer.FONT_HEIGHT + 1.5 + buttonWindowY), -1);
		buttonFontRenderer.drawCenteredString(I18n.format("options.language", new Object[0]).replace("...", ""), (float) (language.x1 + ((language.x2 - language.x1) / 2)), (float) ((language.y1 + ((language.y2 - language.y1) / 2)) - buttonFontRenderer.FONT_HEIGHT + 1.5 + buttonWindowY), -1);
		try {
			buttonFontRenderer.drawCenteredString("Alts", (float) (altManager.x1 + ((altManager.x2 - altManager.x1) / 2)), (float) ((altManager.y1 + ((altManager.y2 - altManager.y1) / 2)) - buttonFontRenderer.FONT_HEIGHT + 1.5 + buttonWindowY), -1);
		} catch (Exception e) {
			buttonFontRenderer.drawCenteredString("Alts", (float) (altManager.x1 + ((altManager.x2 - altManager.x1) / 2)), (float) ((altManager.y1 + ((altManager.y2 - altManager.y1) / 2)) - buttonFontRenderer.FONT_HEIGHT + 1.5 + buttonWindowY), -1);
		}
		buttonWindowY -= 10;
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY - 10), singlePlayer)) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		} else if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY - 10), multiPlayer)) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		} else if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY - 10), settings)) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		} else if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY - 10), language)) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		} else if (GuiUtils.isMouseOverDataDouble5(mouseX, (int) (mouseY - buttonWindowY - 10), altManager)) {
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
