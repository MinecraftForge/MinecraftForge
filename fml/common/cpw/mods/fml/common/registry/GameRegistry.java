/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Mod.Block;
import cpw.mods.fml.common.ModContainer;

public class GameRegistry
{
    private static Multimap<ModContainer, BlockProxy> blockRegistry = ArrayListMultimap.create();
    private static Set<IWorldGenerator> worldGenerators = Sets.newHashSet();
    private static List<IFuelHandler> fuelHandlers = Lists.newArrayList();
    private static List<ICraftingHandler> craftingHandlers = Lists.newArrayList();
    private static List<IPickupNotifier> pickupHandlers = Lists.newArrayList();
    private static List<IPlayerTracker> playerTrackers = Lists.newArrayList();

    /**
     * Register a world generator - something that inserts new block types into the world
     *
     * @param generator
     */
    public static void registerWorldGenerator(IWorldGenerator generator)
    {
        worldGenerators.add(generator);
    }

    /**
     * Callback hook for world gen - if your mod wishes to add extra mod related generation to the world
     * call this
     *
     * @param chunkX
     * @param chunkZ
     * @param world
     * @param chunkGenerator
     * @param chunkProvider
     */
    public static void generateWorld(int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        long worldSeed = world.func_72905_C();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed;

        for (IWorldGenerator generator : worldGenerators)
        {
            fmlRandom.setSeed(chunkSeed);
            generator.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }

    /**
     * Internal method for creating an @Block instance
     * @param container
     * @param type
     * @param annotation
     * @throws Exception
     */
    public static Object buildBlock(ModContainer container, Class<?> type, Block annotation) throws Exception
    {
        Object o = type.getConstructor(int.class).newInstance(findSpareBlockId());
        registerBlock((net.minecraft.block.Block) o);
        return o;
    }

    /**
     * Private and not yet working properly
     *
     * @return a block id
     */
    private static int findSpareBlockId()
    {
        return BlockTracker.nextBlockId();
    }

    /**
     * Register an item with the item registry with a custom name : this allows for easier server->client resolution
     *
     * @param item The item to register
     * @param name The mod-unique name of the item
     */
    public static void registerItem(net.minecraft.item.Item item, String name)
    {
        registerItem(item, name, null);
    }

    /**
     * Register the specified Item with a mod specific name : overrides the standard type based name
     * @param item The item to register
     * @param name The mod-unique name to register it as - null will remove a custom name
     * @param modId An optional modId that will "own" this block - generally used by multi-mod systems
     * where one mod should "own" all the blocks of all the mods, null defaults to the active mod
     */
    public static void registerItem(net.minecraft.item.Item item, String name, String modId)
    {
        GameData.setName(item, name, modId);
    }

    /**
     * Register a block with the world
     *
     */
    @Deprecated
    public static void registerBlock(net.minecraft.block.Block block)
    {
        registerBlock(block, ItemBlock.class);
    }


    /**
     * Register a block with the specified mod specific name : overrides the standard type based name
     * @param block The block to register
     * @param name The mod-unique name to register it as
     */
    public static void registerBlock(net.minecraft.block.Block block, String name)
    {
        registerBlock(block, ItemBlock.class, name);
    }

    /**
     * Register a block with the world, with the specified item class
     *
     * Deprecated in favour of named versions
     *
     * @param block The block to register
     * @param itemclass The item type to register with it
     */
    @Deprecated
    public static void registerBlock(net.minecraft.block.Block block, Class<? extends ItemBlock> itemclass)
    {
        registerBlock(block, itemclass, null);
    }
    /**
     * Register a block with the world, with the specified item class and block name
     * @param block The block to register
     * @param itemclass The item type to register with it
     * @param name The mod-unique name to register it with
     */
    public static void registerBlock(net.minecraft.block.Block block, Class<? extends ItemBlock> itemclass, String name)
    {
        registerBlock(block, itemclass, name, null);
    }
    /**
     * Register a block with the world, with the specified item class, block name and owning modId
     * @param block The block to register
     * @param itemclass The iterm type to register with it
     * @param name The mod-unique name to register it with
     * @param modId The modId that will own the block name. null defaults to the active modId
     */
    public static void registerBlock(net.minecraft.block.Block block, Class<? extends ItemBlock> itemclass, String name, String modId)
    {
        if (Loader.instance().isInState(LoaderState.CONSTRUCTING))
        {
            FMLLog.warning("The mod %s is attempting to register a block whilst it it being constructed. This is bad modding practice - please use a proper mod lifecycle event.", Loader.instance().activeModContainer());
        }
        try
        {
            assert block != null : "registerBlock: block cannot be null";
            assert itemclass != null : "registerBlock: itemclass cannot be null";
            int blockItemId = block.field_71990_ca - 256;
            Constructor<? extends ItemBlock> itemCtor;
            Item i;
            try
            {
                itemCtor = itemclass.getConstructor(int.class);
                i = itemCtor.newInstance(blockItemId);
            }
            catch (NoSuchMethodException e)
            {
                itemCtor = itemclass.getConstructor(int.class, net.minecraft.block.Block.class);
                i = itemCtor.newInstance(blockItemId, block);
            }
            GameRegistry.registerItem(i,name, modId);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Caught an exception during block registration");
            throw new LoaderException(e);
        }
        blockRegistry.put(Loader.instance().activeModContainer(), (BlockProxy) block);
    }

    public static void addRecipe(ItemStack output, Object... params)
    {
        addShapedRecipe(output, params);
    }

    public static IRecipe addShapedRecipe(ItemStack output, Object... params)
    {
        return CraftingManager.func_77594_a().func_92103_a(output, params);
    }

    public static void addShapelessRecipe(ItemStack output, Object... params)
    {
        CraftingManager.func_77594_a().func_77596_b(output, params);
    }

    public static void addRecipe(IRecipe recipe)
    {
        CraftingManager.func_77594_a().func_77592_b().add(recipe);
    }

    public static void addSmelting(int input, ItemStack output, float xp)
    {
        FurnaceRecipes.func_77602_a().func_77600_a(input, output, xp);
    }

    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id)
    {
        TileEntity.func_70306_a(tileEntityClass, id);
    }

