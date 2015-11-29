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
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry.Type;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

import com.google.common.collect.Maps;

public class GameData {
    static final int MIN_BLOCK_ID = 0;
    static final int MAX_BLOCK_ID = 4095;
    static final int MIN_ITEM_ID = 4096;
    static final int MAX_ITEM_ID = 31999;

    private static final GameData mainData = new GameData();
    // public api

    /**
     * Get the currently active block registry.
     *
     * @return Block Registry.
     */
    public static FMLControlledNamespacedRegistry<Block> getBlockRegistry() {
        return getMain().iBlockRegistry;
    }

    /**
     * Get the currently active item registry.
     *
     * @return Item Registry.
     */
    public static FMLControlledNamespacedRegistry<Item> getItemRegistry() {
        return getMain().iItemRegistry;
    }

    /***************************************************
     * INTERNAL CODE FROM HERE ON DO NOT USE!
     ***************************************************/


    static Item findItem(String modId, String name) {
        return getMain().iItemRegistry.getObject(new ResourceLocation(modId, name));
    }

    static Block findBlock(String modId, String name) {
        return getMain().iBlockRegistry.getObject(new ResourceLocation(modId, name));
    }

    static UniqueIdentifier getUniqueName(Block block) {
        if (block == null) return null;
        Object name = getMain().iBlockRegistry.getNameForObject(block);
        return new UniqueIdentifier(name);
    }

    static UniqueIdentifier getUniqueName(Item item) {
        if (item == null) return null;
        Object name = getMain().iItemRegistry.getNameForObject(item);
        return new UniqueIdentifier(name);
    }

    protected static GameData getMain() {
        return mainData;
    }

    // internal registry objects
    private final FMLControlledNamespacedRegistry<Block> iBlockRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.BLOCKS, Block.class, new ResourceLocation("minecraft:air"), MAX_BLOCK_ID, MIN_BLOCK_ID, true);
    private final FMLControlledNamespacedRegistry<Item> iItemRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.ITEMS, Item.class, null, MAX_ITEM_ID, MIN_ITEM_ID, true);

    int registerItem(Item item, String name) // from GameRegistry
    {
        int index = name.indexOf(':');
        if (index != -1)
            FMLLog.bigWarning("Dangerous extra prefix %s for name %s, invalid registry invocation/invalid name?", name.substring(0, index), name);

        ResourceLocation rl = addPrefix(name);
        return registerItem(item, rl, -1);
    }

    private int registerItem(Item item, ResourceLocation name, int idHint) {
        return iItemRegistry.add(idHint, name, item);
    }

    int registerBlock(Block block, String name) // from GameRegistry
    {
        int index = name.indexOf(':');
        if (index != -1)
            FMLLog.bigWarning("Dangerous alternative prefix %s for name %s, invalid registry invocation/invalid name?", name.substring(0, index), name);

        ResourceLocation rl = addPrefix(name);
        return registerBlock(block, rl, -1);
    }

    private int registerBlock(Block block, ResourceLocation name, int idHint) {
        int blockId = iBlockRegistry.add(idHint, name, block);

        for (IBlockState state : block.getBlockState().getValidStates()) {
            GameData.BLOCKSTATE_TO_ID.put(state, blockId << 4 | block.getMetaFromState(state));
        }

        return blockId;
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
    private ResourceLocation addPrefix(String name) {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index);
        String prefix;
        ModContainer mc = Loader.instance().activeModContainer();

        if (mc != null) {
            prefix = mc.getModId();
        } else // no mod container, assume minecraft
        {
            prefix = "minecraft";
        }

        if (!oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
            prefix = oldPrefix;
        }

        return new ResourceLocation(prefix,name);
    }

    void registerSubstitutionAlias(String name, Type type, Object toReplace) throws ExistingSubstitutionException {
        ResourceLocation nameToSubstitute = new ResourceLocation(Loader.instance().activeModContainer().getModId(), name);
        if (type == Type.BLOCK) {
            iBlockRegistry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(), nameToSubstitute, (Block) toReplace);
            iBlockRegistry.activateSubstitution(nameToSubstitute);
        } else if (type == Type.ITEM) {
            iItemRegistry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(), nameToSubstitute, (Item) toReplace);
            iItemRegistry.activateSubstitution(nameToSubstitute);
        }
    }

    private static Map<Block, Item> BLOCK_TO_ITEM = Maps.newHashMap();

    //Internal: DO NOT USE, will change without warning.
    public static Map getBlockItemMap() {
        return BLOCK_TO_ITEM;
    }

    private static ClearableObjectIntIdentityMap<IBlockState> BLOCKSTATE_TO_ID = new ClearableObjectIntIdentityMap<IBlockState>();

    //Internal: DO NOT USE, will change without warning.
    public static ObjectIntIdentityMap getBlockStateIDMap() {
        return BLOCKSTATE_TO_ID;
    }

    //Lets us clear the map so we can rebuild it.
    private static class ClearableObjectIntIdentityMap<I> extends ObjectIntIdentityMap<I> {
        private void clear() {
            this.identityMap.clear();
            this.objectList.clear();
        }
    }

    public <T> RegistryDelegate<T> makeDelegate(T obj, Class<T> rootClass) {
        return PersistentRegistryManager.makeDelegate(obj, rootClass);
    }
}