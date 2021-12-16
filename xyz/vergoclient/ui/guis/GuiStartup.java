package xyz.vergoclient.ui.guis;

import java.io.IOException;

import net.minecraft.client.renderer.GlStateManager;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class GuiStartup extends GuiScreen {
	
	public static boolean hasLoaded = false, hasStartedLoading = false;
	public static TimerUtil waitTimer = new TimerUtil();
	
	public static double percentDoneTarget = 0, percentDone = 0;
	public static String percentText = "Starting Vergo...";

	public String FuckMoon = "Out here proving everyone wrong";
	public String OwOText = "jiggey is a fucking god.";
	
	public GuiStartup() {
		
		waitTimer.reset();
		
		if (hasLoaded && mc != null)
			mc.displayGuiScreen(new GuiMainMenu());
//		else
//			Hummus.startup();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		if (!hasStartedLoading && waitTimer.hasTimeElapsed(3000, true)) {
			hasStartedLoading = true;
			Vergo.startup();
		}
		
		// If it is done loading then show the main menu
		if (hasLoaded)
			mc.displayGuiScreen(new GuiMainMenu());
		
		// Background
		Gui.drawRect(0, 0, width, height, Colors.START_GUI_BACKGROUND.getColor());
		
		// Moves the progress bar
		if (percentDone != percentDoneTarget)
			percentDone += (percentDoneTarget - percentDone) / 8;
		
		if (percentDone >= 0.9995670207585011) {
			percentDone = 1;
		}
		
		if (percentDoneTarget >= 1) 
			percentText = "Finishing up...";
		
		// Progress bar
		Gui.drawRect(width / 8, height - 20, (width / 8) * 7, height - 5, Colors.START_GUI_PROGRESS_BAR_BACKGROUND.getColor());
		Gui.drawRect(width / 8, height - 20, (width / 8) + (((width / 8) * 6) * percentDone), height - 5, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		
		// Draws text in middle of screen and above progress bar
		FontUtil.bakakakmedium.drawCenteredString(percentText, width / 2, height - 25 - FontUtil.arialMedium.FONT_HEIGHT, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		FontUtil.bakakakBigger.drawCenteredString(percentDone >= 0.9 ? OwOText : "Vergo", width / 2, (height / 3) - FontUtil.arialBigger.FONT_HEIGHT, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		FontUtil.bakakakBigger.drawCenteredString(percentDone >= 0.9 ? FuckMoon : "Rismose & jiggey", width / 2, (height / 2) - FontUtil.arialBigger.FONT_HEIGHT, Colors.START_GUI_PROGRESS_BAR_PROGRESS.getColor());
		
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
