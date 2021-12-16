package xyz.vergoclient.modules.impl.movement;

import java.util.ArrayList;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.combat.ModKillAura;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.RotationUtils;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModTargetStrafe extends Module implements OnEventInterface {

	public ModTargetStrafe() {
		super("TargetStrafe", Category.MOVEMENT);
	}
	
	@Override
	public void loadSettings() {
		addSettings(speedSetting, distanceSetting, mode, onGround);
	}
	
	public NumberSetting speedSetting = new NumberSetting("Speed", 0, 0, 1, 0.01);
	public NumberSetting distanceSetting = new NumberSetting("Distance", 3, 0.1, 6, 0.1);
	public ModeSetting mode = new ModeSetting("Circle mode", "Circle", "Circle", "Triangle", "Rectangle", "Pentagon", "Hexagon", "Heptagon", "Octagon", "Nonagon", "Decagon");
	public BooleanSetting onGround = new BooleanSetting("On Ground", true);
	
	// Vars that the module use
	public static transient boolean direction = false;
	public static transient TimerUtil changeTimer = new TimerUtil();
	public static transient int index = 0;
	
	@Override
	public void onEnable() {
		index = 0;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventMove && onGround.isEnabled() && !MovementUtils.isOnGround(0.0001))
			return;
		
		if (mode.is("Circle")) {
			circleEvent(e);
			return;
		}
		
		if ((e instanceof EventMove || e instanceof EventRender3D) && Vergo.config.modKillAura.isEnabled() && ModKillAura.target != null) {
			
			ArrayList<Vec3> path = new ArrayList<Vec3>();
			
			Vec3 lastLine = null;
			
			for (short i = 0; i <= getAmountOfPoints() * 2; i++) {
				
				float f = (float) (((360 / getAmountOfPoints()) * i) * (Math.PI / 180));
				double x2 = ModKillAura.target.posX, z2 = ModKillAura.target.posZ;
				x2 -= (double) (MathHelper.sin(f) * distanceSetting.getValueAsDouble()) * -1;
				z2 += (double) (MathHelper.cos(f) * distanceSetting.getValueAsDouble()) * -1;

				if (lastLine == null) {
					lastLine = new Vec3(x2, ModKillAura.target.posY, z2);
					continue;
				}
				
				lastLine.xCoord = x2;
				lastLine.zCoord = z2;
				
				path.add(new Vec3(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord));
				
			}
			
			if (e instanceof EventMove && e.isPre()) {
				
				EventMove event = (EventMove)e;
				
				if (mc.thePlayer.isCollidedHorizontally && changeTimer.hasTimeElapsed(100, true)) {
					direction = !direction;
				}
				
				Vec3 posToGoTo = null;
				boolean recheck = true;
				if (index >= 0 && index <= path.size() - 1) {
					posToGoTo = path.get(index);
					if (mc.thePlayer.getDistance(posToGoTo.xCoord, mc.thePlayer.posY, posToGoTo.zCoord) <= 0.995 || mc.thePlayer.isCollidedHorizontally) {
						recheck = true;
						posToGoTo = null;
					}else {
						recheck = false;
					}
				}
				
				if (recheck) {
					for (Vec3 temp : path) {
						if (posToGoTo == null || mc.thePlayer.getDistance(temp.xCoord, temp.yCoord, temp.zCoord) <= mc.thePlayer.getDistance(posToGoTo.xCoord, posToGoTo.yCoord, posToGoTo.zCoord)) {
							posToGoTo = temp;
						}
					}
				}
				
				if (posToGoTo == null)
					return;
				
				int index = path.indexOf(posToGoTo);
				if (recheck) {
					index += direction ? -1 : 1;
					if (index < 0)
						index = path.size() - 1;
					else if (index > path.size() - 1)
						index = 0;
					this.index = index;
				}
				posToGoTo = path.get(index);
				
				double currentSpeed = MovementUtils.getSpeed();
				double backupMotX = mc.thePlayer.motionX, backupMotZ = mc.thePlayer.motionZ;
				event.setSpeed(((currentSpeed + speedSetting.getValueAsDouble()) / 100) * 90,
						RotationUtils.getRotationFromPosition(posToGoTo.xCoord, posToGoTo.zCoord, mc.thePlayer.posY)[0]);
				mc.thePlayer.motionX = backupMotX;
				mc.thePlayer.motionZ = backupMotZ;
				
			}
			else if (e instanceof EventRender3D && e.isPre()) {
				
				GlStateManager.pushMatrix();
				
				lastLine = null;
				for (Vec3 line : path) {
					
					if (lastLine == null) {
						lastLine = line;
						continue;
					}
					
					RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, line.xCoord, line.yCoord, line.zCoord);
					lastLine = line;
					
				}
				
				RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, path.get(0).xCoord, path.get(0).yCoord, path.get(0).zCoord);
				
				GlStateManager.popMatrix();
				
			}
			
		}
		
	}
	
	public int getAmountOfPoints() {
		
		if (mode.is("Triangle")) {
			return 3;
		}
		else if (mode.is("Rectangle")) {
			return 4;
		}
		else if (mode.is("Pentagon")) {
			return 5;
		}
		else if (mode.is("Hexagon")) {
			return 6;
		}
		else if (mode.is("Heptagon")) {
			return 7;
		}
		else if (mode.is("Octagon")) {
			return 8;
		}
		else if (mode.is("Nonagon")) {
			return 9;
		}
		else if (mode.is("Decagon")) {
			return 10;
		}
		
		return 360;
		
	}
	
	public void circleEvent(Event e) {
		if (e instanceof EventMove && e.isPre()) {

			EventMove event = (EventMove) e;

			if (mc.thePlayer.isCollidedHorizontally && changeTimer.hasTimeElapsed(100, true)) {
				direction = !direction;
			}

			if (ModKillAura.target == null || Vergo.config.modKillAura.isDisabled()) {
				return;
			} else {

				double currentSpeed = MovementUtils.getSpeed();

				// event.setSpeed(0);

				double yawChange = 45;

				float f = (float) ((RotationUtils.getRotations(ModKillAura.target)[0]
						+ (direction ? -yawChange : yawChange)) * (Math.PI / 180));
				double x2 = ModKillAura.target.posX, z2 = ModKillAura.target.posZ;
				x2 -= (double) (MathHelper.sin(f) * (distanceSetting.getValueAsDouble()) * -1);
				z2 += (double) (MathHelper.cos(f) * (distanceSetting.getValueAsDouble()) * -1);

				float currentSpeed1 = MovementUtils.getSpeed();

				double backupMotX = mc.thePlayer.motionX, backupMotZ = mc.thePlayer.motionZ;
				event.setSpeed(((currentSpeed + speedSetting.getValueAsDouble()) / 100) * 90,
						RotationUtils.getRotationFromPosition(x2, z2, mc.thePlayer.posY)[0]);
				mc.thePlayer.motionX = backupMotX;
				mc.thePlayer.motionZ = backupMotZ;

				if (currentSpeed > MovementUtils.getSpeed()) {
					direction = !direction;
				}

			}

		}

		if (e instanceof EventRender3D && e.isPre()) {
			
			if (ModKillAura.target == null || Vergo.config.modKillAura.isDisabled()) {
				return;
			} else {

				Vec3 lastLine = null;

				for (short i = 0; i <= 360 * 2; i++) {

					float f = (float) ((RotationUtils.getRotations(ModKillAura.target)[0] + (direction ? -i : i)) * (Math.PI / 180));
					double x2 = ModKillAura.target.posX, z2 = ModKillAura.target.posZ;
					x2 -= (double) (MathHelper.sin(f) * distanceSetting.getValueAsDouble()) * -1;
					z2 += (double) (MathHelper.cos(f) * distanceSetting.getValueAsDouble()) * -1;

					if (lastLine == null) {
						lastLine = new Vec3(x2, ModKillAura.target.posY, z2);
						continue;
					}

					if (i != 0) {
						RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, x2, lastLine.yCoord,
								z2);
					}

					// RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, x2,
					// lastLine.yCoord, z2);
					lastLine.xCoord = x2;
					lastLine.zCoord = z2;

				}
				
			}

		}
		
	}

}
