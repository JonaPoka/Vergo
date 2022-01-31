package xyz.vergoclient.util;

import org.lwjgl.opengl.Display;

import xyz.vergoclient.Vergo;

public class DisplayUtils {

	public static void setTitle(String title) {
		Display.setTitle("Vergo " + Vergo.version);
	}

	public static void setCustomTitle(String title) {
		Display.setTitle(title);
	}
	
}
