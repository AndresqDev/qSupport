package q.andres.support.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static q.andres.support.utils.presets.ObjectsPreset.supportObject;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getName().equals(supportObject.supportName)) return;
        final ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) return;
        event.setCancelled(true);
        supportObject.support(currentItem.getItemMeta().getDisplayName(), (Player) event.getWhoClicked());
    }
}
