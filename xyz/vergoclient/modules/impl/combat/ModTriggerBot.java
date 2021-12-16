package xyz.vergoclient.modules.impl.combat;

import java.text.DecimalFormat;

import org.apache.commons.lang3.RandomUtils;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class ModTriggerBot extends Module implements OnEventInterface, OnSettingChangeInterface {

	public ModTriggerBot() {
		super("TriggerBot", Category.COMBAT);
	}
	
	public NumberSetting minAps = new NumberSetting("Min APS", 9, 0.1, 20, 0.1),
			maxAps = new NumberSetting("Max APS", 10, 0.1, 20, 0.1);
	public ModeSetting mode = new ModeSetting("Mode", "Left click", "Left click", "Right click");
	
	@Override
	public void loadSettings() {
		addSettings(minAps, maxAps, mode);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		if (e.setting == minAps) {
			if (aps < minAps.getValueAsDouble())
				aps = minAps.getValueAsDouble();
			if (maxAps.getValueAsDouble() < minAps.getValueAsDouble()) {
				maxAps.setValue(minAps.getValueAsDouble());
			}
		} else if (e.setting == maxAps) {
			if (aps > maxAps.getValueAsDouble())
				aps = maxAps.getValueAsDouble();
			if (minAps.getValueAsDouble() > maxAps.getValueAsDouble()) {
				minAps.setValue(maxAps.getValueAsDouble());
			}
		}
	}
	
	public static transient TimerUtil apsTimer = new TimerUtil();
	public static transient double aps = 10;
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventTick && e.isPre()) {
			setInfo(new DecimalFormat("0.00").format(aps) + " APS");
			try {
				if (mc.objectMouseOver.typeOfHit.equals(MovingObjectType.ENTITY) && apsTimer.hasTimeElapsed((long) (1000/aps), true)) {
					aps = RandomUtils.nextDouble(minAps.getValueAsDouble(), maxAps.getValueAsDouble());
					if (mode.is("Left click")) {
						mc.thePlayer.swingItem();
						mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(mc.objectMouseOver.entityHit, Action.ATTACK));
					}
					else if (mode.is("Right click")) {
//						if (mc.thePlayer.getCurrentEquippedItem() == null) {
//							return;
//						}
//						mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
//						if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
//							new Thread(() -> {
//								try {
//									Thread.sleep(50);
//								} catch (Exception e2) {
//									
//								}
////								mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//							}).start();
//						}
						mc.gameSettings.keyBindUseItem.pressed = !mc.gameSettings.keyBindUseItem.pressed;
					}
				}
			} catch (Exception e2) {
				
			}
		}
	}
	
}
