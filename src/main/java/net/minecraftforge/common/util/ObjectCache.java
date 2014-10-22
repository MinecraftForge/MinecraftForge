package net.minecraftforge.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for caching generated values when needed instead of while loading.
 * Takes some work off the machine during load time and can save some processing power and memory.
 * 
 * Using generic types because they're created at compile-time rather than run-time.
 * 
 * @author egg82
 *
 * @param <E> key
 * @param <T> value
 */
public class ObjectCache<E, T> {
    //vars
    /**
     * HashMap because speed.
     */
    private Map<E, T> cache = new HashMap<E, T>();
    /**
     * Method to call to create the value from the key.
     */
    private Method getFunc = null;
    
    //constructor
    public ObjectCache(Method getFunc) {
        //this would break things
        if (getFunc == null) {
            throw new Error("Parameter calcFunc cannot be null!");
        }
        //this would also break things
        if (getFunc.getParameterTypes().length != 1) {
            throw new Error("calcFunc must take only one parameter.");
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
    public void addObject(E key, T value) {
        //not sure if this is the absolute best way of handling things
        //but it doesn't throw an error, so hey.
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
    public T getObject(E key) {
        if (!cache.containsKey(key)) {
            //try/catch is slow, but there's not a way around it.
            //Better here than making the function throw something and have a try/catch on every execution
            try {
                cache.put(key, (T) getFunc.invoke(this, key));
            } catch (Exception e) {
                //stop, you did something wrong
                //better to throw an Error here for automatic IDE debugging
                //obviously in practice this should never happen, so it's alright if it's slow
                throw new Error(e);
            }
        }
        
        return cache.get(key);
    }
    /**
     * Manually remove a cache object.
     * Probably won't need to be used, but it's nice to have.
     * 
     * @param key key
     */
    public void removeObject(E key) {
        //not sure if this is the absolute best way of handling things
        //but it doesn't throw an error, so hey.
        if (!cache.containsKey(key)) {
            return;
        }
        
        cache.remove(key);
    }
    /**
     * Clear the entire cache.
     * Probably won't need to be used, but it's nice to have.
     * 
     * Doesn't release memory associated with the cache, so be careful.
     */
    public void clear() {
        cache.clear();
    }
    /**
     * Gets the length of the cache.
     * Probably won't need to be used, but it's nice to have.
     * 
     * @return cache length
     */
    public int length() {
        return cache.size();
    }
    
    //private
    
}