package xyz.vergoclient.modules.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
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
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.util.Timer;
import xyz.vergoclient.util.TimerUtil;


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
			this.HClip(scale.getValueAsFloat());
			this.timer.reset();
		}else {
			eventMove.setX(0.0);
			eventMove.setY(0.0);
			eventMove.setZ(0.0);
		}
	}

	private void HClip(final double horizontal) {
		final double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
		mc.thePlayer.posX = 7.5 * -Math.sin(mc.thePlayer.rotationYaw);
		mc.thePlayer.posY = 2.0;
		mc.thePlayer.posZ = 7.5 * Math.cos(mc.thePlayer.rotationYaw);
	}
	
}
