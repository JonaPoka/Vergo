package xyz.vergoclient.commands.implAdmin;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import xyz.vergoclient.commands.CommandManager;
import xyz.vergoclient.commands.OnCommandInterface;
import xyz.vergoclient.security.ApiResponse;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.NetworkManager;

public class CommandRemoveAdmin implements OnCommandInterface {

	@Override
	public void onCommand(String... args) {
		
		if (args.length >= 2) {
			String username = "";
			int funny = 0;
			for (String add : args) {
				if (funny >= 1) {
					if (funny >= 2) {
						username += " ";
					}
					username += add;
				}
				funny++;
			}
			String usernameToBan = username;
			new Thread(() -> {
				try {
					
					String json = NetworkManager.getNetworkManager().sendPost(new HttpPost("https://hummusclient.info/api/admin/removeAdmin"), new BasicNameValuePair("token", AccountUtils.account.token), new BasicNameValuePair("username", usernameToBan));
					ApiResponse apiResponse = new Gson().fromJson(json, ApiResponse.class);
					
					ChatUtils.addChatMessage(apiResponse.statusText);
					
				} catch (Exception e) {
					ChatUtils.addChatMessage("Something went wrong (" + e + ")");
					e.printStackTrace();
				}
			}).start();
		}else {
			ChatUtils.addChatMessage("Use the command right");
		}
		
	}

	@Override
	public String getName() {
		return "removeAdmin";
	}

	@Override
	public String getUsage() {
		return CommandManager.prefix + getName() + " <username to remove admin>";
	}

	@Override
	public String getDescription() {
		return "Removes admin from a Hummus user";
	}

}
