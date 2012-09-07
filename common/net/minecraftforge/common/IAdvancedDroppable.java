package net.minecraftforge.common;

import net.minecraft.src.Entity;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

/**
 * This interface can be implemented by an instance of Item.
 * It allows for an entity to replace the normal floating EntityItem
 * when an item is dropped or otherwise spawned into the world
 * (including ejected from a container).
 */
public interface IAdvancedDroppable {

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to function normally.
     * Called when the item it placed in a world.
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of the entity
     * @param itemstack The
     * @return A new Entity object to spawn or null
     */
    public Entity createEntity(World world, Entity location, ItemStack itemstack);
}
