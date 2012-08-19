package net.minecraftforge.event.block;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

/**
 * This event is used to check if the block can stay/be placed on certain block(used by catus, flowers, torches)
 *
 * @author battlefield
 *
 */

@Cancelable
public class CanBlockStayCheckEvent extends BlockEvent
{
    public boolean canBePlaced = false;
    public World world;
    public int x;
    public int y;
    public int z;

    public CanBlockStayCheckEvent(Block block, World world, int x, int y, int z)
    {
        super(block);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setCanPlace(boolean b)
    {
        canBePlaced = b;
    }

    public boolean canBePlaced()
    {
        return canBePlaced;
    }
}
