package xyz.vergoclient.modules.impl.visual;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventPlayerRender;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModAntiBot;
import xyz.vergoclient.settings.ModeSetting;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class ModPlayerESP extends Module implements OnEventInterface {

	public ModPlayerESP() {
		super("PlayerESP", Category.VISUAL);

	}
	
	public ModeSetting mode = new ModeSetting("Mode", "2D");
	
	@Override
	public void loadSettings() {
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("2D"));

		addSettings(mode);
	}
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}

		if (mode.is("2D")) {
			doTwoD(e);
		}
	}

	private Vec3 getVec3(final EntityPlayer var0) {
		final float timer = mc.timer.renderPartialTicks;
		final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
		final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
		final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
		return new Vec3(x, y, z);
	}
    
    private void doTwoD(Event e) {
		int offset = 0;

		if (e instanceof EventPlayerRender && e.isPre()) {
//			ChatUtils.addChatMessage(positions.size());

			for (Object ent : mc.theWorld.loadedEntityList) {
				if (ent instanceof EntityPlayer && (Vergo.config.modAntibot.isDisabled() || !ModAntiBot.isBot(((EntityPlayer) ent)))) {

					EntityPlayer player = (EntityPlayer) ent;

					if (!player.isUser()) {

						Vec3 vec3 = getVec3(player);
						float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
						float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
						float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);

						double halfWidth = player.width / 2.0D + 0.18F;



					}
				}
			}

		}
	}

}
