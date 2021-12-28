package xyz.vergoclient.modules.impl.miscellaneous;

import net.minecraft.client.Minecraft;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;


public class ModInvMove extends Module implements OnEventInterface {

    protected static Minecraft mc;

    public ModInvMove() {
        super("InvMove", Module.Category.MISCELLANEOUS);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {


    }

    @Override
    public void onEvent(Event e) {

    }

}
