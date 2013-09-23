package main.java.me.avastprods.chunkprotection;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerData {

	private FileConfiguration playerData = null;
	private File playerDataFile = null;
	private String name;

	ChunkProtection clazz;

	public PlayerData(ChunkProtection instance, String name) {
		this.name = name;

		clazz = instance;
	}

	public void load() {
		if (playerDataFile == null) {
			playerDataFile = new File(clazz.getDataFolder(), name + ".yml");
		}

		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
		
		setup();
	}

	public FileConfiguration getData() {
		if (playerData == null) {
			load();
		}

		return playerData;
	}

	public void saveData() {
		if (playerData == null || playerDataFile == null) {
			return;
		}

		try {
			getData().save(playerDataFile);
		} catch (IOException ex) {
			clazz.getLogger().log(Level.SEVERE, "Could not save config to " + playerDataFile, ex);
		}
	}
	
	private void setup() {
		if(!getData().contains("claimed_chunks")) {
			getData().createSection("claimed_chunks");
		}
	}
}
