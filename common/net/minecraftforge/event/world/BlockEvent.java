package net.minecraftforge.event.world;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;

public class BlockEvent extends Event 
{
    /** x coordinate of the block */
    public final int x;
    /** y coordinate of the block */
    public final int y;
    /** z coordinate of the block */
    public final int z;
    /** World containing the block */
    public final World world;
    /** The block */
    public final Block block;
    /** The block's metadata */
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
    public static class HarvestDropsEvent extends BlockEvent 
    {
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
    
    /**
     * Fired when an block is about to be placed.
     * @author HoBoS_TaCo
     */
    @Cancelable
    public static class BlockPlaceEvent extends BlockEvent 
    {
        /** The player placing the block. */
        public final EntityPlayer entityplayer;
        /**
         * The side of another block the player has clicked to place this block.<br>
         * eg placing a block on a wall will require the wall to be right clicked.
         */
        public final int side;
        /** x coordinate of the player's right click */
        public final float hitX;
        /** y coordinate of the player's right click */
        public final float hitY;
        /** z coordinate of the player's right click */
        public final float hitZ;

        /**
         * Fired when an block is about to be placed.
         */
        public BlockPlaceEvent(World world, EntityPlayer entityplayer, Block block, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
        {
            super(x, y, z, world, block, world.getBlockMetadata(x, y, z));
            this.entityplayer = entityplayer;
            this.side = side;
            this.hitX = hitX;
            this.hitY = hitY;
            this.hitZ = hitZ;
        }
    }
    
    /**
     * Fired when an block is about to be broken.
     * @author HoBoS_TaCo
     */
    @Cancelable
    public static class BlockBreakEvent extends BlockEvent 
    {
        /** The player breaking the block. */
        public final EntityPlayerMP entityplayermp;

        /** Fired when an block is about to be broken. */
        public BlockBreakEvent(World world, EntityPlayerMP entityplayermp, Block block, int x, int y, int z)
        {
            super(x, y, z, world, block, world.getBlockMetadata(x, y, z));
            this.entityplayermp = entityplayermp;
        }
    }
}
