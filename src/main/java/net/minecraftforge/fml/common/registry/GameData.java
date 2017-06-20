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

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

import com.google.common.collect.BiMap;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import static net.minecraftforge.fml.common.registry.PersistentRegistryManager.*;

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
    private static final int MIN_ENTITY_ID = 0;
    private static final int MAX_ENTITY_ID = Integer.MAX_VALUE >> 5; // Varint (SPacketSpawnMob)
    private static final int MIN_RECIPE_ID = 0;
    private static final int MAX_RECIPE_ID = Integer.MAX_VALUE >> 5; // Varint CPacketRecipeInfo/SPacketRecipeBook

    private static final ResourceLocation BLOCK_TO_ITEM = new ResourceLocation("minecraft:blocktoitemmap");
    private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");

    private static final GameData mainData = new GameData();

    private static Field blockField;

    public GameData()
    {
        iBlockRegistry       = (FMLControlledNamespacedRegistry<Block>)      makeRegistry(BLOCKS,       Block.class,       MIN_BLOCK_ID, MAX_BLOCK_ID).addCallback(BlockCallbacks.INSTANCE).enableDelegates().setDefaultKey(new ResourceLocation("air")).create();
        iItemRegistry        = (FMLControlledNamespacedRegistry<Item>)       makeRegistry(ITEMS,        Item.class,        MIN_ITEM_ID, MAX_ITEM_ID).addCallback(ItemCallbacks.INSTANCE).enableDelegates().create();
        iPotionRegistry      = (FMLControlledNamespacedRegistry<Potion>)     makeRegistry(POTIONS,      Potion.class,      MIN_POTION_ID, MAX_POTION_ID).addCallback(PotionCallbacks.INSTANCE).create();
        iBiomeRegistry       = (FMLControlledNamespacedRegistry<Biome>)      makeRegistry(BIOMES,       Biome.class,       MIN_BIOME_ID, MAX_BIOME_ID).addCallback(BiomeCallbacks.INSTANCE).create();
        iSoundEventRegistry  = (FMLControlledNamespacedRegistry<SoundEvent>) makeRegistry(SOUNDEVENTS,  SoundEvent.class,  MIN_SOUND_ID, MAX_SOUND_ID).create();
        iPotionTypeRegistry  = (FMLControlledNamespacedRegistry<PotionType>) makeRegistry(POTIONTYPES,  PotionType.class,  MIN_POTIONTYPE_ID, MAX_POTIONTYPE_ID).setDefaultKey(new ResourceLocation("water")).create();
        iEnchantmentRegistry = (FMLControlledNamespacedRegistry<Enchantment>)makeRegistry(ENCHANTMENTS, Enchantment.class, MIN_ENCHANTMENT_ID, MAX_ENCHANTMENT_ID).create();
        iEntityRegistry      = (FMLControlledNamespacedRegistry<EntityEntry>)makeRegistry(ENTITIES,     EntityEntry.class, MIN_ENTITY_ID, MAX_ENTITY_ID).addCallback(EntityCallbacks.INSTANCE).create();
        iRecipeRegistry      = (FMLControlledNamespacedRegistry<IRecipe>)    makeRegistry(RECIPES,      IRecipe.class,     MIN_RECIPE_ID, MAX_RECIPE_ID).disableSaving().create();

        registryTypeMap = Maps.newEnumMap(GameRegistry.Type.class);
        registryTypeMap.put(GameRegistry.Type.BLOCK, iBlockRegistry);
        registryTypeMap.put(GameRegistry.Type.ITEM, iItemRegistry);
        registryTypeMap.put(GameRegistry.Type.POTION, iPotionRegistry);
        registryTypeMap.put(GameRegistry.Type.POTION_TYPE, iPotionTypeRegistry);
        registryTypeMap.put(GameRegistry.Type.BIOME, iBiomeRegistry);
        registryTypeMap.put(GameRegistry.Type.ENCHANTMENT, iEnchantmentRegistry);
        registryTypeMap.put(GameRegistry.Type.SOUND_EVENT, iSoundEventRegistry);
        registryTypeMap.put(GameRegistry.Type.ENTITY, iEntityRegistry);

        try
        {
            blockField = FinalFieldHelper.makeWritable(ReflectionHelper.findField(ItemBlock.class, "block", "field_150939" + "_a"));
        }
        catch (Exception e)
        {
            FMLLog.log(Level.FATAL, e, "Cannot access the 'block' field from ItemBlock, this is fatal!");
            throw Throwables.propagate(e);
        }

        iTileEntityRegistry = new LegacyNamespacedRegistry<Class<? extends TileEntity>>();
    }

    private <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int min, int max)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setIDRange(min, max);
    }
    // internal registry objects
    private final FMLControlledNamespacedRegistry<Block> iBlockRegistry;
    private final FMLControlledNamespacedRegistry<Item> iItemRegistry;
    private final FMLControlledNamespacedRegistry<Potion> iPotionRegistry;
    private final FMLControlledNamespacedRegistry<Biome> iBiomeRegistry;
    private final FMLControlledNamespacedRegistry<SoundEvent> iSoundEventRegistry;
    private final FMLControlledNamespacedRegistry<PotionType> iPotionTypeRegistry;
    private final FMLControlledNamespacedRegistry<Enchantment> iEnchantmentRegistry;
    private final FMLControlledNamespacedRegistry<EntityEntry> iEntityRegistry;

    //TODO: ? These are never used by ID, so they don't need to be full registries/persisted.
    //Need cpw to decide how we want to go about this as they are generic registries that
    //don't follow the same patterns as the other ones.
    private final LegacyNamespacedRegistry<Class<? extends TileEntity>> iTileEntityRegistry;

    //TODO: This is not a recipe that is serilized to disc by ID, so we need to skip over any saving/loading from disc
    private final FMLControlledNamespacedRegistry<IRecipe> iRecipeRegistry;

    private final EnumMap<GameRegistry.Type,FMLControlledNamespacedRegistry> registryTypeMap;

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

    /** INTERNAL ONLY */
    @Deprecated
    public static LegacyNamespacedRegistry<Class<? extends TileEntity>> getTileEntityRegistry() { return getMain().iTileEntityRegistry; }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<EntityEntry> getEntityRegistry() { return getMain().iEntityRegistry; }

    /** INTERNAL ONLY */
    @Deprecated
    public static FMLControlledNamespacedRegistry<IRecipe> getRecipeRegistry() { return getMain().iRecipeRegistry; }

    protected static GameData getMain()
    {
        return mainData;
    }

    void registerSubstitutionAlias(String name, GameRegistry.Type type, Object toReplace) throws ExistingSubstitutionException
    {
        ResourceLocation nameToSubstitute = new ResourceLocation(name);
        FMLControlledNamespacedRegistry registry=registryTypeMap.get(type);
        if(registry==null){
            FMLLog.getLogger().log(Level.WARN,"Cannot register substitution. Registry for type %s not found",type);
        }
        else{
            registry.addSubstitutionAlias(Loader.instance().activeModContainer().getModId(),nameToSubstitute, (IForgeRegistryEntry) toReplace);
            registry.activateSubstitution(nameToSubstitute);
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

    private static class BlockCallbacks implements IForgeRegistry.AddCallback<Block>,IForgeRegistry.ClearCallback<Block>,IForgeRegistry.CreateCallback<Block>, IForgeRegistry.SubstitutionCallback<Block>
    {
        static final BlockCallbacks INSTANCE = new BlockCallbacks();

        @SuppressWarnings("unchecked")
        @Override
        public void onAdd(Block block, int blockId, Map<ResourceLocation,?> slaves)
        {
            ClearableObjectIntIdentityMap<IBlockState> blockstateMap = (ClearableObjectIntIdentityMap<IBlockState>)slaves.get(BLOCKSTATE_TO_ID);

            //So, due to blocks having more in-world states then metadata allows, we have to turn the map into a semi-milti-bimap.
            //We can do this however because the implementation of the map is last set wins. So we can add all states, then fix the meta bimap.
            //Multiple states -> meta. But meta to CORRECT state.

            final boolean[] usedMeta = new boolean[16]; //Hold a list of known meta from all states.
            for (IBlockState state : block.getBlockState().getValidStates())
            {
                final int meta = block.getMetaFromState(state);
                blockstateMap.put(state, blockId << 4 | meta); //Add ALL the things!
                usedMeta[meta] = true;
            }

            for (int meta = 0; meta < 16; meta++)
            {
                if (usedMeta[meta])
                    blockstateMap.put(block.getStateFromMeta(meta), blockId << 4 | meta); // Put the CORRECT thing!
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onClear(IForgeRegistry<Block> registry, Map<ResourceLocation, ?> slaveset)
        {
            ClearableObjectIntIdentityMap<IBlockState> blockstateMap = (ClearableObjectIntIdentityMap<IBlockState>)slaveset.get(BLOCKSTATE_TO_ID);
            blockstateMap.clear();
            final Map<ResourceLocation, Block> originals = (Map<ResourceLocation, Block>)slaveset.get(PersistentRegistryManager.SUBSTITUTION_ORIGINALS);
            final BiMap<Block, Item> blockItemMap = (BiMap<Block, Item>)slaveset.get(BLOCK_TO_ITEM);
            for (Item it : blockItemMap.values())
            {
                if (it instanceof ItemBlock) {
                    ItemBlock itemBlock = (ItemBlock)it;
                    final ResourceLocation registryKey = registry.getKey(itemBlock.block);
                    if (!originals.containsKey(registryKey)) continue;
                    try
                    {
                        FinalFieldHelper.setField(blockField, itemBlock, originals.get(registryKey));
                    }
                    catch (Exception e)
                    {
                        throw Throwables.propagate(e);
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
        {
            final ClearableObjectIntIdentityMap<IBlockState> idMap = new ClearableObjectIntIdentityMap<IBlockState>()
            {
                @SuppressWarnings("deprecation")
                @Override
                public int get(IBlockState key)
                {
                    Integer integer = (Integer)this.identityMap.get(key);
                    // There are some cases where this map is queried to serialize a state that is valid,
                    //but somehow not in this list, so attempt to get real metadata. Doing this hear saves us 7 patches
                    if (integer == null && key != null)
                        integer = this.identityMap.get(key.getBlock().getStateFromMeta(key.getBlock().getMetaFromState(key)));
                    return integer == null ? -1 : integer.intValue();
                }
            };
            ((Map<ResourceLocation,Object>)slaveset).put(BLOCKSTATE_TO_ID, idMap);
            final HashBiMap<Block, Item> map = HashBiMap.create();
            ((Map<ResourceLocation,Object>)slaveset).put(BLOCK_TO_ITEM, map);
        }

        @Override
        public void onSubstituteActivated(Map<ResourceLocation, ?> slaveset, Block original, Block replacement, ResourceLocation name)
        {
            final BiMap<Block, Item> blockItemMap = (BiMap<Block, Item>)slaveset.get(BLOCK_TO_ITEM);
            if (blockItemMap.containsKey(original))
            {
                Item i = blockItemMap.get(original);
                if (i instanceof ItemBlock)
                {
                    try
                    {
                        FinalFieldHelper.setField(blockField, i, replacement);
                    }
                    catch (Exception e)
                    {
                        throw Throwables.propagate(e);
                    }
                }
                blockItemMap.forcePut(replacement,i);
            }
        }
    }

    private static class ItemCallbacks implements IForgeRegistry.AddCallback<Item>,IForgeRegistry.ClearCallback<Item>,IForgeRegistry.CreateCallback<Item>, IForgeRegistry.SubstitutionCallback<Item>
    {
        static final ItemCallbacks INSTANCE = new ItemCallbacks();

        @Override
        public void onAdd(Item item, int blockId, Map<ResourceLocation, ?> slaves)
        {
            if (item instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock)item;
                @SuppressWarnings("unchecked") BiMap<Block, Item> blockToItem = (BiMap<Block, Item>)slaves.get(BLOCK_TO_ITEM);
                final Block block = itemBlock.getBlock().delegate.get();
                blockToItem.forcePut(block, item);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onClear(IForgeRegistry<Item> registry, Map<ResourceLocation, ?> slaveset)
        {
            Map<Block,Item> map = (Map<Block, Item>)slaveset.get(BLOCK_TO_ITEM);
            map.clear();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
        {
            // We share the blockItem map between items and blocks registries
            final BiMap blockItemMap = (BiMap<Block, Item>)registries.get(PersistentRegistryManager.BLOCKS).getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
            ((Map<ResourceLocation,Object>)slaveset).put(BLOCK_TO_ITEM, blockItemMap);
        }

        @Override
        public void onSubstituteActivated(Map<ResourceLocation, ?> slaveset, Item original, Item replacement, ResourceLocation name)
        {
            final BiMap<Block, Item> blockItemMap = (BiMap<Block, Item>)slaveset.get(BLOCK_TO_ITEM);
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
        public void onClear(IForgeRegistry<Potion> registry, Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }

        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
        {
            // no op for the minute?
        }
    }
    private static class BiomeCallbacks implements IForgeRegistry.AddCallback<Biome>,IForgeRegistry.ClearCallback<Biome>,IForgeRegistry.CreateCallback<Biome>
    {
        static final BiomeCallbacks INSTANCE = new BiomeCallbacks();

        @Override
        public void onAdd(Biome biome, int id, Map<ResourceLocation, ?> slaves) {
            // no op for the minute?
        }

        @Override
        public void onClear(IForgeRegistry<Biome> registry, Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }

        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
        {
            // no op for the minute?
        }
    }
    private static class EntityCallbacks implements IForgeRegistry.AddCallback<EntityEntry>,IForgeRegistry.ClearCallback<EntityEntry>,IForgeRegistry.CreateCallback<EntityEntry>
    {
        static final EntityCallbacks INSTANCE = new EntityCallbacks();

        @Override
        public void onAdd(EntityEntry entry, int id, Map<ResourceLocation, ?> slaves)
        {
            if (entry.getEgg() != null)
                EntityList.ENTITY_EGGS.put(entry.getRegistryName(), entry.getEgg());
        }

        @Override
        public void onClear(IForgeRegistry<EntityEntry> registry, Map<ResourceLocation, ?> slaveset)
        {
            // no op for the minute?
        }

        @Override
        public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
        {
            // no op for the minute?
        }
    }
}
