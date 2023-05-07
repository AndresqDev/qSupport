package q.andres.support.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import q.andres.support.utils.presets.ObjectsPreset;

import java.io.IOException;

import static q.andres.support.Main.registerSettings;
import static q.andres.support.managers.CooldownManager.registerCooldowns;
import static q.andres.support.utils.presets.ObjectsPreset.supportObject;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) throws IOException {
        final String[] args = e.getMessage().toLowerCase().split(" ");
        final String cmd = args[0];
        if (cmd.equals("/support") || cmd.equals("/redeem")) {
            final Player player = e.getPlayer();
            e.setCancelled(true);
            if(args.length == 2 && player.isOp()) {
                final String argument = args[1].toLowerCase();
                if(argument.equals("reload") || argument.equals("restart")){
                    registerSettings();
                    registerCooldowns();
                    ObjectsPreset.setObjects();
                    player.sendMessage(ChatColor.GREEN+"Successfully "+argument+"ed qSupport!");
                }
            } else {
                player.openInventory(supportObject.menu);
            }
        }
    }
}
