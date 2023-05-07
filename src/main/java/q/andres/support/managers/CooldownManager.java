package q.andres.support.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static q.andres.support.Main.datafolder;
import static q.andres.support.Main.instance;

public class CooldownManager {
    public static YamlConfiguration cdata;
    public static File cfdata;

    public List<String> getUpdatedCooldowns() { //Only decorative :3
        return cdata.getStringList("DATA");
    }

    public Map<String, String> parseCooldowns(List<String> oldStrings, String type) {
        Map<String, String> newHashmap = new HashMap<>();
        for (String cooldown : oldStrings) {
            String[] parsed = cooldown.split(" ");
            if (parsed[2].equals(type)) {
                newHashmap.put(parsed[0], parsed[1]);
            }
        }
        return newHashmap;
    }

    public void applyCooldown(List<String> newStrings, String type, String UUID) {
        newStrings.removeIf(s -> s.contains(UUID) && s.contains(type));
        newStrings.add(UUID + " " + LocalDateTime.now() + " " + type);
        cdata.set("DATA", newStrings);
        try {
            cdata.save(cfdata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkExpire(LocalDateTime oldDate, int diference) {
        final long difference = ChronoUnit.MINUTES.between(oldDate, LocalDateTime.now());
        return Math.abs(difference) >= diference;
    }

    public String formatTime(LocalDateTime time, int diference) {
        final LocalDateTime targetTime = time.plusMinutes(diference);
        final LocalDateTime now = LocalDateTime.now();
        final long days = ChronoUnit.DAYS.between(now, targetTime);
        final long hours = ChronoUnit.HOURS.between(now, targetTime) % 24;
        final long minutes = ChronoUnit.MINUTES.between(now, targetTime) % 60;
        final long seconds = ChronoUnit.SECONDS.between(now, targetTime) % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d");
        }
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s");
        }
        return sb.toString();
    }

    public static void registerCooldowns() throws IOException {
        cfdata = new File(datafolder, "data/cooldowns.yml");
        cdata = YamlConfiguration.loadConfiguration(cfdata);
        if (!cfdata.exists()) {
            InputStreamReader dCS = new InputStreamReader(instance.getResource("data/cooldowns.yml"), StandardCharsets.UTF_8);
            YamlConfiguration dC = YamlConfiguration.loadConfiguration(dCS);
            cdata.setDefaults(dC);
            cdata.options().copyDefaults(true);
            cdata.save(cfdata);
        }
    }
}
