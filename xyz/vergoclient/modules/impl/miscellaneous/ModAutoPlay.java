package xyz.vergoclient.modules.impl.miscellaneous;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.ui.notifications.Notification;
import xyz.vergoclient.ui.notifications.NotificationManager;
import xyz.vergoclient.ui.notifications.NotificationType;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.ServerUtils;
import xyz.vergoclient.util.Timer;

public class ModAutoPlay extends Module implements OnEventInterface {

    Timer timer;

    public ModAutoPlay() {
        super("AutoPlay", Category.MISCELLANEOUS);
        this.timer = new Timer();
    }

    public ModeSetting teamMode = new ModeSetting("Team Mode", "Solo Normal", "Solo Normal", "Solo Insane", "Teams Normal", "Teams Insane");

    public static NumberSetting commandDelay1 = new NumberSetting("Game Delay", 1, 1, 5, 0.1);

    @Override
    public void onEnable() {
        if(teamMode != null) {
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void loadSettings() {


        addSettings(teamMode, commandDelay1);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventReceivePacket && e.isPre()) {
            if(!ServerUtils.isOnHypixel()) {
                return;
            }

            setInfo(teamMode.getMode());

            EventReceivePacket packetEvent = (EventReceivePacket) e;

            if (packetEvent.packet instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) packetEvent.packet;

                if(teamMode.is("Solo Normal") || teamMode.is("Solo Insane") || teamMode.is("Teams Normal") || teamMode.is("Teams Insane")) {
                    if (packet.getChatComponent().getUnformattedText().contains("You died! Want to play again?") || packet.getChatComponent().getUnformattedText().contains("You won! Want to play again?") ||
                            packet.getChatComponent().getUnformattedText().contains("Queued! Use the bed to return to lobby!")) {
                        NotificationManager.show(new Notification(NotificationType.INFO, "Game Ended!", "Sending you to a new game...", 2));
                        doCommands();
                    } else {
                        if(packet.getChatComponent().getFormattedText().contains("A player has been removed from your game.")) {
                            packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replace("A player", "A skidder"));
                        }
                    }
                }
            }
        }
    }

    public int cooldown = 1000;

    public void doCommands() {
        this.timer.reset();
        //ChatUtils.addChatMessage("Timer Timer Timer Timer " + timer.lastMs);
        if(this.timer.delay(commandDelay1.getValueAsLong() * 1000)) {
            ChatUtils.addChatMessage("Timer Event Triggered! + " + (commandDelay1.getValueAsLong()) * 1000 + " " + cooldown);
            if (teamMode.is("Solo Normal")) {
                mc.thePlayer.sendChatMessage("/play solo_normal");
            } else if(teamMode.is("Solo Insane")) {
                mc.thePlayer.sendChatMessage("/play solo_insane");
            } else if(teamMode.is("Teams Normal")) {
                mc.thePlayer.sendChatMessage("/play teams_normal");
            } else if(teamMode.is("Teams Insane")) {
                mc.thePlayer.sendChatMessage("/play teams_insane");
            }
        }
    }

}
