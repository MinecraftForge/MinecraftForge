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

package net.minecraftforge.fml.common.registry;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class GameData
{
    static final int MIN_BLOCK_ID = 0;
    static final int MAX_BLOCK_ID = 4095;
    static final int MIN_ITEM_ID = 4096;
    static final int MAX_ITEM_ID = 31999;
    public static final int MIN_POTION_ID = 0; // 0-~31 are vanilla, forge start at 32
    public static final int MAX_POTION_ID = 255; // S1DPacketEntityEffect sends bytes, we can only use 255

    private static final GameData mainData = new GameData();
    // public api

    /**
     * Get the currently active block registry.
     *
     * @return Block Registry.
     */
    public static FMLControlledNamespacedRegistry<Block> getBlockRegistry()
    {
        return getMain().iBlockRegistry;
    }

    /**
     * Get the currently active item registry.
     *
     * @return Item Registry.
     */
    public static FMLControlledNamespacedRegistry<Item> getItemRegistry()
    {
        return getMain().iItemRegistry;
    }

    /**
      * Get the currently active potion registry.
      *
      * @return Potion Registry.
      */
    public static FMLControlledNamespacedRegistry<Potion> getPotionRegistry() {
        return getMain().iPotionRegistry;
    }


    /***************************************************
     * INTERNAL CODE FROM HERE ON DO NOT USE!
     ***************************************************/


    static Item findItem(String modId, String name)
    {
        return getMain().iItemRegistry.getObject(new ResourceLocation(modId, name));
    }

    static Block findBlock(String modId, String name)
    {
        return getMain().iBlockRegistry.getObject(new ResourceLocation(modId, name));
    }

    static GameRegistry.UniqueIdentifier getUniqueName(Block block)
    {
        if (block == null)
        {
            return null;
        }
        Object name = getMain().iBlockRegistry.getNameForObject(block);
        return new GameRegistry.UniqueIdentifier(name);
    }

    static GameRegistry.UniqueIdentifier getUniqueName(Item item)
    {
        if (item == null)
        {
            return null;
        }
        Object name = getMain().iItemRegistry.getNameForObject(item);
        return new GameRegistry.UniqueIdentifier(name);
    }

    protected static GameData getMain()
    {
        return mainData;
    }

    // internal registry objects
    private final FMLControlledNamespacedRegistry<Block> iBlockRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.BLOCKS, Block.class, new ResourceLocation("minecraft:air"), MAX_BLOCK_ID, MIN_BLOCK_ID, true, BlockStateCapture.INSTANCE);
    private final FMLControlledNamespacedRegistry<Item> iItemRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.ITEMS, Item.class, null, MAX_ITEM_ID, MIN_ITEM_ID, true, ItemBlockCapture.INSTANCE);
    private final FMLControlledNamespacedRegistry<Potion> iPotionRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.POTIONS, Potion.class, null, MAX_POTION_ID, MIN_POTION_ID, false, PotionArrayCapture.INSTANCE);

    int registerItem(Item item, String name) // from GameRegistry
    {
        return iItemRegistry.add(-1, addPrefix(name), item);
    }

    int registerBlock(Block block, String name) // from GameRegistry
    {
        return iBlockRegistry.add(-1, addPrefix(name), block);
    }

    /**
     * Prefix the supplied name with the current mod id.
     * <p/>
     * If no mod id can be determined, minecraft will be assumed.
     * The prefix is separated with a colon.
     * <p/>
     * If there's already a prefix, it'll be prefixed again if the new prefix
     * doesn't match the old prefix, as used by vanilla calls to addObject.
     *
     * @param name name to prefix.
     * @return prefixed name.
     */
    private ResourceLocation addPrefix(String name)
    {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index);
        name = index == -1 ? name : name.substring(index + 1);
        String prefix;
        ModContainer mc = Loader.instance().activeModContainer();

        if (mc != null)
        {
            prefix = mc.getModId().toLowerCase();
        }
        else // no mod container, assume minecraft
        {
            prefix = "minecraft";
        }

        if (!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
        {
            FMLLog.bigWarning("Dangerous alternative prefix %s for name %s, invalid registry invocation/invalid name?", prefix, name);
            prefix = oldPrefix;
        }

        return new ResourceLocation(prefix, name);
    }

    void registerSubstitutionAlias(String name, GameRegistry.Type type, Object toReplace) throws ExistingSubstitutionException
    {
        ResourceLocation nameToSubstitute = new ResourceLocation(name);
        if (type == GameRegistry.Type.BLOCK)
        {
            iBlockRegistry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(), nameToSubstitute, (Block)toReplace);
            Block orig = iBlockRegistry.activateSubstitution(nameToSubstitute);
            if (BLOCK_TO_ITEM.containsKey(orig))
            {
                Item i = BLOCK_TO_ITEM.get(orig);
                BLOCK_TO_ITEM.forcePut((Block)toReplace,i);
            }
        }
        else if (type == GameRegistry.Type.ITEM)
        {
            iItemRegistry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(), nameToSubstitute, (Item)toReplace);
            Item orig = iItemRegistry.activateSubstitution(nameToSubstitute);
            if (BLOCK_TO_ITEM.containsValue(orig))
            {
                Block b = BLOCK_TO_ITEM.inverse().get(orig);
                BLOCK_TO_ITEM.forcePut(b, (Item)toReplace);
            }
        }
    }

    private static BiMap<Block, Item> BLOCK_TO_ITEM = HashBiMap.create();

    //Internal: DO NOT USE, will change without warning.
    public static Map<Block, Item> getBlockItemMap()
    {
        return BLOCK_TO_ITEM;
    }

    private static ClearableObjectIntIdentityMap<IBlockState> BLOCKSTATE_TO_ID = new ClearableObjectIntIdentityMap<IBlockState>();

    //Internal: DO NOT USE, will change without warning.
    public static ClearableObjectIntIdentityMap<IBlockState> getBlockStateIDMap()
    {
        return BLOCKSTATE_TO_ID;
    }

    //Lets us clear the map so we can rebuild it.
    static class ClearableObjectIntIdentityMap<I> extends ObjectIntIdentityMap<I>
    {
        void clear()
        {
            this.identityMap.clear();
            this.objectList.clear();
        }
    }

    public <T> RegistryDelegate<T> makeDelegate(T obj, Class<T> rootClass)
    {
        return PersistentRegistryManager.makeDelegate(obj, rootClass);
    }

    private static class BlockStateCapture implements FMLControlledNamespacedRegistry.AddCallback<Block>
    {
        static final BlockStateCapture INSTANCE = new BlockStateCapture();

        @Override
        public void onAdd(Block block, int blockId)
        {
            for (IBlockState state : block.getBlockState().getValidStates())
            {
                GameData.BLOCKSTATE_TO_ID.put(state, blockId << 4 | block.getMetaFromState(state));
            }
        }
    }

    private static class ItemBlockCapture implements FMLControlledNamespacedRegistry.AddCallback<Item>
    {
        static final ItemBlockCapture INSTANCE = new ItemBlockCapture();

        @Override
        public void onAdd(Item item, int blockId)
        {
            if (item instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock)item;
                BLOCK_TO_ITEM.forcePut(itemBlock.getBlock().delegate.get(), item);
            }
        }
    }

    private static class PotionArrayCapture implements FMLControlledNamespacedRegistry.AddCallback<Potion>
    {
        static final PotionArrayCapture INSTANCE = new PotionArrayCapture();

        @Override
        public void onAdd(Potion potion, int id) {
            // fix the data in the potion and the potions-array
            potion.id = id;
            Potion.potionTypes[id] = potion;
        }
    }
}