package main.java.me.avastprods.chunkprotection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {

	ChunkProtection clazz;
	
	public CommandManager(ChunkProtection instance) {
		clazz = instance;
	}
	
	public static String prefix = ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + "ChunkProtection" + ChatColor.DARK_BLUE + "] " + ChatColor.RESET;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		
		Player s = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("chunkprotection") || cmd.getName().equalsIgnoreCase("cp")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("claim")) {
					if(args.length == 1) {
						ChunkManager chunkManager = new ChunkManager(clazz);
						Chunk chunk = s.getLocation().getChunk();
						
						if (!chunkManager.isClaimed(chunk)) {
							if (chunkManager.getClaims(s) < chunkManager.getMaxClaims(s)) {
								chunkManager.claimChunk(s);
								
								s.sendMessage(prefix + "Succesfully claimed chunk!");
							} else {
								s.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You have reached the limit of claimed chunks!");
							}
							
						} else {
							s.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "This chunk has already been claimed by" + ChatColor.DARK_RED + chunkManager.getClaimer(chunk).toString().replaceAll("CraftOfflinePlayer", "").replaceAll("name=", "").replace('[', ' ').replace(']', ' '));
						}					
					}
					
				} else if(args[0].equalsIgnoreCase("unclaim")) {
					if(args.length == 1) {
						ChunkManager chunkManager = new ChunkManager(clazz);
						Chunk chunk = s.getLocation().getChunk();
						
						if (chunkManager.isClaimed(chunk)) {
							if(chunkManager.getClaimer(chunk) == s) {
								chunkManager.unclaimChunk(s);
								s.sendMessage(prefix + "Succesfully unclaimed chunk!");
							} else {
								s.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "This chunk is owned by" + ChatColor.DARK_RED + chunkManager.getClaimer(chunk).toString().replaceAll("CraftOfflinePlayer", "").replaceAll("name=", "").replace('[', ' ').replace(']', ' '));
							}
							
						} else {
							s.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "This chunk his not claimed.");
						}
					}
				}
				
			} else {
				s.sendMessage(prefix + "Version " + clazz.getDescription().getVersion() + " developed by " + StringUtils.join(clazz.getDescription().getAuthors().toArray(), ", ", 0, clazz.getDescription().getAuthors().toArray().length));
			}
		}
		
		return false;
	}
}
