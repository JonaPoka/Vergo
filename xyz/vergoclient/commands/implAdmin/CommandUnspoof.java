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

public class CommandUnspoof implements OnCommandInterface {

	@Override
	public void onCommand(String... args) {
		new Thread(() -> {
			try {
				
				String json = NetworkManager.getNetworkManager().sendPost(new HttpPost("https://hummusclient.info/api/admin/unspoofName"), new BasicNameValuePair("token", AccountUtils.account.token));
				ApiResponse apiResponse = new Gson().fromJson(json, ApiResponse.class);
				
				ChatUtils.addChatMessage(apiResponse.statusText);
				
			} catch (Exception e) {
				ChatUtils.addChatMessage("Something went wrong (" + e + ")");
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public String getName() {
		return "unspoof";
	}

	@Override
	public String getUsage() {
		return CommandManager.prefix + getName() + " <username to unspoof>";
	}

	@Override
	public String getDescription() {
		return "Unspoofs your username";
	}

}
