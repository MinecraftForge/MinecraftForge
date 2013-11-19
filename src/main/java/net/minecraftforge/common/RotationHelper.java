package net.minecraftforge.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockDetectorRail;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockMushroomCap;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTripWireSource;
import net.minecraft.block.BlockVine;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.common.ForgeDirection.*;

public class RotationHelper {
    /**
     * Some blocks have the same rotation.
     * The first of these blocks (sorted by itemID) should be listed as a type.
     * Some of the types aren't actual blocks (helper types).
     */
    private static enum BlockType {
        LOG,
        DISPENSER,
        BED,
        RAIL,
        RAIL_POWERED,
        RAIL_ASCENDING,
        RAIL_CORNER,
        TORCH,
        STAIR,
        CHEST,
        SIGNPOST,
        DOOR,
        LEVER,
        BUTTON,
        REDSTONE_REPEATER,
        TRAPDOOR,
        MUSHROOM_CAP,
        MUSHROOM_CAP_CORNER,
        MUSHROOM_CAP_SIDE,
        VINE,
        SKULL,
        ANVIL
    }

    private static final ForgeDirection[] UP_DOWN_AXES = new ForgeDirection[] { UP, DOWN };
    private static final Map<BlockType, BiMap<Integer, ForgeDirection>> MAPPINGS = new HashMap<BlockType, BiMap<Integer, ForgeDirection>>();

    public static ForgeDirection[] getValidVanillaBlockRotations(Block block)
    {
        return (block instanceof BlockBed || block instanceof BlockPumpkin || block instanceof BlockFenceGate || block instanceof BlockEndPortalFrame || block instanceof BlockTripWireSource || block instanceof BlockCocoa || block instanceof BlockRailPowered || block instanceof BlockDetectorRail || block instanceof BlockStairs || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockFurnace || block instanceof BlockLadder || block.blockID == Block.signWall.blockID || block.blockID == Block.signPost.blockID || block instanceof BlockDoor || block instanceof BlockRail || block instanceof BlockButton || block instanceof BlockRedstoneRepeater || block instanceof BlockComparator || block instanceof BlockTrapDoor || block instanceof BlockMushroomCap || block instanceof BlockVine || block instanceof BlockSkull || block instanceof BlockAnvil) ? UP_DOWN_AXES : VALID_DIRECTIONS;
    }