    /**
     * Register a tile entity, with alternative TileEntity identifiers. Use with caution!
     * This method allows for you to "rename" the 'id' of the tile entity.
     *
     * @param tileEntityClass The tileEntity class to register
     * @param id The primary ID, this will be the ID that the tileentity saves as
     * @param alternatives A list of alternative IDs that will also map to this class. These will never save, but they will load
     */
    public static void registerTileEntityWithAlternatives(Class<? extends TileEntity> tileEntityClass, String id, String... alternatives)
    {
        TileEntity.func_70306_a(tileEntityClass, id);
        Map<String,Class> teMappings = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "70326_a", "field_70326_a", "a");
        for (String s: alternatives)
        {
            if (!teMappings.containsKey(s))
            {
                teMappings.put(s, tileEntityClass);
            }
        }
    }

    public static void addBiome(BiomeGenBase biome)
    {
        WorldType.field_77137_b.addNewBiome(biome);
    }

    public static void removeBiome(BiomeGenBase biome)
    {
        WorldType.field_77137_b.removeBiome(biome);
    }

    public static void registerFuelHandler(IFuelHandler handler)
    {
        fuelHandlers.add(handler);
    }
    public static int getFuelValue(ItemStack itemStack)
    {
        int fuelValue = 0;
        for (IFuelHandler handler : fuelHandlers)
        {
            fuelValue = Math.max(fuelValue, handler.getBurnTime(itemStack));
        }
        return fuelValue;
    }

    public static void registerCraftingHandler(ICraftingHandler handler)
    {
        craftingHandlers.add(handler);
    }

    public static void onItemCrafted(EntityPlayer player, ItemStack item, IInventory craftMatrix)
    {
        for (ICraftingHandler handler : craftingHandlers)
        {
            handler.onCrafting(player, item, craftMatrix);
        }
    }

    public static void onItemSmelted(EntityPlayer player, ItemStack item)
    {
        for (ICraftingHandler handler : craftingHandlers)
        {
            handler.onSmelting(player, item);
        }
    }

    public static void registerPickupHandler(IPickupNotifier handler)
    {
        pickupHandlers.add(handler);
    }

    public static void onPickupNotification(EntityPlayer player, EntityItem item)
    {
        for (IPickupNotifier notify : pickupHandlers)
        {
            notify.notifyPickup(item, player);
        }
    }

    public static void registerPlayerTracker(IPlayerTracker tracker)
	{
		playerTrackers.add(tracker);
	}

	public static void onPlayerLogin(EntityPlayer player)
	{
        for (IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerLogin(player);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerLogin event with player tracker %s", tracker.getClass().getName());
            }
	}

	public static void onPlayerLogout(EntityPlayer player)
	{
        for (IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerLogout(player);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerLogout event with player tracker %s", tracker.getClass().getName());
            }
	}

	public static void onPlayerChangedDimension(EntityPlayer player)
	{
        for (IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerChangedDimension(player);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerChangedDimension event with player tracker %s", tracker.getClass()
                        .getName());
            }
	}

	public static void onPlayerRespawn(EntityPlayer player)
	{
        for (IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerRespawn(player);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerRespawn event with player tracker %s", tracker.getClass().getName());
            }
	}


	/**
	 * Look up a mod block in the global "named item list"
	 * @param modId The modid owning the block
	 * @param name The name of the block itself
	 * @return The block or null if not found
	 */
	public static net.minecraft.block.Block findBlock(String modId, String name)
	{
	    return GameData.findBlock(modId, name);
	}

	/**
	 * Look up a mod item in the global "named item list"
	 * @param modId The modid owning the item
	 * @param name The name of the item itself
	 * @return The item or null if not found
	 */
	public static net.minecraft.item.Item findItem(String modId, String name)
    {
        return GameData.findItem(modId, name);
    }

	/**
	 * Manually register a custom item stack with FML for later tracking. It is automatically scoped with the active modid
	 *
	 * @param name The name to register it under
	 * @param itemStack The itemstack to register
	 */
	public static void registerCustomItemStack(String name, ItemStack itemStack)
	{
	    GameData.registerCustomItemStack(name, itemStack);
	}
	/**
	 * Lookup an itemstack based on mod and name. It will create "default" itemstacks from blocks and items if no
	 * explicit itemstack is found.
	 *
	 * If it is built from a block, the metadata is by default the "wildcard" value.
	 *
	 * Custom itemstacks can be dumped from minecraft by setting the system property fml.dumpRegistry to true
	 * (-Dfml.dumpRegistry=true on the command line will work)
	 *
	 * @param modId The modid of the stack owner
	 * @param name The name of the stack
	 * @param stackSize The size of the stack returned
	 * @return The custom itemstack or null if no such itemstack was found
	 */
	public static ItemStack findItemStack(String modId, String name, int stackSize)
	{
	    ItemStack foundStack = GameData.findItemStack(modId, name);
	    if (foundStack != null)
	    {
            ItemStack is = foundStack.func_77946_l();
    	    is.field_77994_a = Math.min(stackSize, is.func_77976_d());
    	    return is;
	    }
	    return null;
	}

	public static class UniqueIdentifier
	{
	    public final String modId;
	    public final String name;
        UniqueIdentifier(String modId, String name)
        {
            this.modId = modId;
            this.name = name;
        }
	}

	/**
	 * Look up the mod identifier data for a block.
	 * Returns null if there is no mod specified mod identifier data, or it is part of a
	 * custom itemstack definition {@link #registerCustomItemStack}
	 *
	 * Note: uniqueness and persistence is only guaranteed by mods using the game registry
	 * correctly.
	 *
	 * @param block to lookup
     * @return a {@link UniqueIdentifier} for the block or null
	 */
	public static UniqueIdentifier findUniqueIdentifierFor(net.minecraft.block.Block block)
	{
	    return GameData.getUniqueName(block);
	}
    /**
     * Look up the mod identifier data for an item.
     * Returns null if there is no mod specified mod identifier data, or it is part of a
     * custom itemstack definition {@link #registerCustomItemStack}
     *
     * Note: uniqueness and persistence is only guaranteed by mods using the game registry
     * correctly.
     *
     * @param item to lookup
     * @return a {@link UniqueIdentifier} for the item or null
     */
    public static UniqueIdentifier findUniqueIdentifierFor(net.minecraft.item.Item item)
    {
        return GameData.getUniqueName(item);
    }
}
