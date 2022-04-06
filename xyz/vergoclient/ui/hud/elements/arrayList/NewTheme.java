package xyz.vergoclient.ui.hud.elements.arrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.Gl.BloomUtil;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.main.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NewTheme implements OnEventInterface {

    public static transient TimerUtil arrayListToggleMovement = new TimerUtil();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRenderGUI && e.isPre()) {
            // Calls the arraylist to be drawn.
            drawArrayList();
        }
    }

    protected static Minecraft mc = Minecraft.getMinecraft();

    public static void drawArrayList() {

        JelloFontRenderer fr = FontUtil.kanitNormal;

        ArrayList<Module> modules = new ArrayList<>();
        ModuleManager.modules.forEach(module -> {
            if (module.arrayListAnimation > 0.01 || module.isEnabled()) modules.add(module);
        });
        modules.sort(Comparator.comparingDouble(module -> fr.getStringWidth(module.getName() + (module.getInfo().isEmpty() ? "" : " " + module.getInfo()))));
        Collections.reverse(modules);

        boolean updateToggleMovement = arrayListToggleMovement.hasTimeElapsed(1000 / 40, true);

        double offset = 0;
        for (Module module : modules) {

            String textToRender = module.getName() + " \2477" + module.getInfo();
            if (module.getInfo().isEmpty())
                textToRender = module.getName();

            if (updateToggleMovement) {
                if (module.isEnabled()) {
                    module.arrayListAnimation += (1 - module.arrayListAnimation) / 8;
                    if (module.arrayListAnimation > 1)
                        module.arrayListAnimation = 1;
                } else {
                    module.arrayListAnimation -= module.arrayListAnimation / 3;
                    if (module.arrayListAnimation < 0)
                        module.arrayListAnimation = 0;
                }
            }

            ScaledResolution sr = new ScaledResolution(mc);

            GlStateManager.pushMatrix();

            double squeeze = module.arrayListAnimation * 2;
            if (squeeze > 1)
                squeeze = 1;

            GlStateManager.translate((float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 0, 0);

            GlStateManager.scale(1, squeeze, 1);

            GlStateManager.translate(-(float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), -((float) (offset * (fr.FONT_HEIGHT + 4)) + 0), 0);

            BlurUtil.blurArea(sr.getScaledWidth() - fr.getStringWidth(textToRender) - 6.5, 3, sr.getScaledWidth(), (offset + 1) * (fr.FONT_HEIGHT + 4));

            String finalString = textToRender;
            double finalOffset = offset;

            //BloomUtil.drawAndBloom(() -> RenderUtils2.drawRect((float) (sr.getScaledWidth() - fr.getStringWidth(finalString) - 6.5), 0, sr.getScaledWidth(), (float) (finalOffset + 1) * (fr.FONT_HEIGHT + 4), new Color(30, 30, 30).getRGB()));

            final String finalText = textToRender;
            final double offsetFin = offset;

            final int startColour = ColorUtils.fadeBetween(new Color(109, 0, 182).getRGB(), new Color(120, 0, 192).getRGB(), 0);
            final int endColour = ColorUtils.fadeBetween(new Color(109, 0, 182).getRGB(), new Color(120, 0, 192).getRGB(), 250);

            BloomUtil.drawAndBloom(() -> ColorUtils.glDrawSidewaysGradientRect(sr.getScaledWidth() - fr.getStringWidth(finalText) - 6.5, (double) 2.5, (offsetFin + 1) * (fr.FONT_HEIGHT + 4), 1, startColour, endColour));

            float align = 3.5f;

            GlStateManager.colorState.alpha = 1;

            GlStateManager.translate((float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), (float) (offset * (fr.FONT_HEIGHT + 4)) + 0, 0);

            GlStateManager.scale(squeeze, squeeze, 1);

            GlStateManager.translate(-(float) (sr.getScaledWidth() - (fr.getStringWidth(textToRender) / 2) - 2), -((float) (offset * (fr.FONT_HEIGHT + 4)) + 0), 0);
            fr.drawString(textToRender, (float) (sr.getScaledWidth() - fr.getStringWidth(textToRender) - align), (float) (offset * (fr.FONT_HEIGHT + 4)) + 5.5f, ColorUtils.fadeColorHorizontal(new Color(109, 0, 182), (int) (offset), 15).getRGB());

            GlStateManager.popMatrix();

            offset++;
            if (squeeze != 1) {
                offset--;
                offset += squeeze;
            }
        }
    }

}