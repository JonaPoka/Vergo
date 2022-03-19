package xyz.vergoclient.ui.notifications;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.Gl.BlurUtil;
import xyz.vergoclient.util.RenderUtils;

import java.awt.*;

public class Notification extends GuiScreen {
    private NotificationType type;
    private String title;
    private String messsage;
    private long start;

    private long fadedIn;
    private long fadeOut;
    private long end;


    public Notification(NotificationType type, String title, String messsage, int length) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        fadedIn = 400 * length;
        fadeOut = fadedIn + 800 * length;
        end = fadeOut + fadedIn;
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public static double offset;
    public static int width;
    public static int height;

    public void render() {
        offset = 0;
        width = 130;
        height = 30;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
        } else {
            offset = width;
        }

        Color color = new Color(29, 29, 29, 150);
        Color color1;

        if(type == NotificationType.TOGGLE_ON) {
            color1 = new Color(104, 189, 71);
        } else if(type == NotificationType.TOGGLE_OFF) {
            color1 = new Color(199, 21, 53);
        } else if(type == NotificationType.WARNING) {
            color1 = new Color(236, 101, 42);
        } else if(type == NotificationType.ERROR) {
            color1 = new Color(156, 0, 0);
        } else if(type == NotificationType.INFO) {
            color1 = new Color(169, 241, 140);
        } else {
            color1 = new Color(20, 20, 20);
        }

        JelloFontRenderer fontRendererTitle = FontUtil.jelloFontMedium;
        JelloFontRenderer fontRendererMsg = FontUtil.jelloFontSmall;

        RenderUtils.drawAlphaRoundedRect(GuiScreen.width - offset + 1, GuiScreen.height - 5 - height, width, height, 2f, color);

        // Fix some weird kinky bug
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        fontRendererTitle.drawString(title, (int) (GuiScreen.width - offset + 8), GuiScreen.height - height - 1, -1);
        fontRendererMsg.drawString(messsage, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 15, -1);

        RenderUtils.drawRoundedRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, width - offset + 4, height, 2f, color1);
    }

}
