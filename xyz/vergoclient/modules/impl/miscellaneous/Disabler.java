package xyz.vergoclient.modules.impl.miscellaneous;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.assets.Icons;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.*;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.*;
import net.minecraft.network.Packet;
import xyz.vergoclient.util.animations.Animation;
import xyz.vergoclient.util.animations.Direction;
import xyz.vergoclient.util.animations.impl.DecelerateAnimation;
import xyz.vergoclient.util.animations.impl.EaseBackIn;
import xyz.vergoclient.util.packet.PacketUtil;

import static org.lwjgl.opengl.GL11.*;

public class Disabler extends Module implements OnEventInterface {

	Timer timer;

	public Disabler() {
		super("Disabler", Category.MISCELLANEOUS);
		this.timer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Disabler", "Watchdog", "Watchdog");

	public BooleanSetting notiToggle = new BooleanSetting("Notification", true);

	private boolean cancel;
	
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();

	Animation notiAnim1 = null, notiAnim2 = null;

	@Override
	public void loadSettings() {


		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Watchdog"));
		addSettings(mode, notiToggle);
		
	}

	public TimerUtil timer1 = new TimerUtil();
	public TimerUtil timer2 = new TimerUtil();
	public TimerUtil timer3= new TimerUtil();


	public boolean hasDisablerFinished;

	@Override
	public void onEnable() {

		if(mode.is("Watchdog")) {
			setInfo("Watchdawg");
		}

		notiAnim1 = new DecelerateAnimation(800, 1, Direction.FORWARDS);

		if(mc.thePlayer.ticksExisted > 60 ) {
			hasDisablerFinished = true;
		}

	}
	
	@Override
	public void onDisable() {


	}

	public static double boxY;

	@Override
	public void onEvent(Event e) {

		/*if(!ServerUtils.isOnHypixel()) {
			setInfo("");
			return;
		}*/

		if(e instanceof EventMove) {
			EventMove moveE = (EventMove) e;
			if (mc.isSingleplayer()) return;
			if (timer1.hasTimeElapsed(10000, true)) {
				cancel = true;
				timer2.reset();
			}
		}

		// Strafe disabler
		if(e instanceof EventSendPacket) {
			EventSendPacket event = (EventSendPacket) e;
			if (event.packet instanceof C03PacketPlayer || event.packet instanceof C03PacketPlayer.C04PacketPlayerPosition || event.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
				if (mc.thePlayer.ticksExisted < 60) {
					hasDisablerFinished = false;
					event.setCanceled(true);
				}
			}
		}

		if(e instanceof EventRenderGUI) {

			if(notiToggle.isEnabled() && !hasDisablerFinished) {

				if(!hasDisablerFinished) {
					notiAnim1.setDirection(Direction.FORWARDS);
				}

				double boxWidth = 200 * notiAnim1.getOutput();
				int boxHeight = 50;

				boxY = 15;

				double imgY = 30 / 2 + 15 / 2;

				float textY = 24;

				// Enables Alias
				glEnable(GL_LINE_SMOOTH);
				glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

				if(hasDisablerFinished && notiAnim1.getDirection() == Direction.BACKWARDS) {

				} else {
					RenderUtils.drawRoundedRect((GuiScreen.width / 2) - (boxWidth / 2), boxY, boxWidth, boxHeight, 5f, new Color(30, 30, 30));
					GlStateManager.color(1, 1, 1, 1);
					RenderUtils.drawImg(new ResourceLocation("Vergo/icons/info.png"), (GuiScreen.width / 2) - (boxWidth / 2) + 5, imgY, 30, 30);
				}

				// Disabled Alias
				glDisable(GL_LINE_SMOOTH);
				glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);

				// If opening animation is done, draw text.
				if(notiAnim1.isDone() && notiAnim1.getDirection() == Direction.FORWARDS) {

					JelloFontRenderer fr = FontUtil.comfortaaHuge;
					JelloFontRenderer fr2 = FontUtil.comfortaaNormal;

					String textToRender = "Disabler Working...";
					String secondaryText = "Please wait...";

					fr.drawString(textToRender, (GuiScreen.width / 2) - (boxWidth / 2) + 38, textY, -1);
					fr2.drawString(secondaryText, (GuiScreen.width / 2) - (boxWidth / 2) + 38, textY + 18, -1);

					int percentage;

					if (mc.thePlayer.ticksExisted < 60) {
						percentage = mc.thePlayer.ticksExisted * 3;
					} else {
						percentage = 200;
						if(notiAnim1.isDone()) {
							notiAnim1.changeDirection();
						}
						hasDisablerFinished = true;
					}

					RenderUtils.drawTestedRoundRect((float) ((GuiScreen.width / 2) - (boxWidth / 2)), 55, percentage, 10, 5f, new Color(255, 255, 255));
				}

			} else {

			}
		}

		// Timer disabler
		if(e instanceof EventSendPacket) {
			EventSendPacket event1 = (EventSendPacket) e;
			if (event1.packet instanceof C03PacketPlayer) {
				C03PacketPlayer c03 = (C03PacketPlayer) event1.packet;
				if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
					event1.setCanceled(true);
				}
				if (cancel) {
					if (!timer2.hasTimeElapsed(400, false)) {
						if(!Vergo.config.modScaffold.isEnabled()) {
							event1.setCanceled(true);
							packets.add(event1.packet);
						}
					} else {
						packets.forEach(PacketUtil::sendPacketNoEvent);
						packets.clear();
						cancel = false;
					}
				}
			}
		}

	}
	
}
