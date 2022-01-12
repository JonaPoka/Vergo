package xyz.vergoclient.modules.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.modules.impl.miscellaneous.ModBlink;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.util.anticheat.Player;


public class ModFly extends Module implements OnEventInterface {

	Timer timer;

	public ModFly() {
		super("Fly", Category.MOVEMENT);
		this.timer = new Timer();
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Vanilla");

	public NumberSetting scale = new NumberSetting("TheFunny", 5, 0, 100, 0.1);

	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Hypixel", "Vanilla"));
		
		addSettings(mode, scale);
	}

	public static BlockPos position = null;
	
	@Override
	public void onEnable() {

	}
	
	@Override
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
	}
	
	@Override
	public void onEvent(Event e) {

		if(e instanceof EventMove) {

			if(mode.is("Hypixel")) {
				doTheFunnyFly(((EventMove) e));
			}

			else if(mode.is("Vanilla")) {
				if (!mc.thePlayer.capabilities.isFlying) {
					mc.thePlayer.capabilities.isFlying = true;
				}
			}

		}

	}

	private void doTheFunnyFly(EventMove eventMove) {

		if(this.timer.delay(1200L)) {
			ChatUtils.addChatMessage("Teleported!");
			this.HClip(scale.getValueAsFloat(), eventMove);
			this.timer.reset();
		}else {
			eventMove.setX(0.0);
			eventMove.setY(0.0);
			eventMove.setZ(0.0);
		}
	}

	private void HClip(final double horizontal , EventMove eventMove) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);

		position = mc.thePlayer.getPosition();

		ChatUtils.addChatMessage("BLOCKPLACE + " + position);

		position.x = (int) (position.getX() + horizontal * -Math.sin(playerYaw));
		position.y = (int) (position.getY() - 2.0);
		position.z = (int) (position.getZ() + horizontal * Math.cos(playerYaw));
		mc.thePlayer.setPosition(position.getX(), position.getY(), position.getZ());
	}
	
}
