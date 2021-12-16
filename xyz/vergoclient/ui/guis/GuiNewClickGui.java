package xyz.vergoclient.ui.guis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.assets.Icons;
import xyz.vergoclient.keybinds.KeyboardManager;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.KeybindSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.Setting;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.GuiUtils;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.TimerUtil;
import xyz.vergoclient.util.datas.DataDouble5;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiNewClickGui extends GuiScreen {
	
	private static GuiNewClickGui clickGui = new GuiNewClickGui();
	public static GuiNewClickGui getClickGui() {
		return clickGui;
	}
	
	public static class CategoryTab {
		public Module.Category category;
		public boolean extended = true, isBeingDragged = false;
		public double posX = 0, posY = 0, offsetPosX = 0, offsetPosY = 0, extendTab = 1;
		public double width = 40, targetWidth = 40, height = categoryFr.getHeight();
		public CopyOnWriteArrayList<ModuleTab> modules = new CopyOnWriteArrayList<>();
	}
	
	public static class ModuleTab {
		public Module module;
	}
	
	public CopyOnWriteArrayList<CategoryTab> tabs = new CopyOnWriteArrayList<>();
	public TimerUtil saveTabsFile = new TimerUtil(), moveAnimations = new TimerUtil();
	
	public Setting selectedSetting = null;
	
	public static JelloFontRenderer categoryFr = FontUtil.arialBig;
	public static JelloFontRenderer moduleFr = FontUtil.arialRegular;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		// Make sure current tabs are synced with everything else
		try {
			if (tabs.get(0) == null || tabs.get(0).modules.get(0) == null || tabs.get(0).modules.get(0).module == null || !Vergo.config.modules.contains(tabs.get(0).modules.get(0).module)) {
				for (CategoryTab tab : tabs) {
					tab.modules.clear();
					if (tab.targetWidth < categoryFr.getStringWidth(tab.category.displayName)) {
						tab.targetWidth = categoryFr.getStringWidth(tab.category.displayName) * 1.4;
					}
					for (Module module : getModulesWithCategory(tab.category)) {
						ModuleTab moduleTab = new ModuleTab();
						moduleTab.module = module;
						if (tab.targetWidth < moduleFr.getStringWidth(module.getName())) {
							tab.targetWidth = moduleFr.getStringWidth(module.getName()) * 1.4;
						}
						tab.modules.add(moduleTab);
					}
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		if (tabs.isEmpty()) {
			for (Module.Category category : Module.Category.values()) {
				CategoryTab tab = new CategoryTab();
				for (GuiClickGui.ClickguiTab clickguiTab : GuiClickGui.tabs.tabs) {
					if (category == clickguiTab.category) {
						tab.posX = clickguiTab.x;
						tab.posY = clickguiTab.y;
						try {
							tab.extended = clickguiTab.extended;
						} catch (Exception e) {
							tab.extended = false;
						}
					}
				}
				tab.category = category;
				if (tab.targetWidth < categoryFr.getStringWidth(category.displayName)) {
					tab.targetWidth = categoryFr.getStringWidth(category.displayName) * 1.4;
				}
				for (Module module : getModulesWithCategory(category)) {
					ModuleTab moduleTab = new ModuleTab();
					moduleTab.module = module;
					if (tab.targetWidth < moduleFr.getStringWidth(module.getName())) {
						tab.targetWidth = moduleFr.getStringWidth(module.getName()) * 1.4;
					}
					tab.modules.add(moduleTab);
				}
				tabs.add(tab);
			}
		}
		else {
			for (CategoryTab tab : tabs) {
				CopyOnWriteArrayList<Module> moduleRefresh = new CopyOnWriteArrayList<>();
				for (ModuleTab moduleTab : tab.modules) {
					if (!getModulesWithCategory(tab.category).contains(moduleTab.module)) {
						tab.modules.remove(moduleTab);
					}else {
						moduleRefresh.add(moduleTab.module);
					}
				}
				for (Module module : getModulesWithCategory(tab.category)) {
					if (!moduleRefresh.contains(module)) {
						ModuleTab moduleTab = new ModuleTab();
						moduleTab.module = module;
						if (tab.targetWidth < moduleFr.getStringWidth(module.getName())) {
							tab.targetWidth = moduleFr.getStringWidth(module.getName()) * 1.4;
						}
						tab.modules.add(moduleTab);
					}
				}
			}
		}
		
		// Render
		for (CategoryTab tab : tabs) {
			
			for (GuiClickGui.ClickguiTab clickguiTab : GuiClickGui.tabs.tabs) {
				if (tab.category == clickguiTab.category) {
					clickguiTab.x = (float) tab.posX;
					clickguiTab.y = (float) tab.posY;
					clickguiTab.extended = tab.extended;
				}
			}
			
			tab.targetWidth = categoryFr.getStringWidth(tab.category.displayName) * 1.4;
			if (tab.extendTab != 1) {
				for (ModuleTab moduleTab : tab.modules) {
					if (tab.targetWidth < (moduleFr.getStringWidth(moduleTab.module.getName()) * 1.4f) + 16) {
						tab.targetWidth = (moduleFr.getStringWidth(moduleTab.module.getName()) * 1.4f) + 16;
					}
					if (moduleTab.module.clickguiExpand != 0) {
						for (Setting setting : moduleTab.module.settings) {
							if (setting instanceof ModeSetting) {
								if (tab.targetWidth < moduleFr.getStringWidth(((ModeSetting)setting).name + ": " + ((ModeSetting)setting).getMode())) {
									tab.targetWidth = moduleFr.getStringWidth(((ModeSetting)setting).name + ": " + ((ModeSetting)setting).getMode()) * 1.4;
								}
							}
							else if (setting instanceof BooleanSetting) {
								if (tab.targetWidth < moduleFr.getStringWidth(setting.name)) {
									tab.targetWidth = moduleFr.getStringWidth(setting.name) * 1.4;
								}
							}
							else if (setting instanceof NumberSetting) {
								if (tab.targetWidth < moduleFr.getStringWidth(setting.name + ": " + ((NumberSetting)setting).getValueAsDouble())) {
									tab.targetWidth = moduleFr.getStringWidth(setting.name + ": " + ((NumberSetting)setting).getValueAsDouble()) * 1.4;
								}
							}
							else if (setting instanceof KeybindSetting) {
								if (tab.targetWidth < moduleFr.getStringWidth(setting.name)) {
									tab.targetWidth = moduleFr.getStringWidth(setting.name) * 1.4;
								}
							}
							else {
								if (tab.targetWidth < moduleFr.getStringWidth(setting.name)) {
									tab.targetWidth = moduleFr.getStringWidth(setting.name) * 1.4;
								}
							}
						}
					}
				}
			}
			
			tab.width += (tab.targetWidth - tab.width) / (6 * (400.0 / Minecraft.getDebugFPS()));
			
			if (tab.isBeingDragged && tab.offsetPosX - 20 > tab.width) {
				System.out.println(tab.offsetPosX + " " + tab.width);
				tab.offsetPosX = tab.width + 15;
			}
			
			// Tab logic
			if (tab.isBeingDragged) {
				tab.posX = mouseX - tab.offsetPosX;
				tab.posY = mouseY - tab.offsetPosY;
			}

			if (tab.extended) {
				tab.extendTab += 0.01 * (400.0 / Minecraft.getDebugFPS());
//				System.out.println((tab.extendTab - 1) / 6);
//				tab.extendTab += (tab.extendTab - 1) / 6;
			}else {
				tab.extendTab -= 0.01 * (400.0 / Minecraft.getDebugFPS());
//				tab.extendTab -= (1 - tab.extendTab) / 6;
			}
			if (tab.extendTab > 1)
				tab.extendTab = 1;
			if (tab.extendTab < 0)
				tab.extendTab = 0;
			
			// Translate to tab pos
			GlStateManager.pushMatrix();
			GlStateManager.translate(tab.posX, tab.posY, 0);
			
			// Module render
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, (tab.height + 4 + ((tab.height + 4) * tab.modules.size())) * -tab.extendTab, 0);
			GlStateManager.translate(0, -1, 0);
			double moduleMove = 0;
			for (ModuleTab moduleTab : tab.modules) {
				
				RenderUtils.enableScissor((int) (tab.posX), (int) (tab.posY + 3), (int) (18 + tab.width), (int) (height * 2));
				
				moduleMove += tab.height + 4;
				drawRect(8, moduleMove + 1, 10 + tab.width, tab.height + 4 + moduleMove, Colors.NEW_CLICK_GUI_GREY.getColor());
				drawRect(0, moduleMove + 2, 18 + tab.width, tab.height + 3 + moduleMove + 2, moduleTab.module.isEnabled() ? Colors.NEW_CLICK_GUI_PURPLE.getColor() : Colors.NEW_CLICK_GUI_CATEGORY.getColor());
				moduleFr.drawString(moduleTab.module.getName(), 16, (float) (moduleMove + (moduleFr.getHeight() * 0.9)), -1);
				if (moduleTab.module.settings.size() > 0) {
					
					// Module logic
					if (moduleTab.module.clickguiExtended && tab.extendTab == 0) {
						moduleTab.module.clickguiExpand += 0.01 * (400.0 / Minecraft.getDebugFPS());
					}else {
						moduleTab.module.clickguiExpand -= 0.01 * (400.0 / Minecraft.getDebugFPS());
					}
					if (moduleTab.module.clickguiExpand > 1)
						moduleTab.module.clickguiExpand = 1;
					if (moduleTab.module.clickguiExpand < 0)
						moduleTab.module.clickguiExpand = 0;
					
					GlStateManager.pushMatrix();
					GlStateManager.color(1, 1, 1, 1);
					mc.getTextureManager().bindTexture(new ResourceLocation("hummus/clickgui/new/arrow.png"));
					GlStateManager.translate(18 + tab.width - 20 + 5, moduleMove + 3 + 5, 0);
					GlStateManager.rotate(-90, 0, 0, 1);
					GlStateManager.rotate((moduleTab.module.clickguiExtended ? 180 : -180) * moduleTab.module.clickguiExpand, 0, 0, 1);
					GlStateManager.translate(-(18 + tab.width - 20 + 5), -(moduleMove + 3 + 5), 0);
					Gui.drawModalRectWithCustomSizedTexture((int) (18 + tab.width - 20), (int) (moduleMove + 3), 0, 0, 10, 10, 10, 10);
					GlStateManager.popMatrix();
					
					if (moduleTab.module.clickguiExpand > 0.0001) {
//						moduleMove += ((tab.height + 3) * moduleTab.module.settings.size()) * moduleTab.module.clickguiExpand;
						if (!tab.extended)
							RenderUtils.enableScissor((int) (tab.posX), (int) (tab.posY + 3 + moduleMove + (tab.height + 1)), (int) (18 + tab.width), (int) (height * 2));
						GlStateManager.pushMatrix();
						GlStateManager.translate(0, -(((tab.height + 3) * moduleTab.module.settings.size()) * (1 - moduleTab.module.clickguiExpand)), 0);
						for (Setting setting : moduleTab.module.settings) {
							GlStateManager.translate(0, tab.height + 3, 0);
							drawRect(0, moduleMove + 2, 18 + tab.width, tab.height + 3 + moduleMove + 2, Colors.NEW_CLICK_GUI_CATEGORY.getColor());
							if (setting instanceof ModeSetting) {
								moduleFr.drawString(((ModeSetting)setting).name + ": " + ((ModeSetting)setting).getMode(), 16, (float) (moduleMove + (moduleFr.getHeight() * 0.9)), -1);
							}
							else if (setting instanceof BooleanSetting) {
								moduleFr.drawString(((BooleanSetting)setting).name, 16, (float) (moduleMove + (moduleFr.getHeight() * 0.9)), -1);
								drawRect(18 + tab.width - 10, moduleMove + 4, 16 + tab.width, tab.height + 2 + moduleMove, ((BooleanSetting)setting).isEnabled() ? Colors.NEW_CLICK_GUI_PURPLE.getColor() : Colors.NEW_CLICK_GUI_GREY.getColor());
							}
							else if (setting instanceof NumberSetting) {
								NumberSetting numberSetting = (NumberSetting) setting;
								
								double max = numberSetting.getMaximum() - numberSetting.getMinimum();
								double value = numberSetting.getMinimum() < 0 ? numberSetting.getValueAsDouble() + Math.abs(numberSetting.getMinimum()) : numberSetting.getMinimum() > 0 ? numberSetting.getValueAsDouble() - Math.abs(numberSetting.getMinimum()) : numberSetting.getValueAsDouble();
								double percentDone = value / max;
								if (selectedSetting == numberSetting) {
									percentDone = (mouseX - tab.posX) / (18 + tab.width);
									numberSetting.setValue((max * percentDone) + numberSetting.getMinimum());
								}
								
								drawRect(0, moduleMove + 2, ((18 + tab.width) * percentDone), tab.height + 3 + moduleMove + 2, Colors.NEW_CLICK_GUI_PURPLE.getColor());
								moduleFr.drawString(setting.name + ": " + ((NumberSetting)setting).getValueAsDouble(), 16, (float) (moduleMove + (moduleFr.getHeight() * 0.9)), -1);
							}
							else if (setting instanceof KeybindSetting) {
								moduleFr.drawString(setting.name + ": " + (selectedSetting == setting ? "BINDING" : Keyboard.getKeyName(((KeybindSetting)setting).getKeycode())), 16, (float) (moduleMove + (moduleFr.getHeight() * 0.9)), -1);
							}
							else {
								moduleFr.drawString("Setting not supported", 16, (float) (moduleMove + (moduleFr.getHeight() * 0.9)), -1);
							}
						}
						GlStateManager.popMatrix();
						moduleMove += ((tab.height + 3) * moduleTab.module.settings.size()) * moduleTab.module.clickguiExpand;
					}
					
				}
				
				RenderUtils.disableScissor();
				
			}
			
			GlStateManager.popMatrix();
			
			// Tab render
			drawRect(0, 0, 18 + tab.width, tab.height + 4, Colors.NEW_CLICK_GUI_CATEGORY.getColor());
			
			// Draws the extend image for the category
			ScaledResolution sr = new ScaledResolution(mc);
			int imageWidth = 10, imageHeight = 10;
			if (tab.extendTab != 1) {
				RenderUtils.glColorWithInt(Colors.NEW_CLICK_GUI_PURPLE.getColor());
				mc.getTextureManager().bindTexture(Icons.CIRCLE.iconLocation);
				Gui.drawModalRectWithCustomSizedTexture(2, 2, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
			}
			if (tab.extendTab != 0) {
				RenderUtils.glColorWithInt(Colors.NEW_CLICK_GUI_GREY.getColor());
				RenderUtils.enableScissor((int) (tab.posX + 2 + (tab.extended ? 0 : 10 * (1 - tab.extendTab))), (int) (tab.posY), (int) (10 * (tab.extended ? tab.extendTab : 18)), (int) (tab.height + 4));
				mc.getTextureManager().bindTexture(Icons.CIRCLE.iconLocation);
				Gui.drawModalRectWithCustomSizedTexture(2, 2, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
				RenderUtils.disableScissor();
				
			}
			
			categoryFr.drawString(tab.category.displayName, 15, (float) (tab.height - (categoryFr.getHeight() - 2.5)), -1);
			
			GlStateManager.popMatrix();
//			break;
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		CopyOnWriteArrayList<CategoryTab> fixedTabs = (CopyOnWriteArrayList<CategoryTab>) tabs.clone();
		Collections.reverse(fixedTabs);
		
		for (CategoryTab tab : fixedTabs) {
			
			if (mouseButton == 1 && tab.isBeingDragged) {
				if (tab.extendTab == 0 || tab.extendTab == 1)
					tab.extended = !tab.extended;
				return;
			}
			
		}
		for (CategoryTab tab : fixedTabs) {
			
			mouseX -= tab.posX;
			mouseY -= tab.posY;
			DataDouble5 data = new DataDouble5();
			data.x1 = 0;
			data.y1 = 0;
			data.x2 = tab.width + 18;
			data.y2 = tab.height + 4;
			if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, data)) {
				if (mouseButton == 1) {
					if (tab.extendTab == 0 || tab.extendTab == 1)
						tab.extended = !tab.extended;
					return;
				}
				else if (mouseButton == 0) {
					tab.isBeingDragged = true;
					tab.offsetPosX = mouseX;
					tab.offsetPosY = mouseY;
					return;
				}
			}
			
			if (mouseY < 0) {
				mouseX += tab.posX;
				mouseY += tab.posY;
				continue;
			}
			double moduleMove = 0;
			moduleMove += (tab.height + 4 + ((tab.height + 4) * tab.modules.size())) * -tab.extendTab;
			for (ModuleTab moduleTab : tab.modules) {
				moduleMove += tab.height + 4;
				data.x1 = 0;
				data.y1 = moduleMove + 2;
				data.x2 = 18 + tab.width;
				data.y2 = tab.height + 4 + moduleMove;
				
				if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, data)) {
					if (mouseButton == 1) {
						if (moduleTab.module.settings.size() > 0 && (moduleTab.module.clickguiExpand == 1 || moduleTab.module.clickguiExpand == 0)) {
							moduleTab.module.clickguiExtended = !moduleTab.module.clickguiExtended;
						}
						return;
					}
					else if (mouseButton == 0) {
						moduleTab.module.toggle();
						return;
					}
				}
				
				if (moduleTab.module.clickguiExpand != 0) {
					double moduleMoveTemp = moduleMove;
					for (Setting setting : moduleTab.module.settings) {
//						GlStateManager.translate(0, tab.height + 3, 0);
						moduleMove += tab.height + 3;
//						drawRect(0, moduleMove + 2, 18 + tab.width, tab.height + 3 + moduleMove + 2, Colors.NEW_CLICK_GUI_CATEGORY.getColor());
						
						data.x1 = 0;
						data.y1 = moduleMove + 1;
						data.x2 = 18 + tab.width;
						data.y2 = tab.height + 3 + moduleMove;
						
						if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, data)) {
							selectedSetting = null;
							if (setting instanceof ModeSetting) {
								((ModeSetting)setting).cycle(mouseButton == 1);
							}
							else if (setting instanceof BooleanSetting) {
								if (mouseButton == 0) {
									((BooleanSetting)setting).toggle();
									return;
								}
							}
							else if (setting instanceof NumberSetting) {
								selectedSetting = setting;
							}
							else if (setting instanceof KeybindSetting) {
								selectedSetting = setting;
							}
							return;
						}
						
					}
					moduleMove = moduleMoveTemp;
					moduleMove += ((tab.height + 3) * moduleTab.module.settings.size()) * moduleTab.module.clickguiExpand;
				}
				
			}
			mouseX += tab.posX;
			mouseY += tab.posY;
//			break;
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			selectedSetting = null;
		}
		CopyOnWriteArrayList<CategoryTab> fixedTabs = (CopyOnWriteArrayList<CategoryTab>) tabs.clone();
		Collections.reverse(fixedTabs);
		for (CategoryTab tab : fixedTabs) {
			if (mouseButton == 0) {
				tab.isBeingDragged = false;
			}
			mouseX -= tab.posX;
			mouseY -= tab.posY;
			DataDouble5 data = new DataDouble5();
			data.x1 = 0;
			data.y1 = 0;
			data.x2 = tab.width + 18;
			data.y2 = tab.height + 4;
			if (GuiUtils.isMouseOverDataDouble5(mouseX, mouseY, data)) {
				if (mouseButton == 1) {
					
				}
				else if (mouseButton == 0) {
					
				}
			}
			mouseX += tab.posX;
			mouseY += tab.posY;
//			break;
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
		if (selectedSetting != null && selectedSetting instanceof KeybindSetting) {
			if (selectedSetting instanceof KeybindSetting) {
				KeybindSetting keybindSetting = ((KeybindSetting)selectedSetting);
				if (keyCode == Keyboard.KEY_ESCAPE) {
					keybindSetting.setKeycode(Keyboard.KEY_NONE);
				}else {
					keybindSetting.setKeycode(keyCode);
				}
				selectedSetting = null;
			}
		}else {
			KeyboardManager.keypress(keyCode);
			if (keyCode == Keyboard.KEY_ESCAPE)
				mc.displayGuiScreen(null);
		}
	}
	
}
