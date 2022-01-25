package xyz.vergoclient.modules.impl.miscellaneous;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.*;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.*;
import net.minecraft.network.Packet;

public class ModDisabler extends Module implements OnEventInterface {

	Timer timer;

	public ModDisabler() {
		super("Disabler", Category.MISCELLANEOUS);
		this.timer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Test", "Test", "Test2");
	
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();

	@Override
	public void loadSettings() {


		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Test", "Test2"));
		addSettings(mode);
		
	}
	
	@Override
	public void onEnable() {

	}
	
	@Override
	public void onDisable() {


	}

	@Override
	public void onEvent(Event e) {

		if(e instanceof EventMove) {

			if(mode.is("Test")) {
				doTheFunnyFly(((EventMove) e));
			}

		}

	}

	public static BlockPos position = null;

	private void doTheFunnyFly(EventMove eventMove) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
		position = mc.thePlayer.getPosition();

		eventMove.setSpeed(0.001);
		position.x = (int) (position.getX() + 2.7 * -Math.sin(playerYaw));
		position.y = (int) (position.getY() - 2.0);
		position.z = (int) (position.getZ() + 2.7 * Math.cos(playerYaw));

		eventMove.setX(position.x);
		eventMove.setY(position.y);
		eventMove.setZ(position.z);
	}

	private void HClip(final double horizontal , EventMove eventMove) {
		/*double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);

		position = mc.thePlayer.getPosition();

		//ChatUtils.addChatMessage("BLOCKPLACE + " + position);

		position.x = (int) (position.getX() + horizontal * -Math.sin(playerYaw));
		position.y = (int) (position.getY() - 2.0);
		position.z = (int) (position.getZ() + horizontal * Math.cos(playerYaw));
		mc.thePlayer.setPosition(position.getX(), position.getY(), position.getZ());*/
	}
	
}
