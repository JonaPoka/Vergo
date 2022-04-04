package xyz.vergoclient.modules.impl.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.combat.KillAura;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.ui.guis.GuiClickGui;
import xyz.vergoclient.util.animations.Animation;
import xyz.vergoclient.util.animations.Direction;
import xyz.vergoclient.util.animations.impl.DecelerateAnimation;
import xyz.vergoclient.util.main.ColorUtils;
import xyz.vergoclient.util.Gl.BloomUtil;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.main.RenderUtils;

import java.awt.*;

public class TargetHud extends Module implements OnEventInterface {

	public TargetHud() {
		super("TargetHud", Category.VISUAL);
	}

	public ModeSetting mode = new ModeSetting("Mode", "Vergo", "Vergo");

	@Override
	public void loadSettings() {

		addSettings(mode);
	}

	Animation scale;

	public static double barSpeed;

	@Override
	public void onEnable() {
		scale = new DecelerateAnimation(300, 1, Direction.FORWARDS);
		barSpeed = 0;
	}

	@Override
	public void onDisable() {

	}


	public static transient double healthBarTarget = 0, healthBar = 0, hurtTime = 0, hurtTimeTarget = 0;

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI && e.isPre()) {
			EventRenderGUI event = (EventRenderGUI) e;

			renderVergoTargetHud(event);
		}
	}

	private void renderVergoTargetHud(EventRenderGUI e) {
		if (mode.is("Vergo")) {

			EntityLivingBase ent = null;

			if (Vergo.config.modKillAura.isEnabled() && KillAura.target != null) {
				ent = KillAura.target;
			}

			if (ent == null) {
				if (mc.currentScreen instanceof GuiClickGui) {
					ent = mc.thePlayer;
				}
			}

			if (ent != null) {
				GlStateManager.pushMatrix();

				GlStateManager.translate(484, 383, 1);

				double barSpeed = 6;
				if (healthBar > healthBarTarget) {
					healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
				} else if (healthBar < healthBarTarget) {
					healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
				}

				String healthStr = null;
				healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";

				JelloFontRenderer fr = FontUtil.bakakakmedium;

				// String data
				String playerName = ent.getName();

				String clientTag = "";

				// All the location and scale data.
				float x = 484;
				float y = 383;
				float width = (float) Math.max(75, FontUtil.arialMedium.getStringWidth(clientTag + playerName) + 25);
				float boxScale = 1;

				// Draws The Text
				fr.drawString(playerName, 30f, 4f, -1);
				fr.drawString(healthStr, 37 + width - FontUtil.bakakakmedium.getStringWidth(healthStr) - 2, 4f, -1);

				// This draws the background blur
				BlurUtil.blurAreaRounded(x, y, 40 + width, 40, 5f);


				// This draws the players 3D model.
				drawPlayerModel(ent);

				// Health bars and all that
				healthBarTarget = 82 * (ent.getHealth() / ent.getMaxHealth());
				if (healthBar > 82) {
					healthBar = 82;
				}


				final int startColour = ColorUtils.fadeBetween(new Color(255, 0, 115).getRGB(), new Color(109, 0, 182).getRGB(), 0);
				final int endColour = ColorUtils.fadeBetween(new Color(255, 0, 115).getRGB(), new Color(109, 0, 182).getRGB(), 250);

				RenderUtils.drawAlphaRoundedRect(27, 30, 82, 5f, 0f, getColor(5, 7, 15, 90));

				ColorUtils.glDrawSidewaysGradientRect(27, 30f, healthBar, 5f, endColour, startColour);

				BloomUtil.drawAndBloom(() -> ColorUtils.glDrawSidewaysGradientRect(x + 27, y + 29.5f, healthBar, 5f, endColour, startColour));

				BloomUtil.drawAndBloom(() -> ColorUtils.drawRoundedRect(x, y, 40 + width, 40, 10f, startColour));

				GlStateManager.popMatrix();
			}
		}
	}

	private void drawPlayerModel(EntityLivingBase ent) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {

		} else {
			GuiInventory.drawEntityOnScreen(15, 34, (int) (28 / ent.height), 0, 0, ent);
		}
		GlStateManager.popMatrix();
	}


	public void renderArmor(EntityPlayer player, int xLocation) {
		int xOffset = xLocation;

		int index;
		ItemStack stack;
		for (index = 3; index >= 0; --index) {
			stack = player.inventory.armorInventory[index];
			if (stack != null) {
				xOffset -= 8;
			}
		}

		for (index = 3; index >= 0; --index) {
			stack = player.inventory.armorInventory[index];
			if (stack != null) {
				ItemStack armourStack = stack.copy();
				if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
					armourStack.stackSize = 1;
				}

				renderItemStack(armourStack, xOffset, 10);
				xOffset += 16;
			}
		}
	}

	private void renderItemStack(ItemStack stack, int x, int y) {
		GlStateManager.pushMatrix();

		GlStateManager.disableAlpha();
		this.mc.getRenderItem().zLevel = -150.0F;

		GlStateManager.disableCull();

		GlStateManager.color(1, 1, 1, 255);
		this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack, x, y);

		GlStateManager.enableCull();

		this.mc.getRenderItem().zLevel = 0;

		GlStateManager.disableBlend();

		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		GlStateManager.disableDepth();
		GlStateManager.disableLighting();

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();

		GlStateManager.scale(2.0F, 2.0F, 2.0F);

		GlStateManager.enableAlpha();

		GlStateManager.popMatrix();
	}

	public static int getColor(Color color) {
		return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static int getColor(int brightness) {
		return getColor(brightness, brightness, brightness, 255);
	}

	public static int getColor(int brightness, int alpha) {
		return getColor(brightness, brightness, brightness, alpha);
	}

	public static int getColor(int red, int green, int blue) {
		return getColor(red, green, blue, 255);
	}

	public static int getColor(int red, int green, int blue, int alpha) {
		int color = 0;
		color |= alpha << 24;
		color |= red << 16;
		color |= green << 8;
		color |= blue;
		return color;
	}

}