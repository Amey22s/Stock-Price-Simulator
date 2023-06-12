package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class which is used to cache data fetched by the api using a map.
 * Here map has key as stock symbol and value is a string list which holds the data fetched by api
 * for that stock.
 */
class Cache {
  private final Map<String, List<String>> cacheMap;

  /**
   * Default constructor to create an object of class Cache and initialize a new HashMap.
   */
  Cache() {
    cacheMap = new HashMap<>();
  }

  void put(String key, List<String> value) {
    cacheMap.put(key, value);
  }

  List<String> get(String key) {
    return cacheMap.get(key);
  }

  boolean containsKey(String key) {
    return cacheMap.containsKey(key);
  }
}
