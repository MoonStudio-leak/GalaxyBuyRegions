package ru.galaxy773.buyregions.requests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.galaxy773.buyregions.data.Region;

public class BuyRequest {

    private static Map<String, Region> requests = new HashMap<>();

    public static Region getRequest(String p) {
        return requests.get(p);
    }

    public static void sendRequest(String p, Region region) {
        requests.put(p, region);
    }

    public static void removeRequest(String p) {
        requests.remove(p);
    }

    public static void removeRegionRequests(Region region) {
        requests.keySet().stream().filter(name -> requests.get(name).getName().equalsIgnoreCase(region.getName())).forEach(name -> requests.remove(name)); // новый синтаксис, ГЕЛЕКСИ КОДЕР НЕ СЛЫШАЛ????
    }

}