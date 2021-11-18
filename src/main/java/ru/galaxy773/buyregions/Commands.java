package ru.galaxy773.buyregions;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.galaxy773.buyregions.data.Region;
import ru.galaxy773.buyregions.data.RegionData;
import ru.galaxy773.buyregions.requests.BuyRequest;
import ru.galaxy773.buyregions.title.Title;
import ru.galaxy773.buyregions.utils.ItemUtil;
import ru.galaxy773.buyregions.utils.StringUtil;
import ru.galaxy773.buyregions.utils.TimeUtil;


public class Commands implements CommandExecutor {
    Permission permission = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();

    private int getPlayerLimit(Player p) {
        String group = "DEFAULT";
        if (p != null) {
            group = this.permission.getPrimaryGroup(p);
        }

        if (!Main.getInstance().getConfig().getConfigurationSection("GROUPS_LIMIT").getValues(false).containsKey(group)) {
            group = "DEFAULT";
        }

        return Main.getInstance().getConfig().getInt("GROUPS_LIMIT." + group);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length < 1 || !args[0].equalsIgnoreCase("delete")) {
      if (sender instanceof Player) {
        Player p = (Player)sender;
        if (args.length == 0) {
          RegionsGUI.openBuyableRegionsGUI(p, 0);
          return true;
        } 
        if (args[0].equalsIgnoreCase("sell")) {
          int limit = getPlayerLimit(p);
          if (RegionData.getPlayerRegions(p).size() >= limit) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.GROUPS_LIMIT").replace("{player_limit}", String.valueOf(limit)), p, null));
            return true;
          } 
          if (args.length != 4) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.COMMAND_SELL_USE"), p, null));
            return true;
          } 
          if (args[2].length() > Main.getInstance().getConfig().getString("BUY_SETTINGS.MAX_PRICE").length()) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PRICE_NOT_CORRECTED"), p, null));
            return true;
          } 
          if (!StringUtils.isNumeric(args[2])) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PRICE_NOT_CORRECTED"), p, null));
            return true;
          } 
          int price = Integer.parseInt(args[2]);
          if (price > Main.getInstance().getConfig().getInt("BUY_SETTINGS.MAX_PRICE")) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.MAX_PRICE"), p, null));
            return true;
          } 
          if (price < Main.getInstance().getConfig().getInt("BUY_SETTINGS.MIN_PRICE")) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.MIN_PRICE"), p, null));
            return true;
          } 
          if (RegionData.regionIsBuy(args[1])) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.REGION_IS_BUY").replace("{region_name}", args[1]), p, null));
            return true;
          } 
          RegionManager regionManager = Main.getInstance().getWorldGuard().getRegionManager(p.getWorld());
          
          ProtectedRegion region = regionManager.getRegion(args[1]);
          LocalPlayer lp = Main.getInstance().getWorldGuard().wrapPlayer(p);
          if (region == null || !region.isOwner(lp)) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PLAYER_NOT_REGION_OWNER").replace("{region_name}", args[1]), p, null));
            return true;
          } 
          Region rg = new Region(p.getName(), args[1], args[3], TimeUtil.getTimeDate(), "", p.getWorld().getName(), price);
          RegionData.registerRegion(rg);
          for (String line : Main.getInstance().getConfig().getStringList("MESSAGES.SELL_REGION")) {
            p.sendMessage(StringUtil.colorize(line, p, rg));
          }
          Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SELL_REGION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SELL_REGION").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.SELL_REGION"), p, rg), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SELL_REGION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SELL_REGION").split(";")[1] : "", p, rg));
          return true;
        } 
        if (args[0].equalsIgnoreCase("edit")) {
          if (args.length < 3) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.COMMAND_EDIT_USE"), p, null));
            return true;
          } 
          if (!RegionData.regionIsBuy(args[1])) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.REGION_NOT_BUY"), p, null));
            return true;
          } 
          Region owning_region = null;
          for (Region region2 : RegionData.getPlayerRegions(p)) {
            if (region2.getName().equalsIgnoreCase(args[1])) {
              owning_region = region2;
            }
          } 
          if (owning_region == null) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PLAYER_NOT_OWNER"), p, owning_region));
            return true;
          } 
          String message = StringUtil.buildString(2, args);
          if (message.equalsIgnoreCase(owning_region.getDescription())) {
            return true;
          }
          if (message.length() > 256) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.DESCRIPTION_BIG_LENGTH").replace("{length}", String.valueOf(message.length())), p, owning_region));
            return true;
          } 
          RegionData.setDescription(owning_region, StringUtil.buildString(2, args));
          p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.SET_DESCRIPTION"), p, owning_region));
          Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SET_DESCRIPTION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SET_DESCRIPTION").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.SET_DESCRIPTION"), p, owning_region), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SET_DESCRIPTION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SET_DESCRIPTION").split(";")[1] : "", p, owning_region));
          return true;
        } 
        if (args[0].equalsIgnoreCase("price")) {
          if (args.length != 3) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.COMMAND_PRICE_USE"), p, null));
            return true;
          } 
          if (args[2].length() > Main.getInstance().getConfig().getString("BUY_SETTINGS.MAX_PRICE").length()) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PRICE_NOT_CORRECTED"), p, null));
            return true;
          } 
          if (!StringUtils.isNumeric(args[2])) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PRICE_NOT_CORRECTED"), p, null));
            return true;
          } 
          int price2 = Integer.parseInt(args[2]);
          if (price2 > Main.getInstance().getConfig().getInt("BUY_SETTINGS.MAX_PRICE")) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.MAX_PRICE"), p, null));
            return true;
          } 
          if (price2 < Main.getInstance().getConfig().getInt("BUY_SETTINGS.MIN_PRICE")) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.MIN_PRICE"), p, null));
            return true;
          } 
          if (!RegionData.regionIsBuy(args[1])) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.REGION_NOT_BUY").replace("{region_name}", args[1]), p, null));
            return true;
          } 
          Region owning_region2 = null;
          for (Region region3 : RegionData.getPlayerRegions(p)) {
            if (region3.getName().equalsIgnoreCase(args[1])) {
              owning_region2 = region3;
            }
          } 
          if (owning_region2 == null) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.PLAYER_NOT_REGION_OWNER"), p, owning_region2));
            return true;
          } 
          if (owning_region2.getPrice() == Integer.parseInt(args[2])) {
            return true;
          }
          RegionData.setPrice(owning_region2, Integer.parseInt(args[2]));
          p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.SET_PRICE"), p, owning_region2));
          Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SET_PRICE").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SET_PRICE").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.SET_PRICE"), p, owning_region2), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SET_PRICE").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SET_PRICE").split(";")[1] : "", p, owning_region2));
          return true;
        } 
        if (args[0].equalsIgnoreCase("tp")) {
          Region request = BuyRequest.getRequest(p.getName());
          if (request == null || !RegionData.getBuyableRegions().contains(request) || !request.notEmpty()) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.TELEPORT_CANCELED"), p, request));
            return true;
          } 
          ItemStack item = ItemUtil.parseItem(Main.getInstance().getConfig().getString("REGION_CHECK_ITEM.ID"));
          ItemMeta meta = item.getItemMeta();
          meta.setDisplayName(StringUtil.colorize(Main.getInstance().getConfig().getString("REGION_CHECK_ITEM.NAME"), p, request));
          List<String> lore = new ArrayList<>();
          for (String line2 : Main.getInstance().getConfig().getStringList("REGION_CHECK_ITEM.LORE")) {
            lore.add(StringUtil.colorize(line2, p, request));
          }
          meta.setLore(lore);
          item.setItemMeta(meta);
          p.getInventory().addItem(item);
          p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.TELEPORT"), p, request));
          Title.sendTitle(p, 20, 40, 20, StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.TELEPORT").contains(";") ? Main.getInstance().getConfig().getString("TITLES.TELEPORT").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.TELEPORT"), p, request), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.TELEPORT").contains(";") ? Main.getInstance().getConfig().getString("TITLES.TELEPORT").split(";")[1] : "", p, request));
          p.teleport(request.getTeleportLocation());
          return true;
        } 
        if (args[0].equalsIgnoreCase("confirm")) {
          Region request = BuyRequest.getRequest(p.getName());
          if (request == null || !RegionData.getBuyableRegions().contains(request) || !request.notEmpty()) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.REGION_NOT_BUY"), p, request));
            return true;
          } 
          if (Main.getInstance().getEconomy().getBalance((OfflinePlayer)p) < request.getPrice()) {
            p.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.NO_MONEY"), p, request));
            return true;
          } 
          ProtectedRegion region4 = request.getProtectedRegion();
          if (region4 != null) {
            region4.getOwners().clear();
            region4.getMembers().clear();
            region4.getOwners().addPlayer(Main.getInstance().getWorldGuard().wrapPlayer(p));
            Main.getInstance().getEconomy().withdrawPlayer((OfflinePlayer)p, request.getPrice());
            Main.getInstance().getEconomy().depositPlayer(request.getOwner(), request.getPrice());
            for (String line3 : Main.getInstance().getConfig().getStringList("MESSAGES.BUY_REGION")) {
              p.sendMessage(StringUtil.colorize(line3, p, request));
            }
            Title.sendTitle(p, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.BUY_REGION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.BUY_REGION").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.BUY_REGION"), p, request), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.BUY_REGION").contains(";") ? Main.getInstance().getConfig().getString("TITLES.BUY_REGION").split(";")[1] : "", p, request));
            p.teleport(request.getTeleportLocation());
            RegionData.unregisterRegion(request);
            Player owner = Bukkit.getPlayer(request.getOwner());
            if (owner != null && owner.isOnline()) {
              owner.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.SELL_REGION_SUCCESS"), owner, request));
              Title.sendTitle(owner, 20, Integer.valueOf(40), Integer.valueOf(20), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SELL_REGION_SUCCESS").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SELL_REGION_SUCCESS").split(";")[0] : Main.getInstance().getConfig().getString("TITLES.SELL_REGION_SUCCESS"), p, request), StringUtil.colorize(Main.getInstance().getConfig().getString("TITLES.SELL_REGION_SUCCESS").contains(";") ? Main.getInstance().getConfig().getString("TITLES.SELL_REGION_SUCCESS").split(";")[1] : "", p, request));
            } 
          } 
        } 
      } 
      return false;
    } 
    if (args.length < 2) {
      sender.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.COMMAND_DELETE_USE"), null, null));
      return true;
    } 
    if (!sender.hasPermission("buyregions.delete")) {
      sender.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.NO_PERMISSIONS"), null, null));
      return true;
    } 
    Region region5 = null;
    for (Region rg2 : RegionData.getBuyableRegions()) {
      if (rg2.getName().equalsIgnoreCase(args[1])) {
        region5 = rg2;
      }
    } 
    if (region5 == null) {
      sender.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.REGION_NOT_BUY"), null, null));
      return true;
    } 
    if (RegionData.getBuyableRegions().contains(region5)) {
      RegionData.unregisterRegion(region5);
    }
    sender.sendMessage(StringUtil.colorize(Main.getInstance().getConfig().getString("MESSAGES.DELETE_REGION"), null, region5));
    return true;
  }
}
