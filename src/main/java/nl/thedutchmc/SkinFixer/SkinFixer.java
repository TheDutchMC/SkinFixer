package nl.thedutchmc.SkinFixer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.SkinFixer.commandHandlers.GetCodeCommandExecutor;
import nl.thedutchmc.SkinFixer.commandHandlers.SetSkinCommandExecutor;
import nl.thedutchmc.SkinFixer.commandHandlers.SkinFixerCommandExecutor;
import nl.thedutchmc.SkinFixer.fileHandlers.ConfigurationHandler;
import nl.thedutchmc.SkinFixer.fileHandlers.StorageHandler;
import nl.thedutchmc.SkinFixer.minecraftEvents.PlayerJoinEventListener;

public class SkinFixer extends JavaPlugin {

	public static SkinFixer INSTANCE;
	public static StorageHandler STORAGE;
	
	public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
	public static String PLUGIN_VERSION;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		PLUGIN_VERSION = Bukkit.getPluginManager().getPlugin("SkinFixer").getDescription().getVersion();
		
		SkinFixer.logInfo("Welcome to SkinFixer version " + PLUGIN_VERSION + " by TheDutchMC!");
		SkinFixer.logInfo("Using NMS Version " + SkinFixer.NMS_VERSION);
		
		//Read the configuration
		ConfigurationHandler configHandler = new ConfigurationHandler();
		configHandler.loadConfig();
	
		//Storage of skins and pending keys
		STORAGE = new StorageHandler();
		STORAGE.loadConfig();
		
		if(Bukkit.getOnlineMode() == true) logInfo("This plugin is not needed on servers running in online mode!");
		
		//Register command executors
		this.getCommand("setskin").setExecutor(new SetSkinCommandExecutor());
		this.getCommand("getcode").setExecutor(new GetCodeCommandExecutor());
		this.getCommand("skinfixer").setExecutor(new SkinFixerCommandExecutor());
		
		//Setup JDA
		if(ConfigurationHandler.useDiscord) {
			JdaHandler jdaHandler = new JdaHandler();
			jdaHandler.setupJda();
		}

		//Register event listeners
		Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
	}
	
	@Override
	public void onDisable() {
		logInfo("Shutting down JDA");
		
		try {
			JdaHandler.shutdownJda();
		} catch(Exception e) {}
		
		logInfo("Thank you for using SkinFixer by TheDutchMC");
	}
	
	public static void logInfo(String log) {
		Bukkit.getLogger().info("[" + SkinFixer.INSTANCE.getDescription().getName() + "] " + log);	
	}
	
	public static void logWarn(String log) {
		Bukkit.getLogger().warning("[" + SkinFixer.INSTANCE.getDescription().getName() + "] " + log);	
	}
}