package ru.galaxy773.buyregions.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import ru.galaxy773.buyregions.Main;

public class RegionData {
    private static List<Region> regions = new ArrayList<>();

    public static List<Region> getBuyableRegions() {// BUYABLE!!! BLYAT, NO CLOWNING SYKA
        return regions;
    }

    public static int getPages() {
        double regions_amount = regions.size();
        double pagez = regions_amount / 36.0D;
        int pages = (int)pagez;
        if (pagez != pages) { // за такие проверки белку можно назвать говнокодером ебаным, долбоёб сраный, если чё она во всех деобфах...
             pages++;
        }
        return pages;
    }

    public static void registerRegion(Region region) {
        Main.getInstance().getSQLConnection().execute("INSERT OR IGNORE INTO regions (owner, name, display_name, description, date_time, world, price) VALUES (?, ?, ?, ?, ?, ?, ?)", new Object[]{region.getOwner(), region.getName(), region.getDisplayName(), region.getDescription(), String.valueOf(region.getDate()) + " " + region.getTime(), region.getWorldName(), region.getPrice()});
        regions.add(region);
    }

    public static void setDescription(Region region, String description) {
        Main.getInstance().getSQLConnection().execute("UPDATE regions SET description = ?", description);
        region.setDescription(description);
    }

    public static void setPrice(Region region, int price) {
        Main.getInstance().getSQLConnection().execute("UPDATE regions SET price = ?", price);
        region.setPrice(price);
    }

    public static boolean regionIsBuy(String region_name) {
        for (Region region : regions) if (region.getName().equalsIgnoreCase(region_name)) return true;
        return false;
    }

    public static List<Region> getPlayerRegions(Player p) {
        List<Region> player_regions = new ArrayList<>();
        for (Region region : regions) if (region.getOwner().equalsIgnoreCase(p.getName())) player_regions.add(region);
        return player_regions;
    }

    public static void unregisterRegion(Region region) {
        Main.getInstance().getSQLConnection().execute("DELETE FROM regions WHERE name = ?", region.getName());
        regions.remove(region);
    }

    public static void loadBuyableRegions() {// BUYABLE!!! BLYAT, NO CLOWNING SYKA
        ResultSet rs = Main.getInstance().getSQLConnection().executeQuery("SELECT * FROM regions");

        try {
            while(rs.next()) {
                String owner = rs.getString("owner");
                String name = rs.getString("name");
                String display_name = rs.getString("display_name");
                String description = rs.getString("description");
                if (description == null || description.isEmpty()) {
                    description = "нету";
                }

                String date_time = rs.getString("date_time");
                String world_name = rs.getString("world");
                int price = rs.getInt("price");
                regions.add(new Region(owner, name, display_name, date_time, description, world_name, price));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void clearBuyableRegions() { // BUYABLE!!! BLYAT, NO CLOWNING SYKA
        Main.getInstance().getSQLConnection().execute("DELETE FROM regions");
        regions.clear();
    }
}
