package net.minecraftforge.event.world;

import net.minecraft.src.World;
import net.minecraft.src.Block;

@Cancelable
@Event.HasResult
public class LavaWaterCollisionEvent extends Event
{
    /**
     * This event is fired when lava and water collide.
     * By default this would create either cobblestone, stone or obsidian, depending on the circumstances.
     * The block that would be created if the event were not handled is provided as the defaultBlock field.
     * 
     * You can cancel the event to prevent any processing, or set the result to ALLOW
     * and mark the collision as handled.
     * 
     * An example of when this would be useful is a new dimension that adds a new stone block.
     * By handling this event correctly, 'cobblestone generators' in the dimension would create the new stone,
     * instead of cobblestone.
     */

    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final Block defaultResult;

    public LavaWaterCollisionEvent(World world, int x, int y, int z, Block defaultResult)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.defaultResult = defaultResult;
    }
}
