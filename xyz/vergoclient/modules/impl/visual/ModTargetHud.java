package xyz.vergoclient.modules.impl.visual;

import java.awt.Color;
import java.text.DecimalFormat;

import com.mojang.authlib.GameProfile;
import javafx.animation.Animation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import optifine.MathUtils;
import org.lwjgl.opengl.GL11;
import sun.font.FontManager;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
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
import xyz.vergoclient.util.*;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;

public class ModTargetHud extends Module implements OnEventInterface {

	public ModTargetHud() {
		super("TargetHud", Category.VISUAL);
	}

	public float animation = 0;

	public ModeSetting mode = new ModeSetting("Mode", "Rismose","coinchan", "Rismose");
	public NumberSetting xOffset = new NumberSetting("X position", 400, 0, 1000, 1);
	public NumberSetting yOffset = new NumberSetting("Y position", 400, 0, 1000, 1);
	public NumberSetting heartSliderX = new NumberSetting("Heart SliderX", 45, 0, 200, 1);
	public NumberSetting heartSliderY = new NumberSetting("Heart SliderY", 45, 0, 200, 1);
	public NumberSetting healthSliderX = new NumberSetting("Health SliderX", 45, 0, 800, 1);
	public NumberSetting healthSliderY = new NumberSetting("Health SliderY", 45, 0, 800, 1);
	public NumberSetting healthWidth = new NumberSetting("Health Width", 140, 0, 250, 1);
	public NumberSetting hurtSliderX = new NumberSetting("Hurt SliderX", 45, 0, 800, 1);
	public NumberSetting hurtSliderY = new NumberSetting("Hurt SliderY", 45, 0, 800, 1);
	public NumberSetting hurtWidth = new NumberSetting("Hurt Width", 140, 0, 250, 1);
	public NumberSetting nameSliderX = new NumberSetting("NameSliderX", 0, 0, 200, 1);
	public NumberSetting nameSliderY = new NumberSetting("NameSliderY", 0, 0, 200, 1 );
	public NumberSetting characterX = new NumberSetting("CharX", 0, 0, 200, 1);
	public NumberSetting characterY = new NumberSetting("CharY", 0, 0, 200, 1);
	public NumberSetting characterScale = new NumberSetting("CharScale", 0, 0, 1000, 1);

	@Override
	public void loadSettings() {

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

		addSettings(mode, xOffset, yOffset, heartSliderX, heartSliderY, healthSliderX, healthSliderY, healthWidth, hurtSliderX, hurtSliderY, hurtWidth, nameSliderX, nameSliderY, characterX, characterY, characterScale);
	}

