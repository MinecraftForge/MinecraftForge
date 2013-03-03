package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

@Event.HasResult
public class LightningGeneratedEvent extends WorldEvent {
	/**
	 * This event is fired when a lightning is about to be spawned.
	 * Setting result to DENY will suppress creation of lightning.
	 */

    public final int x;
    public final int y;
    public final int z;

    public LightningGeneratedEvent(World world, int x, int y, int z) {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
