package q.andres.support;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import q.andres.support.listeners.CommandListener;
import q.andres.support.listeners.InventoryListener;
import q.andres.support.utils.presets.ObjectsPreset;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static q.andres.support.managers.CooldownManager.registerCooldowns;

public class Main extends JavaPlugin {
    public static YamlConfiguration data;
    public static File fdata;
    public static File datafolder;
    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        datafolder = this.getDataFolder();

        try {
            registerSettings();
            registerCooldowns();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        registerListeners();

        ObjectsPreset.setObjects();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"qSupport successfully loaded!, github.com/AndresqDev");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Thanks for using qSupport!, github.com/AndresqDev");
    }

    private void registerListeners() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new CommandListener(), instance);
        pm.registerEvents(new InventoryListener(), instance);
    }

    public static void registerSettings() throws IOException {
        fdata = new File(datafolder, "settings.yml");
        data = YamlConfiguration.loadConfiguration(fdata);
        if (!fdata.exists()) {
            InputStreamReader dCS = new InputStreamReader(instance.getResource("settings.yml"), StandardCharsets.UTF_8);
            YamlConfiguration dC = YamlConfiguration.loadConfiguration(dCS);
            data.setDefaults(dC);
            data.options().copyDefaults(true);
            data.save(fdata);
        }
    }
}
