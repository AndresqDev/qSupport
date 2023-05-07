package q.andres.support.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {

    public static String translate(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> translate(List<String> list) {
        List<String> colored = new ArrayList<>();
        for (String line : list) {
            if (!line.contains("&")) {
                colored.add(line);
            } else {
                colored.add(translate(line));
            }
        }
        return colored;
    }
}
