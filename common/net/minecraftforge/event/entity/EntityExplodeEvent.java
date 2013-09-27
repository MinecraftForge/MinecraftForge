package net.minecraftforge.event.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

/**
 * Called when an entity explodes
 * @author HoBoS-TaCo
 */
@Cancelable
public class EntityExplodeEvent extends EntityEvent
{
    /** The explosion triggered. */
    public final Explosion explosion;
    /** A list of entities which can be affected affected by the explosion. */
    public final List<Entity> affectedEntities;

    /**
     * Called when an entity explodes.
     * <br>This event is Cancelable.
     * @param explosion the explosion triggered.
     * @param affectedEntities list of entities which can be affected affected by the explosion.
     */
    public EntityExplodeEvent(Explosion explosion, List affectedEntities)
    {
        super(explosion.exploder);
        this.explosion = explosion;
        this.affectedEntities = affectedEntities;
    }
}
