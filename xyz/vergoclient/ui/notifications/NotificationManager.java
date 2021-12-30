package xyz.vergoclient.ui.notifications;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.AnimationUtils;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.animations.SmoothAnimation;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class NotificationManager {
    private final float spacing = 10;
    private final float initialHeight = 10;
    CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public void drawScreen(ScaledResolution sr) {

        int count = 0;
        for (Notification notification : notifications) {
            if (notification.timer.hasTimeElapsed((long) notification.getTime(), false) || (notifications.size() > 4 && count == 0)) {
                if (notification.getAnimation() != null) {
                    if (notification.getAnimation().isDone()) {
                        notifications.remove(notification);
                        continue;
                    }
                } else {
                    vanish(notification, AnimationUtils.Direction.DOWN);
                }
            } else {
                if (notification.getAnimation() != null) {
                    if (notification.getAnimation().isDone()) {
                        notification.stopAnimation();
                    }
                }
            }

            JelloFontRenderer title = FontUtil.comfortaaNormal; //Adjustable
            JelloFontRenderer description = FontUtil.comfortaaSmall; //Adjustable
            //MinecraftFontRenderer secondsLeft = FontUtil.tommysmallfont; //Adjustable
            float barThickness = 3; //Adjustable
            float imageSpace = 25; //Adjustable (somewhat)
            ArrayList<String> strings = (ArrayList<String>) description.wrapWords(notification.getDescription(), notification.getWidth() - imageSpace - 20 - barThickness);
            float x = sr.getScaledWidth() - notification.getWidth() - 5;
            float addY = 0;
            for (int i2 = count; i2 > 0; i2--)
                addY += notifications.get(i2).getHeight();
            //addY -= notification.getHeight();

            //float y = sr.getScaledHeight() - notification.getHeight() - initialHeight - addY - (count * spacing);
            if (count == 0) notification.y = sr.getScaledHeight();
            notification.y = notifications.get(Math.max(count - 1, 0)).y - spacing - notification.getHeight();

            if (notification.animationInProgress()) {
                x += (notification.getAnimation().getOutput() * notification.getWidth()) * notification.getDirection().getXy()[0];
                notification.y += (notification.getAnimation().getOutput() * (notification.getHeight() + barThickness + initialHeight)) * notification.getDirection().getXy()[1];
                float scale = Math.max((float) (1 - Math.abs(notification.getAnimation().getOutput())), 0.01f);
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + notification.getWidth() + (1 - scale), notification.y, 0);
                GlStateManager.scale(scale, scale, 1.0f);
                GlStateManager.translate(-(x + notification.getWidth() + (1 - scale)), -notification.y, 0);
            }

            Color color = new Color(28, 28, 28);

            if (!notification.isHeightAdjusted()) {
                notification.setHeight(notification.getHeight() + (description.getHeight() * (strings.size() - 1)));
                notification.setHeightAdjusted(true);
            }

            RenderUtils.drawRoundedRect(x, notification.y - barThickness, notification.getWidth(), notification.getHeight(), notification.getRoundness(), color);
            Gui.drawRect(x, notification.y - barThickness, x + notification.getWidth(), notification.y, new Color(20, 20, 20).getRGB());
            Gui.drawRect(x, notification.y - barThickness, x + notification.getWidth() * Math.min((notification.timer.getTime() / notification.getTime()), 1), notification.y, 0xffffffff);

            title.drawString(notification.getTitle(), x + 45, notification.y + 8, 0xffffff);

            int stringNum = 0;
            for (String string : strings) {
                description.drawString(string, x + 45, notification.y + title.getHeight() + 14 + ((stringNum * 3) + (stringNum * description.getHeight())), 0xbbbbbb);
                stringNum++;
            }
            Gui.drawRect(0, 0, 0, 0, 0xffffff);

            description.drawString(new DecimalFormat("0.0").format((notification.getTime() - notification.timer.getTime()) / 1000D) + "s", x - 25 + notification.getWidth(), notification.y + 8, 0xfefefe);

            switch (notification.getType()) {
                case SUCCESS:
                    //Gui.drawRect(x + 5, notification.y + 5 + barThickness, x + 5 + imageSpace, notification.y + 5 + imageSpace + barThickness, new Color(255, 255, 255, 255).getRGB());
                    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Fan/bestcheckmark.png"));
                    Gui.drawModalRectWithCustomSizedTexture( (int) x + 10, (int) notification.y + 8, 0, 0, (int) imageSpace, (int) imageSpace, imageSpace, imageSpace);
                    break;
                case DISABLE:
                    //Gui.drawRect(x + 5, notification.y + 5, x + 5 + imageSpace, notification.y + 5 + imageSpace + barThickness, new Color(255, 255, 255, 255).getRGB());
                    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Fan/XMarkBest.png"));
                    Gui.drawModalRectWithCustomSizedTexture( (int) x + 10, (int) notification.y + 8, 0, 0, (int) imageSpace, (int) imageSpace, imageSpace, imageSpace);
                    break;
                case INFO:
                    Gui.drawRect(x + 10, notification.y + 8, x + 35, notification.y + 8 + imageSpace, new Color(255, 255, 255, 255).getRGB());
                    break;
                case WARNING:

                    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Fan/WarningSign.png"));
                    Gui.drawModalRectWithCustomSizedTexture( (int) x + 10, (int) notification.y + 8, 0, 0, (int) imageSpace, (int) imageSpace, imageSpace, imageSpace);
                    //Gui.drawRect(x + 10, notification.y + 8, x + 35, notification.y + 8 + imageSpace, new Color(255, 0, 0, 255).getRGB());
                    break;
                default:
                    Gui.drawRect(x + 105, notification.y + 8, x + 35, notification.y + 8 + imageSpace, new Color(0, 0, 0, 255).getRGB());
                    break;
            }
            if (notification.animationInProgress()) {
                GlStateManager.popMatrix();
            }
            count++;
        }
    }

    public void add(Notification notification) {
        notifications.add(notification);
        notification.startAnimation(new SmoothAnimation(250, -1, AnimationUtils.Direction.BACKWARDS));
        notification.setDirection(AnimationUtils.Direction.DOWN);
    }

    public void vanish(int i, AnimationUtils.Direction direction) {
        try {
            Notification notification = notifications.get(i);
            notification.startAnimation(new SmoothAnimation(250, 1));
            notification.setDirection(direction);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void vanish(Notification notification, AnimationUtils.Direction direction) {
        notification.startAnimation(new SmoothAnimation(250, 1));
        notification.setDirection(direction);
    }

    public void removeLast(Predicate<Notification> filter) {
        for (int i = notifications.size() - 1; i >= 0; i--) {
            if (filter.test(notifications.get(i))) {
                notifications.remove(i);
                return;
            }
        }
    }

}