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

public class ModTargetHud extends Module implements OnEventInterface {

	public ModTargetHud() {
		super("TargetHud", Category.VISUAL);
	}

	public ModeSetting mode = new ModeSetting("Mode", "1", "1");
	public NumberSetting xOffset = new NumberSetting("X position", (new ScaledResolution(mc).getScaledWidth() / 2) - 110, 0, new ScaledResolution(mc).getScaledWidth() - 220, 1),
			yOffset = new NumberSetting("Y position", ((new ScaledResolution(mc).getScaledHeight() / 8) * 6) - 32.5, 0, new ScaledResolution(mc).getScaledHeight() - 65, 1);

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

		addSettings(mode, xOffset, yOffset);
	}

	public static transient double healthBarTarget = 0, healthBar = 0, hurtTime = 0, hurtTimeTarget = 0;

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI && e.isPre()) {

			EntityLivingBase target = null;

			if (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
				target = ModKillAura.target;
			}
			else {
				if (Vergo.config.modTPAura.isEnabled() && ModTPAura.target != null) {
					target = ModTPAura.target;
				}
			}

			if (target == null) {
				if (mc.currentScreen instanceof GuiClickGui || mc.currentScreen instanceof GuiNewClickGui) {
					target = mc.thePlayer;
				}
				else {
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
			}
			else if (healthBar < healthBarTarget) {
				healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
			}

			if (hurtTime > hurtTimeTarget) {
				hurtTime = ((hurtTime) - ((hurtTime - hurtTimeTarget) / barSpeed));
			}
			else if (hurtTime < healthBarTarget) {
				hurtTime = ((hurtTime) + ((hurtTimeTarget - hurtTime) / barSpeed));
			}

			ScaledResolution sr = new ScaledResolution(mc);
			FontRenderer fr = mc.fontRendererObj;
			DecimalFormat dec = new DecimalFormat("#");

			if (mode.is("1")) {
				healthBarTarget = sr.getScaledWidth() / 2 - 41 + (((140) / (target.getMaxHealth())) * (target.getHealth()));

				int color = 0xff3396FF;

//				color = Color.HSBtoRGB(1, 1, 1);

				if (Vergo.config.modRainbow.isEnabled()) {
					float hue1 = System.currentTimeMillis() % (int)((4) * 1000) / (float)((4) * 1000);
					color = Color.HSBtoRGB(hue1, 0.65f, 1);
				}

				// Main box
				Gui.drawRect(0, 0, 220, 65, 0x9036393f);
				Gui.drawRect(0, 64, 220, 65, color);
				Gui.drawRect(0, 0, 1, 65, color);
				Gui.drawRect(219, 0, 220, 65, color);
				Gui.drawRect(0, 0, 220, 1, color);

				// Health bar
				fr.drawString("❤", 57, 31f,0xff43b581, false);
				healthBarTarget = (140 * (target.getHealth() / target.getMaxHealth()));
				if (healthBar > 140) {
					healthBar = 140;
				}
				Gui.drawRect(70, 30, 210, 40.5f, 0xffff3d3d);
				Gui.drawRect(70, 30, 70 + healthBar, 40.5f, 0xff43b581);

				// Hurt time bar
				/*fr.drawString("⚔", 57, 39, color, false);
				hurtTimeTarget = 140 - (140 * ((float)target.hurtResistantTime / (float)target.maxHurtResistantTime));
				if (hurtTime > 140) {
					hurtTime = 140;
				}
				Gui.drawRect(70, 40, 210, 45.5f, 0xb836393f);
				Gui.drawRect(70, 40, 70 + hurtTime, 45.5f, color);
				*/

				// Name
				FontUtil.jelloFontGui.drawString(target.getDisplayName().getFormattedText(), 8, 8, color);

				// 3D model of the target
				GlStateManager.disableBlend();
				GlStateManager.color(1, 1, 1, 1);
				GuiInventory.drawEntityOnScreen(30, 60, (int)(30 / target.height), 0, 0, target);

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