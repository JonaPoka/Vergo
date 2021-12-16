package xyz.vergoclient.modules.impl.miscellaneous;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;

public class ModPacketCanceler extends Module implements OnEventInterface {

	public ModPacketCanceler() {
		super("PacketCanceler", Category.MISCELLANEOUS);
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventSendPacket && e.isPre()) {
			
			if (!e.isCanceled()) {
				e.setCanceled(true);
			}
			
		}
		
	}

}
