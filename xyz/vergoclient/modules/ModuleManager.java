package xyz.vergoclient.modules;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.security.account.AccountUtils;
import xyz.vergoclient.ui.guis.GuiStart;
import xyz.vergoclient.util.*;
import xyz.vergoclient.util.datas.DataDouble6;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S40PacketDisconnect;
import org.apache.commons.lang3.RandomUtils;
import org.json.JSONObject;
import xyz.vergoclient.modules.impl.combat.*;
import xyz.vergoclient.modules.impl.miscellaneous.*;
import xyz.vergoclient.modules.impl.movement.*;
import xyz.vergoclient.modules.impl.player.*;
import xyz.vergoclient.modules.impl.visual.*;
import xyz.vergoclient.settings.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

	public ModAutoPlay modAutoPlay = new ModAutoPlay();
	public ModClickgui modClickgui = new ModClickgui();
	public ModTimer modTimer = new ModTimer();
	public ModFly modFly = new ModFly();
	public ModDisabler modDisabler = new ModDisabler();
	public ModAntiBot modAntibot = new ModAntiBot();
	public ModKillAura modKillAura = new ModKillAura();
	public ModWTap modWTap = new ModWTap();
	public ModAutoPot modAutoPot = new ModAutoPot();
	public ModAutoHead modAutoHead = new ModAutoHead();
	//public ModTPAura modTPAura = new ModTPAura();
	public ModVelocity modVelocity = new ModVelocity();
	public ModNightMode modNightMode = new ModNightMode();
	public ModSmallItems modSmallItems = new ModSmallItems();
	public ModAnimations modAnimations = new ModAnimations();
	public ModNoSlow modNoSlow = new ModNoSlow();
	public ModTargetStrafe modTargetStrafe = new ModTargetStrafe();
	public ModSprint modSprint = new ModSprint();
	public ModStrafe modStrafe = new ModStrafe();
	public ModLongJump modLongJump = new ModLongJump();
	public ModAutoArmor modAutoArmor = new ModAutoArmor();
	public ModAutotool modAutotool = new ModAutotool();
	public ModInventoryManager modInventoryManager = new ModInventoryManager();
	public ModAntiVoid modAntivoid = new ModAntiVoid();
	public ModScaffold modScaffold = new ModScaffold();
	public ModBlink modBlink = new ModBlink();
	public ModBedBreaker modBedBreaker = new ModBedBreaker();
	public ModChestStealer modChestStealer = new ModChestStealer();
	public ModPlayerESP modPlayerESP = new ModPlayerESP();
	public ModChams modChams = new ModChams();
	public ModNoFall modNoFall = new ModNoFall();
	public ModDownClip modDownClip = new ModDownClip();
	public ModSpeed modSpeed = new ModSpeed();
	public ModTeams modTeams = new ModTeams();
	public ModBanChecker modBanChecker = new ModBanChecker();
	public ModCakeEater modCakeEater = new ModCakeEater();
	public ModRainbow modRainbow = new ModRainbow();
	public ModReach modReach = new ModReach();
	public ModAutoSaveConfig modAutoSaveConfig = new ModAutoSaveConfig();
	public ModHud modHud = new ModHud();
	public ModNametags modNametags = new ModNametags();
	public ModChestESP modChestESP = new ModChestESP();
	public ModXray modXray = new ModXray();
	public ModChinaHat modChinaHat = new ModChinaHat();
	public ModNoSwingDelay modNoSwingDelay = new ModNoSwingDelay();
	public ModTargetHud modTargetHud = new ModTargetHud();
	public ModAutoClicker modAutoClicker = new ModAutoClicker();
	public ModInstantAutoGapple modAutoInstantGapple = new ModInstantAutoGapple();
	
	private void loadModules() {
		modAutoPlay = new LoaderModule<ModAutoPlay>(modAutoPlay).generate();
		modClickgui = new LoaderModule<ModClickgui>(modClickgui).generate();
		modTimer = new LoaderModule<ModTimer>(modTimer).generate();
		modFly = new LoaderModule<ModFly>(modFly).generate();
		modDisabler = new LoaderModule<ModDisabler>(modDisabler).generate();
		modAntibot = new LoaderModule<ModAntiBot>(modAntibot).generate();
		modKillAura = new LoaderModule<ModKillAura>(modKillAura).generate();
		modWTap = new LoaderModule<ModWTap>(modWTap).generate();
		modAutoPot = new LoaderModule<ModAutoPot>(modAutoPot).generate();
		modAutoHead = new LoaderModule<ModAutoHead>(modAutoHead).generate();
		modLongJump = new LoaderModule<ModLongJump>(modLongJump).generate();
		modVelocity = new LoaderModule<ModVelocity>(modVelocity).generate();
		modNightMode = new LoaderModule<ModNightMode>(modNightMode).generate();
		modSmallItems = new LoaderModule<ModSmallItems>(modSmallItems).generate();
		modAnimations = new LoaderModule<ModAnimations>(modAnimations).generate();
		modNoSlow = new LoaderModule<ModNoSlow>(modNoSlow).generate();
		modTargetStrafe = new LoaderModule<ModTargetStrafe>(modTargetStrafe).generate();
		modSprint = new LoaderModule<ModSprint>(modSprint).generate();
		modStrafe = new LoaderModule<ModStrafe>(modStrafe).generate();
		modAutoArmor = new LoaderModule<ModAutoArmor>(modAutoArmor).generate();
		modAutotool = new LoaderModule<ModAutotool>(modAutotool).generate();
		modInventoryManager = new LoaderModule<ModInventoryManager>(modInventoryManager).generate();
		modAntivoid = new LoaderModule<ModAntiVoid>(modAntivoid).generate();
		modScaffold = new LoaderModule<ModScaffold>(modScaffold).generate();
		modBlink = new LoaderModule<ModBlink>(modBlink).generate();
		modBedBreaker = new LoaderModule<ModBedBreaker>(modBedBreaker).generate();
		modChestStealer = new LoaderModule<ModChestStealer>(modChestStealer).generate();
		modPlayerESP = new LoaderModule<ModPlayerESP>(modPlayerESP).generate();
		modChams = new LoaderModule<ModChams>(modChams).generate();
		modNoFall = new LoaderModule<ModNoFall>(modNoFall).generate();
		modDownClip = new LoaderModule<ModDownClip>(modDownClip).generate();
		modSpeed = new LoaderModule<ModSpeed>(modSpeed).generate();
		modTeams = new LoaderModule<ModTeams>(modTeams).generate();
		modBanChecker = new LoaderModule<ModBanChecker>(modBanChecker).generate();
		modCakeEater = new LoaderModule<ModCakeEater>(modCakeEater).generate();
		modRainbow = new LoaderModule<ModRainbow>(modRainbow).generate();
		modReach = new LoaderModule<ModReach>(modReach).generate();
		modAutoSaveConfig = new LoaderModule<ModAutoSaveConfig>(modAutoSaveConfig).generate();
		modHud = new LoaderModule<ModHud>(modHud).generate();
		modNametags = new LoaderModule<ModNametags>(modNametags).generate();
		modChestESP = new LoaderModule<ModChestESP>(modChestESP).generate();
		modXray = new LoaderModule<ModXray>(modXray).generate();
		modChinaHat = new LoaderModule<ModChinaHat>(modChinaHat).generate();
		modNoSwingDelay = new LoaderModule<ModNoSwingDelay>(modNoSwingDelay).generate();
		modTargetHud = new LoaderModule<ModTargetHud>(modTargetHud).generate();
		modAutoClicker = new LoaderModule<ModAutoClicker>(modAutoClicker).generate();
		modAutoInstantGapple = new LoaderModule<ModInstantAutoGapple>(modAutoInstantGapple).generate();
	}

	public static CopyOnWriteArrayList<OnEventInterface> eventListeners = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

	public static boolean currentlyLoadingConfig = false;
	
	public static boolean wasOnHypixel = false;
	public static boolean preBanned = false;

	public static long sessionTime = System.currentTimeMillis();
	public static TimerUtil banCheckTimer = new TimerUtil();

	public static void fireEvent(Event e) {

		try {
			if (e instanceof EventReceivePacket && e.isPre()) {
				EventReceivePacket event = (EventReceivePacket) e;
				if (event.packet instanceof S00PacketDisconnect) {
//					System.out.println("S00");
					preBanned = true;
					S00PacketDisconnect packet = (S00PacketDisconnect) event.packet;
					if (wasOnHypixel) {
						MiscellaneousUtils.setAltBanStatusHypixel(packet.getReason());
						wasOnHypixel = false;
					}
				} else if (event.packet instanceof S40PacketDisconnect) {
//					System.out.println("S40");
					preBanned = false;
					S40PacketDisconnect packet = (S40PacketDisconnect) event.packet;
					if (wasOnHypixel) {
						MiscellaneousUtils.setAltBanStatusHypixel(packet.getReason());
						wasOnHypixel = false;
					}
				}
			} else if (e instanceof EventSendPacket && e.isPre()) {
				EventSendPacket event = (EventSendPacket) e;
				if (event.packet instanceof C00Handshake) {
					C00Handshake packet = (C00Handshake) event.packet;
					if (packet.getIp().toLowerCase().contains("hypixel.net")) {
//						wasOnHypixel = true;
					} else {
//						wasOnHypixel = false;
					}
					sessionTime = System.currentTimeMillis();
				}
			}
		} catch (Exception e2) {

		}

		// To prevent bugs
		if (!GuiStart.hasLoaded || ModuleManager.currentlyLoadingConfig || Minecraft.getMinecraft() == null
				|| Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null)
			return;

		if (e instanceof EventTick && e.isPre()) {
			RenderUtils.resetPlayerYaw();
			RenderUtils.resetPlayerPitch();
			try {
				if (ServerUtils.isOnHypixel() && Minecraft.getMinecraft().thePlayer.ticksExisted == 250) {
					MiscellaneousUtils.setAltUnbannedHypixel();
				}
			} catch (Exception e2) {

			}

		} else if (AccountUtils.isBanned() && e instanceof EventSendPacket && e.isPre()
				&& System.currentTimeMillis() > sessionTime + 120000) {
			EventSendPacket event = (EventSendPacket) e;
			if (event.packet instanceof C0FPacketConfirmTransaction) {
				Minecraft.getMinecraft().getNetHandler().getNetworkManager()
						.sendPacketNoEvent(new C0FPacketConfirmTransaction(RandomUtils.nextInt(0, 7242) - 3621,
								(short) (RandomUtils.nextInt(0, 7242) - 3621), false));
				e.setCanceled(true);
			} else if (event.packet instanceof C00PacketKeepAlive)
				e.setCanceled(true);
		}
		
		for (OnEventInterface event : ModuleManager.eventListeners) {
			if (!(event instanceof Module) || ((Module) event).isEnabled()) {
				try {
					event.onEvent(e);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		
	}

	public static void onSettingChange(SettingChangeEvent e) {

		// To prevent bugs
		if (!GuiStart.hasLoaded || ModuleManager.currentlyLoadingConfig || Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null)
			return;

		for (Module m : modules) {
			if (m instanceof OnSettingChangeInterface) {
				((OnSettingChangeInterface) m).onSettingChange(e);
			}
		}

	}
	
	// Saves the config
	public void save(String configName) {
		
		JSONObject config = new JSONObject();
		for (Module module : modules) {
			JSONObject moduleJson = new JSONObject();
			moduleJson.put("isEnabled", module.isEnabled());
			JSONObject settings = new JSONObject();
			for (Field field : module.getClass().getDeclaredFields()) {
				try {
					Object value = field.get(module);
					if (value instanceof Setting) {
						
						JSONObject setting = new JSONObject();
						
						if (value instanceof NumberSetting) {
							setting.put("settingType", "num");
							setting.put("value", ((NumberSetting) value).getValueAsDouble());
						} else if (value instanceof ModeSetting) {
							setting.put("settingType", "mode");
							setting.put("value", ((ModeSetting) value).index);
							if (value instanceof FileSetting) {
								setting.remove("settingType");
								setting.put("settingType", "file");
								setting.put("dir", ((FileSetting) value).dir.getPath());
							}
						} else if (value instanceof BooleanSetting) {
							setting.put("settingType", "bool");
							setting.put("isEnabled", ((BooleanSetting) value).isEnabled());
						} else if (value instanceof KeybindSetting) {
							setting.put("settingType", "key");
							setting.put("key", ((KeybindSetting) value).code);
						}
						
						settings.put(((Setting)value).name, setting);
						
					}
				} catch (Exception e) {
					
				}
			}
			moduleJson.put("settings", settings);
			config.put(module.getName(), moduleJson);
		}
		
		FileManager.writeToFile(new File(FileManager.configDir, configName + ".json"), config.toString());
		
//		FileManager.writeToFile(new File(FileManager.configDir, config + ".json"), this);
	}

	// Loads a config from a file
	public static ModuleManager getConfig(String configName) {
		
		File file = new File(FileManager.configDir, configName + ".json");
		
		for (Module module : modules) {
			if (module.isEnabled())
				module.toggle();
		}
		
		currentlyLoadingConfig = true;
		
		if (!file.exists()) {
			ChatUtils.addChatMessage("That file does not exist");
			ModuleManager newConfig = new ModuleManager();
			newConfig.modules.clear();
			newConfig.init();
			currentlyLoadingConfig = false;
			return new ModuleManager();
		}
		
		ModuleManager newConfig = new ModuleManager();
		newConfig.modules.clear();
		newConfig.init();

		
		JSONObject config = new JSONObject(FileManager.readFromFile(new File(FileManager.configDir, configName + ".json")));
		for (Module module : newConfig.modules) {
			try {
				JSONObject moduleJson = config.getJSONObject(module.getName());
				JSONObject settings = moduleJson.getJSONObject("settings");
				for (Field field : module.getClass().getDeclaredFields()) {
					try {
						field.setAccessible(true);
						Object value = field.get(module);
						if (value instanceof Setting) {
							Setting setting = (Setting)value;
							JSONObject settingJson = settings.getJSONObject(setting.name);
							switch (settingJson.getString("settingType")) {
							case "num":
								if (setting instanceof NumberSetting) {
									((NumberSetting)setting).setValue(settingJson.getDouble("value"));
									System.out.println("NUMBER: " + setting.name + settingJson.getDouble("value"));
								}
								break;
							case "mode":
								if (setting instanceof ModeSetting) {
									((ModeSetting)setting).index = settingJson.getInt("value");
									((ModeSetting)setting).cycle(false);
									((ModeSetting)setting).cycle(true);
									System.out.println("MODE: " + setting.name + ((ModeSetting)setting).index);
								}
								break;
							case "file":
								if (setting instanceof FileSetting) {
									((FileSetting)setting).dir = new File(settingJson.getString("dir"));
									((FileSetting)setting).index = settingJson.getInt("value");
									((FileSetting)setting).cycle(false);
									((FileSetting)setting).cycle(true);
								}
								break;
							case "bool":
								if (setting instanceof BooleanSetting) {
									((BooleanSetting)setting).setEnabled(settingJson.getBoolean("isEnabled"));
									System.out.println("BOOLEAN: " + setting.name + settingJson.getBoolean("isEnabled"));
								}
								break;
							case "key":
								if (setting instanceof KeybindSetting) {
									((KeybindSetting)setting).setKeycode(settingJson.getInt("key"));
								}
								break;
							default:
								break;
							}
							field.set(module, setting);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (moduleJson.getBoolean("isEnabled"))
					module.toggle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Vergo.config = newConfig;
		
		currentlyLoadingConfig = false;
		
		return newConfig;
	}

	private transient ArrayList<LoaderModule<?>> moduleLoader = new ArrayList<>();

	// Adds a module to the config, used to make the code look nicer
	public <T extends Module> void AddToConfig(T t) {
		moduleLoader.add(new LoaderModule<T>(t));
	}

	private class LoaderModule<T extends Module> {

		public LoaderModule(T t) {
			this.t = t;
		}

		public T createContents() {
			try {
				t = (T) ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass())
						.getActualTypeArguments()[0]).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return t;
		}

		public T generate() {

			moduleLoader.add(this);

			if (t == null) {
				return createContents();
			} else {
				return t;
			}
		}

		public T t;

	}
	
	public void init() {
		// Prevent bans
		currentlyLoadingConfig = true;

		// Unsubscribes all the file settings from the events
		for (Module m : modules) {
			for (Setting s : m.settings) {
				if (s instanceof FileSetting) {
					((FileSetting) s).unsubscribeFromEvents();
				}
			}
		}
		
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;

		// So the player's pos and motion doesn't change after loading a config
		DataDouble6 posAndMotion = null;
		if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null)
			posAndMotion = new DataDouble6(Minecraft.getMinecraft().thePlayer.posX,
					Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ,
					Minecraft.getMinecraft().thePlayer.motionX, Minecraft.getMinecraft().thePlayer.motionY,
					Minecraft.getMinecraft().thePlayer.motionZ);

		// Unloads all currently loaded modules
		for (Module m : modules) {
			if (m.isEnabled())
				m.toggle();
		}
		modules.clear();
		Vergo.config = this;
		
		// Loads all the modules
		loadModules();
		
		// ArrayList of modules
		CopyOnWriteArrayList<Module> mods = new CopyOnWriteArrayList<>();

		// Adds all the modules
		for (LoaderModule<? extends Module> mod : moduleLoader) {
			mods.add(mod.t);
		}
		
		// Adds all the modules to the modules arraylist
		modules = mods;

		// Fixes any bugs that the module might have after being loaded
		for (Module m : modules) {

			m.setInfo("");

			m.loadSettings();

			// Forces all the settings to refresh after they load
			for (Setting s : m.settings) {
				if (s instanceof NumberSetting) {
					((NumberSetting) s).setValue(((NumberSetting) s).getValueAsDouble());
				} else if (s instanceof ModeSetting) {
					((ModeSetting) s).setMode(((ModeSetting) s).getMode());
					if (s instanceof FileSetting)
						((FileSetting) s).subscribeToEvents();
				} else if (s instanceof BooleanSetting) {
					((BooleanSetting) s).setEnabled(((BooleanSetting) s).isEnabled());
				} else if (s instanceof KeybindSetting) {
					((KeybindSetting) s).setKeycode(((KeybindSetting) s).getKeycode());
				}
			}
		}
		for (Module m : modules) {
				m.toggle();
				m.toggle();
		}
		
		// So the player's pos and motion doesn't change after loading a config
		if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null)
			MiscellaneousUtils.setPosAndMotionWithDataDouble6(posAndMotion);

		// Prevent bans
		Minecraft.getMinecraft().displayGuiScreen(screen);
		currentlyLoadingConfig = false;
	}
	
}
