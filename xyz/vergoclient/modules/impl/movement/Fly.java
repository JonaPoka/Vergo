package xyz.vergoclient.modules.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.Timer;

import java.util.Arrays;


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

	public double y = 0;

	@Override
	public void onEnable() {
		if(mode.is("Hypixel")) {
			y = mc.thePlayer.posY;

			if(mc.thePlayer.onGround) {
				mc.thePlayer.posY = 0.05D;
			}
		}
	}
	
	@Override
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
	}

	public int state;

	@Override
	public void onEvent(Event e) {

		if(e instanceof EventMove) {

			if(mode.is("Vanilla")) {
				mc.thePlayer.setPosition(mc.thePlayer.posX, y - 0.1, mc.thePlayer.posZ);
				mc.thePlayer.motionY = -0.0625;
			}

			/*else if(mode.is("Vanilla")) {
				if (!mc.thePlayer.capabilities.isFlying) {
					mc.thePlayer.capabilities.isFlying = true;
				}
			}*/

		}

	}
	
}
