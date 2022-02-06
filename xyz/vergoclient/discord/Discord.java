package xyz.vergoclient.discord;

import java.io.File;
import java.time.Instant;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.gui.GuiScreen;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.guis.GuiMainMenu;
import xyz.vergoclient.ui.guis.GuiStart;
import xyz.vergoclient.util.OSUtil;
import xyz.vergoclient.util.RandomStringUtil;
import xyz.vergoclient.util.ServerUtils;
import net.minecraft.client.Minecraft;

import static xyz.vergoclient.Vergo.version;

public class Discord extends Thread {
	
	private static boolean changeStatus = true;
	public static Core core;
	
	private final static Instant timeStarted = Instant.now();
	
	public static Activity createActivity() {
		
		Activity activity = new Activity();

		String Release = version;

		try {
			if (Minecraft.getMinecraft().isSingleplayer()) {

				activity.setDetails("Release - " + Release);
				activity.setState("UID - " + AccountUtils.account.uid);
			}
			else if (ServerUtils.isOnHypixel()) {

				activity.setDetails("Release - " + Release);
				activity.setState("UID - " + AccountUtils.account.uid);
			}else {

				activity.setDetails("Release - " + Release);
				activity.setState("UID - " + AccountUtils.account.uid);
			}
		} catch (Exception e) {}

		activity.timestamps().setStart(timeStarted);

		// Makes an image show up
		activity.assets().setLargeImage("pinkvergo");
		
		return activity;
	}

	public static boolean cancelDiscordLoad = false;

	@Override
	public void run() {
		
		setName("Discord thread");

		if (OSUtil.isSolaris() || OSUtil.isUnknown()) {
			System.err.println("Unknown OS! Discord RP Could Not Initialize!");
			return;
		}

		File discordLibrary = getDiscordLib();
		
		if (discordLibrary == null) {
			cancelDiscordLoad = true;
			System.err.println("Error downloading Discord SDK. (2)");
			return;
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {}

		Core.init(discordLibrary);

		try (CreateParams params = new CreateParams()) {
			params.setClientID(920752902925070336L);
			params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
			try (Core core = new Core(params)) {
				
				Discord.core = core;

				while (true) {
					if (changeStatus) {
						core.activityManager().updateActivity(createActivity());
						changeStatus = false;
					}
					changeStatus = true;
					core.runCallbacks();
					try {
						// Sleep a bit to save CPU
						Thread.sleep(16);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static File getDiscordLib() {
		if (OSUtil.isWindows()) {
			if (!FileManager.discordLibWindows.exists()) {
				GuiStart.percentText = "Downloading Discord RPC, Please Wait...";
				FileManager.downloadFile("https://github.com/Hummus-Appreciation-Club/discord-game-lib-dll-download/raw/main/discord_game_sdk.dll", FileManager.discordLibWindows);
			}
			GuiStart.percentText = RandomStringUtil.getRandomLoadingMsg();
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
			if (FileManager.discordLibWindows.exists())
				return FileManager.discordLibWindows;
		}
		else if (OSUtil.isLinux() || OSUtil.isMac()) {
			if (!FileManager.discordLibUnix.exists()) {
				GuiStart.percentText = "Downloading Discord RPC, Please Wait...";
				FileManager.downloadFile("https://github.com/Hummus-Appreciation-Club/discord-game-lib-dll-download/raw/main/discord_game_sdk.so", FileManager.discordLibUnix);
			}
			GuiStart.percentText = RandomStringUtil.getRandomLoadingMsg();
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
			if (FileManager.discordLibUnix.exists())
				return FileManager.discordLibUnix;
		}
		return null;
	}
	
}
