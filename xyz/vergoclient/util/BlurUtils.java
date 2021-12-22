package xyz.vergoclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlurUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    private static ShaderGroup shaderGroup;

    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;

    private static final ResourceLocation shader = new ResourceLocation("shader/post/blur.json");

    private static void init() {
        try {
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("shader/post/blur.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setValues(float strength, float blurWidth, float blurHeight) {

        init();

        shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("Radius").set(strength);
        shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("Radius").set(strength);

        shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);

    }

}