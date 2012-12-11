package cpw.mods.fml.common.registry;

import net.minecraft.entity.Entity;

/**
 * This interface should be implemented by an Entity that can be 'thrown', like snowballs.
 * This was created to mimic ModLoaderMP's 'owner' functionality.
 */
public interface IThrowableEntity
{
    /**
     * Gets the entity that threw/created this entity.
     * @return The owner instance, Null if none.
     */
    public Entity getThrower();

    /**
     * Sets the entity that threw/created this entity.
     * @param entity The new thrower/creator.
     */
    public void setThrower(Entity entity);
}
