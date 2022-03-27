package xyz.vergoclient.ui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.Gl.BloomUtil;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.ColorUtils;

import java.awt.*;

public class Watermark implements OnEventInterface {

    private static final ResourceLocation VERGOSENSE_BACKGROUND_TEXTURE;

    static {
        VERGOSENSE_BACKGROUND_TEXTURE = new ResourceLocation("fuckafriendforfree.png");
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRenderGUI && e.isPre()) {
            drawWatermark();
        }
    }

    public static void drawWatermark() {
        // Draws the watermark in the corner
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        if(Vergo.config.modHud.waterMark.is("Planet")) {
            GlStateManager.scale(1.5f, 1.5f, 1);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.drawImg(new ResourceLocation("Vergo/logo/512x512clear.png"), 0, 0, 30, 30);
        } else if(Vergo.config.modHud.waterMark.is("Rounded")) {
            GlStateManager.scale(1.5f, 1.5f, 1);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.drawImg(new ResourceLocation("Vergo/logo/512x512-transparent-round.png"), 0, 0, 32, 32);
        } else if(Vergo.config.modHud.waterMark.is("vergosense")) {

            JelloFontRenderer fr = FontUtil.comfortaaSmall;

            String vergoStr = "vergo" + EnumChatFormatting.GREEN + "sense" + EnumChatFormatting.WHITE + " | " + ServerUtils.getServerIP() + " | " + AccountUtils.account.username + "#" + FormattingUtil.formatUID();

            RenderUtils2.drawRect(2, 2, (float) (12 + fr.getStringWidth(vergoStr)), 18, new Color(0x434343).getRGB());
            RenderUtils2.drawRect(3f, 3f, (float) (10 + fr.getStringWidth(vergoStr)), 16, new Color(0x434343).darker().getRGB());
            RenderUtils.drawImg(VERGOSENSE_BACKGROUND_TEXTURE, 3f, 3f, (float) (10 + fr.getStringWidth(vergoStr)), 16);
            RenderUtils2.drawRect(5, 5, (float) (6 + fr.getStringWidth(vergoStr)), 12, new Color(0x434343).getRGB());
            RenderUtils2.drawRect(6f, 6f, (float) (4 + fr.getStringWidth(vergoStr)), 10, new Color(0x303030).darker().getRGB());

            fr.drawString(vergoStr, 8f, 9.5f, new Color(0xffffff).getRGB());

        } else if(Vergo.config.modHud.waterMark.is("Simple")) {

            final int startColour = ColorUtils.fadeBetween(new Color(210, 8, 62).getRGB(), new Color(108, 51, 217).getRGB(), 0);
            final int endColour = ColorUtils.fadeBetween(new Color(108, 51, 217).getRGB(), new Color(210, 8, 62).getRGB(), 250);

            JelloFontRenderer fr = FontUtil.comfortaaSmall;

            JelloFontRenderer fr1 = FontUtil.comfortaaSmall;

            String clientName = "Vergo - ";

            String serverName = ServerUtils.getServerIP() + " - ";

            String userName = AccountUtils.account.username;

            BloomUtil.drawAndBloom(() -> ColorUtils.glDrawSidewaysGradientRect(3, 5, (float) (fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + fr1.getStringWidth(userName)) + 6, 1.5f, startColour, endColour));
            BlurUtil.blurArea(3, 6, (float) (fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + fr1.getStringWidth(userName)) + 6, 12f);
            RenderUtils.drawAlphaRoundedRect(3, 6, (float) (fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + fr1.getStringWidth(userName)) + 6, 12f, 0f, new Color(60, 60, 60, 100));

            fr1.drawString(clientName, 5, 11, 0xffffffff);
            fr1.drawString(serverName, fr1.getStringWidth(clientName) + 5, 11, 0xffffffff);
            fr1.drawString(userName, fr1.getStringWidth(clientName) + fr1.getStringWidth(serverName) + 7, 11, 0xffffffff);

        } else if (Vergo.config.modHud.waterMark.is("Text")) {
            final int startColour = ColorUtils.fadeBetween(new Color(210, 8, 62).getRGB(), new Color(108, 51, 217).getRGB(), 0);
            final int endColour = ColorUtils.fadeBetween(new Color(108, 51, 217).getRGB(), new Color(210, 8, 62).getRGB(), 250);

            JelloFontRenderer fr = FontUtil.comfortaaHuge;

            JelloFontRenderer fr1 = FontUtil.comfortaaHuge;

            String clientName = " Vergo ";

            BloomUtil.drawAndBloom(() -> ColorUtils.glDrawSidewaysGradientRect(3, 5, (float) (fr1.getStringWidth(clientName)  + 5.3f), 2f, startColour, endColour));
            BlurUtil.blurArea(3, 6, (float) (fr1.getStringWidth(clientName) + 5), 21f);
            RenderUtils.drawAlphaRoundedRect(3, 6, (float) (fr1.getStringWidth(clientName) + 5), 21f, 2f, new Color(60, 60, 60, 100));

            fr1.drawString(clientName, 5, 12.25f, 0xffffffff);

        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
