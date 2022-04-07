package xyz.vergoclient.modules.impl.miscellaneous.disabler;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.modules.OnEventInterface;

public class HypixelStrafe implements OnEventInterface {

    protected static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventSendPacket) {
            EventSendPacket event = (EventSendPacket) e;
            doStrafeDisable(event);
        }
    }

    public void doStrafeDisable(EventSendPacket e) {
        if(e.packet instanceof C03PacketPlayer || e.packet instanceof C03PacketPlayer.C04PacketPlayerPosition || e.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            if(mc.thePlayer.ticksExisted < 50) {
                e.setCanceled(true);
            }
        }
    }

}
