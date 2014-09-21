package com.clashwars.cwevents.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemUtils {

    @SuppressWarnings("deprecation")
    public static ItemStack getItem(int ID, int amount, short durability, String name, String[] alore) {
        ItemStack item = new ItemStack(ID, amount);
        return getItem(item, durability, name, alore);
    }

    public static ItemStack getItem(Material mat, int amount, short durability, String name, String[] alore) {
        ItemStack item = new ItemStack(mat, amount);
        return getItem(item, durability, name, alore);
    }

    public static ItemStack getItem(ItemStack item, short durability, String name, String[] alore) {
        if (alore != null) {
            for (int i = 0; i < alore.length; i++) {
                alore[i] = Util.integrateColor(alore[i]);
            }
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (alore != null) {
            Collections.addAll(lore, alore);
        }
        meta.setLore(lore);
        if (name != null) {
            meta.setDisplayName(Util.integrateColor(name));
        }
        item.setItemMeta(meta);

        item.setDurability(durability);
        return item;
    }

}
