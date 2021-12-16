package xyz.vergoclient.discord;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import org.apache.logging.log4j.core.jmx.Server;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.guis.GuiStartup;
import xyz.vergoclient.util.MiscellaneousUtils;
import xyz.vergoclient.util.OSUtil;
import xyz.vergoclient.util.ServerUtils;
import net.minecraft.client.Minecraft;

public class Discord extends Thread {
	
	private static boolean changeStatus = true;
	public static Core core;
	
	private final static Instant timeStarted = Instant.now();
	
	public static Activity createActivity() {
		
		Activity activity = new Activity();
		
		try {
			if (Minecraft.getMinecraft().isSingleplayer()) {
				activity.setState("Vergo");
				activity.setDetails("Playing offline (Loser)");
			}
			else if (ServerUtils.isOnHypixel()) {

				activity.setDetails("Playing Hypixel (Vergo Approved)");
				// activity.setState(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id));
			}else {
				activity.setDetails("Destroying on " + Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase());
				// activity.setState(AccountUtils.account.username + " - " + new DecimalFormat("#####0000").format(AccountUtils.account.id));
			}
		} catch (Exception e) {}
		
		// Setting a start time causes an "elapsed" field to appear
		activity.timestamps().setStart(timeStarted);

		// We are in a party with 1 out of 4 people.
//		activity.party().size().setCurrentSize(1);
//		activity.party().size().setMaxSize(4);

		// Makes an image show up
		activity.assets().setLargeImage("vergo");
//		activity.assets().setLargeText("I like hummus");
		
		// Will anyone notice I changed this line?
		// activity.assets().setLargeText("The person with this status is a furry " + FurryUtils.getFurryText());
		
		return activity;
	}
	
	// If it can't download the discord lib then cancel the load
	public static boolean cancelDiscordLoad = false;
	
	// Made in another thread because while loop
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
		
		// Hopefully prevents a crash
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
		
		// Initialize the Core
		Core.init(discordLibrary);

		// Set parameters for the Core
		try (CreateParams params = new CreateParams()) {
			params.setClientID(920752902925070336L);
			params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
			// Create the Core
			try (Core core = new Core(params)) {
				
				Discord.core = core;
				
				// Run callbacks forever
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
	
	// Good version of the code below, a lot less stress on your network and faster in general
	public static File getDiscordLib() {
		if (OSUtil.isWindows()) {
			if (!FileManager.discordLibWindows.exists()) {
				GuiStartup.percentText = "Downloading discord dll...";
				FileManager.downloadFile("https://github.com/Hummus-Appreciation-Club/discord-game-lib-dll-download/raw/main/discord_game_sdk.dll", FileManager.discordLibWindows);
			}
			GuiStartup.percentText = "Starting discord rp...";
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
			if (FileManager.discordLibWindows.exists())
				return FileManager.discordLibWindows;
		}
		else if (OSUtil.isLinux() || OSUtil.isMac()) {
			if (!FileManager.discordLibUnix.exists()) {
				GuiStartup.percentText = "Downloading discord so...";
				FileManager.downloadFile("https://github.com/Hummus-Appreciation-Club/discord-game-lib-dll-download/raw/main/discord_game_sdk.so", FileManager.discordLibUnix);
			}
			GuiStartup.percentText = "Starting discord rp...";
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
			if (FileManager.discordLibUnix.exists())
				return FileManager.discordLibUnix;
		}
		return null;
	}
	
}
