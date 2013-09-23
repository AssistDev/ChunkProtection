package main.java.me.avastprods.chunkprotection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class CommandManager implements CommandExecutor {

	ChunkProtection clazz;

	public CommandManager(ChunkProtection instance) {
		clazz = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player s = (Player) sender;
		Chunk chunk = s.getLocation().getChunk();

		ChunkManager chunkManager = new ChunkManager(clazz);
		MessageData messageData = new MessageData(clazz);

		if (cmd.getName().equalsIgnoreCase("chunkprotection") || cmd.getName().equalsIgnoreCase("cp")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("claim")) {
					if (args.length == 1) {
						if (_a(s)) {
							if (!chunkManager.isClaimed(chunk)) {
								if (chunkManager.getClaims(s) < chunkManager.getMaxClaims(s)) {
									chunkManager.claimChunk(s);

									s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_claimed").replaceAll("%prefix%", messageData.getData().getString("message.command.prefix"))));

								} else {
									s.sendMessage(colorize(messageData.getData().getString("message.command.claim_limit_reached")));
								}

							} else if (chunkManager.getClaimer(chunk) != s) {
								s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_already_claimed")).replaceAll("%claimer%", chunkManager.getClaimer(chunk).getName()));
							} else {
								s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_owned_sender")));
							}
							
						} else {
							s.sendMessage(colorize(messageData.getData().getString("message.other.no_permission")));
						}
					}

				} else if (args[0].equalsIgnoreCase("unclaim")) {
					if (args.length == 1) {
						if (s.hasPermission("chunkprotection.unclaim") || s.hasPermission("cp.unclaim")) {
							if (chunkManager.isClaimed(chunk)) {
								if (chunkManager.getClaimer(chunk) == s) {
									chunkManager.unclaimChunk(s);

									s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_unclaimed").replaceAll("%prefix%", messageData.getData().getString("message.command.prefix"))));

								} else {
									s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_owned_other")).replaceAll("%claimer%", chunkManager.getClaimer(chunk).getName()));
								}

							} else {
								s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_not_claimed")));
							}

						} else {
							s.sendMessage(colorize(messageData.getData().getString("message.other.no_permission")));
						}

					}

				} else if (args[0].equalsIgnoreCase("unclaimall")) {
					if (args.length == 1) {
						if (s.hasPermission("chunkprotection.unclaimall") || s.hasPermission("cp.unclaimall")) {
							if (chunkManager.getClaims(s) > 0) {
								s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_unclaimed_all").replaceAll("%prefix%", messageData.getData().getString("message.command.prefix")).replaceAll("%amount%", String.valueOf(chunkManager.getClaims(s)))));

								chunkManager.unclaimAll(s);
							} else {
								s.sendMessage(colorize(messageData.getData().getString("message.other.no_chunks_owned")));
							}

						} else {
							s.sendMessage(colorize(messageData.getData().getString("message.other.no_permission")));
						}
						
					}

				} else if (args[0].equalsIgnoreCase("trust")) {
					if (args.length == 2) {
						if (s.hasPermission("chunkprotection.trust") || s.hasPermission("cp.trust")) {
							OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

							if (chunkManager.isClaimed(chunk)) {
								if (chunkManager.getClaimer(chunk) == s) {
									if (!chunkManager.getTrusted(chunk).contains(target.getName())) {
										chunkManager.addTrusted(chunk, target);

										s.sendMessage(colorize(messageData.getData().getString("message.command.trust").replaceAll("%prefix%", messageData.getData().getString("message.command.prefix")).replaceAll("%trusted%", target.getName())));

									} else {
										s.sendMessage(colorize(messageData.getData().getString("message.command.target_already_trusted")).replaceAll("%target%", target.getName()));
									}

								} else {
									s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_owned_other")).replaceAll("%claimer%", chunkManager.getClaimer(chunk).getName()));
								}

							} else {
								s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_not_claimed")));
							}

						} else {
							s.sendMessage(colorize(messageData.getData().getString("message.other.no_permission")));
						}

					} else {
						s.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + "/chunkprotection trust <player>");
					}

				} else if (args[0].equalsIgnoreCase("untrust")) {
					if (args.length == 2) {
						if (s.hasPermission("chunkprotection.untrust") || s.hasPermission("cp.untrust")) {
							OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

							if (chunkManager.isClaimed(chunk)) {
								if (chunkManager.getClaimer(chunk) == s) {
									if (chunkManager.getTrusted(chunk).contains(target.getName())) {
										chunkManager.removeTrusted(chunk, target);

										s.sendMessage(colorize(messageData.getData().getString("message.command.untrust").replaceAll("%prefix%", messageData.getData().getString("message.command.prefix")).replaceAll("%untrusted%", target.getName())));

									} else {
										s.sendMessage(colorize(messageData.getData().getString("message.command.target_not_trusted")).replaceAll("%target%", target.getName()));
									}

								} else {
									s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_owned_other")).replaceAll("%claimer%", chunkManager.getClaimer(chunk).getName()));
								}

							} else {
								s.sendMessage(colorize(messageData.getData().getString("message.command.chunk_not_claimed")));
							}

						} else {
							s.sendMessage(colorize(messageData.getData().getString("message.other.no_permission")));
						}

					} else {
						s.sendMessage(colorize(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + "/chunkprotection untrust <player>"));
					}
				}

			} else {
				s.sendMessage(colorize(messageData.getData().getString("message.command.prefix")) + "Version " + clazz.getDescription().getVersion() + " developed by " + StringUtils.join(clazz.getDescription().getAuthors().toArray(), ", ", 0, clazz.getDescription().getAuthors().toArray().length));
			}
		}

		return false;
	}

	private String colorize(String string) {
		return string.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	
	private boolean _a(Player a_) {
		for (PermissionAttachmentInfo perm : a_.getEffectivePermissions()) {
			if (perm.getPermission().startsWith("chunkprotection.claim")) {
				return true;
			}
		}
		
		return false;
	}
}
