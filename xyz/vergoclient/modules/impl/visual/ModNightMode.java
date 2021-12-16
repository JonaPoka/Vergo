package xyz.vergoclient.modules.impl.visual;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventGetSkyAndFogColor;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.potion.Potion;

public class ModNightMode extends Module implements OnEventInterface {

	public ModNightMode() {
		super("NightMode", Category.VISUAL);
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			mc.theWorld.setWorldTime(18000);
			mc.theWorld.setRainStrength(0);
			mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
		}
		else if (e instanceof EventReceivePacket && e.isPre()) {
			if (((EventReceivePacket)e).packet instanceof S03PacketTimeUpdate) {
				e.setCanceled(true);
			}
		}
		else if (e instanceof EventGetSkyAndFogColor && e.isPre()) {
//			EventGetSkyAndFogColor event = (EventGetSkyAndFogColor)e;
//			double color = 40;
//			event.color = new Vec3(color / 255, color / 255, color / 255);
		}
		
	}

}
