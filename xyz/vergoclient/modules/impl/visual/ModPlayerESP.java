package xyz.vergoclient.modules.impl.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
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
	
	public ModeSetting mode = new ModeSetting("Mode", "Rainbow", "Rainbow", "White 2D", "Exhi");
	public NumberSetting lineWidth = new NumberSetting("Line width", 1.3, 1, 3, 0.1);
	
	public static transient ArrayList<PlayerToRender> positions = new ArrayList<PlayerToRender>();
	public static class PlayerToRender extends DataFloat4{
		public Entity entity = null;
	}
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Rainbow", "White 2D", "Exhi"));
		
		addSettings(mode, lineWidth);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}
		
		if (mode.is("Rainbow"))
			rainbowEspEvent(e);
		else if (mode.is("White 2D"))
			white2dEspEvent(e);
		else if (mode.is("Exhi"))
			exhiEsp(e);
	}
	
	// Projects 3d coords to a 2d spot on your screen, allows me to make a 2d esp
    private static final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
	
    private static Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(x, y, z, modelMatrix, projectionMatrix, viewport, windowPosition)) {
            return new Vector3f(windowPosition.get(0) / scaleFactor,
                    (mc.displayHeight - windowPosition.get(1)) / scaleFactor, windowPosition.get(2));
        }

        return null;
    }
    
    private Vec3 getVec3(final EntityPlayer var0) {
        final float timer = mc.timer.renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }
    
    private void rainbowEspEvent(Event e) {
    	int offset = 0;
    	
		if (e instanceof EventRenderGUI && e.isPre()) {
//			ChatUtils.addChatMessage(positions.size());
			for (PlayerToRender data : positions) {
				offset = 0;
				GlStateManager.pushMatrix();
	            float x1 = data.x1;
	            float x2 = data.x2;
	            float y1 = data.y1;
	            float y2 = data.y2;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GlStateManager.enableAlpha();
                GL11.glEnable(GL11.GL_BLEND);
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 2);
                
                GlStateManager.color(0, 0, 0, 1);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                GL11.glVertex2f(x1, y1);
                GL11.glVertex2f(x1, y2);
                GL11.glVertex2f(x2, y2);
                GL11.glVertex2f(x2, y1);
                GL11.glEnd();
                
                GL11.glLineWidth(lineWidth.getValueAsFloat());
                GL11.glBegin(GL11.GL_LINES);
                
                for (float d = 0.01f; d <= 1; d += 0.01f) {
                	offset += 10;
                	float hue = (System.currentTimeMillis() + offset) % (int)(4 * 1000) / (float)(4 * 1000);
            		int color = Color.HSBtoRGB(hue, 1, 1);
                    float r = (float)(color >> 16 & 255) / 255.0f;
                    float g = (float)(color >> 8 & 255) / 255.0f;
                    float b = (float)(color & 255) / 255.0f;
                	GlStateManager.color(r, g, b, 1);
                    GL11.glVertex2f(x1 + ((x2 - x1) * (d - 0.01f)), y2);
                    GL11.glVertex2f(x1 + ((x2 - x1) * d), y2);
                }
                
                for (float d = 1; d >= 0.01f; d -= 0.01f) {
                	offset += 10;
                	float hue = (System.currentTimeMillis() + offset) % (int)(4 * 1000) / (float)(4 * 1000);
            		int color = Color.HSBtoRGB(hue, 1, 1);
                    float r = (float)(color >> 16 & 255) / 255.0f;
                    float g = (float)(color >> 8 & 255) / 255.0f;
                    float b = (float)(color & 255) / 255.0f;
                	GlStateManager.color(r, g, b, 1);
                    GL11.glVertex2f(x2, y1 + ((y2 - y1) * (d - 0.01f)));
                    GL11.glVertex2f(x2,  y1 + ((y2 - y1) * d));
                }
                
                for (float d = 1; d >= 0.01f; d -= 0.01f) {
                	offset += 10;
                	float hue = (System.currentTimeMillis() + offset) % (int)(4 * 1000) / (float)(4 * 1000);
            		int color = Color.HSBtoRGB(hue, 1, 1);
                    float r = (float)(color >> 16 & 255) / 255.0f;
                    float g = (float)(color >> 8 & 255) / 255.0f;
                    float b = (float)(color & 255) / 255.0f;
                	GlStateManager.color(r, g, b, 1);
                    GL11.glVertex2f(x1 + ((x2 - x1) * (d - 0.01f)), y1);
                    GL11.glVertex2f(x1 + ((x2 - x1) * d), y1);
                }
                
                for (float d = 0.01f; d <= 1; d += 0.01f) {
                	offset += 10;
                	float hue = (System.currentTimeMillis() + offset) % (int)(4 * 1000) / (float)(4 * 1000);
            		int color = Color.HSBtoRGB(hue, 1, 1);
                    float r = (float)(color >> 16 & 255) / 255.0f;
                    float g = (float)(color >> 8 & 255) / 255.0f;
                    float b = (float)(color & 255) / 255.0f;
                	GlStateManager.color(r, g, b, 1);
                    GL11.glVertex2f(x1, y1 + ((y2 - y1) * (d - 0.01f)));
                    GL11.glVertex2f(x1,  y1 + ((y2 - y1) * d));
                }
                
                GL11.glEnd();
                
//                GlStateManager.disableAlpha();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
	            GlStateManager.popMatrix();
			}
		}
		else if (e instanceof EventRender3D && e.isPre()) {
			positions.clear();
			for (Object ent : mc.theWorld.loadedEntityList) {
				if (ent instanceof EntityPlayer
						&& (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) ent)))) {
					EntityPlayer player = (EntityPlayer) ent;
					if (!player.isUser()) {
						
						GlStateManager.pushMatrix();
			            Vec3 vec3 = getVec3(player);
			            float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
			            float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
			            float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);
			            double halfWidth = player.width / 2.0D + 0.18F;
			            AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
			                    posY + player.height + 0.18D, posZ + halfWidth);
			            double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ},
			                    {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ},
			                    {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
			            Vector3f projection;
			            Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);
			            for (double[] vec : vectors) {
			                projection = project2D((float) vec[0], (float) vec[1], (float) vec[2], new ScaledResolution(mc).getScaleFactor());
			                if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
			                    position.x = Math.min(position.x, projection.x);
			                    position.y = Math.min(position.y, projection.y);
			                    position.z = Math.max(position.z, projection.x);
			                    position.w = Math.max(position.w, projection.y);
			                }
			            }
