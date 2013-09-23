package main.java.me.avastprods.chunkprotection;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventManager implements Listener {

	ChunkProtection clazz;

	public EventManager(ChunkProtection instance) {
		clazz = instance;
	}

	static final ArrayList<Material> blacklist = new ArrayList<Material>();

	public void setupBlacklist() {
		Material[] a = new Material[] { Material.BED, Material.ANVIL, Material.ENCHANTMENT_TABLE, Material.DISPENSER, Material.DROPPER, Material.HOPPER, Material.MINECART, Material.DRAGON_EGG, Material.BEACON, Material.BOAT, Material.ENDER_CHEST, Material.CHEST, Material.NOTE_BLOCK,
				Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.WOODEN_DOOR, Material.LEVER, Material.FURNACE, Material.TRAP_DOOR, Material.FENCE_GATE, Material.REDSTONE_COMPARATOR, Material.WORKBENCH, Material.JUKEBOX };

		for (Material b : a) {
			blacklist.add(b);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		ChunkManager chunkManager = new ChunkManager(clazz);
		MessageData messageData = new MessageData(clazz);

		if (chunkManager.isClaimed(event.getBlock().getChunk())) {
			if (chunkManager.getClaimer(event.getBlock().getChunk()) != event.getPlayer()) {
				if (!chunkManager.getTrusted(event.getBlock().getChunk()).contains(event.getPlayer().getName())) {
					event.setCancelled(true);

					event.getPlayer().sendMessage(colorize(messageData.getData().getString("message.event.break_deny").replaceAll("%claimer%", chunkManager.getClaimer(event.getBlock().getChunk()).getName())));
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		ChunkManager chunkManager = new ChunkManager(clazz);
		MessageData messageData = new MessageData(clazz);

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (chunkManager.isClaimed(event.getClickedBlock().getChunk())) {
				if (chunkManager.getClaimer(event.getClickedBlock().getChunk()) != event.getPlayer()) {
					if (!chunkManager.getTrusted(event.getClickedBlock().getChunk()).contains(event.getPlayer().getName())) {
						if (EventManager.blacklist.contains(event.getClickedBlock().getType())) {
							event.getPlayer().sendMessage(colorize(messageData.getData().getString("message.event.interact_deny").replaceAll("%claimer%", chunkManager.getClaimer(event.getClickedBlock().getChunk()).getName())));

						} else {
							if (event.getPlayer().getItemInHand().getType().isBlock() && event.getPlayer().getItemInHand().getType() != Material.AIR) {
								event.getPlayer().sendMessage(colorize(messageData.getData().getString("message.event.place_deny").replaceAll("%claimer%", chunkManager.getClaimer(event.getClickedBlock().getChunk()).getName())));
							}
						}

						event.setCancelled(true);
					}
				}		
			}
		}
	}

	public String colorize(String string) {
		return string.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
}
