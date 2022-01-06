package xyz.vergoclient.modules.impl.visual;

import org.lwjgl.opengl.GL11;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.MiscellaneousUtils;
import xyz.vergoclient.util.RotationUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import static xyz.vergoclient.modules.impl.visual.ModTargetHud.healthBar;

public class ModNametags extends Module implements OnEventInterface {

	public ModNametags() {
		super("Nametags", Category.VISUAL);
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			Vec3 playerPos = MiscellaneousUtils.getRenderEntityPos(mc.thePlayer);
			
			for (EntityPlayer player : mc.theWorld.playerEntities) {
				if (player.isUser() || player.isDead || (Vergo.config.modAntibot.isEnabled() && ModAntiBot.isBot(player))) {
					continue;
				}
				Vec3 targetPos = MiscellaneousUtils.getRenderEntityPos(player);
				double x = targetPos.xCoord, y = targetPos.yCoord + player.getEyeHeight() + 1, z = targetPos.zCoord;
				String text = "§f" + player.getDisplayName().getUnformattedTextForChat();
				String scoreTitle = text;
				String textNoColorFormatting = "";
				boolean removeNext = false;
				for (char c : scoreTitle.toCharArray()) {
					if (c == "§".toCharArray()[0]) {
						removeNext = true;
					}
					else if (!removeNext) {
						textNoColorFormatting += c;
					}else {
						removeNext = false;
					}
				}
				int color = -1;
				double opacity = 1;
				float[] rots = RotationUtils.getRotationFromPosition(playerPos.xCoord, playerPos.zCoord, playerPos.yCoord + mc.thePlayer.getEyeHeight(), x, z, y);
				
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.enablePolygonOffset();
				GL11.glPolygonOffset(1.0f, -1100000.0f);
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.enableBlend();
				double scale = 35;
				GlStateManager.scale(1/scale, 1/scale, 1/scale);
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.enableTexture2D();
				GlStateManager.translate((x - mc.getRenderManager().renderPosX) * scale,
						(y - mc.getRenderManager().renderPosY) * -scale, (z - mc.getRenderManager().renderPosZ) * -scale);
				GlStateManager.rotate((float) rots[0], 0, 1, 0);
				GlStateManager.rotate((float) rots[1], 1, 0, 0);
				JelloFontRenderer fr = FontUtil.juraNormal;
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.color(1, 1, 1, (float) (opacity));
				GlStateManager.translate(-fr.getStringWidth(text) / 2, 0, 0);
				
				// Render stuff
				// Tinted background
				Gui.drawRect(-3, -3, fr.getStringWidth(text) + 3, fr.FONT_HEIGHT + 3, 0x90000000);
				
				// Heath bar
				double health = (fr.getStringWidth(text) + 6) * (player.getHealth() / player.getMaxHealth());

				Gui.drawRect(-3, fr.FONT_HEIGHT, fr.getStringWidth(text) + 3, fr.FONT_HEIGHT + 3, 0x90000000);
				Gui.drawRect(-3, fr.FONT_HEIGHT, health - 3, fr.FONT_HEIGHT + 3, Colors.BLUE.getColor());
				
				// Render name
				GlStateManager.translate(0, 1, 0);
//				GlStateManager.colorState.alpha = 1;
//				fr.drawString(textNoColorFormatting, -0.1F, 0.0F, -1);
//				GlStateManager.colorState.alpha = 1;
//				fr.drawString(textNoColorFormatting, 0.0F, -0.1F, -1);
//				GlStateManager.colorState.alpha = 1;
//				fr.drawString(textNoColorFormatting, 0.1F, 0.0F, -1);
//				GlStateManager.colorState.alpha = 1;
//				fr.drawString(textNoColorFormatting, 0.0F, 0.1F, -1);
				GlStateManager.colorState.alpha = 1;
				fr.drawString(text, 0, 0, color);
				
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.disableTexture2D();
				GlStateManager.disableBlend();
				GlStateManager.enableDepth();
				GlStateManager.enableLighting();
				GlStateManager.disablePolygonOffset();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
				
			}
			
		}
		
	}

}
