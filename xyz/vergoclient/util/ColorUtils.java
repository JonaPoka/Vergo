package xyz.vergoclient.util;

import java.awt.*;

public class ColorUtils {

    /*

        Someone cool and sexy gave us this code.
        Forever grateful <3

     */

    public static Color fadeColor(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index * 2.0F / (float) count * 2.0F) % 2 - 1.0F);
        brightness = 0.2F + 0.6F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

}