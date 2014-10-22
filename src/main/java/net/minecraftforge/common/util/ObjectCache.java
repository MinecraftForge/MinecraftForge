package net.minecraftforge.common.util;

import java.util.function.Function;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

/**
 * Used for caching generated values when needed instead of while loading.
 * Takes some work off the machine during load time and can save some processing power and memory.
 * 
 * @author egg82
 *
 * @param <K> key
 * @param <V> value
 */
public class ObjectCache<K, V> {
    //vars
    /**
     * HashMap because speed.
     */
    private TMap<K, V> cache = new THashMap<K, V>();
    /**
     * Method to call to create the value from the key.
     */
    private Function<K, V> getFunc = null;
    
    //constructor
    public ObjectCache(Function<K, V> getFunc) {
        //this would break things
        if (getFunc == null) {
            throw new Error("Parameter calcFunc cannot be null!");
        }
        
        this.getFunc = getFunc;
    }
    
    //public
    /**
     * Manually add objects to the cache.
     * This may need to be used inside the "getFunc" method in some cases.
     * 
     * @param key key
     * @param value value
     */
    public void addObject(K key, V value) {
        if (cache.containsKey(key)) {
            return;
        }
        
        cache.put(key, value);
    }
    /**
     * get/create the cache object using the getfunc supplied in the constructor.
     * 
     * @param key key
     * @return value
     */
    public V getObject(K key) {
        V val = cache.get(key);
        
        if (val != null) {
            return val;
        }
        
        val = getFunc.apply(key);
        cache.put(key, val);
        
        return val;
    }
    /**
     * Gets the length of the cache.
     * Useful for debugging.
     * 
     * @return cache length
     */
    public int length() {
        return cache.size();
    }
    
    //private
    
}