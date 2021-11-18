package ru.galaxy773.buyregions;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.galaxy773.buyregions.data.RegionData;
import ru.galaxy773.buyregions.sql.SQLConnection;
import ru.galaxy773.buyregions.utils.TimeUtil;

public class Main extends JavaPlugin {
    private static Main instance;
    public Economy economy;
    public WorldGuardPlugin wg;
    public SQLConnection connection;
    private boolean clear;

    public static Main getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public WorldGuardPlugin getWorldGuard() {
        return this.wg;
    }

    public SQLConnection getSQLConnection() {
        return this.connection;
    }

    @Override
    public void onEnable() {
        instance = this;
        System.out.println("GalaxyBuyRegions update to last version MineCraft - 1.12.2 loading...");
        getConfig().options().copyDefaults(true);
        saveConfig();
        sqlConnection();
        connectEconomy();
        connectWorldGuard();
        new RegionsGUI();
        RegionData.loadBuyableRegions();
        getServer().getPluginManager().registerEvents(new EventListener(), (Plugin)this);
        getCommand("ndom").setExecutor(new Commands());
        this.clear = false;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (Main.this.clear) {
                    RegionData.clearBuyableRegions();
                } else {
                    Main.this.clear = true;
                }
            }
        }, 0L, TimeUtil.getTimeLeftInSeconds() * 20);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public void connectEconomy() {
        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        if (economy == null) {
            System.out.println("Economy plugin not found, plugin has been disable");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void connectWorldGuard() {
        wg = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null) {
            System.out.println("WorldGuard plugin not found, plugin has been disable");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void sqlConnection() {
        (connection = new SQLConnection()).openConnection();
        connection.execute("CREATE TABLE IF NOT EXISTS regions (owner VARCHAR(16) NOT NULL, name VARCHAR(48) NOT NULL UNIQUE, display_name VARCHAR(32) NOT NULL, description VARCHAR(256), date_time VARCHAR(20) NOT NULL, world VARCHAR(32) NOT NULL, price INT NOT NULL)");
    }

}
