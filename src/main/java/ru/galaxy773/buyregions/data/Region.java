package ru.galaxy773.buyregions.data;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ru.galaxy773.buyregions.Main;
import ru.galaxy773.buyregions.utils.StringUtil;


public class Region
{
  private String owner;
  private String name;
  private String display_name;
  private String date;
  private String time;
  private String description;
  private List<String> formated_description;
  private int price;
  private String world_name;
  private World world;
  private Location teleport_location;
  private String coordinates;
  private String position;
  private final RegionManager regionManager;
  
  public Region(String owner, String name, String display_name, String date_time, String description, String world_name, int price) {
    this.owner = owner;
    this.name = name;
    this.display_name = display_name;
    this.date = date_time.split(" ")[0];
    this.time = date_time.split(" ")[1];
    this.description = description;
    this.formated_description = new ArrayList<>();
    String line = "";
    char[] letters = description.toCharArray();
    for (int chr = 0; chr < letters.length; chr++) {
      line = line + letters[chr];
      if (line.length() == 48) {
        this.formated_description.add(line);
        line = "";
      }
      else if (chr == letters.length - 1) {
        this.formated_description.add(line);
      } 
    } 
    this.price = price;
    this.world_name = world_name;
    
    this.regionManager = Main.getInstance().getWorldGuard().getRegionManager(Bukkit.getWorld(world_name));
    
    setWorld();
    setTeleportLocation();
    setCoordinates();
    setPosition();
  }
  
  public boolean notEmpty() {
    ProtectedRegion region = getProtectedRegion();
    LocalPlayer lp = null;
    if (Bukkit.getPlayer(this.owner) != null && Bukkit.getPlayer(this.owner).isOnline()) {
      lp = Main.getInstance().getWorldGuard().wrapPlayer(Bukkit.getPlayer(this.owner));
    } else {
      
      lp = Main.getInstance().getWorldGuard().wrapOfflinePlayer(Bukkit.getOfflinePlayer(this.owner));
    } 
    return (region != null && lp != null && region.isOwner(lp));
  }
  
  public String getOwner() {
    return this.owner;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDisplayName() {
    return this.display_name;
  }
  
  public String getDate() {
    return this.date;
  }
  
  public String getTime() {
    return this.time;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public List<String> getFormatedDescription() {
    return this.formated_description;
  }
  
  public String getWorldName() {
    return this.world_name;
  }
  
  public int getPrice() {
    return this.price;
  }
  
  public Location getTeleportLocation() {
    return this.teleport_location;
  }
  
  public String getCoordinates() {
    return this.coordinates;
  }
  
  public String getPosition() {
    return this.position;
  }
  
  public void setPrice(int price) {
    this.price = price;
  }
  
  public void setDescription(String description) {
    this.description = description;
    this.formated_description.clear();
    String line = "";
    char[] letters = description.toCharArray();
    for (int chr = 0; chr < letters.length; chr++) {
      line = String.valueOf(String.valueOf(line)) + letters[chr];
      if (line.length() == 48) {
        this.formated_description.add(line);
        line = "";
      }
      else if (chr == letters.length - 1) {
        this.formated_description.add(line);
      } 
    } 
  }
  
  public ProtectedRegion getProtectedRegion() {
    if (this.world == null) return null; 
    if (!this.regionManager.hasRegion(this.name)) return null; 
    return this.regionManager.getRegion(this.name);
  }
  
  private void setWorld() {
    ProtectedRegion region = null;
    World world = Bukkit.getWorld(this.world_name);
    if (world != null) {
      region = this.regionManager.getRegion(this.name);
      if (region != null) {
        this.world = world;
      }
    } 
  }
  
  private void setTeleportLocation() {
    ProtectedRegion region = getProtectedRegion();
    if (region != null) {
      int x = (int) region.getMinimumPoint().getX();
      int z = (int) region.getMinimumPoint().getZ();
      this.teleport_location = new Location(this.world, x, this.world.getHighestBlockYAt(x, z), z);
    } 
  }
  
  private void setCoordinates() {
    this.coordinates = "0, 0, 0";
    ProtectedRegion region = getProtectedRegion();
    if (region != null) {
      this.coordinates = (region.getMinimumPoint().getX() + (int)((region.getMaximumPoint().getX() - region.getMinimumPoint().getX()) / 2.0D)) + ", " + (region.getMinimumPoint().getY() + (int)((region.getMaximumPoint().getY() - region.getMinimumPoint().getY()) / 2.0D)) + ", " + (region.getMinimumPoint().getZ() + (int)((region.getMaximumPoint().getZ() - region.getMinimumPoint().getZ()) / 2.0D));
    }
  }
  
  private void setPosition() {
    if (this.teleport_location == null) {
      this.position = StringUtil.colorize(Main.getInstance().getConfig().getString("REGION_POSITIONS.NORMAL"), null, null);
    }
    else if (this.teleport_location.getBlockY() <= 50) {
      this.position = StringUtil.colorize(Main.getInstance().getConfig().getString("REGION_POSITIONS.DOWN"), null, null);
    }
    else if (this.teleport_location.getBlockY() > 50 && this.teleport_location.getBlockY() <= 110) {
      this.position = StringUtil.colorize(Main.getInstance().getConfig().getString("REGION_POSITIONS.UP"), null, null);
    } else {
      
      this.position = StringUtil.colorize(Main.getInstance().getConfig().getString("REGION_POSITIONS.NORMAL"), null, null);
    } 
  }
}
