package xyz.vergoclient.modules.impl.visual;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import org.lwjgl.opengl.GL11;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.combat.KillAura;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.guis.GuiClickGui;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.MiscellaneousUtils;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.animations.OpacityAnimation;

import java.awt.*;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;

public class TargetHud extends Module implements OnEventInterface {

	public TargetHud() {
		super("TargetHud", Category.VISUAL);
	}

	public float animation = 0;

	public ModeSetting mode = new ModeSetting("Mode", "Vergo Blue","Vergo Blue", "Vergo Red", "New Vergo");

	@Override
	public void loadSettings() {

		// mode.modes.addAll(Arrays.asList("Rismose", "Vergo Blue", "Vergo Red"));

		addSettings(mode);
	}

	private OpacityAnimation boxOpacity = new OpacityAnimation(0), textOpacity = new OpacityAnimation(0), barOpacity = new OpacityAnimation(0), armorOpacity = new OpacityAnimation(0);

	@Override
	public void onEnable() {
		boxOpacity.setOpacity(0);
		barOpacity.setOpacity(0);
		armorOpacity.setOpacity(0);
		textOpacity.setOpacity(0);
	}

	@Override
	public void onDisable() {
		//opacity.setOpacity(0);
	}


	public static transient double healthBarTarget = 0, healthBar = 0, hurtTime = 0, hurtTimeTarget = 0;

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI && e.isPre()) {
			if (mode.is("Rismose")) {
				EntityLivingBase target = null;

				if (Vergo.config.modKillAura.isEnabled() && KillAura.target != null) {
					target = KillAura.target;
				}

				if (target == null) {
					boxOpacity.setOpacity(0);
					barOpacity.setOpacity(0);
					armorOpacity.setOpacity(0);
					textOpacity.setOpacity(0);
					if (mc.currentScreen instanceof GuiClickGui) {
						target = mc.thePlayer;
					} else {
						healthBar = 0;
						return;
					}
				}

				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.translate(400, 400, 0);

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

				for (NetworkPlayerInfo info : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.getNetHandler().getPlayerInfoMap())) {
					if (target instanceof EntityMob || target instanceof EntityAnimal) {

					} else if (target instanceof EntityPlayer) {
						if (mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) == target) {
							mc.getTextureManager().bindTexture(info.getLocationSkin());
							drawScaledCustomSizeModalRect(16, 7, 8.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);
							if (((EntityPlayer) target).isWearing(EnumPlayerModelParts.HAT)) {
								drawScaledCustomSizeModalRect(16, 7, 40.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);
							}
							GlStateManager.bindTexture(0);
							break;
						}
					}
				}

				GlStateManager.popAttrib();
				GlStateManager.popMatrix();

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

			} else if (mode.is("Vergo Blue")) {

				EntityLivingBase ent = null;

				if (Vergo.config.modKillAura.isEnabled() && KillAura.target != null) {
					ent = KillAura.target;
				}

				if (ent == null) {
					if (mc.currentScreen instanceof GuiClickGui) {
						ent = mc.thePlayer;
					} else {
						boxOpacity.setOpacity(0);
						barOpacity.setOpacity(0);
						armorOpacity.setOpacity(0);
						textOpacity.setOpacity(0);
						return;
					}
				}

				// Lower is faster, higher is slower
				double barSpeed = 6;
				if (healthBar > healthBarTarget) {
					healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
				} else if (healthBar < healthBarTarget) {
					healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
				}

				String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";


				/* healthStr = String.valueOf((healthBar * (ent.getHealth() / ent.getMaxHealth())));
				if (healthBar > 10) {
					healthBar = 10;
				} */


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


				float width = (float) Math.max(75, FontUtil.arialMedium.getStringWidth(clientTag + playerName) + 25);


			/*if (BlurBuffer.blurEnabled()) {
				BlurBuffer.blurRoundArea(x + .5f, y + .5f, 28 + width - 1f, 30 - 1f, 2f, true);
			}*/

				//更改TargetHUD在屏幕坐标的初始位置

				GlStateManager.translate(400, 400, 0);

				boxOpacity.interp(250, 15);
				armorOpacity.interp(250, 15);
				barOpacity.interp(250, 15);
				textOpacity.interp(250, 15);

				//GL11.glColor4f();

				// RenderUtils2.drawBorderedRect(0, 0, 40 + width, 40, 1, getColor(20, 20, 20, (int) boxOpacity.getOpacity()), getColor(29, 29, 29, (int) boxOpacity.getOpacity()));
				RenderUtils.drawAlphaRoundedRect(0, 0, 40 + width, 40, 5, getColor(19, 24, 44, (int) boxOpacity.getOpacity()));

				FontUtil.bakakakmedium.drawString(clientTag + playerName, 30f, 4f, getColor(255, 255, 255, (int) textOpacity.getOpacity()));
				FontUtil.bakakakmedium.drawString(healthStr, 37 + width - FontUtil.bakakakmedium.getStringWidth(healthStr) - 2, 4f, getColor(255, 255, 255, (int) textOpacity.getOpacity()));

				boolean isNaN = Float.isNaN(ent.getHealth());
				float health = isNaN ? 20 : ent.getHealth();
				float maxHealth = isNaN ? 20 : ent.getMaxHealth();
				float healthPercent = MiscellaneousUtils.clampValue(health / maxHealth, 0, 1);

				//RenderUtils2.drawRoundedRect(30, 31.5f, 26 + width - 2, 34.5f, RenderUtils2.reAlpha(0, 0.35f));

				float barWidth = (26 + width - 2) - 37;
				float drawPercent = 47 + (barWidth / 100) * (healthPercent * 100);


				healthBarTarget = (((82) / (ent.getMaxHealth())) * (ent.getHealth()));

				// Health bar
				healthBarTarget = 82 * (ent.getHealth() / ent.getMaxHealth());
				if (healthBar > 82) {
					healthBar = 82;
				}

				// if (this.animation <= 0) {
				// this.animation = drawPercent;
				// }

				// if (ent.hurtTime <= 6) {
				// this.animation = AnimationUtils.getAnimationState(this.animation, drawPercent, (float) Math.max(10, (Math.abs(this.animation - drawPercent) * 30) * 0.4));
				// }


				/*RenderUtils2.drawRoundedRect(30, 31.5f, this.animation, 5f, new Color(142, 2, 32).getRGB());
				RenderUtils2.drawRoundedRect(30, 31.5f, drawPercent, 5f, new Color(142, 2, 32).getRGB());*/

				RenderUtils.drawAlphaRoundedRect(27, 30, 82, 5f, 3f, getColor(6, 9, 19, (int) barOpacity.getOpacity()));
				RenderUtils.drawAlphaRoundedRect(27, 30, healthBar, 5f, 3f, getColor(36, 96, 252, (int) barOpacity.getOpacity()));
				// RenderUtils.drawAlphaRoundedRect(27, 29, healthBar, 5f, 3f, getColor(142, 2, 32, (int) barOpacity.getOpacity()));

				float f3 = 33 + (barWidth / 100f) * (ent.getTotalArmorValue() * 5);
				if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {

				} else {
					this.renderArmor((EntityPlayer) ent, 67);
				}

				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();

				GlStateManager.resetColor();
				// 3D model of the target
				GlStateManager.disableBlend();
				GlStateManager.color(1, 1, 1, armorOpacity.getOpacity());
				if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {

				} else {
					GuiInventory.drawEntityOnScreen(15, 34, (int) (28 / ent.height), 0, 0, ent);
				}
				GL11.glPopMatrix();
			} else if (mode.is("New Vergo")) {

				EntityLivingBase ent = null;

				if (Vergo.config.modKillAura.isEnabled() && KillAura.target != null) {
					ent = KillAura.target;
				}

				if (ent == null) {
					if (mc.currentScreen instanceof GuiClickGui) {
						ent = mc.thePlayer;
					} else {
						boxOpacity.setOpacity(0);
						barOpacity.setOpacity(0);
						armorOpacity.setOpacity(0);
						textOpacity.setOpacity(0);
						return;
					}
				}

				// Lower is faster, higher is slower
				double barSpeed = 6;
				if (healthBar > healthBarTarget) {
					healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
				} else if (healthBar < healthBarTarget) {
					healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
				}

				String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";


				/* healthStr = String.valueOf((healthBar * (ent.getHealth() / ent.getMaxHealth())));
				if (healthBar > 10) {
					healthBar = 10;
				} */


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


				float width = (float) Math.max(75, FontUtil.arialMedium.getStringWidth(clientTag + playerName) + 25);


			/*if (BlurBuffer.blurEnabled()) {
				BlurBuffer.blurRoundArea(x + .5f, y + .5f, 28 + width - 1f, 30 - 1f, 2f, true);
			}*/

				//更改TargetHUD在屏幕坐标的初始位置

				GlStateManager.translate(x, y, 0);

				boxOpacity.interp(250, 15);
				armorOpacity.interp(250, 15);
				barOpacity.interp(250, 15);
				textOpacity.interp(250, 15);

				//GL11.glColor4f();

				// RenderUtils2.drawBorderedRect(0, 0, 40 + width, 40, 1, getColor(20, 20, 20, (int) boxOpacity.getOpacity()), getColor(29, 29, 29, (int) boxOpacity.getOpacity()));
				//RenderUtils.drawAlphaRoundedRect(0, 0, 40 + width, 40, 5, getColor(11, 13, 20, (int) boxOpacity.getOpacity()));

				BlurUtil.blurArea(x, y, 40 + width, 40);

				FontUtil.bakakakmedium.drawString(clientTag + playerName, 30f, 4f, getColor(255, 255, 255, (int) textOpacity.getOpacity()));
				FontUtil.bakakakmedium.drawString(healthStr, 37 + width - FontUtil.bakakakmedium.getStringWidth(healthStr) - 2, 4f, getColor(255, 255, 255, (int) textOpacity.getOpacity()));

				boolean isNaN = Float.isNaN(ent.getHealth());
				float health = isNaN ? 20 : ent.getHealth();
				float maxHealth = isNaN ? 20 : ent.getMaxHealth();
				float healthPercent = MiscellaneousUtils.clampValue(health / maxHealth, 0, 1);

				//RenderUtils2.drawRoundedRect(30, 31.5f, 26 + width - 2, 34.5f, RenderUtils2.reAlpha(0, 0.35f));

				float barWidth = (26 + width - 2) - 37;
				float drawPercent = 47 + (barWidth / 100) * (healthPercent * 100);


				healthBarTarget = (((82) / (ent.getMaxHealth())) * (ent.getHealth()));

				// Health bar
				healthBarTarget = 82 * (ent.getHealth() / ent.getMaxHealth());
				if (healthBar > 82) {
					healthBar = 82;
				}

				// if (this.animation <= 0) {
				// this.animation = drawPercent;
				// }

				// if (ent.hurtTime <= 6) {
				// this.animation = AnimationUtils.getAnimationState(this.animation, drawPercent, (float) Math.max(10, (Math.abs(this.animation - drawPercent) * 30) * 0.4));
				// }


				/*RenderUtils2.drawRoundedRect(30, 31.5f, this.animation, 5f, new Color(142, 2, 32).getRGB());
				RenderUtils2.drawRoundedRect(30, 31.5f, drawPercent, 5f, new Color(142, 2, 32).getRGB());*/

				RenderUtils.drawAlphaRoundedRect(27, 30, 82, 5f, 3f, getColor(5, 7, 15, (int) barOpacity.getOpacity()));
				RenderUtils.drawAlphaRoundedRect(27, 30, healthBar, 5f, 3f, getColor(207, 84, 74, (int) barOpacity.getOpacity()));
				// RenderUtils.drawAlphaRoundedRect(27, 29, healthBar, 5f, 3f, getColor(142, 2, 32, (int) barOpacity.getOpacity()));

				float f3 = 33 + (barWidth / 100f) * (ent.getTotalArmorValue() * 5);
				if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {

				} else {
					this.renderArmor((EntityPlayer) ent, 67);
				}

				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();

				GlStateManager.resetColor();
				// 3D model of the target
				GlStateManager.disableBlend();
				GlStateManager.color(1, 1, 1, armorOpacity.getOpacity());
				if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {

				} else {
					GuiInventory.drawEntityOnScreen(15, 34, (int) (28 / ent.height), 0, 0, ent);
				}
				GL11.glPopMatrix();
			} else if (mode.is("Vergo Red")) {

				EntityLivingBase ent = null;

				if (Vergo.config.modKillAura.isEnabled() && KillAura.target != null) {
					ent = KillAura.target;
				}

				if (ent == null) {
					if (mc.currentScreen instanceof GuiClickGui) {
						ent = mc.thePlayer;
					} else {
						boxOpacity.setOpacity(0);
						barOpacity.setOpacity(0);
						armorOpacity.setOpacity(0);
						textOpacity.setOpacity(0);
						return;
					}
				}

				// Lower is faster, higher is slower
				double barSpeed = 6;
				if (healthBar > healthBarTarget) {
					healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
				} else if (healthBar < healthBarTarget) {
					healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
				}

				String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";


				/* healthStr = String.valueOf((healthBar * (ent.getHealth() / ent.getMaxHealth())));
				if (healthBar > 10) {
					healthBar = 10;
				} */


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


				float width = (float) Math.max(75, FontUtil.arialMedium.getStringWidth(clientTag + playerName) + 25);


			/*if (BlurBuffer.blurEnabled()) {
				BlurBuffer.blurRoundArea(x + .5f, y + .5f, 28 + width - 1f, 30 - 1f, 2f, true);
			}*/

				//更改TargetHUD在屏幕坐标的初始位置

				GlStateManager.translate(x, y, 0);

				boxOpacity.interp(250, 5);
				armorOpacity.interp(250, 5);
				barOpacity.interp(250, 5);
				textOpacity.interp(250, 5);

				//GL11.glColor4f();

				// RenderUtils2.drawBorderedRect(0, 0, 40 + width, 40, 1, getColor(20, 20, 20, (int) boxOpacity.getOpacity()), getColor(29, 29, 29, (int) boxOpacity.getOpacity()));
				RenderUtils.drawRoundedRect(0, 0, 40 + width, 40, 5, new Color(19, 24, 44));


				FontUtil.bakakakmedium.drawString(clientTag + playerName, 30f, 4f, getColor(255, 255, 255, (int) textOpacity.getOpacity()));
				FontUtil.bakakakmedium.drawString(healthStr, 37 + width - FontUtil.bakakakmedium.getStringWidth(healthStr) - 2, 4f, getColor(255, 255, 255, (int) textOpacity.getOpacity()));

				boolean isNaN = Float.isNaN(ent.getHealth());
				float health = isNaN ? 20 : ent.getHealth();
				float maxHealth = isNaN ? 20 : ent.getMaxHealth();
				float healthPercent = MiscellaneousUtils.clampValue(health / maxHealth, 0, 1);

				//RenderUtils2.drawRoundedRect(30, 31.5f, 26 + width - 2, 34.5f, RenderUtils2.reAlpha(0, 0.35f));

				float barWidth = (26 + width - 2) - 37;
				float drawPercent = 47 + (barWidth / 100) * (healthPercent * 100);


				healthBarTarget = (((82) / (ent.getMaxHealth())) * (ent.getHealth()));

				// Health bar
				healthBarTarget = 82 * (ent.getHealth() / ent.getMaxHealth());
				if (healthBar > 82) {
					healthBar = 82;
				}

				// if (this.animation <= 0) {
				// this.animation = drawPercent;
				// }

				// if (ent.hurtTime <= 6) {
				// this.animation = AnimationUtils.getAnimationState(this.animation, drawPercent, (float) Math.max(10, (Math.abs(this.animation - drawPercent) * 30) * 0.4));
				// }


				/*RenderUtils2.drawRoundedRect(30, 31.5f, this.animation, 5f, new Color(142, 2, 32).getRGB());
				RenderUtils2.drawRoundedRect(30, 31.5f, drawPercent, 5f, new Color(142, 2, 32).getRGB());*/

				RenderUtils.drawRoundedRect(27, 30, 82, 5f, 3f, new Color(6, 9, 19, 255));
				RenderUtils.drawRoundedRect(27, 30, healthBar, 5f, 3f, new Color(252, 14, 65));
				// RenderUtils.drawAlphaRoundedRect(27, 29, healthBar, 5f, 3f, getColor(142, 2, 32, (int) barOpacity.getOpacity()));

				float f3 = 33 + (barWidth / 100f) * (ent.getTotalArmorValue() * 5);
				if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {

				} else {
					this.renderArmor((EntityPlayer) ent, 67);
				}

				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();

				GlStateManager.resetColor();
				// 3D model of the target
				GlStateManager.disableBlend();
				GlStateManager.color(1, 1, 1, armorOpacity.getOpacity());
				if (ent instanceof EntityMob || ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntityArmorStand) {
					GuiInventory.drawEntityOnScreen(15, 34, (int) (28 / ent.height), 0, 0, ent);
				} else {

				}
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

		GlStateManager.color(1, 1, 1, armorOpacity.getOpacity());
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