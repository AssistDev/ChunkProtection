package main.java.me.avastprods.chunkprotection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ChunkManager {

	ChunkProtection clazz;

	public ChunkManager(ChunkProtection instance) {
		clazz = instance;
	}

	public void claimChunk(Player chunkClaimer) {
		Chunk chunk = chunkClaimer.getLocation().getChunk();

		PlayerData playerData = new PlayerData(clazz, chunkClaimer.getName());

		playerData.getData().createSection("claimed_chunks." + chunk.toString());
		playerData.saveData();
	}

	public void unclaimChunk(Player chunkClaimer) {
		Chunk chunk = chunkClaimer.getLocation().getChunk();

		PlayerData playerData = new PlayerData(clazz, chunkClaimer.getName());

		playerData.getData().set("claimed_chunks." + chunk.toString(), null);
		playerData.saveData();
	}

	public boolean isClaimed(Chunk chunk) {
		if(getClaimer(chunk) != Bukkit.getOfflinePlayer("null")) {
			return true;
		}
		
		return false;
	}

	public int getClaims(Player chunkClaimer) {
		PlayerData database = new PlayerData(clazz, chunkClaimer.getName());
		return database.a();
	}

	public int getMaxClaims(Player chunkClaimer) {
		PlayerData database = new PlayerData(clazz, chunkClaimer.getName());
		return database.b();
	}
	
	public OfflinePlayer getClaimer(Chunk chunk) {
		File[] files = clazz.getDataFolder().listFiles();
		
		for(File file : files) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
			if(cfg.contains("claimed_chunks." + chunk.toString())) {
				return Bukkit.getOfflinePlayer(file.getName().split(".yml")[0]);
			}
		}
		
		return Bukkit.getOfflinePlayer("null");	
	}
	
	public List<String> getTrusted(Chunk chunk) {
		if(isClaimed(chunk)) {
			File file = new File(clazz.getDataFolder(), getClaimer(chunk) + ".yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
			if(cfg.contains("trusted_players") && !cfg.getStringList("trusted_players").isEmpty()) {
				return cfg.getStringList("trusted_players");
			}
		}
		
		return new ArrayList<String>();
	}
}