	public static transient double healthBarTarget = 0, healthBar = 0, hurtTime = 0, hurtTimeTarget = 0;

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI && e.isPre()) {

			/*if (mode.is("Complex")) {

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


				// Gui.drawRect(70, 40, 210, 45.5f, 0xb836393f);
				// Gui.drawRect(70, 40, 70 + hurtTime, 45.5f, color);

				RenderUtils.drawRoundedRect(65, 45, 140, 10f, 2, new Color(23, 23, 33));
				RenderUtils.drawRoundedRect(65, 45, healthBar, 10f, 2, new Color(48, 194, 124));

				// Name
				if (target.getDisplayName().getFormattedText().length() < 9) {
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
				GuiInventory.drawEntityOnScreen(27, 58, (int) (45 / target.height), 0, 0, target);*/
			if (mode.is("Rismose")) {


				EntityLivingBase target = null;

				if (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
					target = ModKillAura.target;
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

				healthBarTarget = (((117) / (target.getMaxHealth())) * (target.getHealth()));

				int color = 0xff3396FF;

				if (Vergo.config.modRainbow.isEnabled()) {
					float hue1 = System.currentTimeMillis() % (int) ((4) * 1000) / (float) ((4) * 1000);
					color = Color.HSBtoRGB(hue1, 0.65f, 1);
				}

				// Main box
				RenderUtils.drawRoundedRect(0, 0, 133, 60, 4, new Color(37, 38, 54));

				// Health bar
				healthBarTarget = (117 * (target.getHealth() / target.getMaxHealth()));
				if (healthBar > 117) {
					healthBar = 117;
				}

				// Hurt time bar
				hurtTimeTarget = 117 - (117 * ((float) target.hurtResistantTime / (float) target.maxHurtResistantTime));
				if (hurtTime > 117) {
					hurtTime = 117;
				}

				// Render the backgrounds
				RenderUtils.drawRoundedRect(8, 36, 117, 7f, 3, new Color(21, 21, 35));
				RenderUtils.drawRoundedRect(8, 46, 117, 7f, 3, new Color(21, 21, 35));

				// Render the bars
				RenderUtils.drawRoundedRect(8, 36, healthBar, 7f, 3, new Color(48, 194, 124));
				RenderUtils.drawRoundedRect(8, 46, hurtTime, 7f, 3, new Color(255, 153, 51));


				if (target instanceof EntityMob || target instanceof EntityAnimal) {

				} else if (target instanceof EntityPlayer) {
					this.renderArmor((EntityPlayer) target, 85);
				}

				GlStateManager.resetColor();
				for (NetworkPlayerInfo info : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.getNetHandler().getPlayerInfoMap())) {
					if (target instanceof EntityMob || target instanceof EntityAnimal) {

						GlStateManager.resetColor();

					} else if (target instanceof EntityPlayer) {
						GlStateManager.resetColor();
						if (mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) == target) {
							mc.getTextureManager().bindTexture(info.getLocationSkin());
							GlStateManager.resetColor();
							drawScaledCustomSizeModalRect(16, 7, 8.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);
							if (((EntityPlayer) target).isWearing(EnumPlayerModelParts.HAT)) {
								drawScaledCustomSizeModalRect(16, 7, 40.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);
							}
							GlStateManager.bindTexture(0);
							break;
						}
					}
				}


				// POSITONS
				// HEALTH: X-8?, Y-43?, W-117?
				// HURT: X-8?, Y-36?, W-117?


				// Name
				/* if (target.getDisplayName().getFormattedText().length() < 9) {
					int length = target.getDisplayName().getFormattedText().length();
					int nine = 9;
					int newLength = length - nine;
					FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 85 + newLength, 13, color);
				}
				if (target.getDisplayName().getFormattedText().length() == 9) {
					FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 85, 13, color);
				} else {
					if (target.getDisplayName().getFormattedText().length() == 10) {
						int length = target.getDisplayName().getFormattedText().length();
						int nine = 9;
						int newLength = length - nine;
						FontUtil.bakakakBig.drawString(target.getDisplayName().getFormattedText(), 85 - newLength, 13, color);
					}
				} */
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();

		/*} else if (mode.is("Paper")) {
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
			if (target.getDisplayName().getFormattedText().length() < 9) {
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
			//GlStateManager.popAttrib();
			//GlStateManager.popMatrix();*/
			} else if (mode.is("coinchan")) {

				EntityLivingBase ent = null;

				if (Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
					ent = ModKillAura.target;
				}

				if (ent == null) {
					if (mc.currentScreen instanceof GuiClickGui || mc.currentScreen instanceof GuiNewClickGui) {
						ent = mc.thePlayer;
					} else {
						return;
					}
				}

				int x = 484;
				int y = 359;

				// EXTREMELY SECRET. DO NOT FUCKING RE-USE. SERIOUSLY, I WILL GET FUCKING SUED.

				Color color;

				GlStateManager.pushMatrix();
				String playerName = ent.getName();

				String clientTag = "";

				//IRCUser user = IRCUser.getIRCUserByIGN(playerName);

				//if (user != null) {
				//	clientTag = "\247" + user.rank.charAt(0) + "[" + user.rank.substring(1) + "|" + user.username + "] \247f";
				//}

				String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";
				float width = (float) Math.max(75, FontUtil.arialMedium.getStringWidth(clientTag + playerName) + 25);

			/*if (BlurBuffer.blurEnabled()) {
				BlurBuffer.blurRoundArea(x + .5f, y + .5f, 28 + width - 1f, 30 - 1f, 2f, true);
			}*/

				//更改TargetHUD在屏幕坐标的初始位置
				GlStateManager.translate(x, y, 0);
				RenderUtils2.drawBorderedRect(0, 0, 40 + width, 40, 1, new Color(20, 20, 20, 200), new Color(70, 70, 70, 200));

				FontUtil.arialMedium.drawString(clientTag + playerName, 30f, 4f, 0xffffffff);
				FontUtil.arialMedium.drawString(healthStr, 37 + width - FontUtil.arialMedium.getStringWidth(healthStr) - 2, 4f, 0xffcccccc);

				boolean isNaN = Float.isNaN(ent.getHealth());
				float health = isNaN ? 20 : ent.getHealth();
				float maxHealth = isNaN ? 20 : ent.getMaxHealth();
				float healthPercent = MiscellaneousUtils.clampValue(health / maxHealth, 0, 1);

				//RenderUtils2.drawRoundedRect(30, 31.5f, 26 + width - 2, 34.5f, RenderUtils2.reAlpha(0, 0.35f));

				float barWidth = (26 + width - 2) - 37;
				float drawPercent = 47 + (barWidth / 100) * (healthPercent * 100);

				if (this.animation <= 0) {
					this.animation = drawPercent;
				}

				if (ent.hurtTime <= 6) {
					this.animation = AnimationUtils.getAnimationState(this.animation, drawPercent, (float) Math.max(10, (Math.abs(this.animation - drawPercent) * 30) * 0.4));
				}


				/*RenderUtils2.drawRoundedRect(30, 31.5f, this.animation, 5f, new Color(142, 2, 32).getRGB());
				RenderUtils2.drawRoundedRect(30, 31.5f, drawPercent, 5f, new Color(142, 2, 32).getRGB());*/

				RenderUtils.drawRoundedRect( 27, 27, this.animation, 5f, 3f, new Color(142, 2, 32));
				RenderUtils.drawRoundedRect(27, 27, drawPercent, 5f, 3f, new Color(142, 2, 32));

				float f3 = 33 + (barWidth / 100f) * (ent.getTotalArmorValue() * 5);
				this.renderArmor((EntityPlayer) ent, 75);

				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();

				GlStateManager.resetColor();
				// 3D model of the target
				GlStateManager.disableBlend();
				GlStateManager.color(1, 1, 1, 1);
				GuiInventory.drawEntityOnScreen(15, 34, (int) (28 / ent.height), 0, 0, ent);
				GL11.glPopMatrix();
			}
		}
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

	private void renderPlayer2d(final double n, final double n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8, final float n9, final float n10, final AbstractClientPlayer abstractClientPlayer) {
		mc.getTextureManager().bindTexture(abstractClientPlayer.getLocationSkin());
		GL11.glEnable(3042);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		drawScaledCustomSizeModalRect((int)n, (int)n2, n3, n4, n5, n6, n7, n8, n9, n10);
		GL11.glDisable(3042);
	}

}