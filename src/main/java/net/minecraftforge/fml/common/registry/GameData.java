/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common.registry;

import java.util.Map;

import com.google.common.collect.HashBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.BiMap;
import org.apache.logging.log4j.Level;

public class GameData
{
    private static final int MIN_BLOCK_ID = 0;
    private static final int MAX_BLOCK_ID = 4095;
    private static final int MIN_ITEM_ID = 4096;
    private static final int MAX_ITEM_ID = 31999;
    private static final int MIN_POTION_ID = 0; // 0-~31 are vanilla, forge start at 32
    private static final int MAX_POTION_ID = 255; // SPacketEntityEffect sends bytes, we can only use 255
    private static final int MIN_BIOME_ID = 0;
    private static final int MAX_BIOME_ID = 255; // Maximum number in a byte in the chunk
    private static final int MIN_SOUND_ID = 0; // Varint
    private static final int MAX_SOUND_ID = Integer.MAX_VALUE >> 5; // Varint (SPacketSoundEffect)
    private static final int MIN_POTIONTYPE_ID = 0; // Int
    private static final int MAX_POTIONTYPE_ID = Integer.MAX_VALUE >> 5; // Int (SPacketEffect)
    private static final int MIN_ENCHANTMENT_ID = 0; // Int
    private static final int MAX_ENCHANTMENT_ID = Short.MAX_VALUE - 1; // Short - serialized as a short in ItemStack NBTs.

    private static final ResourceLocation BLOCK_TO_ITEM = new ResourceLocation("minecraft:blocktoitemmap");
    private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");

    private static final GameData mainData = new GameData();

