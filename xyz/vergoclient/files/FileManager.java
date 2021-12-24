package xyz.vergoclient.files;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileManager {
	
	// Files used by the client
	public static File mainDir = new File("Vergo"),
			configDir = new File(mainDir, "configs"),
			keybindsDir = new File(mainDir, "keybinds"),
			libsDir = new File(mainDir, "libs"),
			killsultDir = new File(mainDir, "killsults"),
			assetsDir = new File(mainDir, "assets"),
			capesDir = new File(mainDir, "capes"),
			scriptsDir = new File(mainDir, "scripts"),
			defaultKeybindsFile = new File(keybindsDir, "default.json"),
			altsFile = new File(mainDir, "alts.json"),
			discordLibUnix = new File(libsDir, "discord_game_sdk.so"),
			discordLibWindows = new File(libsDir, "discord_game_sdk.dll"),
			clickguiTabs = new File(mainDir, "tabs.json");
	
	// Creates dirs if they don't exist
	public static void init() {
		if (!mainDir.exists()) 
			mainDir.mkdirs();
		if (!configDir.exists()) 
			configDir.mkdirs();
		if (!keybindsDir.exists()) 
			keybindsDir.mkdirs();
		if (!libsDir.exists()) 
			libsDir.mkdirs();
		if (!killsultDir.exists()) 
			killsultDir.mkdirs();
		if (!assetsDir.exists()) 
			assetsDir.mkdirs();
		if (!capesDir.exists()) 
			capesDir.mkdirs();
		if (!scriptsDir.exists()) 
			scriptsDir.mkdirs();
		createKillsultFiles();
	}
	
	// Creates killsult files
	public static void createKillsultFiles() {
		
		String furry = "OwO %player% \n"
				+ "UwU %player% \n"
				+ "Awoo %player% \n"
				+ "%player% OwO \n"
				+ "%player% UwU \n"
				+ "%player% Awoo \n"
				+ "%player% OwO? \n"
				+ "%player% UwU? \n"
				+ "%player% Awoo? \n"
				+ "%player% #LegalizeAwoo \n"
				+ "%player% browses furaffinity \n"
				+ "%player% joined r/furry_irl \n"
				+ "%player% should legalize awoo \n"
				+ "%player% help me legalize awoo \n"
				+ "Hello %player% would you like to OwO with me? \n"
				+ "Hello %player% would you like to UwU with me? \n"
				+ "Hello %player% would you like to Awoo with me? \n"
				+ "%player% should become a furry \n"
				+ "%player% is a furry \n";
		
		String toxic = "L %player% \n"
				+ "EZ %player% \n"
				+ "Bad %player% \n"
				+ "Get good noob %player% \n"
				+ "LLL %player% \n"
				+ "LLLL %player% \n"
				+ "Uninstall the game %player% \n"
				+ "Noob %player% \n"
				+ "EZ EZ EZ %player% \n"
				+ "Bad lol %player% \n"
				+ "Git gud %player% \n"
				+ "%player% L \n"
				+ "%player% EZ \n"
				+ "%player% Bad \n"
				+ "%player% get good noob \n"
				+ "%player% LLL \n"
				+ "%player% LLLL \n"
				+ "%player% uninstall the game \n"
				+ "%player% noob \n"
				+ "%player% EZ EZ EZ \n"
				+ "%player% bad lol \n"
				+ "%player% git gud \n"
				+ "%player% how are you so bad? \n"
				+ "%player% loser \n";
		
		String kinky = "%player% choke me harder daddy \n"
				+ "%player% this collar makes me so horny \n"
				+ "%player% spank me harder! \n"
				+ "%player% fuck me \n"
				+ "%player% shove your warm cock harder in me \n"
				+ "%player% your hard cock feels so good \n"
				+ "%player% *sucks your dick* \n"
				+ "%player% I cant live without your cock \n"
				+ "%player% fuck me \n"
				+ "%player% *moans* \n"
				+ "%player% make me into your little pet \n"
				+ "%player% Im your sex slave \n"
				+ "%player% I want to suck your dick \n"
				+ "%player% I have the weird urge to suck your dick \n"
				+ "%player% please fuck me \n"
				+ "%player% please do femboy maid sex rp with me \n"
				+ "%player% f-list.net is my favorite website, you should check it out \n";
		
		String skidma = "%player% YAAAA! Its rewind time, this year I DONT want Sigma or LeakedPVP \n"
				+ "%player% Download Sigma to get get instabanned while listening to some shitty music! \n"
				+ "%player% Quick Quiz: I am a skidded minecraft client, who am I? SIGMA \n"
				+ "%player% Sigma makes you die \n"
				+ "%player% Look a divinity! He definitely must avoid sigma! \n"
				+ "%player% I am not racist, but I only discriminate against Sigma users. so git gut noobs \n"
				+ "%player% Don't piss me off or you will discover the true power of Sigma's bitcoin miner \n"
				+ "%player% What is the worst client? Sigma or Sigma? \n"
				+ "%player% In need of the worst present for Christmas? Sigma will get you banned! \n"
				+ "%player% I don't hack I just AVOID sigma \n"
				+ "%player% I have a good bitcoin miner config, don't blame me \n"
				+ "%player% Want some skills? avoid sigma \n"
				+ "%player% Why Sigma? Cause it is the addition of bitcoin mining and incredible botnet abilities \n";
		
		String femboy = "%player% is a cute femboy \n"
				+ "%player% loves femboys \n"
				+ "%player% femboys are so cute \n"
				+ "%player% idk why but femboys turn me on \n"
				+ "%player% femboy maids are just the best \n"
				+ "%player% won't you be my little femboy? \n"
				+ "%player% is the cutest femboy \n"
				+ "%player% just put on their femboy maid dress \n"
				+ "%player% isn't astolfo just the best? \n"
				+ "%player% wanna cosplay as astolfo for me \n"
				+ "%player% I touch myself to femboys \n"
				+ "%player% touch's themselves to femboys \n"
				+ "%player% is such a good femboy that it's turning me on \n"
				+ "%player% puts on thigh highs \n";
		
	}
	
	// Writes a byte array to a file
	public static void writeToFile(File file, byte[] bytes) {
		try {
			FileUtils.writeByteArrayToFile(file, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Writes a string to a file
	public static void writeToFile(File file, String string) {
		writeToFile(file, string.getBytes());
	}
	
	// Writes an object serialized in json
	public static void writeToFile(File file, Object obj) {
		
		// We use pretty printing because it looks nicer
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		// Writes to the file
		writeToFile(file, gson.toJson(obj).toString().getBytes());
		
	}
	
	// Reads a string from a file
	public static String readFromFile(File file) {
		try {
			StringBuilder builder = new StringBuilder();
			for (String line : FileUtils.readLines(file, Charset.defaultCharset()))
				builder.append(line);
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	// Reads a json file and returns an object
	public static <T> T readFromFile(File file, T t) {
		try {
			return (T) new Gson().fromJson(readFromFile(file), t.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	// Downloads a file
	public static void downloadFile(String url, File file) {
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			byte[] bytes = IOUtils.toByteArray(connection.getInputStream());
			connection.getInputStream().close();
			writeToFile(file, bytes);
		} catch (Exception e) {
			
		}
	}
	
}
