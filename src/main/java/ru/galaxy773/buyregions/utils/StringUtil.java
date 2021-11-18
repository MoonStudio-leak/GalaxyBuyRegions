package ru.galaxy773.buyregions.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.galaxy773.buyregions.data.Region;

public class StringUtil {
    public static String buildString(int index, String[] args) {
        String message = "";

        for(int i = index; i < args.length; ++i) {
            message = String.valueOf(message) + args[i] + " ";
        }

        return message;
    }

    public static String colorize(String text, Player p, Region region) {
        String message = text;
        if (region != null) {
            message = text.replace("{region_name}", region.getName()).replace("{region_owner}", region.getOwner()).replace("{region_display_name}", region.getDisplayName()).replace("{region_coordinates}", region.getCoordinates()).replace("{region_date}", region.getDate()).replace("{region_time}", region.getTime()).replace("{region_position}", region.getPosition()).replace("{region_world}", region.getWorldName()).replace("{region_price}", String.valueOf(region.getPrice()));
        }

        if (p != null) {
            message = message.replace("{player}", p.getName());
        }

        return ChatColor.translateAlternateColorCodes('&', "&r" + message);
    }
}
