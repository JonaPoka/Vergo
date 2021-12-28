package xyz.vergoclient.assets;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.ui.Hud;
import xyz.vergoclient.ui.guis.GuiAltManager;
import xyz.vergoclient.ui.guis.GuiMainMenu;
import xyz.vergoclient.util.ColorUtils;
import xyz.vergoclient.util.RenderUtils;
import net.minecraft.client.Minecraft;

public enum Colors {
	
	HUMMUS(0xffcbb277),
	BLACK(0xff000000),
	CREAM(0xffFFFDD0),
	WHITE(0xffffffff),
	GRAY(0xff333333),
	RED(0xffff3d3d),
	GREEN(0xff43b581),
	BLUE(0xff3396FF),
	YELLOW(0xfffff12e),
	CLICK_GUI_OFF(0xff282828),
	CLICK_GUI_ON(0xff9f70d5),
	CLICK_GUI_CAT(0xff1d1d1d),
	CLICK_GUI_SETTING(0xff202020),
	ARRAY_LIST_MODULE_NAMES(0xff9f70d5),
	ALT_MANAGER_BACKGROUND(0xff333333),
	ALT_MANAGER_BUTTONS(0xff282828),
	ALT_MANAGER_PURPLE(0xff9f70d5),
	MAIN_MENU_BACKGROUND(0xff292929),
	MAIN_MENU_BUTTON_WINDOW(0xff404040),
	MAIN_MENU_TITLE_BACKGROUND_COLOR(0xff353535),
	START_GUI_BACKGROUND(0xff292929),
	START_GUI_PROGRESS_BAR_BACKGROUND(0xff505050),
	START_GUI_PROGRESS_BAR_PROGRESS(0xff3396FF),
	NEW_CLICK_GUI_CATEGORY(0xff434343),
	NEW_CLICK_GUI_PURPLE(0xff9f73ff),
	NEW_CLICK_GUI_GREY(0xff5d5d5d);
	
	private int color;
	private int getColorNoRainbowOverride() {
		return color;
	}
	public int getColor() {
		
		if (Hud.arrayListColor != -1 && getColorNoRainbowOverride() == ARRAY_LIST_MODULE_NAMES.getColorNoRainbowOverride()) {
			if (Vergo.config.modHud.arrayListColors.is("Default")) {
				return color;
			}
			else if (Vergo.config.modHud.arrayListColors.is("Rainbow")) {
				if (getColorNoRainbowOverride() == ARRAY_LIST_MODULE_NAMES.getColorNoRainbowOverride()) {
					return RenderUtils.getRainbow(Hud.arrayListRainbow, 0.5f, 1f);
				}
			}
		}
		
		try {
			if (Vergo.config.modRainbow.isEnabled() && (Minecraft.getMinecraft().currentScreen == null || (!(Minecraft.getMinecraft().currentScreen instanceof GuiAltManager) && !(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu)))) {
				if (getColorNoRainbowOverride() == ARRAY_LIST_MODULE_NAMES.getColorNoRainbowOverride()) {
					return RenderUtils.getRainbow(Hud.arrayListRainbow, 0.5f, 1f);
				}
			}
		} catch (Exception e) {
			
		}
		
		return color;
	}
	
	private Colors(int color) {
		this.color = color;
	}
	
}
