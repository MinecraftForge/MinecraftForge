package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Allows for custom entity data and logic to be hooked to existing entity classes.
 *
 * @author cpw, mithion
 *
 */
public interface IExtendedEntityProperties {
    /**
     * Called when the entity that this class is attached to is saved.
     * Any custom entity data  that needs saving should be saved here.
     * @param compound The compound to save to.
     */
    public void saveNBTData(NBTTagCompound compound);

    /**
     * Called when the entity that this class is attached to is loaded.
     * In order to hook into this, you will need to subscribe to the EntityConstructing event.
     * Otherwise, you will need to initialize manually.
     * @param compound The compound to load from.
     */
    public void loadNBTData(NBTTagCompound compound);

    /**
     * Used to initialize the extended properties with the entity that this is attached to, as well
     * as the world object.
     * Called automatically if you register with the EntityConstructing event.
     * May be called multiple times if the extended properties is moved over to a new entity.
     *  Such as when a player switches dimension {Minecraft re-creates the player entity}
     * @param entity  The entity that this extended properties is attached to
     * @param world  The world in which the entity exists
     */
    public void init(Entity entity, World world);
}
