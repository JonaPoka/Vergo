package xyz.vergoclient.ui.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.keybinds.KeyboardManager;
import xyz.vergoclient.modules.Module;

import java.awt.*;
import java.io.IOException;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.SerializedName;

import xyz.vergoclient.modules.impl.miscellaneous.ClickGui;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.animations.Animation;
import xyz.vergoclient.util.animations.Direction;
import xyz.vergoclient.util.animations.impl.DecelerateAnimation;
import xyz.vergoclient.util.animations.impl.EaseBackIn;
import xyz.vergoclient.util.datas.DataDouble5;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.KeybindSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.Setting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class GuiClickGui extends GuiScreen {

	private Animation openingAnimation;
	private EaseBackIn fadeAnimation;
	private DecelerateAnimation configHover;

	// The file for the tabs so we can save them with gson
	public static class TabFile{
		@SerializedName(value = "tabs")
		public ArrayList<ClickguiTab> tabs = new ArrayList<>();
	}
	
	// List of the tabs
	public static transient TabFile tabs = new TabFile();
	
	// This is used for dragging tabs
	public static transient ClickguiTab selectedTab = null;
	
	// This is used for settings that you can drag
	public static transient Button selectedButton = null;
	
	// The timer for the clickgui animations
	public static transient TimerUtil colorChangeTimer = new TimerUtil();
	
	// The tabs
	public static class ClickguiTab {
		
		@SerializedName(value = "x")
		public float x = 0;
		
		@SerializedName(value = "y")
		public float y = 0;
		
		public boolean extended = false;
		
		public transient float offsetX = 0, offsetY = 0;
		public transient double maxWidth = 0, maxWidthTarget = 0;
		
		@SerializedName(value = "category")
		public Module.Category category;
	}
	
	// Use the same clickgui every time we open it
	private static GuiClickGui clickGui = new GuiClickGui();
	public static GuiClickGui getClickGui() {
		return clickGui;
	}
	
	// Buttons for the gui
	public static class Button{
		public DataDouble5 posAndColor = new DataDouble5();
		public Runnable action = new Runnable() {@Override public void run() {System.out.println("This is the default action for the button, please change me in the source code");}};
		public Module module = null;
		public ClickguiTab tab = null;
		public Setting setting = null;
	}
	
	// List of the buttons
	public static CopyOnWriteArrayList<Button> clickguiButtons = new CopyOnWriteArrayList<>();
	
	// The colors used in the clickgui
	public static int moduleBackgroundDisabled = 0x2E3741,
					  moduleTextColor = -1;

	public Color var21;

	@Override
	public void initGui() {
		openingAnimation = new EaseBackIn(400, .4f, 2f);

		selectedTab = null;
		selectedButton = null;
		clickguiButtons.clear();
		if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (this.mc.entityRenderer.theShaderGroup != null) {
				this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			}
			mc.entityRenderer.loadShader(new ResourceLocation("shader/post/blur.json"));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		ScaledResolution sr = new ScaledResolution(mc);

		// Allows the user to drag the tabs
		if (selectedTab != null) {
			selectedTab.x = mouseX - selectedTab.offsetX;
			selectedTab.y = mouseY - selectedTab.offsetY;
		}
		
		// So you can drag number settings
		if (selectedButton != null && selectedButton.setting != null && selectedButton.setting instanceof NumberSetting) {
			NumberSetting numberSetting = (NumberSetting) selectedButton.setting;
			double subtractNum = selectedButton.posAndColor.x1 + 3;
			double barEndAdd = 0;
			barEndAdd += numberSetting.minimum < 0 ? -numberSetting.minimum : 0;
			barEndAdd += numberSetting.maximum < 0 ? -numberSetting.maximum : 0;
			barEndAdd += numberSetting.minimum > 0 ? -numberSetting.minimum : 0;
			double realMin = numberSetting.minimum + barEndAdd,
					realMax = numberSetting.maximum + barEndAdd;
			double percent = (mouseX - subtractNum) / ((selectedButton.posAndColor.x2 - 3) - subtractNum);
			numberSetting.setValue(((realMax) * percent) - barEndAdd);
		}

		int accountBoxX = (int) FontUtil.tahomaFont.getStringWidth(AccountUtils.account.username + " - " + AccountUtils.account.uid) + 10;
		int accountBoxY = 40;

		Gui.drawRect(GuiScreen.width - accountBoxX, GuiScreen.height - accountBoxY, GuiScreen.width - 3, GuiScreen.height - 3, new Color(19, 19, 19, 195).getRGB());
		Gui.drawRect(GuiScreen.width - accountBoxX, GuiScreen.height - accountBoxY, GuiScreen.width - 107, GuiScreen.height - 3, new Color(19, 19, 19, 195).getRGB());

		String uidReformat = "";

		if(AccountUtils.account.uid < 9) {
			uidReformat = "00" + AccountUtils.account.uid;
		} else if(AccountUtils.account.uid > 9 && AccountUtils.account.uid < 99) {
			uidReformat = "0" + AccountUtils.account.uid;
		} else {
			uidReformat = "" + AccountUtils.account.uid;
		}

		FontUtil.comfortaaNormal.drawString(AccountUtils.account.username, GuiScreen.width - 98, GuiScreen.height - 28, -1);
		FontUtil.comfortaaNormal.drawString(uidReformat + "", GuiScreen.width - 99, GuiScreen.height - 14, -1);

		// The font renderer
		JelloFontRenderer fr = FontUtil.juraNormal;
		
		// Will replace the old arraylist after it is created
		CopyOnWriteArrayList<Button> clickguiButtons = new CopyOnWriteArrayList<>();
		
		// Draw every category and create buttons
		for (ClickguiTab tab : tabs.tabs) {
			
			Module.Category category = tab.category;
			
			// Gets the modules that are in that category
			ArrayList<Module> modules = getModulesWithCategory(category);
			
			// Max width for the tabs
			double maxWidthTarget = 0, maxWidth = tab.maxWidth;
			for (Module module : modules) {
				if (fr.getStringWidth(module.getName()) >= maxWidthTarget)
					maxWidthTarget = fr.getStringWidth(module.getName());
				if (module.clickguiExpand > 0.15f)
					
					// For the settings
					for (Setting setting : module.settings) {
						if (setting instanceof BooleanSetting) {
							if (FontUtil.jelloFontBoldSmall.getStringWidth(setting.name) >= maxWidthTarget)
								maxWidthTarget = FontUtil.jelloFontBoldSmall.getStringWidth(setting.name);
						}
						else if (setting instanceof ModeSetting) {
							if (FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ":  " + ((ModeSetting)setting).getMode()) - 15 >= maxWidthTarget)
								maxWidthTarget = FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ":  " + ((ModeSetting)setting).getMode()) - 15;
						}
						else if (setting instanceof KeybindSetting) {
							if (selectedButton != null && selectedButton.setting != null && setting == selectedButton.setting) {
								if (FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ":  BINDING") - 15 >= maxWidthTarget)
									maxWidthTarget = FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ":  BINDING") - 15;
							}else {
								if (FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ":  " + Keyboard.getKeyName(((KeybindSetting)setting).getKeycode())) - 15 >= maxWidthTarget)
									maxWidthTarget = FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ":  " + Keyboard.getKeyName(((KeybindSetting)setting).getKeycode())) - 15;
							}
						}
						else if (setting instanceof NumberSetting) {
							DecimalFormat decimalFormat = new DecimalFormat("#.###");
							String longestValue = decimalFormat.format(((NumberSetting)setting).getValueAsDouble());
							if (((NumberSetting)setting).clickguiMaxLength.equals("") || FontUtil.jelloFontBoldSmall.getStringWidth(decimalFormat.format(((NumberSetting)setting).getValueAsDouble())) > FontUtil.jelloFontBoldSmall.getStringWidth(((NumberSetting)setting).clickguiMaxLength)) {
								for (double i = ((NumberSetting)setting).getMinimum(); i <= ((NumberSetting)setting).getMaximum(); i += ((NumberSetting)setting).getIncrement())
									if (FontUtil.jelloFontBoldSmall.getStringWidth(decimalFormat.format(i)) > FontUtil.jelloFontBoldSmall.getStringWidth(longestValue))
										longestValue = decimalFormat.format(i);
								((NumberSetting)setting).clickguiMaxLength = longestValue;
							}else {
								longestValue = ((NumberSetting)setting).clickguiMaxLength;
							}
							if (FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ": " + longestValue) - 15 >= maxWidthTarget)
								maxWidthTarget = FontUtil.jelloFontBoldSmall.getStringWidth(setting.name + ": " + longestValue) - 15;
						}
					}
			}
			if (fr.getStringWidth(tab.category.displayName) >= maxWidthTarget)
				maxWidthTarget = fr.getStringWidth(tab.category.displayName);
			
			maxWidthTarget += 18;
			
			maxWidth += (maxWidthTarget - maxWidth) / 8;
			tab.maxWidth = maxWidth;
			
			DataDouble5 tabCoords = new DataDouble5();
			tabCoords.x1 = tab.x - 2;
			tabCoords.x2 = tab.x + 2 + maxWidth;
			tabCoords.y1 = tab.y - 2;
			tabCoords.y2 = tab.y + 1 + fr.FONT_HEIGHT + 4;
			tabCoords.data = 0;
			Button tabButton = new Button();
			tabButton.posAndColor = tabCoords;
			tabButton.action = new Runnable() {
				@Override
				public void run() {
					selectedTab = tab;
				}
			};
			tabButton.tab = tab;
			clickguiButtons.add(tabButton);
			
			double offset = 0;
			for (Module module : modules) {
				offset++;
				DataDouble5 coords = new DataDouble5();
				coords.x1 = tab.x - 2;
				coords.x2 = tab.x + maxWidth + 2;
				coords.y1 = tab.y - 1 + 2 + (offset * (fr.FONT_HEIGHT + 4));
				coords.y2 = tab.y + fr.FONT_HEIGHT + 3 + 2 + (offset * (fr.FONT_HEIGHT + 4));
				coords.data = module.isEnabled() && !(module instanceof ClickGui) ? 1 : 0;
				if (!this.clickguiButtons.isEmpty()) {
					for (Button colorCheck : this.clickguiButtons) {
						if (colorCheck.module == module) {
							coords.data = colorCheck.posAndColor.data;
							break;
						}
					}
				}
				Button button = new Button();
				button.posAndColor = coords;
				button.action = new Runnable() {
					@Override
					public void run() {
						module.toggle();
					}
				};
				button.module = module;
				clickguiButtons.add(button);
				if (module.clickguiExpand > 0.15f) {
					double maxOffset = (((module.settings.size() + offset + 1) * (fr.FONT_HEIGHT + 4)) - (offset * (fr.FONT_HEIGHT + 4))) * module.clickguiExpand;
					double notLargerThan = ((module.settings.size() + offset + 1) * (fr.FONT_HEIGHT + 4)) - (offset * (fr.FONT_HEIGHT + 4));
					for (Setting s : module.settings) {
						offset++;
						DataDouble5 coordsSetting = new DataDouble5();
						coordsSetting.x1 = tab.x - 2;
						coordsSetting.x2 = tab.x + maxWidth + 2;
						coordsSetting.y1 = tab.y - 1 + 2 + (offset * (fr.FONT_HEIGHT + 4));
						coordsSetting.y2 = tab.y + fr.FONT_HEIGHT + 3 + 2 + (offset * (fr.FONT_HEIGHT + 4));
						coordsSetting.data = 0;
						coordsSetting.y1 -= notLargerThan;
						coordsSetting.y2 -= notLargerThan;
						coordsSetting.y1 += maxOffset;
						coordsSetting.y2 += maxOffset;
						Button buttonSetting = new Button();
						buttonSetting.posAndColor = coordsSetting;
						buttonSetting.setting = s;
						buttonSetting.action = new Runnable() {
							@Override
							public void run() {
								selectedButton = buttonSetting;
							}
						};
						if (coordsSetting.y1 >= button.posAndColor.y1) {
							clickguiButtons.remove(button);
							clickguiButtons.add(buttonSetting);
							clickguiButtons.add(button);
						}
					}
					
					// Because the next module is place below the settings when the bottom of the
					// last setting is higher than the current module's bottom it will go inside of it.
					// This single if statement fixes that by detaching the modules
					// below the setting
					if (maxOffset / (fr.FONT_HEIGHT + 4) < 1)
						maxOffset = (fr.FONT_HEIGHT + 4);
					
					offset -= notLargerThan / (fr.FONT_HEIGHT + 4);
					offset += maxOffset / (fr.FONT_HEIGHT + 4);
				}
			}
			
		}
		
		// Replace the old arraylist
		this.clickguiButtons = clickguiButtons;
		
		// If the animations should update
		boolean updateColorAnimations = colorChangeTimer.hasTimeElapsed(1000 / 60, true);
		
		// Draw all the buttons
		for (Button button : clickguiButtons) {
			if (button.tab != null) {

				var21 = new Color(19, 19, 19, 226);

				GlStateManager.pushMatrix();
				if(button.tab.category == Module.Category.VISUAL) {
					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + 5, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
					FontUtil.neurialGrotesk.drawCenteredString(button.tab.category.displayName, (float) button.posAndColor.x1 + 43, (float) (button.posAndColor.y1) + 7.5f,	 moduleTextColor);
				}

				if(button.tab.category == Module.Category.COMBAT) {
					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + 5, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
					FontUtil.neurialGrotesk.drawCenteredString(button.tab.category.displayName, (float) button.posAndColor.x1 + 37, (float) (button.posAndColor.y1) + 7.5f,	 moduleTextColor);
				}

				if(button.tab.category == Module.Category.MOVEMENT) {
					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + 5, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
					FontUtil.neurialGrotesk.drawCenteredString(button.tab.category.displayName, (float) button.posAndColor.x1 + 41, (float) (button.posAndColor.y1) + 7.5f,	 moduleTextColor);
				}

				if(button.tab.category == Module.Category.MISCELLANEOUS) {
					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + 5, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
					FontUtil.neurialGrotesk.drawCenteredString(button.tab.category.displayName, (float) button.posAndColor.x1 + 49, (float) (button.posAndColor.y1) + 7.5f,	 moduleTextColor);
				}

				if(button.tab.category == Module.Category.PLAYER) {
					Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1 + 5, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
					FontUtil.neurialGrotesk.drawCenteredString(button.tab.category.displayName, (float) button.posAndColor.x1 + 55, (float) (button.posAndColor.y1) + 7.5f,	 moduleTextColor);
				}

				//RenderUtils.drawRoundedRect(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, 3f, new Color(19, 24, 44));
				GlStateManager.color(1, 1, 1, 1);
				//FontUtil.neurialGrotesk.drawString(button.tab.category.displayName, (float) button.posAndColor.x1 + 3, (float) (button.posAndColor.y1) + 7, moduleTextColor);
				GlStateManager.popMatrix();
				
				// Draws the image for the category
				/*ScaledResolution sr = new ScaledResolution(mc);
				mc.getTextureManager().bindTexture(button.tab.category.icon.iconLocation);
				int imageWidth = 24, imageHeight = 24;
				imageWidth /= 2;
				imageHeight /= 2;
				Gui.drawModalRectWithCustomSizedTexture((int) (button.posAndColor.x2 - 13), (int) button.tab.y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);*/
				
			}
			else if (button.module != null) {
				GlStateManager.pushMatrix();

				var21 = new Color(19, 19, 19, 200);

				Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
				Gui.drawRectNoAlphaChange(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, new Color(255, 55, 115).getRGB(), (float) button.posAndColor.data);
				GlStateManager.color(1, 1, 1, 1);
				FontUtil.neurialGrotesk.drawString(button.module.getName(), button.posAndColor.x1 + 4, (float) (button.posAndColor.y1 + 3), moduleTextColor);
				if (!button.module.settings.isEmpty()) {
					GlStateManager.translate((button.posAndColor.x2 - (FontUtil.jelloFontBoldSmall.getStringWidth("v")) * 2.3f) + (FontUtil.jelloFontBoldSmall.getStringWidth("v") / 2), (float) (button.posAndColor.y1 + 2.7 + (FontUtil.jelloFontBoldSmall.FONT_HEIGHT / 2)), 1);
//					GlStateManager.rotate(180 * button.module.clickguiFlip, 0, 0, 0);
					GlStateManager.scale(1, (button.module.clickguiFlip * 2) - 1, 1);
					if (button.module.clickguiFlip <= 0.5) {
						GlStateManager.scale(-1, 1, 1);
						GlStateManager.translate(-FontUtil.jelloFontBoldSmall.getStringWidth("v"), -FontUtil.jelloFontBoldSmall.FONT_HEIGHT + 2, 0);
					}
//					GlStateManager.translate(FontUtil.jelloFontBoldSmall.getStringWidth("v") * button.module.clickguiFlip, 0, 0);
					GlStateManager.translate(-(button.posAndColor.x2 - (FontUtil.jelloFontBoldSmall.getStringWidth("v")) * 2.3f), -((float) (button.posAndColor.y1 + 2.7)), 1);
					FontUtil.neurialGrotesk.drawString("v", button.posAndColor.x2 - (FontUtil.jelloFontBoldSmall.getStringWidth("v")) * 2.3f, (float) (button.posAndColor.y1 + 2.7 + ((FontUtil.jelloFontBoldSmall.FONT_HEIGHT / 2)) * (button.module.clickguiFlip <= 0.5 ? 1 : -1)), moduleTextColor);
				}
				GlStateManager.popMatrix();
				
				if (updateColorAnimations) {
					
					// The color transition when toggled
					if (button.module.isEnabled()) {
						button.posAndColor.data += 0.15;
						if (button.posAndColor.data > 1)
							button.posAndColor.data = 1;
					}else {
						button.posAndColor.data -= 0.15;
						if (button.posAndColor.data < 0)
							button.posAndColor.data = 0;
					}
					
					// The flip of the V if the module is expanded
					if (!button.module.clickguiExtended) {
						button.module.clickguiFlip += 0.1;
						if (button.module.clickguiFlip > 1)
							button.module.clickguiFlip = 1;
					}else {
						button.module.clickguiFlip -= 0.1;
						if (button.module.clickguiFlip < 0)
							button.module.clickguiFlip = 0;
					}
					
					// The extend animation for when you show the modules settings
					if (button.module.clickguiExtended) {
						button.module.clickguiExpand += (1 - button.module.clickguiExpand) / 6;
					}else {
						button.module.clickguiExpand += (-button.module.clickguiExpand) / 6;
					}
					
				}
				
				
			}
			else if (button.setting != null) {

				var21 = new Color(19, 19, 19, 226);

				Gui.drawRect(button.posAndColor.x1, button.posAndColor.y1, button.posAndColor.x2, button.posAndColor.y2, var21.getRGB());
				Setting setting = button.setting;
				if (setting instanceof BooleanSetting) {
					FontUtil.neurialGrotesk.drawString(button.setting.name, button.posAndColor.x1 + 4, (float) (button.posAndColor.y1 + 3), moduleTextColor);
					//Gui.drawRect(button.posAndColor.x2 - 12, button.posAndColor.y1 + 2, button.posAndColor.x2 - 2, button.posAndColor.y1 + 12, ((BooleanSetting)setting).isEnabled() ? Colors.BLUE.getColor() : Colors.CLICK_GUI_SETTING.getColor());
					if(((BooleanSetting)setting).isEnabled()) {
						GlStateManager.color(0.03f, 0.03f, 0.03f, 1f);
						RenderUtils2.drawFullCircle((float) button.posAndColor.x2 - 8, (float) button.posAndColor.y1 + 6, 5);
						GlStateManager.color(1, 1, 1, 1);
						RenderUtils2.drawFullCircle((float) button.posAndColor.x2 - 8, (float) button.posAndColor.y1 + 6, 4);
					} else {
						GlStateManager.color(0.03f, 0.03f, 0.03f, 1f);
						RenderUtils2.drawFullCircle((float) button.posAndColor.x2 - 8, (float) button.posAndColor.y1 + 6, 5);
					}
				}
				else if (setting instanceof ModeSetting) {
					FontUtil.neurialGrotesk.drawString(button.setting.name + ":", button.posAndColor.x1 + 4, (float) (button.posAndColor.y1 + 3), moduleTextColor);
					FontUtil.neurialGrotesk.drawString(((ModeSetting)button.setting).getMode(), button.posAndColor.x2 - FontUtil.jelloFontBoldSmall.getStringWidth(((ModeSetting)button.setting).getMode()) - 2, (float) (button.posAndColor.y1 + 3), moduleTextColor);
				}
				else if (setting instanceof KeybindSetting) {
					FontUtil.neurialGrotesk.drawString(button.setting.name + ":", button.posAndColor.x1 + 4, (float) (button.posAndColor.y1 + 3), moduleTextColor);
					if (selectedButton != null && selectedButton.setting != null && setting == selectedButton.setting) {
						FontUtil.neurialGrotesk.drawString("BINDING", button.posAndColor.x2 - FontUtil.jelloFontBoldSmall.getStringWidth("BINDING") - 2, (float) (button.posAndColor.y1 + 3), moduleTextColor);
					}else {
						FontUtil.neurialGrotesk.drawString(Keyboard.getKeyName(((KeybindSetting)button.setting).getKeycode()), button.posAndColor.x2 - FontUtil.jelloFontBoldSmall.getStringWidth(Keyboard.getKeyName(((KeybindSetting)button.setting).getKeycode())) - 2, (float) (button.posAndColor.y1 + 3), moduleTextColor);
					}
				}
				else if (setting instanceof NumberSetting) {
					NumberSetting numberSetting = (NumberSetting)setting;
					var21 = new Color(12, 12, 12, 236);
					Gui.drawRect(button.posAndColor.x1 + 3, button.posAndColor.y1 + 3, button.posAndColor.x2 - 3, button.posAndColor.y2 - 3, var21.getRGB());
					double barEndAdd = 0;
					barEndAdd += numberSetting.minimum < 0 ? -numberSetting.minimum : 0;
					barEndAdd += numberSetting.maximum < 0 ? -numberSetting.maximum : 0;
					barEndAdd += numberSetting.minimum > 0 ? -numberSetting.minimum : 0;
					double realMin = numberSetting.minimum + barEndAdd,
							realMax = numberSetting.maximum + barEndAdd;
					double barEndX = 0 + ((button.posAndColor.x2 - button.posAndColor.x1 - 6) * ((numberSetting.getValueAsDouble() + barEndAdd) / (realMax)));
					Gui.drawRect(button.posAndColor.x1 + barEndX + 3, button.posAndColor.y1 + 2, button.posAndColor.x1 + barEndX + 1, button.posAndColor.y2 - 2, new Color(255, 55, 115).getRGB());
					FontUtil.neurialGrotesk.drawString(button.setting.name + ": " + new DecimalFormat("#.###").format(numberSetting.getValueAsDouble()), button.posAndColor.x1 + 4, (float) (button.posAndColor.y1 + 3), moduleTextColor);
				}
				else {
					FontUtil.neurialGrotesk.drawString("Setting not supported", button.posAndColor.x1 + 4, (float) (button.posAndColor.y1 + 3), moduleTextColor);
				}
			}
		}

		RenderUtils.scale(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, (float) openingAnimation.getOutput() + .6f, () -> {
			super.drawScreen(mouseX, mouseY, partialTicks);
		});
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		selectedTab = null;
		selectedButton = null;
		if (mouseButton == 0) {
			boolean tabAlreadySelected = selectedTab != null;
			for (Button button : clickguiButtons) {
				
				if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, button.posAndColor)) {
					
					// For the settings
					if (button.setting != null) {
						Setting setting = button.setting;
						
						if (setting instanceof BooleanSetting) {
							((BooleanSetting)setting).toggle();
						}
						else if (setting instanceof ModeSetting) {
							((ModeSetting)setting).cycle(false);
						}
						else if (setting instanceof KeybindSetting) {
							selectedButton = button;
						}
						else if (setting instanceof NumberSetting) {
							selectedButton = button;
						}
						
					}else {
						button.action.run();
					}
					
					break;
				}
			}
			if (!tabAlreadySelected && selectedTab != null) {
				selectedTab.offsetX = mouseX - selectedTab.x;
				selectedTab.offsetY = mouseY - selectedTab.y;
			}
		}
		else if (mouseButton == 1) {
			for (Button button : clickguiButtons) {
				if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, button.posAndColor)) {
					
					if (button.setting != null) {
						Setting setting = button.setting;
						
						if (setting instanceof ModeSetting) {
							((ModeSetting)setting).cycle(true);
						}
						
						break;
					}
					
					if (button.module != null && !button.module.settings.isEmpty()) {
						button.module.clickguiExtended = !button.module.clickguiExtended;
						break;
					}
					
				}
			}
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		selectedTab = null;
		if (selectedButton != null && selectedButton.setting != null && !(selectedButton.setting instanceof KeybindSetting)) {
			selectedButton = null;
		}
	}
	
	public static ArrayList<Module> getModulesWithCategory(Module.Category category){
		ArrayList<Module> modulesToReturn = new ArrayList<>();
		for (Module m : ModuleManager.modules) {
			if (m.getCategory() == category) {
				modulesToReturn.add(m);
			}
		}
		return modulesToReturn;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (selectedButton != null && selectedButton.setting != null && selectedButton.setting instanceof KeybindSetting) {
			Setting setting = selectedButton.setting;
			if (setting instanceof KeybindSetting) {
				KeybindSetting keybindSetting = ((KeybindSetting)setting);
				if (keyCode == Keyboard.KEY_ESCAPE) {
					keybindSetting.setKeycode(Keyboard.KEY_NONE);
				}else {
					keybindSetting.setKeycode(keyCode);
				}
				selectedButton = null;
			}
		}else {
			KeyboardManager.keypress(keyCode);
			if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RSHIFT) {
				mc.displayGuiScreen(null);
				if (mc.entityRenderer.theShaderGroup != null) {
					mc.entityRenderer.theShaderGroup.deleteShaderGroup();
					mc.entityRenderer.theShaderGroup = null;
				} else {
					// do nothing
				}
			}
		}
	}
	
}
