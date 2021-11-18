package ru.galaxy773.buyregions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.galaxy773.buyregions.data.Region;
import ru.galaxy773.buyregions.data.RegionData;
import ru.galaxy773.buyregions.utils.ItemUtil;
import ru.galaxy773.buyregions.utils.StringUtil;




public class RegionsGUI
{
  private static ItemStack info;
  private static ItemStack back;
  private static ItemStack next;
  private static ItemStack close;
  private static Map<String, Integer> data = new HashMap<>();

  
  public RegionsGUI() {
    info = ItemUtil.parseItem(String.valueOf(Material.getMaterial(Main.getInstance().getConfig().getInt("GUI.INFO_ITEM.ID"))));
    ItemMeta infoMeta = info.getItemMeta();
    infoMeta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.INFO_ITEM.NAME"), null, null));
    List<String> info_lore = new ArrayList<>();
    for (String line : Main.getInstance().getConfig().getStringList("GUI.INFO_ITEM.LORE")) {
      info_lore.add(StringUtil.colorize(line, null, null));
    }
    infoMeta.setLore(info_lore);
    info.setItemMeta(infoMeta);
    back = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
    SkullMeta backMeta = (SkullMeta)back.getItemMeta();
    backMeta.setOwner("MHF_ArrowLeft");
    backMeta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.BACK_ITEM.NAME"), null, null));
    List<String> back_lore = new ArrayList<>();
    for (String line2 : Main.getInstance().getConfig().getStringList("GUI.BACK_ITEM.LORE")) {
      back_lore.add(StringUtil.colorize(line2, null, null));
    }
    backMeta.setLore(back_lore);
    back.setItemMeta((ItemMeta)backMeta);
    next = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
    SkullMeta nextMeta = (SkullMeta)next.getItemMeta();
    nextMeta.setOwner("MHF_ArrowRight");
    nextMeta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.NEXT_ITEM.NAME"), null, null));
    List<String> next_lore = new ArrayList<>();
    for (String line3 : Main.getInstance().getConfig().getStringList("GUI.NEXT_ITEM.LORE")) {
      next_lore.add(StringUtil.colorize(line3, null, null));
    }
    nextMeta.setLore(next_lore);
    next.setItemMeta((ItemMeta)nextMeta);
    close = ItemUtil.parseItem(Main.getInstance().getConfig().getString("GUI.CLOSE_ITEM.ID"));
    ItemMeta closeMeta = close.getItemMeta();
    closeMeta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.CLOSE_ITEM.NAME"), null, null));
    List<String> close_lore = new ArrayList<>();
    for (String line4 : Main.getInstance().getConfig().getStringList("GUI.CLOSE_ITEM.LORE")) {
      close_lore.add(StringUtil.colorize(line4, null, null));
    }
    closeMeta.setLore(close_lore);
    close.setItemMeta(closeMeta);
  }
  
  public static void removeUser(Player p) {
    if (data.containsKey(p.getName())) {
      data.remove(p.getName());
    }
  }
  
  public static int getPage(Player p) {
    if (!data.containsKey(p.getName())) {
      return 0;
    }
    return ((Integer)data.get(p.getName())).intValue();
  }
  
