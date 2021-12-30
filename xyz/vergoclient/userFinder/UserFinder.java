package xyz.vergoclient.userFinder;

import com.google.gson.Gson;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventSetCape;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.visual.ModCape;
import xyz.vergoclient.security.ApiResponse;
import xyz.vergoclient.security.ApiResponse.ResponseStatus;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.util.ChatFilterBypassUtils;
import xyz.vergoclient.util.NetworkManager;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class UserFinder implements OnEventInterface {
	
	public TimerUtil userFinderTimer = new TimerUtil();
	public HashMap<String, String> hummusUsers = new HashMap<String, String>();
	
	@Override
	public void onEvent(Event e) {

		if (e instanceof EventTick && e.isPre()) {

			if (e instanceof EventReceivePacket && e.isPre()) {

				Minecraft mc = Minecraft.getMinecraft();

				String color = "§d";

				EventReceivePacket packetEvent = (EventReceivePacket) e;
				if (packetEvent.packet instanceof S02PacketChat) {

					S02PacketChat packet = (S02PacketChat) packetEvent.packet;

					boolean killsultEventFix = false;

					for (String username : hummusUsers.keySet()) {

						if (packet.getChatComponent().getUnformattedText().replace("⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎", "").contains(username)) {
//						packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replace("⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎", "").replaceAll(username, username + " §7(" + color + ChatFilterBypassUtils.insertPeriodically(hummusUsers.get(username), "⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎", 1) + "§7) "));
							killsultEventFix = true;
							break;
						}

					}

					for (String username : hummusUsers.keySet()) {

						if (packet.getChatComponent().getUnformattedText().replace("⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎", "").contains(username)) {
							packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replace("⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎", "").replaceAll(username, username + " §7(" + color + ChatFilterBypassUtils.insertPeriodically(hummusUsers.get(username), "⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎", 1) + "§7) "));
						}

					}

				}

			} else if (e instanceof EventSetCape) {
				EventSetCape event = (EventSetCape) e;
				if (event.player.isUser() || hummusUsers.keySet().contains(event.player.getName())) {
					event.resourceLocation = Vergo.config.modCape.isEnabled() && !Vergo.config.modCape.capeFile.is("None") && ModCape.capeLocation != null ? ModCape.capeLocation : new ResourceLocation("Vergo/cape.png");
				}
			}

		}
	}

}
