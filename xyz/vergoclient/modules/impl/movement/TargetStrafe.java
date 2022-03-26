package xyz.vergoclient.modules.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.combat.KillAura;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.*;

public class TargetStrafe extends Module implements OnEventInterface {

	public TargetStrafe() {
		super("TargetStrafe", Category.MOVEMENT);
	}


	protected static Minecraft mc = Minecraft.getMinecraft();
	private static int strafe = 1;

	public static NumberSetting range = new NumberSetting("Range", 1.5, 1, 8, 0.1);

	@Override
	public void onEnable() {

		if (Vergo.config.modDisabler.isDisabled() && ServerUtils.isOnHypixel()) {
			Vergo.config.modDisabler.toggle();
			ChatUtils.addProtMsg("Disabler has been enabled for strafe.");
		}
	}

	@Override
	public void onDisable() {
		// Do nothing
	}

	@Override
	public void loadSettings() {
		addSettings(range);
	}

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick) {
			setInfo("Hypixel " + canStrafe());
		}

		if (e instanceof EventMove) {
			EventMove event = (EventMove) e;
			strafe(event);
		}
	}

	public static boolean strafe(EventMove e) {
		return strafe(e, MovementUtils.getSpeed());
	}

	public static boolean strafe(EventMove e, double moveSpeed) {
		if (canStrafe()) {
			setSpeed(e, moveSpeed, RotationUtils.getYaw(KillAura.target.getPositionVector()), strafe,
					mc.thePlayer.getDistanceToEntity(KillAura.target) <= range.getValueAsDouble() ? 0 : 1);
			return true;
		}
		return false;
	}

	public static boolean canStrafe() {
		if (!Vergo.config.modTargetStrafe.isEnabled() || !MovementUtils.isMoving()) {
			return false;
		}

		/*if (!(Vergo.config.modSpeed.isEnabled() || Vergo.config.modFly.isEnabled())) {
			return false;
		}*/

		if(KillAura.target == null) {
			return false;
		}

		return Vergo.config.modKillAura.isEnabled()
				&& !KillAura.target.isDead;
	}

	public static void setSpeed(EventMove moveEvent, double speed, float yaw, double strafe, double forward) {
		if (forward == 0 && strafe == 0) {
			moveEvent.setX(0);
			moveEvent.setZ(0);
		} else {
			if (forward != 0) {
				if (strafe > 0) {
					yaw += ((forward > 0) ? -45 : 45);
				} else if (strafe < 0) {
					yaw += ((forward > 0) ? 45 : -45);
				}
				strafe = 0;
				if (forward > 0) {
					forward = 1.0;
				} else if (forward < 0) {
					forward = -1.0;
				}
			}

			moveEvent.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
			moveEvent.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
		}
	}
}