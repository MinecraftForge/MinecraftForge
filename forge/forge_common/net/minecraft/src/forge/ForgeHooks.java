/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
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
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraft.src.forge.packets.PacketEntitySpawn;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class ForgeHooks
{
    // List Handling Hooks
    // ------------------------------------------------------------
    public static void onTakenFromCrafting(EntityPlayer player, ItemStack stack, IInventory craftMatrix)
    {
        for (ICraftingHandler handler : craftingHandlers)
        {
            handler.onTakenFromCrafting(player, stack, craftMatrix);
        }
    }
    static LinkedList<ICraftingHandler> craftingHandlers = new LinkedList<ICraftingHandler>();

    public static void onDestroyCurrentItem(EntityPlayer player, ItemStack orig)
    {
        for (IDestroyToolHandler handler : destroyToolHandlers)
        {
            handler.onDestroyCurrentItem(player, orig);
        }
    }
    static LinkedList<IDestroyToolHandler> destroyToolHandlers = new LinkedList<IDestroyToolHandler>();

    public static boolean onUseBonemeal(World world, int blockID, int x, int y, int z)
    {
        for (IBonemealHandler handler : bonemealHandlers)
        {
            if (handler.onUseBonemeal(world, blockID, x, y, z))
            {
                return true;
            }
        }
        return false;
    }
    static LinkedList<IBonemealHandler> bonemealHandlers = new LinkedList<IBonemealHandler>();

    public static boolean onUseHoe(ItemStack hoe, EntityPlayer player, World world, int x, int y, int z)
    {
        for (IHoeHandler handler : hoeHandlers)
        {
            if (handler.onUseHoe(hoe, player, world, x, y, z))
            {
                return true;
            }
        }
        return false;
    }
    static LinkedList<IHoeHandler> hoeHandlers = new LinkedList<IHoeHandler>();

    public static EnumStatus sleepInBedAt(EntityPlayer player, int x, int y, int z)
    {
        for (ISleepHandler handler : sleepHandlers)
        {
            EnumStatus status = handler.sleepInBedAt(player, x, y, z);
            if (status != null)
            {
                return status;
            }
        }
        return null;
    }
    static LinkedList<ISleepHandler> sleepHandlers = new LinkedList<ISleepHandler>();

    public static void onWorldLoad(World world) 
    {
		for (ICustomSaveHandler handler : saveHandlers) 
		{
			handler.onWorldLoad(world);
		}
	}
	
	public static void onWorldSave(World world) 
	{
		for (ICustomSaveHandler handler : saveHandlers) 
		{
			handler.onWorldSave(world);
		}
	}
	
	public static void onChunkLoad(World world, Chunk chunk)
	{
		for (ICustomSaveHandler handler : saveHandlers) 
		{
			handler.onChunkLoad(world, chunk);
		}
	}
	
	public static void onChunkUnload(World world, Chunk chunk)
	{
		for (ICustomSaveHandler handler : saveHandlers) 
		{
			handler.onChunkUnload(world, chunk);
		}
	}
	
	public static void saveChunkData(World world, Chunk chunk, NBTTagCompound nbttagcompound)
	{
		for (ICustomSaveHandler handler : saveHandlers) 
		{
			handler.saveChunkData(world, chunk, nbttagcompound);
		}
	}
	
	public static void loadChunkData(World world, Chunk chunk, NBTTagCompound nbttagcompound)
	{
		for (ICustomSaveHandler handler : saveHandlers) 
		{
			handler.loadChunkData(world, chunk, nbttagcompound);
		}
	}
	static LinkedList<ICustomSaveHandler> saveHandlers = new LinkedList<ICustomSaveHandler>();

    public static void onMinecartUpdate(EntityMinecart minecart, int x, int y, int z)
    {
        for (IMinecartHandler handler : minecartHandlers)
        {
            handler.onMinecartUpdate(minecart, x, y, z);
        }
    }

    public static void onMinecartEntityCollision(EntityMinecart minecart, Entity entity)
    {
        for (IMinecartHandler handler : minecartHandlers)
        {
            handler.onMinecartEntityCollision(minecart, entity);
        }
    }

    public static boolean onMinecartInteract(EntityMinecart minecart, EntityPlayer player)
    {
        boolean canceled = true;
        for (IMinecartHandler handler : minecartHandlers)
        {
            boolean tmp = handler.onMinecartInteract(minecart, player, canceled);
            canceled = canceled && tmp;
        }
        return canceled;
    }

    static LinkedList<IMinecartHandler> minecartHandlers = new LinkedList<IMinecartHandler>();

    public static void onConnect(NetworkManager network)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.onConnect(network);
        }
    }

    public static void onLogin(NetworkManager network, Packet1Login login)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.onLogin(network, login);
        }
    }

    public static void onDisconnect(NetworkManager network, String message, Object[] args)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.onDisconnect(network, message, args);
        }
    }
    static LinkedList<IConnectionHandler> connectionHandlers = new LinkedList<IConnectionHandler>();

    public static boolean onItemPickup(EntityPlayer player, EntityItem item)
    {
        boolean cont = true;
        for (IPickupHandler handler : pickupHandlers)
        {
            cont = cont && handler.onItemPickup(player, item);
            if (!cont || item.item.stackSize <= 0)
            {
                return false;
            }
        }
        return cont;
    }
    static LinkedList<IPickupHandler> pickupHandlers = new LinkedList<IPickupHandler>();
    
    public static void addActiveChunks(World world, Set<ChunkCoordIntPair> chunkList)
    {
        for(IChunkLoadHandler loader : chunkLoadHandlers)
        {
            loader.addActiveChunks(world, chunkList);
        }
    }

    public static boolean canUnloadChunk(Chunk chunk)
    {
        for(IChunkLoadHandler loader : chunkLoadHandlers)
        {
            if(!loader.canUnloadChunk(chunk))
            {
                return false;
            }
        }
        return true;
    }
    
    public static boolean canUpdateEntity(Entity entity)
    {
        for(IChunkLoadHandler loader : chunkLoadHandlers)
        {
            if(loader.canUpdateEntity(entity))
            {
                return true;
            }
        }
        return false;
    }
    static LinkedList<IChunkLoadHandler> chunkLoadHandlers = new LinkedList<IChunkLoadHandler>();
    
    public static boolean onEntityInteract(EntityPlayer player, Entity entity, boolean isAttack)
    {
        for (IEntityInteractHandler handler : entityInteractHandlers)
        {
            if (!handler.onEntityInteract(player, entity, isAttack))
            {
                return false;
            }
        }
        return true;
    }
    static LinkedList<IEntityInteractHandler> entityInteractHandlers = new LinkedList<IEntityInteractHandler>();

    public static String onServerChat(EntityPlayer player, String message)
    {
        for (IChatHandler handler : chatHandlers)
        {
            message = handler.onServerChat(player, message);
            if (message == null)
            {
                return null;
            }
        }
        return message;
    }
    
    public static boolean onChatCommand(EntityPlayer player, boolean isOp, String command)
    {
        for (IChatHandler handler : chatHandlers)
        {
            if (handler.onChatCommand(player, isOp, command))
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean onServerCommand(Object listener, String username, String command)
    {
        for (IChatHandler handler : chatHandlers)
        {
            if (handler.onServerCommand(listener, username, command))
            {
                return true;
            }
        }
        return false;
    }
    
    public static String onServerCommandSay(Object listener, String username, String message)
    {
        for (IChatHandler handler : chatHandlers)
        {
            message = handler.onServerCommandSay(listener, username, message);
            if (message == null)
            {
                return null;
            }
        }
        return message;
    }
    
    public static String onClientChatRecv(String message)
    {
        for (IChatHandler handler : chatHandlers)
        {
            message = handler.onClientChatRecv(message);
            if (message == null)
            {
                return null;
            }
        }
        return message;
    }
    static LinkedList<IChatHandler> chatHandlers = new LinkedList<IChatHandler>();
    
    public static void onWorldLoad(World world)
    {
        for (ISaveEventHandler handler : saveHandlers)
        {
            handler.onWorldLoad(world);
        }
    }

    public static void onWorldSave(World world)
    {
        for (ISaveEventHandler handler : saveHandlers)
        {
            handler.onWorldSave(world);
        }
    }

    public static void onChunkLoad(World world, Chunk chunk)
    {
        for (ISaveEventHandler handler : saveHandlers)
        {
            handler.onChunkLoad(world, chunk);
        }
    }

    public static void onChunkUnload(World world, Chunk chunk)
    {
        for (ISaveEventHandler handler : saveHandlers)
        {
            handler.onChunkUnload(world, chunk);
        }
    }

    public static void onChunkLoadData(World world, Chunk chunk, NBTTagCompound data)
    {
        for (ISaveEventHandler handler : saveHandlers)
        {
            handler.onChunkLoadData(world, chunk, data);
        }
    }

    public static void onChunkSaveData(World world, Chunk chunk, NBTTagCompound data)
    {
        for (ISaveEventHandler handler : saveHandlers)
        {
            handler.onChunkSaveData(world, chunk, data);
        }
    }
    static LinkedList<ISaveEventHandler> saveHandlers = new LinkedList<ISaveEventHandler>();

    // Plant Management
    // ------------------------------------------------------------
    static class ProbableItem
    {
        public ProbableItem(int item, int metadata, int quantity, int start, int end)
        {
            WeightStart = start;
            WeightEnd = end;
            ItemID = item;
            Metadata = metadata;
            Quantity = quantity;
        }
        int WeightStart, WeightEnd;
        int ItemID, Metadata;
        int Quantity;
    }

    static ProbableItem getRandomItem(List<ProbableItem> list, int prop)
    {
        int n = Collections.binarySearch(list, prop, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                ProbableItem pi = (ProbableItem)o1;
                Integer i1 = (Integer)o2;
                if (i1 < pi.WeightStart)
                {
                    return 1;
                }
                if (i1 >= pi.WeightEnd)
                {
                    return -1;
                }
                return 0;
            }
        });
        if (n < 0)
        {
            return null;
        }
        return list.get(n);
    }

    static List<ProbableItem> plantGrassList;
    static int plantGrassWeight;

    static List<ProbableItem> seedGrassList;
    static int seedGrassWeight;

    public static void plantGrassPlant(World world, int x, int y, int z)
    {
        int index = world.rand.nextInt(plantGrassWeight);
        ProbableItem item = getRandomItem(plantGrassList, index);
        if (item == null)
        {
            return;
        }
        world.setBlockAndMetadataWithNotify(x, y, z, item.ItemID, item.Metadata);
    }

    public static void addPlantGrass(int item, int metadata, int probability)
    {
        plantGrassList.add(new ProbableItem(item, metadata, 1, plantGrassWeight, plantGrassWeight + probability));
        plantGrassWeight += probability;
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

    public static void addGrassSeed(int item, int metadata, int quantity, int probability)
    {
        seedGrassList.add(new ProbableItem(item, metadata, quantity, seedGrassWeight, seedGrassWeight + probability));
        seedGrassWeight += probability;
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
            return false;
        }

        List info = (List)toolClasses.get(stack.itemID);
        if (info == null)
        {
            return stack.canHarvestBlock(block);
        }
        Object[] tmp = info.toArray();
        String toolClass = (String)tmp[0];
        int harvestLevel = (Integer)tmp[1];

        Integer blockHarvestLevel = (Integer)toolHarvestLevels.get(Arrays.asList(block.blockID, metadata, toolClass));
        if (blockHarvestLevel == null)
        {
            return stack.canHarvestBlock(block);
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

    public static boolean onArrowLoose(ItemStack itemstack, World world, EntityPlayer player, int heldTime)
    {
        for (IArrowLooseHandler handler : arrowLooseHandlers)
        {
            if (handler.onArrowLoose(itemstack, world, player, heldTime))
            {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<IArrowLooseHandler> arrowLooseHandlers = new ArrayList<IArrowLooseHandler>();

    public static ItemStack onArrowNock(ItemStack itemstack, World world, EntityPlayer player)
    {
        for (IArrowNockHandler handler : arrowNockHandlers)
        {
            ItemStack ret = handler.onArrowNock(itemstack, world, player);
            if (ret != null)
            {
                return ret;
            }
        }
        return null;
    }
    public static ArrayList<IArrowNockHandler> arrowNockHandlers = new ArrayList<IArrowNockHandler>();

    public static final int majorVersion    = 3;
    public static final int minorVersion    = 0;
    public static final int revisionVersion = 0;
    public static final int buildVersion    = 5;
    static
    {
        plantGrassList = new ArrayList<ProbableItem>();
        plantGrassList.add(new ProbableItem(Block.plantYellow.blockID, 0, 1, 0, 20));
        plantGrassList.add(new ProbableItem(Block.plantRed.blockID, 0, 1, 20, 30));
        plantGrassWeight = 30;

        seedGrassList = new ArrayList<ProbableItem>();
        seedGrassList.add(new ProbableItem(Item.seeds.shiftedIndex, 0, 1, 0, 10));
        seedGrassWeight = 10;
        
        System.out.printf("MinecraftForge v%d.%d.%d.%d Initialized\n", majorVersion, minorVersion, revisionVersion, buildVersion);
        ModLoader.getLogger().info(String.format("MinecraftForge v%d.%d.%d.%d Initialized\n", majorVersion, minorVersion, revisionVersion, buildVersion));
    }

    static boolean toolInit = false;
    static HashMap toolClasses = new HashMap();
    static HashMap toolHarvestLevels = new HashMap();
    static HashSet toolEffectiveness = new HashSet();

    private static IPacketHandler forgePacketHandler = null;
    public static void setPacketHandler(IPacketHandler handler)
    {
        if (forgePacketHandler != null)
        {
            throw new RuntimeException("Attempted to set Forge's Internal packet handler after it was already set");
        }
        forgePacketHandler = handler;
    }
    public static IPacketHandler getPacketHandler()
    {
        return forgePacketHandler;
    }
}

