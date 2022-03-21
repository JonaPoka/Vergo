package xyz.vergoclient.modules.impl.movement;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.*;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;


public class Fly extends Module implements OnEventInterface {

	Timer timer;

	public Fly() {
		super("Fly", Category.MOVEMENT);
		this.timer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");

	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList( "Vanilla"));
		
		addSettings(mode);
	}

	public static BlockPos position = null;

	public static double y = 0;

	@Override
	public void onEnable() {
		//if(mode.is("Hypixel")) {
			//y = mc.thePlayer.posY;

			//if(mc.thePlayer.onGround) {
			//	mc.thePlayer.posY = 0.05D;
			//}
		//}

		y = mc.thePlayer.posY;

		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);

	}
	
	@Override
	public void onDisable() {
		if(mode.is("Vanilla")) {

			mc.thePlayer.capabilities.isFlying = false;

		}
	}

	public int state;

	@Override
	public void onEvent(Event e) {


		if(e instanceof EventRender3D) {
			GlStateManager.color(1.0f, 1.0f, 1.0f);

			// Alias
			glEnable(GL_LINE_SMOOTH);
			glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

			RenderUtils.drawLine(mc.thePlayer.posX - 0.5, mc.thePlayer.posY, mc.thePlayer.posZ - 0.6f, mc.thePlayer.posX + 0.5, mc.thePlayer.posY, mc.thePlayer.posZ - 0.6f);

			RenderUtils.drawLine(mc.thePlayer.posX - 0.5, mc.thePlayer.posY, mc.thePlayer.posZ - 0.6f, mc.thePlayer.posX - 0.5, mc.thePlayer.posY, mc.thePlayer.posZ + 0.4f);

			RenderUtils.drawLine(mc.thePlayer.posX + 0.5, mc.thePlayer.posY, mc.thePlayer.posZ + 0.4f, mc.thePlayer.posX - 0.5, mc.thePlayer.posY, mc.thePlayer.posZ + 0.4f);

			RenderUtils.drawLine(mc.thePlayer.posX + 0.5, mc.thePlayer.posY, mc.thePlayer.posZ + 0.4, mc.thePlayer.posX + 0.5, mc.thePlayer.posY, mc.thePlayer.posZ - 0.6f);

			// Alias be gone
			glDisable(GL_LINE_SMOOTH);
			glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
		}

		if(e instanceof EventMove) {

			EventMove event = (EventMove) e;

			if(mode.is("Vanilla")) {

				if(!mc.thePlayer.capabilities.isFlying) {
					mc.thePlayer.capabilities.isFlying = true;
				}

			}

		}

	}
	
}
