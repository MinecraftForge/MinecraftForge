package net.minecraftforge.common;

import static net.minecraftforge.common.BlockItemizeHelper.BlockStyle.*;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.BlockMushroomCap;
import net.minecraft.block.BlockNetherStalk;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTripWire;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockItemizeHelper
{
    public static enum BlockStyle {
        // No value has been cached. This is the default value.
        UNDECIDED,
        // Do not return an ItemStack.
        NOTHING,
        // Call Block.getDamageValue to get the ItemStack
        USE_GET_DAMAGE_VALUE,
        // Call Block.idDropped to get the ItemStack
        USE_ID_DROPPED,
        // Return the first thing from Block.getBlockDropped.
        USE_GET_BLOCK_DROPPED,
        // Return the block with its metadata
        CLONE_MD,

        // These blocks do weird things and need to be special-cased
        STEM,
        SLAB,
        CAKE,
        CROP,
        DOOR,
        REDSTONE_ORE,
        PISTON_EXTENSION,
        BED
    }

    static BlockStyle getBlockStyle(Block block)
    {
        if (block.placingItemStyle == UNDECIDED)
        {
            block.placingItemStyle = classifyBlock(block);
        }
        return block.placingItemStyle;
    }

    private static BlockStyle classifyBlock(Block block)
    {
        // These blocks don't have a good way of extracting the item,
        // so no instanceof.
        if (block == Block.cake)
        {
            return CAKE;
        }
        if (block == Block.oreRedstone || block == Block.oreRedstoneGlowing)
        {
            return REDSTONE_ORE;
        }
        if (block == Block.pistonExtension)
        {
            return PISTON_EXTENSION;
        }
        if (block == Block.melonStem || block == Block.pumpkinStem)
        {
            return STEM;
        }
        if (block instanceof BlockSign || block instanceof BlockFlowerPot || block instanceof BlockRedstoneWire || block instanceof BlockBrewingStand
                || block instanceof BlockReed || block instanceof BlockTripWire || block instanceof BlockCauldron || block instanceof BlockRedstoneRepeater
                || block instanceof BlockComparator || block instanceof BlockRedstoneTorch || block instanceof BlockFarmland || block instanceof BlockFurnace
                || block instanceof BlockMushroomCap || block instanceof BlockRedstoneLight)
        {
            return USE_ID_DROPPED;
        }
        if (block instanceof BlockCocoa || block instanceof BlockNetherStalk || block instanceof BlockSkull)
        {
            return USE_GET_BLOCK_DROPPED;
        }
        if (block instanceof BlockPistonMoving || block instanceof BlockPortal || block instanceof BlockEndPortal || block instanceof BlockSilverfish
                || block instanceof BlockMobSpawner)
        {
            return NOTHING;
        }
        if (block instanceof BlockOre)
        {
            return CLONE_MD;
        }
        // Special blocks
        if (block instanceof BlockHalfSlab)
        {
            return SLAB;
        }
        if (block instanceof BlockCrops)
        {
            return CROP;
        }
        if (block instanceof BlockBed)
        {
            return BED;
        }
        if (block instanceof BlockDoor)
        {
            return DOOR;
        }
        return USE_GET_DAMAGE_VALUE;
    }

    private static ItemStack makeItemStack(int itemId, int stackSize, int damage)
    {
        if (itemId == 0)
        {
            return null;
        }
        return new ItemStack(itemId, stackSize, damage);
    }

    public static ItemStack getPlacingItem(Block block, MovingObjectPosition target, World world, int x, int y, int z)
    {
        int md;
        switch (classifyBlock(block))
        {
            default:
            case UNDECIDED:
            case NOTHING:
            case PISTON_EXTENSION:
                return null;
            case USE_GET_DAMAGE_VALUE:
                return new ItemStack(block, 1, block.getDamageValue(world, x, y, z));
            case USE_ID_DROPPED:
            case BED:
                md = world.getBlockMetadata(x, y, z);
                return makeItemStack(block.idDropped(md, world.rand, 0), 1, 0);
            case USE_GET_BLOCK_DROPPED:
                md = world.getBlockMetadata(x, y, z);
                ArrayList<ItemStack> drops = block.getBlockDropped(world, x, y, z, md, 0);
                if (drops.isEmpty())
                {
                    return null;
                }
                return drops.get(0);
            case CLONE_MD:
                md = world.getBlockMetadata(x, y, z);
                return new ItemStack(block, 1, md);
            case STEM:
                if (block == Block.pumpkinStem)
                {
                    return new ItemStack(Item.pumpkinSeeds);
                }
                else if (block == Block.melonStem)
                {
                    return new ItemStack(Item.melonSeeds);
                }
                else
                {
                    return null;
                }
            case SLAB:
                md = world.getBlockMetadata(x, y, z);
                int slabId = block.idDropped(md, world.rand, 0);
                int dropped = block.quantityDropped(world.rand);
                return makeItemStack(slabId, dropped, block.damageDropped(md));
            case CAKE:
                md = world.getBlockMetadata(x, y, z);
                return md == 0 ? new ItemStack(Item.cake) : null;
            case CROP:
                return new ItemStack(block.idDropped(0, world.rand, 0), 1, block.getDamageValue(world, x, y, z));
            case DOOR:
                md = world.getBlockMetadata(x, y, z);
                int doorId = block.idDropped(md, world.rand, 0);
                if (doorId == 0)
                {
                    return null;
                }
                return new ItemStack(doorId, 1, 0);
            case REDSTONE_ORE:
                return new ItemStack(Block.oreRedstone);
        }
    }

    public static ItemStack getDescribingItem(Block block, MovingObjectPosition target, World world, int x, int y, int z)
    {
        int md;
        switch (classifyBlock(block))
        {
            case CAKE:
                return new ItemStack(Item.cake);
            case CROP:
                return new ItemStack(block.idDropped(0, world.rand, 0), 1, block.getDamageValue(world, x, y, z));
            case DOOR:
            case BED:
                int id = block.idDropped(0, world.rand, 0);
                if (id == 0)
                {
                    return null;
                }
                return new ItemStack(id, 1, 0);
            case PISTON_EXTENSION:
                md = world.getBlockMetadata(x, y, z);
                return new ItemStack((md & 8) != 0 ? Block.pistonStickyBase : Block.pistonBase);
            default:
                return getPlacingItem(block, target, world, x, y, z);
        }
    }
}
