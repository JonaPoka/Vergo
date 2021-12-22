package xyz.vergoclient.modules.impl.miscellaneous;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventChatMessage;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.ServerUtils;
import xyz.vergoclient.util.TimerUtil;

import java.util.Arrays;

public class ModAutoPlay extends Module implements OnEventInterface {

    public ModAutoPlay() {
        super("AutoPlay", Category.MISCELLANEOUS);
    }

    public ModeSetting teamMode = new ModeSetting("Team Mode", "Solo Normal", "Solo Normal", "Solo Insane", "Teams Normal", "Teams Insane");

    public NumberSetting commandDelay = new NumberSetting("Game Delay", 1, 1, 5, 0.1);

    public TimerUtil gameDelay = new TimerUtil();

    @Override
    public void onEnable() {
        if(!ServerUtils.isOnHypixel()) {
            toggle();
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void loadSettings() {
        addSettings(teamMode, commandDelay);
    }

    @Override
    public void onEvent(Event e) {

        setInfo(teamMode.getMode());

        if (e instanceof EventReceivePacket && e.isPre()) {
            EventReceivePacket packetEvent = (EventReceivePacket) e;

            if (packetEvent.packet instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) packetEvent.packet;

                if(teamMode.is("Solo Normal") || teamMode.is("Solo Insane")) {
                    if (packet.getChatComponent().getUnformattedText().contains("You died! Want to play again?") || packet.getChatComponent().getUnformattedText().contains("You won! Want to play again?") ||
                            packet.getChatComponent().getUnformattedText().contains("Queued! Use the bed to return to lobby!")) {
                        gameDelay.reset();
                        ChatUtils.addChatMessage("Sending you to a new game...");
                            if (teamMode.is("Solo Normal")) {
                                mc.thePlayer.sendChatMessage("/play solo_normal");
                            } else if (teamMode.is("Solo Insane")) {
                                mc.thePlayer.sendChatMessage("/play solo_insane");
                            }
                    }
                }


            }
        }
    }

}
