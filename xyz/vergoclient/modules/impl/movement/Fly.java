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

			if(mode.is("Hypixel")) {
				doTheFunnyFly(((EventMove) e));
			}

			else if(mode.is("Vanilla")) {
				if (!mc.thePlayer.capabilities.isFlying) {
					mc.thePlayer.capabilities.isFlying = true;
				}
			}

		}


		if(mode.is("Hypixel")) {
			if(e instanceof EventReceivePacket) {

				state = 0;

				if (((EventReceivePacket) e).packet instanceof S08PacketPlayerPosLook && this.state == 0) {
					this.state = 1;
				}

				if (((EventReceivePacket) e).packet instanceof C03PacketPlayer.C04PacketPlayerPosition && this.state == 1) {
					((C03PacketPlayer.C04PacketPlayerPosition) ((EventReceivePacket) e).packet).onGround = false;
				}

				if (((EventReceivePacket) e).packet instanceof C03PacketPlayer && this.state == 1) {
					((C03PacketPlayer) ((EventReceivePacket) e).packet).onGround = false;
				}

				if (((EventReceivePacket) e).packet  instanceof C03PacketPlayer.C05PacketPlayerLook && this.state == 1) {
					((C03PacketPlayer.C05PacketPlayerLook) ((EventReceivePacket) e).packet).onGround = false;
				}

				if (((EventReceivePacket) e).packet instanceof C03PacketPlayer.C06PacketPlayerPosLook && this.state == 1) {
					((C03PacketPlayer.C06PacketPlayerPosLook) ((EventReceivePacket) e).packet).onGround = false;
				}
			}

		}

	}

	private void doTheFunnyFly(EventMove eventMove) {

		if(this.timer.delay(1200L)) {

			//this.HClip(2.7, eventMove);

			this.timer.reset();
		}else {
			eventMove.setX(0.0);
			eventMove.setZ(0.0);
		}
	}

	private void HClip(final double horizontal , EventMove eventMove) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);

		position = mc.thePlayer.getPosition();

		//ChatUtils.addChatMessage("BLOCKPLACE + " + position);

		position.x = (int) (position.getX() + horizontal * -Math.sin(playerYaw));
		position.y = (int) (position.getY() - 2.0);
		position.z = (int) (position.getZ() + horizontal * Math.cos(playerYaw));
		eventMove.setX(position.getX());
		eventMove.setY(position.getY());
		eventMove.setZ(position.getZ());
	}
	
}