//			            entityPosMap.put(player, new float[]{position.x, position.z, position.y, position.w});
//			            Gui.drawRect(position.x, position.z, position.y, position.w, 0xffffffff);
			            PlayerToRender data = new PlayerToRender();
			            data.x1 = position.x;
			            data.x2 = position.z;
			            data.y1 = position.y;
			            data.y2 = position.w;
			            data.entity = player;
			            positions.add(data);
			            GL11.glPopMatrix();
						
					}
				}
			}
		}
	}
    
    private void white2dEspEvent(Event e) {
    	
		if (e instanceof EventRenderGUI && e.isPre()) {
//			ChatUtils.addChatMessage(positions.size());
			for (PlayerToRender data : positions) {
				GlStateManager.pushMatrix();
	            float x1 = data.x1;
	            float x2 = data.x2;
	            float y1 = data.y1;
	            float y2 = data.y2;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GlStateManager.enableAlpha();
                GL11.glEnable(GL11.GL_BLEND);
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                GL11.glLineWidth(lineWidth.getValueAsFloat());
                
                GlStateManager.color(1, 1, 1, 1);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                GL11.glVertex2f(x1, y1);
                GL11.glVertex2f(x1, y2);
                GL11.glVertex2f(x2, y2);
                GL11.glVertex2f(x2, y1);
                GL11.glEnd();
                
//                GlStateManager.disableAlpha();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
	            GlStateManager.popMatrix();
			}
		}
		else if (e instanceof EventRender3D && e.isPre()) {
			positions.clear();
			for (Object ent : mc.theWorld.loadedEntityList) {
				if (ent instanceof EntityPlayer
						&& (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) ent)))) {
					EntityPlayer player = (EntityPlayer) ent;
					if (!player.isUser()) {
						
						GlStateManager.pushMatrix();
			            Vec3 vec3 = getVec3(player);
			            float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
			            float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
			            float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);
			            double halfWidth = player.width / 2.0D + 0.18F;
			            AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
			                    posY + player.height + 0.18D, posZ + halfWidth);
			            double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ},
			                    {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ},
			                    {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
			            Vector3f projection;
			            Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);
			            for (double[] vec : vectors) {
			                projection = project2D((float) vec[0], (float) vec[1], (float) vec[2], new ScaledResolution(mc).getScaleFactor());
			                if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
			                    position.x = Math.min(position.x, projection.x);
			                    position.y = Math.min(position.y, projection.y);
			                    position.z = Math.max(position.z, projection.x);
			                    position.w = Math.max(position.w, projection.y);
			                }
			            }
