package q.andres.support.managers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import q.andres.support.Main;
import q.andres.support.utils.CC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SupportManager implements Listener {
    public Inventory menu;
    public String supportName;
    private String lang_error;
    private String lang_use;
    private final CooldownManager cooldown;
    private final ConsoleCommandSender sender;
    private final Table<String, Integer, List<String>> supports = HashBasedTable.create();

    public SupportManager() {
        cooldown = new CooldownManager();
        sender = Bukkit.getConsoleSender();
    }

    public void set() {
        YamlConfiguration cfg = Main.data;
        supportName = CC.translate(cfg.getString("inventory_name"));
        lang_error = CC.translate(cfg.getString("lang.cooldown_error"));
        lang_use = CC.translate(cfg.getString("lang.support_use"));
        menu = Bukkit.createInventory(null, cfg.getInt("slots_row_size") * 9, supportName);
        final ConfigurationSection supportsSection = cfg.getConfigurationSection("supports");
        for (String key : supportsSection.getKeys(false)) {
            final ConfigurationSection supportSection = supportsSection.getConfigurationSection(key);
            final String DisplayName = CC.translate(supportSection.getString("display_name"));
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(supportSection.getString("head"));
            skullMeta.setDisplayName(DisplayName);
            skullMeta.setLore(CC.translate(cfg.getStringList("default_lore")));
            skull.setItemMeta(skullMeta);
            menu.setItem(supportSection.getInt("slot"), skull);
            supports.put(DisplayName, supportSection.getInt("cooldown"), supportSection.getStringList("commands"));
        }
    }

    private String setPlaceholders(String str, String name, String DisplayName) {
        return str.replace("%player%", name).replace("%supported%", DisplayName);
    }

    public void support(String DisplayName, Player player) {
        final Map<Integer, List<String>> rowMap = supports.row(DisplayName);

        for (Map.Entry<Integer, List<String>> entry : rowMap.entrySet()) {
            //Consts

            final String name = player.getName();
            final int diference = entry.getKey();

            //Cooldown System (Created by me)

            if (diference != 0) {
                //Consts , Esta es la implementacion del sistema de cooldowns.

                final String UniqueID = player.getUniqueId().toString();
                final String CooldownType = "support";
                final List<String> CooldownList = cooldown.getUpdatedCooldowns();
                final Map<String, String> CooldownPlayer = cooldown.parseCooldowns(CooldownList, CooldownType);

                if (!CooldownPlayer.containsKey(UniqueID)) {
                    cooldown.applyCooldown(CooldownList, CooldownType, UniqueID);
                } else {
                    final LocalDateTime parsedDate = LocalDateTime.parse(CooldownPlayer.get(UniqueID));
                    if (cooldown.checkExpire(parsedDate, diference)) {
                        cooldown.applyCooldown(CooldownList, CooldownType, UniqueID);
                    } else {
                        player.closeInventory();
                        player.sendMessage(CC.translate(setPlaceholders(lang_error, name, DisplayName).replace("%cooldown%", cooldown.formatTime(parsedDate, diference))));
                        return;
                    }
                }
            }

            //Command Logic

            for (String cmd : entry.getValue()) {
                Bukkit.dispatchCommand(sender, setPlaceholders(cmd.replace("/", ""), name, DisplayName));
            }
            player.closeInventory();
            player.sendMessage(setPlaceholders(lang_use, name, DisplayName));
        }
    }
}