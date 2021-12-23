package xyz.vergoclient.modules.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
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
import xyz.vergoclient.util.TimerUtil;


public class ModFly extends Module implements OnEventInterface, OnSettingChangeInterface {

	public ModFly() {
		super("Fly", Category.MOVEMENT);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "None");
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("None"));
		
		addSettings(mode);
	}

	public TimerUtil tU = new TimerUtil();
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		if (e.setting == mode) {

		}
	}
	
	@Override
	public void onEnable() {
		ChatUtils.addChatMessage("Nothing to use. Disabling.");
		toggle();
	}
	
	@Override
	public void onDisable() {

		MovementUtils.setMotion(MovementUtils.getBaseMoveSpeed());
		mc.thePlayer.motionY = 0;
		
	}
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {
			if(mode.is("Hypixel Lag TP")) {
				setInfo("Lagpixel");
			} else if(mode.is("KRYPTIC")) {
				setInfo("Debug");
			}
		}

		if (e instanceof EventUpdate && e.isPre()) {

			if(mode.is("Hypixel Lag TP")) {

				if(tU.hasTimeElapsed(10, true)) {
					Vergo.config.modBlink.toggle();
				}

				float playersYaw = mc.thePlayer.rotationYaw;

				MovementUtils.getDirection(playersYaw);

				mc.thePlayer.jump();
				mc.getNetHandler().getNetworkManager().sendPacket( new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ, false));

			} else if(mode.is("KRYPTIC")) {
				int i = 0;
				while (i <= 48) {
					Vergo.config.modBlink.silentToggle();
					this.mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.0514865, this.mc.thePlayer.posZ, false));
					this.mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.0618865, this.mc.thePlayer.posZ, false));
					this.mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12, this.mc.thePlayer.posZ, false));
					Vergo.config.modBlink.silentToggle();
					++i;
				}
				this.mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, true));
			}
		}

	}
	
}
