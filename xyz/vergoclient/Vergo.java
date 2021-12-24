package xyz.vergoclient;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import xyz.vergoclient.assets.Icons;
import xyz.vergoclient.commands.CommandManager;
import xyz.vergoclient.discord.Discord;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.files.FileSaver;
import xyz.vergoclient.files.impl.FileKeybinds;
import xyz.vergoclient.keybinds.KeyboardManager;
import xyz.vergoclient.modules.ModuleManager;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.Hud;
import xyz.vergoclient.ui.guis.GuiAltManager;
import xyz.vergoclient.ui.guis.GuiClickGui;
import xyz.vergoclient.userFinder.UserFinder;
import xyz.vergoclient.ui.guis.GuiStart;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.anticheat.Player;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.modules.Module;

public class Vergo {
	
	// The current config
	public static ModuleManager config;
	
	// Discord
	public static Discord discord = new Discord();
	
	// The version of the client
	public static transient String version = " DEV-BUILD";
	
	// Cached icons
	public static transient CopyOnWriteArrayList<ResourceLocation> cachedIcons = new CopyOnWriteArrayList<>();
	
	// Startup tasks
	public static transient CopyOnWriteArrayList<StartupTask> startupTasks = new CopyOnWriteArrayList<>();
	
	// The command manager
	public static transient CommandManager commandManager;
	
	// The player (used for bypasses)
	private static transient Player player = new Player();
	
	// Creates and executes startup tasks
	public static void startup() {
		
		// Creates startup tasks
		startupTasks.addAll(Arrays.asList(
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						// Starts the file manager
						FileManager.init();
						
						if (FileManager.defaultKeybindsFile.exists()) {
							KeyboardManager.keybinds = FileManager.readFromFile(FileManager.defaultKeybindsFile, new FileKeybinds());
						}else {
							KeyboardManager.keybinds = new FileKeybinds();
						}
						
						if (FileManager.altsFile.exists()) {
							GuiAltManager.altsFile = FileManager.readFromFile(FileManager.altsFile, GuiAltManager.altsFile);
						}
						
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						for (Icons icon : Icons.values())
							cachedIcons.add(icon.iconLocation);
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						if (!MiscellaneousUtils.canAccessInternet())
							return;
						if (!discord.isAlive())
							discord.start();
						while (!Discord.cancelDiscordLoad)
							if (FileManager.discordLibWindows.exists() || FileManager.discordLibUnix.exists())
								break;
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						config = new ModuleManager();
						config.init();
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						Hud.init();
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						if (FileManager.clickguiTabs.exists()) {
							GuiClickGui.tabs = FileManager.readFromFile(FileManager.clickguiTabs, new GuiClickGui.TabFile());
						}else {
							for (Module.Category category : Module.Category.values()) {
								GuiClickGui.ClickguiTab clickguiTab = new GuiClickGui.ClickguiTab();
								clickguiTab.category = category;
								GuiClickGui.tabs.tabs.add(clickguiTab);
							}
						}
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						commandManager = new CommandManager();
						commandManager.init();
						ModuleManager.eventListeners.add(commandManager);
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						FileSaver.init();
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						ModuleManager.eventListeners.add(new UserFinder());
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						ModuleManager.eventListeners.add(player);
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						Runtime.getRuntime().addShutdownHook(new Thread() {
							public void run() {
								try {
									NetworkManager.getNetworkManager().sendPost(new HttpPost("https://hummusclient.info/api/user/logoutFromAlt"), new BasicNameValuePair("token", AccountUtils.account.token));
								} catch (Exception e) {
									
								}
							}
						});
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
						/*if (FileManager.authTokenFile.exists()) {
							try {
								String response = NetworkManager.getNetworkManager().sendPost(new HttpPost("https://hummusclient.info/api/account/checkToken"), new BasicNameValuePair("token", FileManager.readFromFile(FileManager.authTokenFile)));
								JSONObject json = new JSONObject(response);
								ApiResponse apiResponse = new ApiResponse();
								for (ApiResponse.ResponseStatus responseStatus : ApiResponse.ResponseStatus.values()) {
									if (responseStatus.toString().equals(json.getString("status"))) {
										apiResponse.status = responseStatus;
									}
								}
								apiResponse.statusText = json.getString("statusText");
								apiResponse.responseObject = json.get("responseObject");
//								ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
								if (apiResponse.status == ResponseStatus.OK) {
									AccountUtils.account = new Gson().fromJson(new JSONObject(response).getJSONObject("responseObject").toString(), Account.class);
									FileManager.writeToFile(FileManager.authTokenFile, AccountUtils.account.token);
									if (AccountUtils.account.isAdmin || AccountUtils.account.id == 1) {
										commandManager.hummusAdminInit();
									}
									new Thread(() -> {
										while (true) {
											try {
												ApiResponse apiResponse1 = new Gson().fromJson(NetworkManager.getNetworkManager().sendPost(new HttpPost("https://hummusclient.info/api/user/loginToAlt"), new BasicNameValuePair("token", AccountUtils.account.token), new BasicNameValuePair("username", Minecraft.getMinecraft().session.getUsername())), ApiResponse.class);
												if (apiResponse1.status == ResponseStatus.OK) {
													break;
												}
												Thread.sleep(10000);
											} catch (Exception e) {
												
											}
										}
									}).start();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}*/
					}
				}
			));
		
		// This fixes a bug
		startupTasks.add(new StartupTask(RandomStringUtil.getRandomLoadingMsg()));
		
		// Runs startup tasks
		new Thread(() -> {
			
			for (StartupTask startupTask : startupTasks) {
				GuiStart.percentText = startupTask.taskText;
				startupTask.task();
				GuiStart.percentDoneTarget = ((double)startupTasks.indexOf(startupTask)) / ((double)startupTasks.size() - 1);
			}
			
			// Makes sure the startup screen lingers for at least 2.5 secs
			GuiStart.percentText = RandomStringUtil.getRandomLoadingMsg();
			try {
				Thread.sleep(2500);
			} catch (Exception e) {}
				GuiStart.hasLoaded = true;

		}).start();
		
	}

	public static class StartupTask {
		public StartupTask(String taskText) {
			this.taskText = taskText;
		}

		public String taskText;

		public void task() {

		}
	}

	public static Player getPlayer() {
		return player;
	}

	public static void setPlayer(Player player) {
		Vergo.player = player;
	}
	
}
