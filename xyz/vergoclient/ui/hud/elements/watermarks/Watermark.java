package xyz.vergoclient.ui.hud.elements.watermarks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import sun.java2d.pipe.SpanShapeRenderer;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.Gl.BloomUtil;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.main.*;

import java.awt.*;

public class Watermark implements OnEventInterface {

    private static final ResourceLocation VERGOSENSE_BACKGROUND_TEXTURE;

    static {
        VERGOSENSE_BACKGROUND_TEXTURE = new ResourceLocation("fuckafriendforfree.png");
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRenderGUI && e.isPre()) {
            EventRenderGUI event = (EventRenderGUI) e;
            drawWatermark(event);
        }
    }

    public static void drawWatermark(EventRenderGUI e) {
        if(Vergo.config.modHud.waterMark.is("Simple")) {
            SimpleWatermark sw = new SimpleWatermark();

            sw.onEvent(e);
        }

        if(Vergo.config.modHud.waterMark.is("vergosense")) {
            vergosenseWatermark vergosenseWatermark = new vergosenseWatermark();

            vergosenseWatermark.onEvent(e);
        }
    }
}