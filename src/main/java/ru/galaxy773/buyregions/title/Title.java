package ru.galaxy773.buyregions.title;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import java.lang.reflect.*;

/**
 * @Author source code: NaulbiMIX
 * @Author default source code: WinLocker02
 */

public class Title {

    public static void sendTitle(Player player, int fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            Object e;
            Object chatSubtitle;
            Constructor<?> subtitleConstructor;
            Object subtitlePacket;
            if (title != null) {
                e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
                e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null);
                chatSubtitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle);
                sendPacket(player, subtitlePacket);
            }

            if (subtitle != null) {
                e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
                e = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}