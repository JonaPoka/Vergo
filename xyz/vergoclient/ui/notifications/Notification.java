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

        fadedIn = 200 * length;
        fadeOut = fadedIn + 500 * length;
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

    public void render() {
        double offset = 0;
        int width = 150;
        int height = 30;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
        } else {
            offset = width;
        }

        Color color = new Color(29, 29, 29);
        Color color1;

        if (type == NotificationType.INFO)
            color1 = new Color(92, 199, 128);
        else if (type == NotificationType.WARNING)
            color1 = new Color(205, 65, 0);
        else {
            color1 = new Color(149, 12, 30);
        }

        JelloFontRenderer fontRendererTitle = FontUtil.comfortaaNormal;
        JelloFontRenderer fontRendererMsg = FontUtil.comfortaaSmall;

        //drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width, GuiScreen.height - 5, color.getRGB());
        //drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width - offset + 4, GuiScreen.height - 5, color1.getRGB());

        RenderUtils.drawRoundedRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, width, height, 2f, color);

        //drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width - offset + 4, GuiScreen.height - 5, color1.getRGB());

        fontRendererTitle.drawString(title, (int) (GuiScreen.width - offset + 8), GuiScreen.height - height, -1);
        fontRendererMsg.drawString(messsage, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 15, -1);

        RenderUtils.drawRoundedRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, width - offset + 4, height, 2f, color1);
    }

}