//			            entityPosMap.put(player, new float[]{position.x, position.z, position.y, position.w});
//			            Gui.drawRect(position.x, position.z, position.y, position.w, 0xffffffff);
			            PlayerToRender data = new PlayerToRender();
			            data.x1 = position.x;
			            data.x2 = position.z;
			            data.y1 = position.y;
			            data.y2 = position.w;
			            data.entity = player;
			            positions.add(data);
			            GL11.glPopMatrix();
						
					}
				}
			}
		}
	}
    
    private void exhiEsp(Event e) {
    	int offset = 0;
    	
		if (e instanceof EventRenderGUI && e.isPre()) {
//			ChatUtils.addChatMessage(positions.size());
			for (PlayerToRender data : positions) {
				offset = 0;
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
	            float x1 = data.x1;
	            float x2 = data.x2;
	            float y1 = data.y1;
	            float y2 = data.y2;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GlStateManager.enableAlpha();
                GL11.glEnable(GL11.GL_BLEND);
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                
                float distance = ((x2 - x1) * 0.3f );
                if (distance < -120) {
                	distance = -120;
                }
                else if (distance > 120)
                	distance = 120;
                
                // Top left corner
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 5);
                GlStateManager.color(0, 0, 0, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x1, y1 + distance + 0.75f);
                GL11.glVertex2f(x1, y1);
                GL11.glVertex2f(x1 + 0.75f + distance, y1);
                GL11.glEnd();
                GL11.glLineWidth(lineWidth.getValueAsFloat());
                GlStateManager.color(1, 1, 1, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x1, y1 + distance);
                GL11.glVertex2f(x1, y1);
                GL11.glVertex2f(x1 + distance, y1);
                GL11.glEnd();
                
                // Top right corner
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 5);
                GlStateManager.color(0, 0, 0, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x2, y1 + distance + 0.75f);
                GL11.glVertex2f(x2, y1);
                GL11.glVertex2f(x2 + 0.25f - distance, y1);
                GL11.glEnd();
                GL11.glLineWidth(lineWidth.getValueAsFloat());
                GlStateManager.color(1, 1, 1, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x2, y1 + distance);
                GL11.glVertex2f(x2, y1);
                GL11.glVertex2f(x2 + 1 - distance, y1);
                GL11.glEnd();
                
                // Bottom left corner
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 5);
                GlStateManager.color(0, 0, 0, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x1, y2 - distance - 1);
                GL11.glVertex2f(x1, y2);
                GL11.glVertex2f(x1 - 1 + 0.75f + distance, y2);
                GL11.glEnd();
                GL11.glLineWidth(lineWidth.getValueAsFloat());
                GlStateManager.color(1, 1, 1, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x1, y2 - distance);
                GL11.glVertex2f(x1, y2);
                GL11.glVertex2f(x1 - 1 + distance, y2);
                GL11.glEnd();
                
                // Bottom right corner
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 5);
                GlStateManager.color(0, 0, 0, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x2, y2 - distance - 1);
                GL11.glVertex2f(x2, y2);
                GL11.glVertex2f(x2 + 1 - 0.75f - distance, y2);
                GL11.glEnd();
                GL11.glLineWidth(lineWidth.getValueAsFloat());
                GlStateManager.color(1, 1, 1, 1);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x2, y2 - distance);
                GL11.glVertex2f(x2, y2);
                GL11.glVertex2f(x2 + 1 - distance, y2);
                GL11.glEnd();
                
                // Health bar
                float distanceFromEsp = distance * 0.35f;
                GlStateManager.color(0, 0, 0, 1);
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 5);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x1 - distanceFromEsp, y1 - 1);
                GL11.glVertex2f(x1 - distanceFromEsp, y2 + 1);
                GL11.glEnd();
                GlStateManager.color(39.0f / 255, 146.0f / 255, 46.0f / 255, 1);
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 2);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x1 - distanceFromEsp, y1 + ((y2 - y1) * (1 - (((EntityPlayer)data.entity).getHealth() / ((EntityPlayer)data.entity).getMaxHealth()))));
                GL11.glVertex2f(x1 - distanceFromEsp, y2);
                GL11.glEnd(); 
                GL11.glLineWidth(lineWidth.getValueAsFloat() + 3);
                GlStateManager.color(0, 0, 0, 1);
                for (float progress = 0; progress <= 1; progress += 1f / 10) {
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    GL11.glVertex2f(x1 - distanceFromEsp, y1 + ((y2 - y1) * progress));
                    GL11.glVertex2f(x1 - distanceFromEsp, y1 + ((y2 - y1) * progress) + 0.1f);
                    GL11.glEnd();
                }
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GlStateManager.popAttrib();
	            GlStateManager.popMatrix();
			}
		}
		else if (e instanceof EventRender3D && e.isPre()) {
			positions.clear();
			for (Object ent : mc.theWorld.loadedEntityList) {
				if (ent instanceof EntityPlayer
						&& (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) ent)))) {
					EntityPlayer player = (EntityPlayer) ent;
					if (!player.isUser()) {
						
						GlStateManager.pushMatrix();
			            Vec3 vec3 = getVec3(player);
			            float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
			            float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
			            float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);
			            double halfWidth = player.width / 2.0D + 0.18F;
			            AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
			                    posY + player.height + 0.18D, posZ + halfWidth);
			            double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ},
			                    {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ},
			                    {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
			            Vector3f projection;
			            Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);
			            for (double[] vec : vectors) {
			                projection = project2D((float) vec[0], (float) vec[1], (float) vec[2], new ScaledResolution(mc).getScaleFactor());
			                if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
			                    position.x = Math.min(position.x, projection.x);
			                    position.y = Math.min(position.y, projection.y);
			                    position.z = Math.max(position.z, projection.x);
			                    position.w = Math.max(position.w, projection.y);
			                }
			            }
//			            entityPosMap.put(player, new float[]{position.x, position.z, position.y, position.w});
//			            Gui.drawRect(position.x, position.z, position.y, position.w, 0xffffffff);
			            PlayerToRender data = new PlayerToRender();
			            data.x1 = position.x;
			            data.x2 = position.z;
			            data.y1 = position.y;
			            data.y2 = position.w;
			            data.entity = player;
			            positions.add(data);
			            GL11.glPopMatrix();
						
					}
				}
			}
		}
    }
    
}
