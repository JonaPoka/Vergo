package xyz.vergoclient.modules.impl.visual;

import org.lwjgl.opengl.GL11;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventPlayerRender;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;

public class ModChams extends Module implements OnEventInterface {

	public ModChams() {
		super("Chams", Category.VISUAL);
	}

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventPlayerRender) {
			
			if (((EventPlayerRender)e).entity.isUser()) {
				if (e.isPre()) {
//					GL11.glEnable(32823);
//					GL11.glPolygonOffset(1.0f, -1099999.0f);
				}
				else if (e.isPost()) {
//					GL11.glDisable(32823);
//					GL11.glPolygonOffset(1.0f, 1099999.0f);
				}
				return;
			}
			
			if (e.isPre()) {
				GL11.glEnable(32823);
				
				// Changed so other modules can have render priority 
//				GL11.glPolygonOffset(1.0f, -1100000.0f);
				
				GL11.glPolygonOffset(1.0f, -1099998.0f);
			}
			else if (e.isPost()) {
				GL11.glDisable(32823);
				
				// Changed so other modules can have render priority 
//				GL11.glPolygonOffset(1.0f, 1100000.0f);
				
				GL11.glPolygonOffset(1.0f, 1099998.0f);
			}
			
		}

	}

}
