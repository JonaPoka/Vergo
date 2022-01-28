package xyz.vergoclient.ui.guis;

import java.awt.*;
import java.io.IOException;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.Display;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.util.*;
import net.minecraft.util.ResourceLocation;

public class GuiStart extends GuiScreen {

	public static boolean hasLoaded = false, hasStartedLoading = false;
	public static TimerUtil waitTimer = new TimerUtil();

	public static double percentDoneTarget = 0, percentDone = 0;
	public static String percentText = RandomStringUtil.getRandomLoadingMsg();

	public GuiStart() {

		waitTimer.reset();

		if (hasLoaded && mc != null) {
			mc.displayGuiScreen(new GuiMultiplayer(new GuiAltManager()));
			//mc.displayGuiScreen(new GuiSelectWorld(new GuiAltManager()));
			//mc.displayGuiScreen(new xyz.vergoclient.ui.guis.GuiMainMenu());
			//mc.displayGuiScreen(new GuiMainMenu());
		} //else {
		//	Vergo.startup();
		//}
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
			//mc.displayGuiScreen(new GuiMultiplayer(new GuiAltManager()));
			//mc.displayGuiScreen(new GuiSelectWorld(new GuiAltManager()));
			mc.displayGuiScreen(new xyz.vergoclient.ui.guis.GuiMainMenu());
			//mc.displayGuiScreen(new GuiMainMenu());

		// Background
		Gui.drawRect(0, 0, width, height, new Color(18, 18, 18).getRGB());

		GlStateManager.enableBlend();
		GlStateManager.color((float) 1.0, (float) 1.0, (float) 1.0, 1.0f);
		RenderUtils.drawImg(new ResourceLocation("Vergo/logo/smd.png"), width / 2 - 60, height / 2 - 60, 100, 100);
		GlStateManager.disableBlend();
		FontUtil.bakakRegular.drawCenteredString(percentText, width / 2 - 10, height / 2 + 70 - FontUtil.arialMedium.FONT_HEIGHT, new Color(180, 0, 79).getRGB());

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