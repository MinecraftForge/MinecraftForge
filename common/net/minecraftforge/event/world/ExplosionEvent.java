package net.minecraftforge.event.world;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class ExplosionEvent extends Event {

    public final Entity entity;
    public final double x;
    public final double y;
    public final double z;
    public final float strength;
    public final boolean isFlaming;
    public final boolean destroysBlocks;
    
    public ExplosionEvent(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean destroysBlocks) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.strength = strength;
        this.isFlaming = isFlaming;
        this.destroysBlocks = destroysBlocks;
    }
}
