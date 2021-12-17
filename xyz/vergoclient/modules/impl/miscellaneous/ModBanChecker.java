package xyz.vergoclient.modules.impl.miscellaneous;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.NetworkManager;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.network.play.server.S02PacketChat;

public class ModBanChecker extends Module implements OnEventInterface {

	public ModBanChecker() {
		super("BanChecker", Category.MISCELLANEOUS);
	}
	
	public static int lastStaffBanCount = -1;
	public static TimerUtil lastStaffCheck = new TimerUtil();
	public static String apiKey = "";
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			
			if (mc.thePlayer.ticksExisted == 60) {
				mc.thePlayer.sendChatMessage("/api new");
				return;
			}
			
			if (apiKey.isEmpty()) {
				return;
			}
			
			if (lastStaffCheck.hasTimeElapsed(30000, true)) {
				new Thread("Hypixel ban checker thread") {
					public void run() {
						try {
							String json = NetworkManager.getNetworkManager().sendGet(new HttpGet("https://api.hypixel.net/watchdogStats?key=" + apiKey));
							JSONObject jsonObject = new JSONObject(json);
							if (jsonObject.getBoolean("success")) {
								if (lastStaffBanCount < 0) {
									lastStaffBanCount = jsonObject.getInt("staff_total");
									return;
								}
								int bannedLastCheck = jsonObject.getInt("staff_total") - lastStaffBanCount;
//								ChatUtils.addChatMessage(bannedLastCheck + " " + jsonObject.getInt("staff_total") + " " + lastStaffBanCount);
								if (bannedLastCheck > 0) {
									ChatUtils.addChatMessage(" ");
									ChatUtils.addChatMessage("Staff has banned " + bannedLastCheck + (bannedLastCheck > 1 ? " players" : " player") + " in the last 30 seconds");
									ChatUtils.addChatMessage(" ");
									lastStaffBanCount = jsonObject.getInt("staff_total");
								}
							}
						} catch (Exception e2) {
							
						}
					}
				}.start();
			}
			
		}
		else if (e instanceof EventReceivePacket && e.isPre()) {
			if (((EventReceivePacket)e).packet instanceof S02PacketChat) {
				if (((S02PacketChat)((EventReceivePacket)e).packet).getChatComponent().getUnformattedTextForChat().startsWith("Your new API key is ")) {
					apiKey = ((S02PacketChat)((EventReceivePacket)e).packet).getChatComponent().getFormattedText().replaceAll("§aYour new API key is §r§b", "").replaceAll("§r", "");
//					ChatUtils.addChatMessage(apiKey);
					e.setCanceled(true);
				}
			}
		}
		
	}

}
