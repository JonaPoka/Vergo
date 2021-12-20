package xyz.vergoclient.modules.impl.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.entity.AbstractClientPlayer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventPlayerRender;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.datas.DataFloat4;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class ModPlayerESP extends Module implements OnEventInterface {

	public ModPlayerESP() {
		super("PlayerESP", Category.VISUAL);

	}
	
	public ModeSetting mode = new ModeSetting("Mode", "3D Box");
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("3D Box"));
		
		addSettings(mode);
	}
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}

		if (mode.is("3D Box")) {
			doBoxESP(e);
		}
	}
    
    private void doBoxESP(Event e) {
    	int offset = 0;
    	
		if (e instanceof EventPlayerRender && e.isPre()) {
//			ChatUtils.addChatMessage(positions.size());
			AbstractClientPlayer player = ((EventPlayerRender) e).entity;
			RenderUtils.drawPlayerBox(player.posX, player.posY, player.posZ, player);

		}
    }
    
}
