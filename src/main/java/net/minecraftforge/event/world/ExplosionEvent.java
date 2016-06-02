package net.minecraftforge.event.world;

import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/** ExplosionEvent triggers when an explosion happens in the world.<br>
 * <br>
 * ExplosionEvent.Start is fired before the explosion actually occurs.<br>
 * ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.<br>
 * <br>
 * ExplosionEvent.Start is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * ExplosionEvent.Detonate can modify the affected blocks and entities.<br>
 * Children do not use {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}.<br>
 * Children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 */
public class ExplosionEvent extends Event
{
    private final World world;
    private final Explosion explosion;

    public ExplosionEvent(World world, Explosion explosion)
    {
        this.world = world;
        this.explosion = explosion;
    }

    public World getWorld()
    {
        return world;
    }

    public Explosion getExplosion()
    {
        return explosion;
    }

    /** ExplosionEvent.Start is fired before the explosion actually occurs.  Canceling this event will stop the explosion.<br>
     * <br>
     * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * This event does not use {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}.<br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     */
    @Cancelable
    public static class Start extends ExplosionEvent
    {
        public Start(World world, Explosion explosion)
        {
            super(world, explosion);
        }
    }

    /** ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.  These lists can be modified to change the outcome.<br>
     * <br>
     * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * This event does not use {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}.<br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     */
    public static class Detonate extends ExplosionEvent
    {
        private final List<Entity> entityList;

        public Detonate(World world, Explosion explosion, List<Entity> entityList)
        {
            super(world, explosion);
            this.entityList = entityList;
        }

        /** return the list of blocks affected by the explosion. */
        public List<BlockPos> getAffectedBlocks()
        {
            return getExplosion().getAffectedBlockPositions();
        }

        /** return the list of entities affected by the explosion. */
        public List<Entity> getAffectedEntities()
        {
            return entityList;
        }
    }
}