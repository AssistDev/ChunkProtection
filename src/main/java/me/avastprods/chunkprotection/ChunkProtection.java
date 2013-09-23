package main.java.me.avastprods.chunkprotection;

import org.bukkit.plugin.java.JavaPlugin;

public class ChunkProtection extends JavaPlugin {

	public void onEnable() {
		getDataFolder().mkdir();

		getCommand("chunkprotection").setExecutor(new CommandManager(this));
		getCommand("cp").setExecutor(new CommandManager(this));

		getServer().getPluginManager().registerEvents(new EventManager(this), this);

		new EventManager(this).setupBlacklist();
		new MessageData(this).load();
	}
}
