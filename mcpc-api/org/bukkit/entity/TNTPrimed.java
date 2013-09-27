package org.bukkit.entity;

/**
 * Represents a Primed TNT.
 */
public interface TNTPrimed extends Explosive {
    /**
     * Set the number of ticks until the TNT blows up after being primed.
     *
     * @param fuseTicks The fuse ticks
     */
    public void setFuseTicks(int fuseTicks);

    /**
     * Retrieve the number of ticks until the explosion of this TNTPrimed
     * entity
     *
     * @return the number of ticks until this TNTPrimed explodes
     */
    public int getFuseTicks();

    /**
     * Gets the source of this primed TNT. The source is the entity
     * responsible for the creation of this primed TNT.
     * (I.E. player ignites TNT with flint and steel.) Take note
     * that this can be null if there is no suitable source.
     * (created by the {@link org.bukkit.World#spawn(Location, Class)}
     * method, for example.)
     *
     * The source will become null if the chunk this primed TNT is in
     * is unloaded then reloaded. If the source Entity becomes invalidated
     * for any reason, such being removed from the world, the returned value
     * will be null.
     *
     * @return the source of this primed TNT
     */
    public Entity getSource();
}
