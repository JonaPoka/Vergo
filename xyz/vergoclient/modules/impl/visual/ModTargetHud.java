package xyz.vergoclient.modules.impl.visual;

import java.awt.Color;
import java.text.DecimalFormat;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.combat.ModKillAura;
import xyz.vergoclient.modules.impl.combat.ModTPAura;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.guis.GuiClickGui;
import xyz.vergoclient.ui.guis.GuiNewClickGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import xyz.vergoclient.util.RenderUtils;

public class ModTargetHud extends Module implements OnEventInterface {

	public ModTargetHud() {
		super("TargetHud", Category.VISUAL);
	}

	public ModeSetting mode = new ModeSetting("Mode", "Complex", "Complex", "Paper");
	public NumberSetting xOffset = new NumberSetting("X position", (new ScaledResolution(mc).getScaledWidth() / 2) - 110, 0, new ScaledResolution(mc).getScaledWidth() - 220, 1),
			yOffset = new NumberSetting("Y position", ((new ScaledResolution(mc).getScaledHeight() / 8) * 6) - 32.5, 0, new ScaledResolution(mc).getScaledHeight() - 65, 1);
		    /*heartSliderX = new NumberSetting("Heart SliderX", 45, 0, 200, 1 ), heartSliderY = new NumberSetting("Heart SliderY", 45, 0, 200, 1 ),
			healthSliderX = new NumberSetting("Health SliderX", 45, 0, 200, 1 ), healthSliderY = new NumberSetting("Health SliderY", 45, 0, 200, 1 ),
			healthWidth = new NumberSetting("Health Width", 140, 0, 250, 1),
		    nameSliderX = new NumberSetting("NameSliderX", 0, 0, 200, 1), nameSliderY = new NumberSetting("NameSliderY", 0, 0, 200, 1 ),
			characterX = new NumberSetting("CharX", 0, 0, 200, 1), characterY = new NumberSetting("CharY", 0, 0, 200, 1),
	        characterScale = new NumberSetting("CharScale", 0, 0, 1000, 1);*/

	@Override
	public void loadSettings() {

		xOffset.maximum = new ScaledResolution(mc).getScaledWidth() - 220;
		yOffset.maximum = new ScaledResolution(mc).getScaledHeight() - 65;

		if (xOffset.getValueAsDouble() < 0) {
			xOffset.setValue(0);
		}

		if (xOffset.getValueAsDouble() > xOffset.getMaximum()) {
			xOffset.setValue(xOffset.getMaximum());
		}

		if (yOffset.getValueAsDouble() < 0) {
			yOffset.setValue(0);
		}

		if (yOffset.getValueAsDouble() > xOffset.getMaximum()) {
			yOffset.setValue(yOffset.getMaximum());
		}

		addSettings(mode, xOffset, yOffset/*, heartSliderX, heartSliderY, healthSliderX, healthSliderY, healthWidth, nameSliderX, nameSliderY, characterX, characterY, characterScale*/);
	}

