package net.minecraftforge.event.world;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

/**
 * Event for when a block determines the items to drop. The array can be modified, or the event itself canceled.
 * @author dmillerw
 *
 */
@Cancelable
public class BlockDropsEvent extends WorldEvent
{
    public final World world;
    public final ArrayList<ItemStack> drops;
    public final int x;
    public final int y;
    public final int z;
    public final int metadata;
    public final int fortune;
    
    public BlockDropsEvent(World world, ArrayList<ItemStack> drops, int x, int y, int z, int metadata, int fortune)
    {
        super(world);
        this.world = world;
        this.drops = drops;
        this.x = x;
        this.y = y;
        this.z = z;
        this.metadata = metadata;
        this.fortune = fortune;
    }
}
