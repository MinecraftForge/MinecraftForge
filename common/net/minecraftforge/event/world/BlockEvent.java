package net.minecraftforge.event.world;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class BlockEvent extends Event {
    public final int x;
    public final int y;
    public final int z;
    public final World world;
    public final Block block;
    public final int blockMetadata;
    public BlockEvent(int x, int y, int z, World world, Block block, int blockMetadata)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.block = block;
        this.blockMetadata = blockMetadata;
    }
    
    /**
     * Fired when a block is about to drop it's harvested items. The {@link #drops} array can be amended, as can the {@link #dropChance}.
     * <strong>Note well:</strong> the {@link #harvester} player field is null in a variety of scenarios. Code expecting null.
     * 
     * The {@link #dropChance} is used to determine which items in this array will actually drop, compared to a random number. If you wish, you 
     * can pre-filter yourself, and set {@link #dropChance} to 1.0f to always drop the contents of the {@link #drops} array.
     * 
     * {@link #isSilkTouching} is set if this is considered a silk touch harvesting operation, vs a normal harvesting operation. Act accordingly.
     * 
     * @author cpw
     */
    public static class HarvestDropsEvent extends BlockEvent {
        public final int fortuneLevel;
        public final ArrayList<ItemStack> drops;
        public final boolean isSilkTouching;
        public float dropChance; // Change to e.g. 1.0f, if you manipulate the list and want to guarantee it always drops
        public final EntityPlayer harvester; // May be null for non-player harvesting such as explosions or machines

        public HarvestDropsEvent(int x, int y, int z, World world, Block block, int blockMetadata, int fortuneLevel, float dropChance, ArrayList<ItemStack> drops, EntityPlayer harvester, boolean isSilkTouching)
        {
            super(x, y, z, world, block, blockMetadata);
            this.fortuneLevel = fortuneLevel;
            this.dropChance = dropChance;
            this.drops = drops;
            this.isSilkTouching = isSilkTouching;
            this.harvester = harvester;
        }
    }

}