	public static transient double healthBarTarget = 0, healthBar = 0, hurtTime = 0, hurtTimeTarget = 0;

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI && e.isPre()) {

			if (mode.is("Complex")) {

				EntityLivingBase target = null;

				if (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
					target = ModKillAura.target;
				} else {
					if (Vergo.config.modTPAura.isEnabled() && ModTPAura.target != null) {
						target = ModTPAura.target;
					}
				}

				if (target == null) {
					if (mc.currentScreen instanceof GuiClickGui || mc.currentScreen instanceof GuiNewClickGui) {
						target = mc.thePlayer;
					} else {
						healthBar = 0;
						return;
					}
				}

				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.translate(xOffset.getValueAsDouble(), yOffset.getValueAsDouble(), 0);

				// Lower is faster, higher is slower
				double barSpeed = 5;
				if (healthBar > healthBarTarget) {
					healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
				} else if (healthBar < healthBarTarget) {
					healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
				}

				if (hurtTime > hurtTimeTarget) {
					hurtTime = ((hurtTime) - ((hurtTime - hurtTimeTarget) / barSpeed));
				} else if (hurtTime < healthBarTarget) {
					hurtTime = ((hurtTime) + ((hurtTimeTarget - hurtTime) / barSpeed));
				}

				ScaledResolution sr = new ScaledResolution(mc);
				FontRenderer fr = mc.fontRendererObj;
				DecimalFormat dec = new DecimalFormat("#");

				healthBarTarget = sr.getScaledWidth() / 2 - 41 + (((140) / (target.getMaxHealth())) * (target.getHealth()));

				int color = 0xff3396FF;

				if (Vergo.config.modRainbow.isEnabled()) {
					float hue1 = System.currentTimeMillis() % (int) ((4) * 1000) / (float) ((4) * 1000);
					color = Color.HSBtoRGB(hue1, 0.65f, 1);
				}

				// Main box
				RenderUtils.drawRoundedRect(0, 0, 220, 65, 3, new Color(37, 38, 54));

				// Health bar
				fr.drawString("❤", 52, 46, new Color(48, 194, 124).getRGB(), false);
				healthBarTarget = (140 * (target.getHealth() / target.getMaxHealth()));
				if (healthBar > 140) {
					healthBar = 140;
				}

				RenderUtils.drawRoundedRect(65, 45, 140, 10f, 2, new Color(23, 23, 33));
				RenderUtils.drawRoundedRect(65, 45, healthBar, 10f, 2, new Color(48, 194, 124));

				// Name
				if(target.getDisplayName().getFormattedText().length() < 9) {
					int length = target.getDisplayName().getFormattedText().length();
					int nine = 9;
					int newLength = length - nine;
					FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 99 + newLength, 16, color);
				}
				if (target.getDisplayName().getFormattedText().length() == 9) {
					FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 99, 16, color);
				} else {
					if (target.getDisplayName().getFormattedText().length() >= 10) {
						int length = target.getDisplayName().getFormattedText().length();
						int nine = 9;
						int newLength = length - nine;
						FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 99 - newLength, 16, color);
					}
				}

				// 3D model of the target
				GlStateManager.disableBlend();
				GlStateManager.color(1, 1, 1, 1);
				GuiInventory.drawEntityOnScreen(27, 58, (int) (45 / target.height), 0, 0, target);

			}

		else if(mode.is("Paper")) {

				EntityLivingBase target = null;

				if (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
					target = ModKillAura.target;
				} else {
					if (Vergo.config.modTPAura.isEnabled() && ModTPAura.target != null) {
						target = ModTPAura.target;
					}
				}

				if (target == null) {
					if (mc.currentScreen instanceof GuiClickGui || mc.currentScreen instanceof GuiNewClickGui) {
						target = mc.thePlayer;
					} else {
						healthBar = 0;
						return;
					}
				}

				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.translate(xOffset.getValueAsDouble(), yOffset.getValueAsDouble(), 0);

				// Lower is faster, higher is slower
				double barSpeed = 5;
				if (healthBar > healthBarTarget) {
					healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
				} else if (healthBar < healthBarTarget) {
					healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
				}

				if (hurtTime > hurtTimeTarget) {
					hurtTime = ((hurtTime) - ((hurtTime - hurtTimeTarget) / barSpeed));
				} else if (hurtTime < healthBarTarget) {
					hurtTime = ((hurtTime) + ((hurtTimeTarget - hurtTime) / barSpeed));
				}

				ScaledResolution sr = new ScaledResolution(mc);
				FontRenderer fr = mc.fontRendererObj;
				DecimalFormat dec = new DecimalFormat("#");

				healthBarTarget = sr.getScaledWidth() / 2 - 41 + (((140) / (target.getMaxHealth())) * (target.getHealth()));

				int color = 0xff3396FF;

				if (Vergo.config.modRainbow.isEnabled()) {
					float hue1 = System.currentTimeMillis() % (int) ((4) * 1000) / (float) ((4) * 1000);
					color = Color.HSBtoRGB(hue1, 0.65f, 1);
				}

				// Main box
				RenderUtils.drawRoundedRect(0, 0, 220, 65, 3, new Color(37, 38, 54));

				// Health bar
				healthBarTarget = (140 * (target.getHealth() / target.getMaxHealth()));
				if (healthBar > 140) {
					healthBar = 140;
				}

				RenderUtils.drawRoundedRect(16, 43, 188, 12f, 2, new Color(23, 23, 33));
				RenderUtils.drawRoundedRect(16, 43, healthBar + 48, 12f, 2, new Color(48, 194, 124));

				// Name
				if(target.getDisplayName().getFormattedText().length() < 9) {
					int length = target.getDisplayName().getFormattedText().length();
					int nine = 9;
					int newLength = length - nine;
					FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 85 + newLength, 13, color);
				}
				if (target.getDisplayName().getFormattedText().length() == 9) {
					FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 85, 13, color);
				} else {
					if (target.getDisplayName().getFormattedText().length() >= 10) {
						int length = target.getDisplayName().getFormattedText().length();
						int nine = 9;
						int newLength = length - nine;
						FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 85 - newLength, 13, color);
					}
				}
		}

			GlStateManager.popAttrib();
			GlStateManager.popMatrix();

		}

		else if (e instanceof EventTick && e.isPre()) {
			if (xOffset.maximum > new ScaledResolution(mc).getScaledWidth() - 220)
				xOffset.maximum = new ScaledResolution(mc).getScaledWidth() - 220;
			if (yOffset.maximum > new ScaledResolution(mc).getScaledHeight() - 65)
				yOffset.maximum = new ScaledResolution(mc).getScaledHeight() - 65;
		}

	}

}