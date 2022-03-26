package xyz.vergoclient.modules.impl.miscellaneous;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.ui.notifications.ingame.Notification;
import xyz.vergoclient.ui.notifications.ingame.NotificationManager;
import xyz.vergoclient.ui.notifications.ingame.NotificationType;
import xyz.vergoclient.util.ServerUtils;
import xyz.vergoclient.util.Timer;

public class AutoPlayGG extends Module implements OnEventInterface {

    Timer timer;

    public AutoPlayGG() {
        super("AutoPlay", Category.MISCELLANEOUS);
        this.timer = new Timer();
    }

    public ModeSetting teamMode = new ModeSetting("Team Mode", "Sky Solo Normal", "Sky Solo Normal", "Sky Solo Insane"/*, "Teams Normal", "Teams Insane"*/);

    public static BooleanSetting autoGG = new BooleanSetting("Auto-GG", true);

    @Override
    public void onEnable() {
        if(teamMode != null) {
            this.timer.reset();
        }

        if(autoGG.isEnabled()) {
            setInfo("AutoGG");
        } else {
            setInfo("");
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void loadSettings() {

        addSettings(teamMode, autoGG);
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

                if(teamMode.is("Sky Solo Normal") || teamMode.is("Sky Solo Insane") ) {
                    if (packet.getChatComponent().getUnformattedText().contains("You died! Want to play again?") || packet.getChatComponent().getUnformattedText().contains("You won! Want to play again?") ||
                            packet.getChatComponent().getUnformattedText().contains("Queued! Use the bed to return to lobby!") || packet.getChatComponent().getUnformattedText().contains("Queued! Use the bed to cancel!")) {
                        if(autoGG.isEnabled()) {
                            mc.thePlayer.sendChatMessage("gg");
                        }
                        //NotificationManager.show(new Notification(NotificationType.TOGGLE_ON, "Game Ended!", "Sending you to a new game...", 2));
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
        if (teamMode.is("Sky Solo Normal")) {
            mc.thePlayer.sendChatMessage("/play solo_normal");
        } else if(teamMode.is("Sky Solo Insane")) {
            mc.thePlayer.sendChatMessage("/play solo_insane");
        } else if(teamMode.is("Teams Normal")) {
            mc.thePlayer.sendChatMessage("/play teams_normal");
        } else if(teamMode.is("Teams Insane")) {
            mc.thePlayer.sendChatMessage("/play teams_insane");
        }
    }

}
