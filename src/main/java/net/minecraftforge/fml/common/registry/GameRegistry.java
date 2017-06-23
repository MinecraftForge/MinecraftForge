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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.IEntitySelectorFactory;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import javax.annotation.Nonnull;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GameRegistry
{
    private static Set<IWorldGenerator> worldGenerators = Sets.newHashSet();
    private static Map<IWorldGenerator, Integer> worldGeneratorIndex = Maps.newHashMap();
    private static List<IFuelHandler> fuelHandlers = Lists.newArrayList();
    private static List<IWorldGenerator> sortedGeneratorList;
    private static List<IEntitySelectorFactory> entitySelectorFactories = Lists.newArrayList();

    /**
     * Register a world generator - something that inserts new block types into the world
     *
     * @param generator           the generator
     * @param modGenerationWeight a weight to assign to this generator. Heavy weights tend to sink to the bottom of
     *                            list of world generators (i.e. they run later)
     */
    public static void registerWorldGenerator(IWorldGenerator generator, int modGenerationWeight)
    {
        worldGenerators.add(generator);
        worldGeneratorIndex.put(generator, modGenerationWeight);
        if (sortedGeneratorList != null)
        {
            sortedGeneratorList = null;
        }
    }

    /**
     * Registers a entity selector factory which is used to create predicates whenever a command containing selectors is executed
     * Any non vanilla arguments that you expect has to be registered. Otherwise Minecraft will throw an CommandException on usage.
     *
     * If you want to react to a command like "/kill @e[xyz=5]", you would have to register the argument "xyz" here and check for that argument in the factory.
     * One factory can listen to any number of arguments as long as they are registered here.
     *
     * For inter mod compatibility you might want to use "modid:xyz" (e.g. "forge:min_health") as argument.
     *
     * For an example usage, see CustomEntitySelectorTest
     * @param arguments Expected string arguments in commands
     */
    public static void registerEntitySelector(IEntitySelectorFactory factory, String... arguments)
    {
        entitySelectorFactories.add(factory);
        for (String s : arguments)
        {
            EntitySelector.addArgument(s);
        }
    }

    /**
     * Creates a list of entity selectors using the registered factories.
     * Should probably only be called by Forge
     */
    public static List<Predicate<Entity>> createEntitySelectors(Map<String, String> arguments, String mainSelector, ICommandSender sender, Vec3d position)
    {
        List<Predicate<Entity>> selectors = Lists.newArrayList();
        for (IEntitySelectorFactory factory : entitySelectorFactories)
        {
            try
            {
                selectors.addAll(factory.createPredicates(arguments, mainSelector, sender, position));
            }
            catch (Exception e)
            {
                FMLLog.log.error("Exception caught during entity selector creation with {} for argument map {} of {} for {} at {}", factory,
                        arguments, mainSelector, sender, position, e);
            }
        }
        return selectors;
    }

    /**
     * Callback hook for world gen - if your mod wishes to add extra mod related generation to the world
     * call this
     *
     * @param chunkX         Chunk X coordinate
     * @param chunkZ         Chunk Z coordinate
     * @param world          World we're generating into
     * @param chunkGenerator The chunk generator
     * @param chunkProvider  The chunk provider
     */
    public static void generateWorld(int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (sortedGeneratorList == null)
        {
            computeSortedGeneratorList();
        }
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed;

        for (IWorldGenerator generator : sortedGeneratorList)
        {
            fmlRandom.setSeed(chunkSeed);
            generator.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }

    private static void computeSortedGeneratorList()
    {
        ArrayList<IWorldGenerator> list = Lists.newArrayList(worldGenerators);
        Collections.sort(list, new Comparator<IWorldGenerator>()
        {
            @Override
            public int compare(IWorldGenerator o1, IWorldGenerator o2)
            {
                return Ints.compare(worldGeneratorIndex.get(o1), worldGeneratorIndex.get(o2));
            }
        });
        sortedGeneratorList = ImmutableList.copyOf(list);
    }

    /**
     * Register the previously named {@link IForgeRegistry} object with the registry system.
     * Always ensure the object is already named using {@link IForgeRegistryEntry#setRegistryName(ResourceLocation)}
     * or another mechanism.
     *
     * Note: That DOES NOT create the ItemBlock for you if this is a Block, you should register that item separately.
     *
     * @param object The object to register with a registry
     * @param <K> The registry supertype
     * @throws IllegalArgumentException if the object is not yet named (use {@link #register(IForgeRegistryEntry, ResourceLocation)} instead)
     * @return The object
     */
    @SuppressWarnings("unchecked")
    public static <K extends IForgeRegistryEntry<?>> K register(K object)
    {
        return (K)GameData.register_impl(object);
    }

    /**
     * Register the unnamed {@link IForgeRegistry} object with the registry system.
     * Always make sure you have not previously named the object.
     *
     * It is advised that you set the object's registry name and use {@link #register(IForgeRegistryEntry)} instead.
     *
     * Note: That DOES NOT create the ItemBlock for you if this is a Block, you should register that item separately.
     *
     * @param object The object to register
     * @param name The name to register it with
     * @param <K> The registry supertype
     * @throws IllegalStateException if the object already has an existing name (use {@link #register(IForgeRegistryEntry)} instead)
     * @return The object
     */
    @SuppressWarnings("unchecked")
    public static <K extends IForgeRegistryEntry<?>> K register(K object, ResourceLocation name)
    {
        return (K)GameData.register_impl(object, name);
    }


    /**
     * Registers a named block with the Block registry. This WILL create a new ItemBlock for you with the
     * same name and register it with the ItemRegistry. This method is created as a convince method for
     * modder and SHOULD NOT be used. Modders should create and register their ItemBlocks like normal items.
     *
     * @param block The block to register with a registry
     * @throws IllegalArgumentException if the object is not yet named
     * @return The block
     */
    @Deprecated //Modders SHOULD NOT use this, so it'll stay deprecated. Purely added to make lazy modders happy -.-
    public static Block registerWithItem(Block block)
    {
        register(block);
        register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        return block;
    }

    /**
     * Retrieves the registry associated with this super class type.
     * If the return is non-null it is HIGHLY recommended that modders cache this
     * value as the return will never change for a given type in a single run of Minecraft once set.
     *
     * @param registryType The base class of items in this registry.
     * @return The registry, Null if none is registered.
     */
    public static <K extends IForgeRegistryEntry<K>> IForgeRegistry<K> findRegistry(Class<K> registryType)
    {
        return PersistentRegistryManager.findRegistryByType(registryType);
    }

    /**
     * Add a forced persistent substitution alias for the block or item to another block or item. This will have
     * the effect of using the substituted block or item instead of the original, where ever it is
     * referenced.
     *
     * @param nameToSubstitute The name to link to (this is the NEW block or item)
     * @param type             The type (Block or Item)
     * @param object           a NEW instance that is type compatible with the existing instance
     * @throws ExistingSubstitutionException     if someone else has already registered an alias either from or to one of the names
     * @throws IncompatibleSubstitutionException if the substitution is incompatible
     */
    public static void addSubstitutionAlias(String nameToSubstitute, GameRegistry.Type type, Object object) throws ExistingSubstitutionException
    {
        GameData.getMain().registerSubstitutionAlias(nameToSubstitute, type, object);
    }

    @Deprecated //TODO Make IRecipe a registry
    public static IRecipe addShapedRecipe(ResourceLocation name, @Nonnull ItemStack output, Object... params)
    {
        throw new RuntimeException("TODO: Forge implement IRecipe registry");
        //return CraftingManager.addRecipe(output, params);
    }

    @Deprecated //TODO Make IRecipe a registry
    public static void addShapelessRecipe(ResourceLocation name, @Nonnull ItemStack output, Ingredient... params)
    {
        throw new RuntimeException("TODO: Forge implement IRecipe registry");
        //CraftingManager.getInstance().addShapelessRecipe(output, params);
    }

    @Deprecated //TODO Make IRecipe a registry
    public static void addRecipe(ResourceLocation name, IRecipe recipe)
    {
        throw new RuntimeException("TODO: Forge implement IRecipe registry");
        //CraftingManager.getInstance().getRecipeList().add(recipe);
    }

    public static void addSmelting(Block input, @Nonnull ItemStack output, float xp)
    {
        FurnaceRecipes.instance().addSmeltingRecipeForBlock(input, output, xp);
    }

    public static void addSmelting(Item input, @Nonnull ItemStack output, float xp)
    {
        FurnaceRecipes.instance().addSmelting(input, output, xp);
    }

    public static void addSmelting(@Nonnull ItemStack input, @Nonnull ItemStack output, float xp)
    {
        FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }

    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id)
    {
        GameData.getTileEntityRegistry().putObject(new ResourceLocation(id), tileEntityClass);
    }

    /**
     * Register a tile entity, with alternative TileEntity identifiers. Use with caution!
     * This method allows for you to "rename" the 'id' of the tile entity.
     *
     * @param tileEntityClass The tileEntity class to register
     * @param id              The primary ID, this will be the ID that the tileentity saves as
     * @param alternatives    A list of alternative IDs that will also map to this class. These will never save, but they will load
     */
    public static void registerTileEntityWithAlternatives(Class<? extends TileEntity> tileEntityClass, String id, String... alternatives)
    {
        GameRegistry.registerTileEntity(tileEntityClass, id);
        for (String s : alternatives)
        {
            GameData.getTileEntityRegistry().addLegacyName(new ResourceLocation(s), new ResourceLocation(id));
        }
    }

    public static void registerFuelHandler(IFuelHandler handler)
    {
        fuelHandlers.add(handler);
    }

    public static int getFuelValue(@Nonnull ItemStack itemStack)
    {
        int fuelValue = 0;
        for (IFuelHandler handler : fuelHandlers)
        {
            fuelValue = Math.max(fuelValue, handler.getBurnTime(itemStack));
        }
        return fuelValue;
    }

    public enum Type
    {
        BLOCK,
        ITEM;
    }

    /**
     * ObjectHolder can be used to automatically populate public static final fields with entries
     * from the registry. These values can then be referred within mod code directly.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD})
    public @interface ObjectHolder
    {
        /**
         * If used on a class, this represents a modid only.
         * If used on a field, it represents a name, which can be abbreviated or complete.
         * Abbreviated names derive their modid from an enclosing ObjectHolder at the class level.
         *
         * @return either a modid or a name based on the rules above
         */
        String value();
    }

    /**
     * ItemStackHolder can be used to automatically populate public static final fields with
     * {@link ItemStack} instances, referring a specific item, potentially configured with NBT.
     * These values can then be used in things like recipes and other places where ItemStacks
     * might be required.
     * <p/>
     * If the item is not found, the field will be populated with null.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ItemStackHolder
    {
        /**
         * The registry name of the item being looked up.
         *
         * @return The registry name
         */
        public String value();

        /**
         * The metadata or damage value for the itemstack, defaults to 0.
         *
         * @return the metadata value
         */
        public int meta() default 0;

        /**
         * The string serialized nbt value for the itemstack. Defaults to empty for no nbt.
         *
         * @return a nbt string
         */
        public String nbt() default "";
    }

    /**
     * Makes an {@link ItemStack} based on the itemName reference, with supplied meta, stackSize and nbt, if possible
     * <p/>
     * Will return null if the item doesn't exist (because it's not from a loaded mod for example)
     * Will throw a {@link RuntimeException} if the nbtString is invalid for use in an {@link ItemStack}
     *
     * @param itemName  a registry name reference
     * @param meta      the meta
     * @param stackSize the stack size
     * @param nbtString an nbt stack as a string, will be processed by {@link JsonToNBT}
     * @return a new itemstack
     */
    @Nonnull
    public static ItemStack makeItemStack(String itemName, int meta, int stackSize, String nbtString)
    {
        if (itemName == null)
        {
            throw new IllegalArgumentException("The itemName cannot be null");
        }
        Item item = GameData.getItemRegistry().getObject(new ResourceLocation(itemName));
        if (item == null)
        {
            FMLLog.log.trace("Unable to find item with name {}", itemName);
            return ItemStack.EMPTY;
        }
        ItemStack is = new ItemStack(item, stackSize, meta);
        if (!Strings.isNullOrEmpty(nbtString))
        {
            NBTBase nbttag = null;
            try
            {
                nbttag = JsonToNBT.getTagFromJson(nbtString);
            } catch (NBTException e)
            {
                FMLLog.log.warn("Encountered an exception parsing ItemStack NBT string {}", nbtString, e);
                throw Throwables.propagate(e);
            }
            if (!(nbttag instanceof NBTTagCompound))
            {
                FMLLog.log.warn("Unexpected NBT string - multiple values {}", nbtString);
                throw new RuntimeException("Invalid NBT JSON");
            }
            else
            {
                is.setTagCompound((NBTTagCompound)nbttag);
            }
        }
        return is;
    }
}