    public GameData()
    {
        iBlockRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.BLOCKS, Block.class, new ResourceLocation("minecraft:air"), MIN_BLOCK_ID, MAX_BLOCK_ID, true, BlockCallbacks.INSTANCE, BlockCallbacks.INSTANCE, BlockCallbacks.INSTANCE);
        iItemRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.ITEMS, Item.class, null, MIN_ITEM_ID, MAX_ITEM_ID, true, ItemCallbacks.INSTANCE, ItemCallbacks.INSTANCE, ItemCallbacks.INSTANCE);
        iPotionRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.POTIONS, Potion.class, null, MIN_POTION_ID, MAX_POTION_ID, false, PotionCallbacks.INSTANCE, PotionCallbacks.INSTANCE, PotionCallbacks.INSTANCE);
        iBiomeRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.BIOMES, Biome.class, null, MIN_BIOME_ID, MAX_BIOME_ID, false, BiomeCallbacks.INSTANCE, BiomeCallbacks.INSTANCE, BiomeCallbacks.INSTANCE);
        iSoundEventRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.SOUNDEVENTS, SoundEvent.class, null, MIN_SOUND_ID, MAX_SOUND_ID, false, null, null, null);
        ResourceLocation WATER = new ResourceLocation("water");
        iPotionTypeRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.POTIONTYPES, PotionType.class, WATER, MIN_POTIONTYPE_ID, MAX_POTIONTYPE_ID, false, null, null, null);
        iEnchantmentRegistry = PersistentRegistryManager.createRegistry(PersistentRegistryManager.ENCHANTMENTS, Enchantment.class, null, MIN_ENCHANTMENT_ID, MAX_ENCHANTMENT_ID, false, null, null, null);
    }
    // internal registry objects
    private final FMLControlledNamespacedRegistry<Block> iBlockRegistry;
    private final FMLControlledNamespacedRegistry<Item> iItemRegistry;
    private final FMLControlledNamespacedRegistry<Potion> iPotionRegistry;
    private final FMLControlledNamespacedRegistry<Biome> iBiomeRegistry;
    private final FMLControlledNamespacedRegistry<SoundEvent> iSoundEventRegistry;
    private final FMLControlledNamespacedRegistry<PotionType> iPotionTypeRegistry;
    private final FMLControlledNamespacedRegistry<Enchantment> iEnchantmentRegistry;

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<Block> getBlockRegistry()
    {
        return getMain().iBlockRegistry;
    }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<Item> getItemRegistry()
    {
        return getMain().iItemRegistry;
    }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<Potion> getPotionRegistry() {
        return getMain().iPotionRegistry;
    }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<Biome> getBiomeRegistry() { return getMain().iBiomeRegistry; }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<SoundEvent> getSoundEventRegistry() { return getMain().iSoundEventRegistry; }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<PotionType> getPotionTypesRegistry() { return getMain().iPotionTypeRegistry; }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<Enchantment> getEnchantmentRegistry() { return getMain().iEnchantmentRegistry; }

    @Deprecated
    static Item findItem(String modId, String name)
    {
        return getMain().iItemRegistry.getObject(new ResourceLocation(modId, name));
    }

    @Deprecated
    static Block findBlock(String modId, String name)
    {
        return getMain().iBlockRegistry.getObject(new ResourceLocation(modId, name));
    }

    protected static GameData getMain()
    {
        return mainData;
    }

    @Deprecated
    int registerItem(Item item, String name) // from GameRegistry
    {
        return iItemRegistry.add(-1, addPrefix(name), item);
    }

    @Deprecated
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
        final BiMap<Block, Item> blockItemMap = getBlockItemMap();
        if (type == GameRegistry.Type.BLOCK)
        {
            iBlockRegistry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(), nameToSubstitute, (Block)toReplace);
            Block orig = iBlockRegistry.activateSubstitution(nameToSubstitute);
            if (blockItemMap.containsKey(orig))
            {
                Item i = blockItemMap.get(orig);
                blockItemMap.forcePut((Block)toReplace,i);
            }
        }
        else if (type == GameRegistry.Type.ITEM)
        {
            iItemRegistry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(), nameToSubstitute, (Item)toReplace);
            Item orig = iItemRegistry.activateSubstitution(nameToSubstitute);
            if (blockItemMap.containsValue(orig))
            {
                Block b = blockItemMap.inverse().get(orig);
                blockItemMap.forcePut(b, (Item)toReplace);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static BiMap<Block,Item> getBlockItemMap()
    {
        return (BiMap<Block,Item>)getMain().iItemRegistry.getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
    }

    @SuppressWarnings("unchecked")
    static <K extends IForgeRegistryEntry<K>> K register_impl(Object object, ResourceLocation location)
    {
        if (object == null)
        {
            FMLLog.getLogger().log(Level.ERROR, "Attempt to register a null object");
            throw new NullPointerException("Attempt to register a null object");
        }
        ((K)object).setRegistryName(location);
        // explicit type is needed for some reason
        return GameData.<K>register_impl(object);
    }

    // argument is Object due to incorrect inferrence of the type if source level = 6, probably related to https://bugs.openjdk.java.net/browse/JDK-8026527
    @SuppressWarnings("unchecked")
    static <K extends IForgeRegistryEntry<K>> K register_impl(Object object)
    {
        K castedObj = (K)object;
        if (object == null)
        {
            FMLLog.getLogger().log(Level.ERROR, "Attempt to register a null object");
            throw new NullPointerException("Attempt to register a null object");
        }
        if (castedObj.getRegistryName() == null)
        {
            FMLLog.getLogger().log(Level.ERROR, "Attempt to register object without having set a registry name {} (type {})", object, object.getClass().getName());
            throw new IllegalArgumentException(String.format("No registry name set for object %s (%s)", object, object.getClass().getName()));
        }
        final IForgeRegistry<K> registry = PersistentRegistryManager.findRegistry(castedObj);
        registry.register(castedObj);
        return castedObj;
    }

    @SuppressWarnings("unchecked")
    public static ObjectIntIdentityMap<IBlockState> getBlockStateIDMap()
    {
        return (ObjectIntIdentityMap<IBlockState>)getMain().iBlockRegistry.getSlaveMap(BLOCKSTATE_TO_ID, ObjectIntIdentityMap.class);
    }

    public static void vanillaSnapshot()
    {
        PersistentRegistryManager.freezeVanilla();
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

    public <T extends IForgeRegistryEntry<T>> RegistryDelegate<T> makeDelegate(T obj, Class<T> rootClass)
    {
        return PersistentRegistryManager.makeDelegate(obj, rootClass);
    }

    private static class BlockCallbacks implements IForgeRegistry.AddCallback<Block>,IForgeRegistry.ClearCallback<Block>,IForgeRegistry.CreateCallback<Block>
    {
        static final BlockCallbacks INSTANCE = new BlockCallbacks();

        @SuppressWarnings("unchecked")
        @Override
        public void onAdd(Block block, int blockId, Map<ResourceLocation,?> slaves)
        {
            ClearableObjectIntIdentityMap<IBlockState> blockstateMap = (ClearableObjectIntIdentityMap<IBlockState>)slaves.get(BLOCKSTATE_TO_ID);
            for (IBlockState state : block.getBlockState().getValidStates())
            {
                final int meta = block.getMetaFromState(state); // meta value the block assigns for the state
                final int bsmeta = blockId << 4 | meta; // computed blockstateid for that meta
                final IBlockState blockState = block.getStateFromMeta(meta); // state that the block assigns for the meta value
                blockstateMap.put(blockState, bsmeta); // store assigned state with computed blockstateid
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onClear(Map<ResourceLocation, ?> slaveset)
        {
            ClearableObjectIntIdentityMap<IBlockState> blockstateMap = (ClearableObjectIntIdentityMap<IBlockState>)slaveset.get(BLOCKSTATE_TO_ID);
            blockstateMap.clear();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset)
        {
            final ClearableObjectIntIdentityMap<Block> idMap = new ClearableObjectIntIdentityMap<Block>();
            ((Map<ResourceLocation,Object>)slaveset).put(BLOCKSTATE_TO_ID, idMap);

        }
    }

    private static class ItemCallbacks implements IForgeRegistry.AddCallback<Item>,IForgeRegistry.ClearCallback<Item>,IForgeRegistry.CreateCallback<Item>
    {
        static final ItemCallbacks INSTANCE = new ItemCallbacks();

        @Override
        public void onAdd(Item item, int blockId, Map<ResourceLocation, ?> slaves)
        {
            if (item instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock)item;
                @SuppressWarnings("unchecked") BiMap<Block, Item> blockToItem = (BiMap<Block, Item>)slaves.get(BLOCK_TO_ITEM);
                blockToItem.forcePut(itemBlock.getBlock().delegate.get(), item);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onClear(Map<ResourceLocation, ?> slaveset)
        {
            Map<Block,Item> map = (Map<Block, Item>)slaveset.get(BLOCK_TO_ITEM);
            map.clear();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset)
        {
            final HashBiMap<Block, Item> map = HashBiMap.create();
            ((Map<ResourceLocation,Object>)slaveset).put(BLOCK_TO_ITEM, map);
        }
    }

    private static class PotionCallbacks implements IForgeRegistry.AddCallback<Potion>,IForgeRegistry.ClearCallback<Potion>,IForgeRegistry.CreateCallback<Potion>
    {
        static final PotionCallbacks INSTANCE = new PotionCallbacks();

        @Override
        public void onAdd(Potion potion, int id, Map<ResourceLocation, ?> slaves) {
            // no op for the minute?
        }

        @Override
        public void onClear(Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }

        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }
    }
    private static class BiomeCallbacks implements IForgeRegistry.AddCallback<Biome>,IForgeRegistry.ClearCallback<Biome>,IForgeRegistry.CreateCallback<Biome>
    {
        static final BiomeCallbacks INSTANCE = new BiomeCallbacks();

        @Override
        public void onAdd(Biome potion, int id, Map<ResourceLocation, ?> slaves) {
            // no op for the minute?
        }

        @Override
        public void onClear(Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }

        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }
    }
}
