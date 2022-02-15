package xyz.vergoclient;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
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
import xyz.vergoclient.ui.guis.GuiStart;
import xyz.vergoclient.ui.guis.LogInGui;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.anticheat.Player;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.modules.Module;

public class Vergo {

	public static ModuleManager config;

	public static Discord discord = new Discord();

	public static transient String version = "b1.0";

	public static transient boolean beta = true;

	public static transient CopyOnWriteArrayList<ResourceLocation> cachedIcons = new CopyOnWriteArrayList<>();

	public static transient CopyOnWriteArrayList<StartupTask> startupTasks = new CopyOnWriteArrayList<>();

	public static transient CommandManager commandManager;

	private static transient Player player = new Player();

	public static void startup() {
		
		// Startup tasks initiate here.
		startupTasks.addAll(Arrays.asList(
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {
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
						ModuleManager.eventListeners.add(player);
					}
				},
				new StartupTask(RandomStringUtil.getRandomLoadingMsg()) {
					@Override
					public void task() {

					}
				}
			));

		startupTasks.add(new StartupTask(RandomStringUtil.getRandomLoadingMsg()));

		new Thread(() -> {

			for (StartupTask startupTask : startupTasks) {
				GuiStart.percentText = startupTask.taskText;
				startupTask.task();
				GuiStart.percentDoneTarget = ((double)startupTasks.indexOf(startupTask)) / ((double)startupTasks.size() - 1);
			}

			try {
				Thread.sleep(2500);
			} catch (Exception e) {}

			if (AccountUtils.isLoggedIn()) {
				GuiStart.hasLoaded = true;
			}else {
				Minecraft.getMinecraft().displayGuiScreen(new LogInGui());
			}
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
