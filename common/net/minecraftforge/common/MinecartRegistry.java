package net.minecraftforge.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MinecartRegistry
{
    private static Map<MinecartKey, ItemStack> itemForMinecart = new HashMap<MinecartKey, ItemStack>();
    private static Map<ItemStack, MinecartKey> minecartForItem = new HashMap<ItemStack, MinecartKey>();
    /**
     * Registers a custom minecart and its corresponding item.
     * This should be the item used to place the minecart by the user,
     * not the item dropped by the cart.
     * @param cart The minecart.
     * @param item The item used to place the cart.
     */
    public static void registerMinecart(Class<? extends EntityMinecart> cart, ItemStack item)
    {
        registerMinecart(cart, 0, item);
    }

    /**
     * Registers a minecart and its corresponding item.
     * This should be the item used to place the minecart by the user,
     * not the item dropped by the cart.
     * @param minecart The minecart.
     * @param type The minecart type, used to differentiate carts that have the same class.
     * @param item The item used to place the cart.
     */
    public static void registerMinecart(Class<? extends EntityMinecart> minecart, int type, ItemStack item)
    {
        MinecartKey key = new MinecartKey(minecart, type);
        itemForMinecart.put(key, item);
        minecartForItem.put(item, key);
    }

    /**
     * Removes a previously registered Minecart. Useful for replacing the vanilla minecarts.
     * @param minecart
     * @param type
     */
    public static void removeMinecart(Class<? extends EntityMinecart> minecart, int type)
    {
        MinecartKey key = new MinecartKey(minecart, type);
        ItemStack item = itemForMinecart.remove(key);
        if (item != null)
        {
            minecartForItem.remove(item);
        }
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * The player should be able to use this item to place the minecart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @param minecart The cart class
     * @return An ItemStack that can be used to place the cart.
     */
    public static ItemStack getItemForCart(Class<? extends EntityMinecart> minecart)
    {
        return getItemForCart(minecart, 0);
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * The player should be able to use this item to place the minecart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @param minecart The cart class
     * @param type The minecartType value
     * @return An ItemStack that can be used to place the cart.
     */
    public static ItemStack getItemForCart(Class<? extends EntityMinecart> minecart, int type)
    {
        ItemStack item = itemForMinecart.get(new MinecartKey(minecart, type));
        if (item == null)
        {
            return null;
        }
        return item.copy();
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * The player should be able to use this item to place the minecart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @param cart The cart entity
     * @return An ItemStack that can be used to place the cart.
     */
    public static ItemStack getItemForCart(EntityMinecart cart)
    {
        return getItemForCart(cart.getClass(), cart.getMinecartType());
    }

    /**
     * The function will return the cart class for a given item.
     * If the item was not registered via the registerMinecart function it will return null.
     * @param item The item to test.
     * @return Cart if mapping exists, null if not.
     */
    public static Class<? extends EntityMinecart> getCartClassForItem(ItemStack item)
    {
        MinecartKey key = null;
        for (Map.Entry<ItemStack, MinecartKey> entry : minecartForItem.entrySet())
        {
            if (entry.getKey().isItemEqual(item))
            {
                key = entry.getValue();
                break;
            }
        }
        if (key != null)
        {
            return key.minecart;
        }
        return null;
    }

    /**
     * The function will return the cart type for a given item.
     * Will return -1 if the mapping doesn't exist.
     * If the item was not registered via the registerMinecart function it will return null.
     * @param item The item to test.
     * @return the cart minecartType value.
     */
    public static int getCartTypeForItem(ItemStack item)
    {
        MinecartKey key = null;
        for (Map.Entry<ItemStack, MinecartKey> entry : minecartForItem.entrySet())
        {
            if (entry.getKey().isItemEqual(item))
            {
                key = entry.getValue();
                break;
            }
        }
        if (key != null)
        {
            return key.type;
        }
        return -1;
    }

    /**
     * Will return a set of all registered minecart items.
     * @return a copy of the set of all minecart items
     */
    public static Set<ItemStack> getAllCartItems()
    {
        Set<ItemStack> ret = new HashSet<ItemStack>();
        for (ItemStack item : minecartForItem.keySet())
        {
            ret.add(item.copy());
        }
        return ret;
    }
    
    static
    {
        registerMinecart(EntityMinecart.class, 0, new ItemStack(Item.minecartEmpty));
        registerMinecart(EntityMinecart.class, 1, new ItemStack(Item.minecartCrate));
        registerMinecart(EntityMinecart.class, 2, new ItemStack(Item.minecartPowered));
    }
    
    public static class MinecartKey
    {
        public final Class<? extends EntityMinecart> minecart;
        public final int type;

        public MinecartKey(Class<? extends EntityMinecart> cls, int typtID)
        {
            minecart = cls;
            type = typtID;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }

            if (getClass() != obj.getClass())
            {
                return false;
            }

            final MinecartKey other = (MinecartKey)obj;
            if (this.minecart != other.minecart && (this.minecart == null || !this.minecart.equals(other.minecart)))
            {
                return false;
            }

            return (this.type == other.type);
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 59 * hash + (this.minecart != null ? this.minecart.hashCode() : 0);
            hash = 59 * hash + this.type;
            return hash;
        }
    }
}
