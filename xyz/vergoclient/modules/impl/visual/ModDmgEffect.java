package xyz.vergoclient.modules.impl.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.util.RenderUtils;

public class ModDmgEffect extends Module implements OnEventInterface {

    public ModDmgEffect() {
        super("DmgParticle", Category.VISUAL);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRender3D) {
            for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                if (!(entity instanceof EntityCrit2FX)) continue;
                EntityCritFX p = (EntityCritFX)(entity);
                double x = p.getPosition().getX();
                double n = x - mc.getRenderManager().viewerPosX;
                double y = p.getPosition().getY();
                double n2 = y - mc.getRenderManager().viewerPosY;
                double z = p.getPosition().getZ();
                double n3 = z - mc.getRenderManager().viewerPosZ;
                GlStateManager.pushMatrix();
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
                GlStateManager.translate((float)((float)n), (float)((float)n2), (float)((float)n3));
                GlStateManager.rotate((-mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
                float textY = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
                GlStateManager.rotate(mc.getRenderManager().playerViewX, (float)textY, (float)0.0f, (float)0.0f);
                double size = 0.03;
                GlStateManager.scale((double)-0.03, (double)-0.03, (double)0.03);
                RenderUtils.enableRender2D();
                RenderUtils.disableRender2D();
                GL11.glDepthMask((boolean)false);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glDepthMask((boolean)true);
                GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
                GlStateManager.disablePolygonOffset();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

}
