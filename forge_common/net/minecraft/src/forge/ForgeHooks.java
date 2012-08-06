/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import net.minecraft.src.EnumStatus;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraft.src.mod_MinecraftForge;
import net.minecraft.src.forge.packets.PacketEntitySpawn;
import net.minecraft.src.forge.packets.PacketHandlerBase;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class ForgeHooks
{

    public static void plantGrassPlant(World world, int x, int y, int z)
    {
        int index = world.rand.nextInt(plantGrassWeight);
        ProbableItem item = getRandomItem(plantGrassList, index);
        if (item == null || Block.blocksList[item.ItemID] == null)
        {
            return;
        }
        if (mod_MinecraftForge.DISABLE_DARK_ROOMS && !Block.blocksList[item.ItemID].canBlockStay(world, x, y, z))
        {
            return;
        }
        world.setBlockAndMetadataWithNotify(x, y, z, item.ItemID, item.Metadata);
    }

    public static ItemStack getGrassSeed(World world)
    {
        int index = world.rand.nextInt(seedGrassWeight);
        ProbableItem item = getRandomItem(seedGrassList, index);
        if (item == null)
        {
            return null;
        }
        return new ItemStack(item.ItemID, item.Quantity, item.Metadata);
    }

    // Tool Path
    // ------------------------------------------------------------
    public static boolean canHarvestBlock(Block block, EntityPlayer player, int metadata)
    {
        if (block.blockMaterial.isHarvestable())
        {
            return true;
        }
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null)
        {
            return player.canHarvestBlock(block);
        }

        List info = (List)toolClasses.get(stack.itemID);
        if (info == null)
        {
            return player.canHarvestBlock(block);
        }
        Object[] tmp = info.toArray();
        String toolClass = (String)tmp[0];
        int harvestLevel = (Integer)tmp[1];

        Integer blockHarvestLevel = (Integer)toolHarvestLevels.get(Arrays.asList(block.blockID, metadata, toolClass));
        if (blockHarvestLevel == null)
        {
            return player.canHarvestBlock(block);
        }
        if (blockHarvestLevel > harvestLevel)
        {
            return false;
        }
        return true;
    }

    public static float blockStrength(Block block, EntityPlayer player, int metadata)
    {
        float hardness = block.getHardness(metadata);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(block, player, metadata))
        {
            return 1.0F / hardness / 100F;
        }
        else
        {
            return player.getCurrentPlayerStrVsBlock(block, metadata) / hardness / 30F;
        }
    }

    public static boolean isToolEffective(ItemStack stack, Block block, int metadata)
    {
        List toolClass = (List)toolClasses.get(stack.itemID);
        if (toolClass == null)
        {
            return false;
        }
        return toolEffectiveness.contains(Arrays.asList(block.blockID, metadata, (String)toolClass.get(0)));
    }

    static void initTools()
    {
        if (toolInit)
        {
            return;
        }
        toolInit = true;

        MinecraftForge.setToolClass(Item.pickaxeWood,    "pickaxe", 0);
        MinecraftForge.setToolClass(Item.pickaxeStone,   "pickaxe", 1);
        MinecraftForge.setToolClass(Item.pickaxeSteel,   "pickaxe", 2);
        MinecraftForge.setToolClass(Item.pickaxeGold,    "pickaxe", 0);
        MinecraftForge.setToolClass(Item.pickaxeDiamond, "pickaxe", 3);

        MinecraftForge.setToolClass(Item.axeWood,    "axe", 0);
        MinecraftForge.setToolClass(Item.axeStone,   "axe", 1);
        MinecraftForge.setToolClass(Item.axeSteel,   "axe", 2);
        MinecraftForge.setToolClass(Item.axeGold,    "axe", 0);
        MinecraftForge.setToolClass(Item.axeDiamond, "axe", 3);

        MinecraftForge.setToolClass(Item.shovelWood,    "shovel", 0);
        MinecraftForge.setToolClass(Item.shovelStone,   "shovel", 1);
        MinecraftForge.setToolClass(Item.shovelSteel,   "shovel", 2);
        MinecraftForge.setToolClass(Item.shovelGold,    "shovel", 0);
        MinecraftForge.setToolClass(Item.shovelDiamond, "shovel", 3);

        MinecraftForge.setBlockHarvestLevel(Block.obsidian,     "pickaxe", 3);
        MinecraftForge.setBlockHarvestLevel(Block.oreDiamond,   "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.blockDiamond, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreGold,      "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.blockGold,    "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreIron,      "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.blockSteel,   "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.oreLapis,     "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.blockLapis,   "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(Block.oreRedstone,  "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(Block.oreRedstoneGlowing, "pickaxe", 2);
        MinecraftForge.removeBlockEffectiveness(Block.oreRedstone, "pickaxe");
        MinecraftForge.removeBlockEffectiveness(Block.obsidian,    "pickaxe");
        MinecraftForge.removeBlockEffectiveness(Block.oreRedstoneGlowing, "pickaxe");

        Block[] pickeff =
        {
            Block.cobblestone, Block.stairDouble,
            Block.stairSingle, Block.stone,
            Block.sandStone,   Block.cobblestoneMossy,
            Block.oreCoal,     Block.ice,
            Block.netherrack,  Block.oreLapis,
            Block.blockLapis
        };
        for (Block block : pickeff)
        {
            MinecraftForge.setBlockHarvestLevel(block, "pickaxe", 0);
        }

        Block[] spadeEff =
        {
            Block.grass,     Block.dirt,
            Block.sand,      Block.gravel,
            Block.snow,      Block.blockSnow,
            Block.blockClay, Block.tilledField,
            Block.slowSand,  Block.mycelium
        };
        for (Block block : spadeEff)
        {
            MinecraftForge.setBlockHarvestLevel(block, "shovel", 0);
        }

        Block[] axeEff =
        {
            Block.planks,      Block.bookShelf,
            Block.wood,        Block.chest,
            Block.stairDouble, Block.stairSingle,
            Block.pumpkin,     Block.pumpkinLantern
        };
        for (Block block : axeEff)
        {
            MinecraftForge.setBlockHarvestLevel(block, "axe", 0);
        }

    }

    public static HashMap<Class, EntityTrackerInfo> entityTrackerMap = new HashMap<Class, EntityTrackerInfo>();

    /**
     * Builds the 'Spawn' packet using the Custom Payload packet on the 'Forge' channel.
     * Supports entities that have custom spawn data, as well as the generic 'Owner' construct.
     *
     * @param entity The entity instance to spawn
     * @return The spawn packet, or null if we arn't spawning it.
     */
    public static Packet getEntitySpawnPacket(Entity entity)
    {
        EntityTrackerInfo info = MinecraftForge.getEntityTrackerInfo(entity, false);
        if (info == null)
        {
            return null;
        }

        PacketEntitySpawn pkt = new PacketEntitySpawn(entity, info.Mod, info.ID);
        return pkt.getPacket();
    }

    public static Hashtable<Integer, NetworkMod> networkMods = new Hashtable<Integer, NetworkMod>();
    public static Hashtable<BaseMod, IGuiHandler> guiHandlers = new Hashtable<BaseMod, IGuiHandler>();

    static boolean toolInit = false;
    static HashMap toolClasses = new HashMap();
    static HashMap toolHarvestLevels = new HashMap();
    static HashSet toolEffectiveness = new HashSet();

    private static PacketHandlerBase forgePacketHandler = null;
    public static void setPacketHandler(PacketHandlerBase handler)
    {
        if (forgePacketHandler != null)
        {
            throw new RuntimeException("Attempted to set Forge's Internal packet handler after it was already set");
        }
        forgePacketHandler = handler;
    }
    public static PacketHandlerBase getPacketHandler()
    {
        return forgePacketHandler;
    }

    public static boolean onItemDataPacket(NetworkManager net, Packet131MapData pkt) 
    {
        NetworkMod mod = MinecraftForge.getModByID(pkt.itemID);
        if (mod == null)
        {
            ModLoader.getLogger().log(Level.WARNING, String.format("Received Unknown MapData packet %d:%d", pkt.itemID, pkt.uniqueID));
            return false;
        }
        mod.onPacketData(net, pkt.uniqueID, pkt.itemData);
        return true;
    }
}