    public static boolean rotateVanillaBlock(Block block, World worldObj, int x, int y, int z, ForgeDirection axis)
    {
        if (worldObj.isRemote)
        {
            return false;
        }

        if (axis == UP || axis == DOWN)
        {
            if (block instanceof BlockBed || block instanceof BlockPumpkin || block instanceof BlockFenceGate || block instanceof BlockEndPortalFrame || block instanceof BlockTripWireSource || block instanceof BlockCocoa)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.BED);
            }
            if (block instanceof BlockRail)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.RAIL);
            }
            if (block instanceof BlockRailPowered || block instanceof BlockDetectorRail)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.RAIL_POWERED);
            }
            if (block instanceof BlockStairs)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.STAIR);
            }
            if (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockFurnace || block instanceof BlockLadder || block.blockID == Block.signWall.blockID)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.CHEST);
            }
            if (block.blockID == Block.signPost.blockID)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.SIGNPOST);
            }
            if (block instanceof BlockDoor)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.DOOR);
            }
            if (block instanceof BlockButton)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.BUTTON);
            }
            if (block instanceof BlockRedstoneRepeater || block instanceof BlockComparator)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.REDSTONE_REPEATER);
            }
            if (block instanceof BlockTrapDoor)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.TRAPDOOR);
            }
            if (block instanceof BlockMushroomCap)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.MUSHROOM_CAP);
            }
            if (block instanceof BlockVine)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.VINE);
            }
            if (block instanceof BlockSkull)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.SKULL);
            }
            if (block instanceof BlockAnvil)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x1, BlockType.ANVIL);
            }
        }

        if (block instanceof BlockLog)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0xC, BlockType.LOG);
        }
        if (block instanceof BlockDispenser || block instanceof BlockPistonBase || block instanceof BlockPistonExtension || block instanceof BlockHopper)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.DISPENSER);
        }
        if (block instanceof BlockTorch)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.TORCH);
        }
        if (block instanceof BlockLever)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.LEVER);
        }

        return false;
    }

    private static boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis, int mask, BlockType blockType)
    {
        int rotMeta = worldObj.getBlockMetadata(x, y, z);
        if (blockType == BlockType.DOOR && (rotMeta & 0x8) == 0x8)
        {
            return false;
        }
        int masked = rotMeta & ~mask;
        int meta = rotateMetadata(axis, blockType, rotMeta & mask);
        if (meta == -1)
        {
            return false;
        }
        worldObj.setBlockMetadataWithNotify(x, y, z, meta & mask | masked, 3);
        return true;
    }

    private static int rotateMetadata(ForgeDirection axis, BlockType blockType, int meta)
    {
        if (blockType == BlockType.RAIL || blockType == BlockType.RAIL_POWERED)
        {
            if (meta == 0x0 || meta == 0x1)
            {
                return ~meta & 0x1;
            }
            if (meta >= 0x2 && meta <= 0x5)
            {
                blockType = BlockType.RAIL_ASCENDING;
            }
            if (meta >= 0x6 && meta <= 0x9 && blockType == BlockType.RAIL)
            {
                blockType = BlockType.RAIL_CORNER;
            }
        }
        if (blockType == BlockType.SIGNPOST)
        {
            return (axis == UP) ? (meta + 0x4) % 0x10 : (meta + 0xC) % 0x10;
        }
        if (blockType == BlockType.LEVER && (axis == UP || axis == DOWN))
        {
            switch (meta)
            {
            case 0x5:
                return 0x6;
            case 0x6:
                return 0x5;
            case 0x7:
                return 0x0;
            case 0x0:
                return 0x7;
            }
        }
        if (blockType == BlockType.MUSHROOM_CAP)
        {
            if (meta % 0x2 == 0)
            {
                blockType = BlockType.MUSHROOM_CAP_SIDE;
            }
            else
            {
                blockType = BlockType.MUSHROOM_CAP_CORNER;
            }
        }
        if (blockType == BlockType.VINE)
        {
            return ((meta << 1) | ((meta & 0x8) >> 3));
        }

        ForgeDirection orientation = metadataToDirection(blockType, meta);
        ForgeDirection rotated = orientation.getRotation(axis);
        return directionToMetadata(blockType, rotated);
    }

    private static ForgeDirection metadataToDirection(BlockType blockType, int meta)
    {
        if (blockType == BlockType.LEVER)
        {
            if (meta == 0x6)
            {
                meta = 0x5;
            }
            else if (meta == 0x0)
            {
                meta = 0x7;
            }
        }

        if (MAPPINGS.containsKey(blockType))
        {
            BiMap<Integer, ForgeDirection> biMap = MAPPINGS.get(blockType);
            if (biMap.containsKey(meta))
            {
                return biMap.get(meta);
            }
        }

        if (blockType == BlockType.TORCH)
        {
            return ForgeDirection.getOrientation(6 - meta);
        }
        if (blockType == BlockType.STAIR)
        {
            return ForgeDirection.getOrientation(5 - meta);
        }
        if (blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL)
        {
            return ForgeDirection.getOrientation(meta);
        }
        if (blockType == BlockType.BUTTON)
        {
            return ForgeDirection.getOrientation(6 - meta);
        }
        if (blockType == BlockType.TRAPDOOR)
        {
            return ForgeDirection.getOrientation(meta + 2).getOpposite();
        }

        return ForgeDirection.UNKNOWN;
    }

    private static int directionToMetadata(BlockType blockType, ForgeDirection direction)
    {
        if ((blockType == BlockType.LOG || blockType == BlockType.ANVIL) && (direction.offsetX + direction.offsetY + direction.offsetZ) < 0)
        {
            direction = direction.getOpposite();
        }

        if (MAPPINGS.containsKey(blockType))
        {
            BiMap<ForgeDirection, Integer> biMap = MAPPINGS.get(blockType).inverse();
            if (biMap.containsKey(direction))
            {
                return biMap.get(direction);
            }
        }

        if (blockType == BlockType.TORCH)
        {
            if (direction.ordinal() >= 1)
            {
                return 6 - direction.ordinal();
            }
        }
        if (blockType == BlockType.STAIR)
        {
            return 5 - direction.ordinal();
        }
        if (blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL)
        {
            return direction.ordinal();
        }
        if (blockType == BlockType.BUTTON)
        {
            if (direction.ordinal() >= 2)
            {
                return 6 - direction.ordinal();
            }
        }
        if (blockType == BlockType.TRAPDOOR)
        {
            return direction.getOpposite().ordinal() - 2;
        }

        return -1;
    }

    static
    {
        BiMap<Integer, ForgeDirection> biMap;

        biMap = HashBiMap.create(3);
        biMap.put(0x0, UP);
        biMap.put(0x4, EAST);
        biMap.put(0x8, SOUTH);
        MAPPINGS.put(BlockType.LOG, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x0, SOUTH);
        biMap.put(0x1, WEST);
        biMap.put(0x2, NORTH);
        biMap.put(0x3, EAST);
        MAPPINGS.put(BlockType.BED, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x2, EAST);
        biMap.put(0x3, WEST);
        biMap.put(0x4, NORTH);
        biMap.put(0x5, SOUTH);
        MAPPINGS.put(BlockType.RAIL_ASCENDING, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x6, WEST);
        biMap.put(0x7, NORTH);
        biMap.put(0x8, EAST);
        biMap.put(0x9, SOUTH);
        MAPPINGS.put(BlockType.RAIL_CORNER, biMap);

        biMap = HashBiMap.create(6);
        biMap.put(0x1, EAST);
        biMap.put(0x2, WEST);
        biMap.put(0x3, SOUTH);
        biMap.put(0x4, NORTH);
        biMap.put(0x5, UP);
        biMap.put(0x7, DOWN);
        MAPPINGS.put(BlockType.LEVER, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x0, WEST);
        biMap.put(0x1, NORTH);
        biMap.put(0x2, EAST);
        biMap.put(0x3, SOUTH);
        MAPPINGS.put(BlockType.DOOR, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x0, NORTH);
        biMap.put(0x1, EAST);
        biMap.put(0x2, SOUTH);
        biMap.put(0x3, WEST);
        MAPPINGS.put(BlockType.REDSTONE_REPEATER, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x1, EAST);
        biMap.put(0x3, SOUTH);
        biMap.put(0x7, NORTH);
        biMap.put(0x9, WEST);
        MAPPINGS.put(BlockType.MUSHROOM_CAP_CORNER, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x2, NORTH);
        biMap.put(0x4, WEST);
        biMap.put(0x6, EAST);
        biMap.put(0x8, SOUTH);
        MAPPINGS.put(BlockType.MUSHROOM_CAP_SIDE, biMap);

        biMap = HashBiMap.create(2);
        biMap.put(0x0, SOUTH);
        biMap.put(0x1, EAST);
        MAPPINGS.put(BlockType.ANVIL, biMap);
    }
}
