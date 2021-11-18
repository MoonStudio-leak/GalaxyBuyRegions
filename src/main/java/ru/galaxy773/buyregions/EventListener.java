package ru.galaxy773.buyregions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.galaxy773.buyregions.data.Region;
import ru.galaxy773.buyregions.data.RegionData;
import ru.galaxy773.buyregions.requests.BuyRequest;
import ru.galaxy773.buyregions.title.Title;
import ru.galaxy773.buyregions.utils.StringUtil;

public class EventListener
  implements Listener
{
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || !e.getCurrentItem().hasItemMeta()) {
      return;
    }
    Player p = (Player)e.getWhoClicked();
    if (e.getInventory().getSize() == 45) {
      e.setCancelled(true);
      if (e.getRawSlot() == 39) {
        RegionsGUI.openBuyableRegionsGUI(p, 0);
        return;
      } 
      if (e.getRawSlot() == 41) {
        RegionsGUI.openBuyableRegionsGUI(p, 0);
        return;
      } 
      if (e.getRawSlot() == 44) {
        p.closeInventory();
        return;
      } 
      List<Region> player_regions = new ArrayList<>();
      for (Region region : RegionData.getBuyableRegions()) {
        if (region.getOwner().equalsIgnoreCase(p.getName())) {
          player_regions.add(region);
        }
      } 
      if (e.getRawSlot() <= player_regions.size() && player_regions.get(e.getRawSlot()) != null) {
        p.closeInventory();
        p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.BACK_REGION"), p, player_regions.get(e.getRawSlot())));
        Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.BACK_REGION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.BACK_REGION").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.BACK_REGION"), p, player_regions.get(e.getRawSlot())), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.BACK_REGION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.BACK_REGION").split(";")[1] : "", p, player_regions.get(e.getRawSlot())));
        RegionData.unregisterRegion(player_regions.get(e.getRawSlot()));
      }
    
    } else if (RegionsGUI.getPage(p) != 0 && e.getInventory().getSize() == 45) {
      e.setCancelled(true);
      if (e.getRawSlot() == 36) {
        RegionsGUI.openLotsGUI(p);
        return;
      } 
      if (e.getRawSlot() == 39 && RegionsGUI.getPage(p) != 1) {
        RegionsGUI.openBuyableRegionsGUI(p, (RegionsGUI.getPage(p) == 0) ? 0 : (RegionsGUI.getPage(p) - 1));
        return;
      } 
      if (e.getRawSlot() == 41 && RegionsGUI.getPage(p) != RegionData.getPages()) {
        if (RegionsGUI.getPage(p) == RegionData.getPages() + 1) {
          return;
        }
        RegionsGUI.openBuyableRegionsGUI(p, RegionsGUI.getPage(p) + 1);
      } else {
        
        if (e.getRawSlot() == 44) {
          p.closeInventory();
          return;
        } 
        int region_index = (RegionsGUI.getPage(p) - 1) * 36 + e.getRawSlot();
        if (RegionData.getBuyableRegions().size() <= region_index) {
          return;
        }
        Region region = RegionData.getBuyableRegions().get(region_index);
        if (region != null) {
          if (region.getOwner().equalsIgnoreCase(p.getName())) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.BUY_OWNING_REGION"), p, region));
          }
          else if (!region.notEmpty()) {
            p.closeInventory();
            Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.REGION_NOY_BUY").contains(";") ? Main.getInstance().getConfig().getString("TITLES.REGION_NOY_BUY").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.REGION_NOY_BUY"), p, region), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.REGION_NOY_BUY").contains(";") ? Main.getInstance().getConfig().getString("TITLES.REGION_NOY_BUY").split(";")[1] : "", p, region));
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.REGION_NOT_BUY"), p, region));
            RegionData.unregisterRegion(region);
          } else {
            
            p.closeInventory();
            List<String> messages = new ArrayList<>();
            for (String line : Main.getInstance().getConfig().getStringList("MESSAGES.SEND_REQUEST")) {
              p.sendMessage(StringUtil.colorize(line, p, region));
            }
            Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SEND_REQUEST").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SEND_REQUEST").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.SEND_REQUEST"), p, region), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SEND_REQUEST").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SEND_REQUEST").split(";")[1] : "", p, region));
            BuyRequest.sendRequest(p.getName(), region);
          } 
        }
      } 
    } 
  }
  
  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    RegionsGUI.removeUser((Player)e.getPlayer());
  }
  
  @EventHandler
  public void onClose(PlayerQuitEvent e) {
    RegionsGUI.removeUser(e.getPlayer());
    BuyRequest.removeRequest(e.getPlayer().getName());
  }
}
