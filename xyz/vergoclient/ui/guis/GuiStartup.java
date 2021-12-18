package xyz.vergoclient.ui.guis;

import java.awt.*;
import java.io.IOException;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.util.RandomStringUtil;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.util.ResourceLocation;

public class GuiStartup extends GuiScreen {
	
	public static boolean hasLoaded = false, hasStartedLoading = false;
	public static TimerUtil waitTimer = new TimerUtil();
	
	public static double percentDoneTarget = 0, percentDone = 0;
	public static String percentText = RandomStringUtil.getRandomLoadingMsg();
	
	public GuiStartup() {
		
		waitTimer.reset();
		
		if (hasLoaded && mc != null)
			mc.displayGuiScreen(new GuiMultiplayer(new GuiAltManager()));
//		else
//			Vergo.startup();
	}

	int count = 0;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		if (!hasStartedLoading && waitTimer.hasTimeElapsed(3000, true)) {
			hasStartedLoading = true;
			Vergo.startup();
		}
		
		// If it is done loading then show the main menu
		if (hasLoaded)
			mc.displayGuiScreen(new GuiMultiplayer(new GuiAltManager()));
		
		// Background
		Gui.drawRect(0, 0, width, height, new Color(18, 18, 18).getRGB());
		
		/* Moves the progress bar
		if (percentDone != percentDoneTarget)
			percentDone += (percentDoneTarget - percentDone) / 8;
		
		if (percentDone >= 0.9995670207585011) {
			percentDone = 1;
		}

		if (percentDoneTarget >= 1)
			if(count == 0) {
				percentText = RandomStringUtil.getRandomLoadingMsg();
				count++;
			}
		
		// Progress bar
		Gui.drawRect(width / 8, height - 20, (width / 8) * 7, height - 5, Colors.START_GUI_PROGRESS_BAR_BACKGROUND.getColor());
		Gui.drawRect(width / 8, height - 20, (width / 8) + (((width / 8) * 6) * percentDone), height - 5, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		
		// Draws text in middle of screen and above progress bar
		FontUtil.bakakakmedium.drawCenteredString(percentText, width / 2, height - 25 - FontUtil.arialMedium.FONT_HEIGHT, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		FontUtil.bakakakBigger.drawCenteredString(percentDone >= 0.9 ? jiggeyGOD : "Vergo", width / 2, (height / 3) - FontUtil.arialBigger.FONT_HEIGHT, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		FontUtil.bakakakBigger.drawCenteredString(percentDone >= 0.9 ? FuckMoon : "Rismose & jiggey", width / 2, (height / 2) - FontUtil.arialBigger.FONT_HEIGHT, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		*/


		GlStateManager.enableBlend();
		GlStateManager.color((float) 1.0, (float) 1.0, (float) 1.0, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/logo/512x512clear.png"), width / 2 - 60, height / 2 - 60, 100, 100);
		GlStateManager.disableBlend();
		FontUtil.bakakakmedium.drawCenteredString(percentText, width / 2 - 10, height / 2 + 70 - FontUtil.arialMedium.FONT_HEIGHT, new Color(144, 106, 235).getRGB());

		// Renders all the cached images
		ScaledResolution sr = new ScaledResolution(mc);
		for (ResourceLocation cachedIcon : Vergo.cachedIcons) {
			mc.getTextureManager().bindTexture(cachedIcon);
			int imageWidth = 1, imageHeight = 1;
			Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() + 10, sr.getScaledHeight() + 10, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
		}
	}
	
	// We override this so you can't trigger the startup tasks more than once
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
	}
	
}
