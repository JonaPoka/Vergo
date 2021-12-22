package xyz.vergoclient.modules.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.MovementUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;


public class ModFly extends Module implements OnEventInterface, OnSettingChangeInterface {

	public ModFly() {
		super("Fly", Category.MOVEMENT);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel Lag TP");
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Hypixel Lag TP"));
		
		addSettings(mode);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		if (e.setting == mode) {

		}
	}
	
	@Override
	public void onEnable() {

	}
	
	@Override
	public void onDisable() {

		MovementUtils.setMotion(MovementUtils.getBaseMoveSpeed());
		mc.thePlayer.motionY = 0;
		
	}
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			setInfo("Lagpixel");
		}

		if (e instanceof EventUpdate && e.isPre()) {



		}

	}
	
}
