package xyz.vergoclient.modules.impl.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventPlayerRender;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.datas.DataFloat4;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class ModPlayerESP extends Module implements OnEventInterface {

	public ModPlayerESP() {
		super("PlayerESP", Category.VISUAL);

	}
	
	public ModeSetting mode = new ModeSetting("Mode", "2D");
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("2D"));
		
		addSettings(mode);
	}
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}

		if (mode.is("2D")) {
			doTwoD(e);
		}
	}

	private Vec3 getVec3(final EntityPlayer var0) {
		final float timer = mc.timer.renderPartialTicks;
		final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
		final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
		final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
		return new Vec3(x, y, z);
	}
    
    private void doTwoD(Event e) {
		int offset = 0;

		if (e instanceof EventPlayerRender && e.isPre()) {
//			ChatUtils.addChatMessage(positions.size());

			for (Object ent : mc.theWorld.loadedEntityList) {
				if (ent instanceof EntityPlayer
						&& (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) ent)))) {
					EntityPlayer player = (EntityPlayer) ent;
					if (!player.isUser()) {

						Vec3 vec3 = getVec3(player);
						float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
						float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
						float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);
						double halfWidth = player.width / 2.0D + 0.18F;

						AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
								posY + player.height + 0.18D, posZ + halfWidth);

						drawOutlinedBox(bb, 0xffffffff);

					}
				}
			}
		}
	}

	public static void drawOutlinedBox(AxisAlignedBB boundingBox, int color) {
		drawOutlinedBox(boundingBox, color, true);
	}

	public static void enableRender3D(boolean disableDepth) {
		if (disableDepth) {
			GL11.glDepthMask(false);
			GL11.glDisable(2929);
		}

		GL11.glDisable(3008);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(1.0F);
	}

	public static void disableRender3D(boolean enableDepth) {
		if (enableDepth) {
			GL11.glDepthMask(true);
			GL11.glEnable(2929);
		}

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glDisable(2848);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void setColor(int colorHex) {
		float alpha = (float)(colorHex >> 24 & 255) / 255.0F;
		float red = (float)(colorHex >> 16 & 255) / 255.0F;
		float green = (float)(colorHex >> 8 & 255) / 255.0F;
		float blue = (float)(colorHex & 255) / 255.0F;
		GL11.glColor4f(red, green, blue, alpha == 0.0F ? 1.0F : alpha);
	}

	public static void drawOutlinedBox(AxisAlignedBB boundingBox, int color, boolean disableDepth) {
		if (boundingBox != null) {
			enableRender3D(disableDepth);
			setColor(color);
			GL11.glBegin(3);
			GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
			GL11.glEnd();
			GL11.glBegin(3);
			GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
			GL11.glEnd();
			GL11.glBegin(1);
			GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
			GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			GL11.glEnd();
			disableRender3D(disableDepth);
		}
	}

    
}