  public static void openBuyableRegionsGUI(Player p, int page) {
    Inventory inv = Bukkit.createInventory((InventoryHolder)null, 45, StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.BUYABLE.NAME").replace("{page}", String.valueOf(page + 1)), p, null));
    List<Region> regions_in_page = new ArrayList<>();
    List<Region> regions = RegionData.getBuyableRegions();
    if (!regions.isEmpty()) {
      for (int region = page * 36; region < (page + 1) * 36 && 
        region < regions.size(); region++)
      {
        
        regions_in_page.add(regions.get(region));
      }
    }
    ItemStack item = ItemUtil.parseItem(String.valueOf(Material.getMaterial(Main.getInstance().getConfig().getString("GUI.BUYABLE.REGION_ITEM.ID"))));
    for (Region region2 : regions_in_page) {
      ItemStack clone = item.clone();
      ItemMeta meta = clone.getItemMeta();
      meta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.BUYABLE.REGION_ITEM.NAME"), p, region2));
      List<String> lore = new ArrayList<>();
      for (String line : Main.getInstance().getConfig().getStringList("GUI.BUYABLE.REGION_ITEM.LORE")) {
        if (line.contains("{online}")) {
          String online = Main.getInstance().getConfig().getString("GUI.VARIABLES.ONLINE");
          if (Bukkit.getPlayer(region2.getOwner()) == null || !Bukkit.getPlayer(region2.getOwner()).isOnline()) {
            online = Main.getInstance().getConfig().getString("GUI.VARIABLES.OFFLINE");
          }
          line = line.replace("{online}", online);
        } 
        if (line.contains("{region_description}")) {
          line = line.replace("{region_description}", "");
          lore.add(StringUtil.colorize(line, p, region2));
          for (String description : region2.getFormatedDescription()) {
            lore.add(StringUtil.colorize(description, null, null));
          }
          continue;
        } 
        lore.add(StringUtil.colorize(line, p, region2));
      } 
      
      meta.setLore(lore);
      clone.setItemMeta(meta);
      inv.addItem(new ItemStack[] { clone });
    } 
    int lots_count = 0;
    for (Region region3 : regions) {
      if (region3.getOwner().equalsIgnoreCase(p.getName())) {
        lots_count++;
      }
    } 
    ItemStack lots = ItemUtil.parseItem(Main.getInstance().getConfig().getString("GUI.LOTS_ITEM.ID"));
    ItemMeta lotsMeta = lots.getItemMeta();
    lotsMeta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.LOTS_ITEM.NAME"), p, null));
    List<String> lots_lore = new ArrayList<>();
    for (String line2 : Main.getInstance().getConfig().getStringList("GUI.LOTS_ITEM.LORE")) {
      lots_lore.add(StringUtil.colorize(line2.replace("{player_lots_amount}", String.valueOf(lots_count)), p, null));
    }
    lotsMeta.setLore(lots_lore);
    lots.setItemMeta(lotsMeta);
    inv.setItem(36, lots);
    inv.setItem(37, info);
    inv.setItem(39, back);
    inv.setItem(41, next);
    inv.setItem(44, close);
    p.openInventory(inv);
    if (!data.containsKey(p.getName())) {
      data.put(p.getName(), Integer.valueOf(page + 1));
    } else {
      
      data.replace(p.getName(), Integer.valueOf(page + 1));
    } 
  }
  
  public static void openLotsGUI(Player p) {
    Inventory inv = Bukkit.createInventory((InventoryHolder)null, 45, StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.LOTS.NAME"), p, null));
    List<Region> regions = RegionData.getBuyableRegions();
    List<Region> player_regions = new ArrayList<>();
    for (Region region : regions) {
      if (region.getOwner().equalsIgnoreCase(p.getName())) {
        player_regions.add(region);
      }
    } 
    ItemStack item = ItemUtil.parseItem(String.valueOf(Material.getMaterial(Main.getInstance().getConfig().getString("GUI.LOTS.REGION_ITEM.ID"))));
    for (Region region2 : player_regions) {
      ItemStack clone = item.clone();
      ItemMeta meta = clone.getItemMeta();
      meta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.LOTS.REGION_ITEM.NAME"), p, region2));
      List<String> lore = new ArrayList<>();
      for (String line : Main.getInstance().getConfig().getStringList("GUI.LOTS.REGION_ITEM.LORE")) {
        if (line.contains("{description}")) {
          line = line.replace("{description}", "");
          lore.add(StringUtil.colorize(line, p, region2));
          for (String description : region2.getFormatedDescription()) {
            lore.add(StringUtil.colorize(description, null, null));
          }
          continue;
        } 
        lore.add(StringUtil.colorize(line, p, region2));
      } 
      
      meta.setLore(lore);
      clone.setItemMeta(meta);
      inv.addItem(new ItemStack[] { clone });
    } 
    ItemStack lots = ItemUtil.parseItem(String.valueOf(Material.getMaterial(Main.getInstance().getConfig().getString("GUI.LOTS_ITEM.ID"))));
    ItemMeta lotsMeta = lots.getItemMeta();
    lotsMeta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("GUI.LOTS_ITEM.NAME"), p, null));
    List<String> lots_lore = new ArrayList<>();
    for (String line2 : Main.getInstance().getConfig().getStringList("GUI.LOTS_ITEM.LORE")) {
      lots_lore.add(StringUtil.colorize(line2.replace("{player_lots_amount}", String.valueOf(player_regions.size())), p, null));
    }
    lotsMeta.setLore(lots_lore);
    lots.setItemMeta(lotsMeta);
    inv.setItem(36, lots);
    inv.setItem(37, info);
    inv.setItem(39, back);
    inv.setItem(41, next);
    inv.setItem(44, close);
    p.openInventory(inv);
  }
}
