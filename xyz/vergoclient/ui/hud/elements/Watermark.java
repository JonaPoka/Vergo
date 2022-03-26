package xyz.vergoclient.ui.hud.elements;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.modules.OnEventInterface;

public class Watermark implements OnEventInterface {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender3D) {
            if(Vergo.config.modHud.waterMark.is("Planet")) {

            }
        }
    }
}
