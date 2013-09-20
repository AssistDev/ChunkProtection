package main.java.me.avastprods.chunkprotection;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerData {

	private FileConfiguration playerData = null;
	private File playerDataFile = null;
	private String name;
	private Player chunkClaimer;

	ChunkProtection clazz;

	public PlayerData(ChunkProtection instance, String name) {
		this.name = name;
		this.chunkClaimer = Bukkit.getPlayer(name);

		clazz = instance;
	}

	public void load() {
		if (playerDataFile == null) {
			playerDataFile = new File(clazz.getDataFolder(), name + ".yml");
		}

		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
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

	public int a() {
		return getData().contains(name + ".claimcount") ? getData().getInt(name + ".claimcount") : 0;
	}

	public int b() {
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
}
