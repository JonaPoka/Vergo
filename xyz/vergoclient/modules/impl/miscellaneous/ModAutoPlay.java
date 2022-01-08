package xyz.vergoclient.modules.impl.miscellaneous;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.ServerUtils;

public class ModAutoPlay extends Module implements OnEventInterface {

    public ModAutoPlay() {
        super("AutoPlay", Category.MISCELLANEOUS);
    }

    public ModeSetting teamMode = new ModeSetting("Team Mode", "Solo Normal", "Solo Normal", "Solo Insane", "Teams Normal", "Teams Insane");

    public NumberSetting commandDelay = new NumberSetting("Game Delay", 1, 1, 5, 0.1);

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

                if(teamMode.is("Solo Normal") || teamMode.is("Solo Insane") || teamMode.is("Teams Normal") || teamMode.is("Teams Insane")) {
                    if (packet.getChatComponent().getUnformattedText().contains("You died! Want to play again?") || packet.getChatComponent().getUnformattedText().contains("You won! Want to play again?") ||
                            packet.getChatComponent().getUnformattedText().contains("Queued! Use the bed to return to lobby!")) {
                        triggerNewGame();
                    } else {
                        if(packet.getChatComponent().getFormattedText().contains("A player has been removed from your game.")) {
                            packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replace("A player", "A skidder"));
                        }
                    }
                }
            }
        }
    }

    private void triggerNewGame() {
            if (teamMode.is("Solo Normal")) {
                mc.thePlayer.sendChatMessage("/play solo_normal");
            } else if (teamMode.is("Solo Insane")) {
                mc.thePlayer.sendChatMessage("/play solo_insane");
            } else if(teamMode.is("Teams Normal")) {
                mc.thePlayer.sendChatMessage("/play teams_normal");
            } else if(teamMode.is("Teams Insane")) {
                mc.thePlayer.sendChatMessage("/play teams_insane");
            }
    }

}
