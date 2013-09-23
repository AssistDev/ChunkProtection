package main.java.me.avastprods.chunkprotection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

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

	public int unclaimAll(Player chunkClaimer) {
		PlayerData playerData = new PlayerData(clazz, chunkClaimer.getName());

		int counter = 0;

		for (String keys : playerData.getData().getConfigurationSection("claimed_chunks").getKeys(false)) {
			playerData.getData().set("claimed_chunks." + keys, null);
			counter++;
		}

		playerData.getData().set("claimcount", 0);
		playerData.saveData();

		return counter;
	}

	public boolean isClaimed(Chunk chunk) {
		if (getClaimer(chunk) != Bukkit.getOfflinePlayer("null")) {
			return true;
		}

		return false;
	}

	public int getClaims(Player chunkClaimer) {
		PlayerData playerData = new PlayerData(clazz, chunkClaimer.getName());

		return playerData.getData().getConfigurationSection("claimed_chunks").getKeys(false).size() > 0 ? playerData.getData().getConfigurationSection("claimed_chunks").getKeys(false).size() : 0;
	}

	public int getMaxClaims(Player chunkClaimer) {
		for (PermissionAttachmentInfo perm : chunkClaimer.getEffectivePermissions()) {
			if (perm.getPermission().startsWith("chunkprotection.claim")) {
				int max = 0;

				try {
					max = Integer.parseInt(perm.getPermission().split("chunkprotection.claim.")[1]);
					return max;
				} catch (NumberFormatException ex) {
					System.out.println("Max permission amount invalid!\n at: " + "Player '" + chunkClaimer + "'\n at: Permission '" + perm.getPermission() + "'\nCheck your permission file!");
				}
			}
		}

		return 0;
	}

	public OfflinePlayer getClaimer(Chunk chunk) {
		File[] files = clazz.getDataFolder().listFiles();

		for (File file : files) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

			if (cfg.contains("claimed_chunks." + chunk.toString())) {
				return Bukkit.getOfflinePlayer(file.getName().split(".yml")[0]);
			}
		}

		return Bukkit.getOfflinePlayer("null");
	}

	public void addTrusted(Chunk chunk, OfflinePlayer target) {
		File file = new File(clazz.getDataFolder(), getClaimer(chunk).getName() + ".yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		List<String> trustedList = cfg.contains("trusted_players") ? cfg.getStringList("trusted_players") : new ArrayList<String>();
		trustedList.add(target.getName());

		cfg.set("trusted_players", trustedList);

		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeTrusted(Chunk chunk, OfflinePlayer target) {
		File file = new File(clazz.getDataFolder(), getClaimer(chunk).getName() + ".yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		List<String> trustedList = cfg.contains("trusted_players") ? cfg.getStringList("trusted_players") : new ArrayList<String>();
		trustedList.remove(target.getName());

		cfg.set("trusted_players", trustedList);

		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getTrusted(Chunk chunk) {
		if (isClaimed(chunk)) {
			File file = new File(clazz.getDataFolder(), getClaimer(chunk).getName() + ".yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

			if (cfg.contains("trusted_players") && !cfg.getStringList("trusted_players").isEmpty()) {
				return cfg.getStringList("trusted_players");
			}
		}

		return new ArrayList<String>();
	}
}
