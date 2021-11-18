package ru.galaxy773.buyregions.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static ItemStack parseItem(String item) {
        ItemStack stack = null;
        if (item.contains(":")) {
            stack = new ItemStack(Material.getMaterial(item.split(":")[0]), 1, Byte.parseByte(item.split(":")[1]));
        } else {
            stack = new ItemStack(Material.getMaterial(item));
        }

        if (stack == null) {
            stack = new ItemStack(Material.BARRIER);
        }

        return stack;
    }
}
