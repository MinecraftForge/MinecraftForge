/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.tags.TagRegistry;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fml.network.FMLPlayMessages.SyncCustomTagTypes;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeTagHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Map<ResourceLocation, ITagCollection<?>> customTagTypes = Collections.emptyMap();
    private static Set<ResourceLocation> customTagTypeNames = Collections.emptySet();
    private static boolean tagTypesSet = false;

    @Nullable
    private static <T extends IForgeRegistryEntry<T>> TagRegistry<T> getTagRegistry(IForgeRegistry<T> registry)
    {
        return (TagRegistry<T>) TagRegistryManager.get(registry.getRegistryName());
    }

    private static void validateRegistrySupportsTags(IForgeRegistry<?> registry)
    {
        //Note: We also check against getTagRegistry in case someone decides to use the helpers for tag creation for types supported by vanilla
        if (getTagRegistry(registry) == null && (!(registry instanceof ForgeRegistry) || ((ForgeRegistry<?>) registry).getTagFolder() == null))
        {
            throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
        }
    }

    /**
     * Helper method that creates a named tag for a forge registry, erroring if the registry doesn't support custom tag types. If the custom tag types
     * have not been set yet, this method falls back and creates the tag reference delaying adding it to the tag registry to allow for statically
     * initializing and referencing the tag.
     * @param registry Registry the tag is for
     * @param name     Name of the tag
     * @param <T>      Type of the registry
     * @return A named tag
     */
    public static <T extends IForgeRegistryEntry<T>> ITag.INamedTag<T> makeWrapperTag(IForgeRegistry<T> registry, ResourceLocation name)
    {
        validateRegistrySupportsTags(registry);
        if (tagTypesSet)
        {
            TagRegistry<T> tagRegistry = getTagRegistry(registry);
            if (tagRegistry == null) throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
            return tagRegistry.func_232937_a_(name.toString());
        }
        return TagRegistry.createDelayedTag(registry.getRegistryName(), name);
    }

    /**
     * Helper method that creates an optional tag for a forge registry, erroring if the registry doesn't support custom tag types. If the custom tag types
     * have not been set yet, this method falls back and creates the tag reference delaying adding it to the tag registry to allow for statically
     * initializing and referencing the tag.
     * @param registry Registry the tag is for
     * @param name     Name of the tag
     * @param <T>      Type of the registry
     * @return An optional tag
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(IForgeRegistry<T> registry, ResourceLocation name)
    {
        return createOptionalTag(registry, name, null);
    }

    /**
     * Helper method that creates an optional tag for a forge registry, erroring if the registry doesn't support custom tag types. If the custom tag types
     * have not been set yet, this method falls back and creates the tag reference delaying adding it to the tag registry to allow for statically
     * initializing and referencing the tag.
     * @param registry Registry the tag is for
     * @param name     Name of the tag
     * @param defaults Default values for the optional tag
     * @param <T>      Type of the registry
     * @return An optional tag
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(IForgeRegistry<T> registry, ResourceLocation name, @Nullable Supplier<Set<T>> defaults)
    {
        validateRegistrySupportsTags(registry);
        if (tagTypesSet)
        {
            TagRegistry<T> tagRegistry = getTagRegistry(registry);
            if (tagRegistry == null) throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
            return tagRegistry.createOptional(name, defaults);
        }
        return TagRegistry.createDelayedOptional(registry.getRegistryName(), name, defaults);
    }

    /**
     * Helper method for creating named tags for custom forge registries. If the custom tag types have not been set yet, this method falls back and creates
     * the tag reference delaying adding it to the tag registry to allow for statically initializing and referencing the tag.
     * @param registryName Name of the registry the tag is for
     * @param name         Name of the tag
     * @param <T>          Type of the registry
     * @return A named tag
     * @implNote This method only errors instantly if tag types have already been set, otherwise the error is delayed until after registries finish initializing
     * and we can validate if the custom registry really does support custom tags.
     */
    public static <T extends IForgeRegistryEntry<T>> ITag.INamedTag<T> makeWrapperTag(ResourceLocation registryName, ResourceLocation name)
    {
        if (tagTypesSet)
        {
            IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry == null) throw new IllegalArgumentException("Could not find registry named: " + registryName);
            return makeWrapperTag(registry, name);
        }
        return TagRegistry.createDelayedTag(registryName, name);
    }

    /**
     * Helper method for creating optional tags for custom forge registries. If the custom tag types have not been set yet, this method falls back and creates
     * the tag reference delaying adding it to the tag registry to allow for statically initializing and referencing the tag.
     * @param registryName Name of the registry the tag is for
     * @param name         Name of the tag
     * @param <T>          Type of the registry
     * @return An optional tag
     * @implNote This method only errors instantly if tag types have already been set, otherwise the error is delayed until after registries finish initializing
     * and we can validate if the custom registry really does support custom tags.
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(ResourceLocation registryName, ResourceLocation name)
    {
        return createOptionalTag(registryName, name, null);
    }

    /**
     * Helper method for creating optional tags for custom forge registries. If the custom tag types have not been set yet, this method falls back and creates
     * the tag reference delaying adding it to the tag registry to allow for statically initializing and referencing the tag.
     * @param registryName Name of the registry the tag is for
     * @param name         Name of the tag
     * @param defaults     Default values for the optional tag
     * @param <T>          Type of the registry
     * @return An optional tag
     * @implNote This method only errors instantly if tag types have already been set, otherwise the error is delayed until after registries finish initializing
     * and we can validate if the custom registry really does support custom tags.
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(ResourceLocation registryName, ResourceLocation name, @Nullable Supplier<Set<T>> defaults)
    {
        if (tagTypesSet)
        {
            IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry == null) throw new IllegalArgumentException("Could not find registry named: " + registryName);
            return createOptionalTag(registry, name, defaults);
        }
        return TagRegistry.createDelayedOptional(registryName, name, defaults);
    }

    /**
     * Gets the all the registry names of registries that support custom tag types.
     */
    public static Set<ResourceLocation> getCustomTagTypeNames()
    {
        return customTagTypeNames;
    }

    /**
     * Gets a map of registry name to tag collection for all custom tag types.
     *
     * @apiNote Prefer interacting with this via the current {@link ITagCollectionSupplier} and using one of the forge extension getCustomTypeCollection methods
     */
    public static Map<ResourceLocation, ITagCollection<?>> getCustomTagTypes()
    {
        return customTagTypes;
    }

    /**
     * Sets the set containing the resource locations representing the registry name of each forge registry that supports custom tag types.
     *
     * @apiNote Internal: Calling this manually <strong>WILL</strong> cause a crash to occur as it can only be called once, and is done so by
     * forge after all registries have been initialized.
     */
    public static void setCustomTagTypes(Set<ResourceLocation> customTagTypes)
    {
        if (tagTypesSet) throw new RuntimeException("Custom tag types have already been set, this method should only be called by forge, and after registries are initialized");
        tagTypesSet = true;
        customTagTypeNames = ImmutableSet.copyOf(customTagTypes);
        TagRegistry.performDelayedAdd();
    }

    /**
     * Creates a map for custom tag type to tag reader
     *
     * @apiNote Internal: For use by NetworkTagManager
     */
    public static Map<ResourceLocation, TagCollectionReader<?>> createCustomTagTypeReaders()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        ImmutableMap.Builder<ResourceLocation, TagCollectionReader<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : customTagTypeNames)
        {
            ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry != null && registry.getTagFolder() != null)
            {
                builder.put(registryName, new TagCollectionReader<>(rl -> Optional.ofNullable(registry.getValue(rl)), "tags/" + registry.getTagFolder(), registryName.getPath()));
            }
        }
        return builder.build();
    }

    /**
     * Resets the cached collections for the various custom tag types.
     *
     * @apiNote Internal
     */
    public static void resetCachedTagCollections()
    {
        ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : customTagTypeNames)
        {
            TagRegistry<?> tagRegistry = TagRegistryManager.get(registryName);
            if (tagRegistry != null)
            {
                builder.put(registryName, ITagCollection.func_242202_a(tagRegistry.func_241288_c_().stream().distinct().collect(Collectors.toMap(INamedTag::func_230234_a_, namedTag -> namedTag))));
            }
        }
        customTagTypes = builder.build();
    }

    /**
     * Used to ensure that all custom tag types have a defaulted collection when vanilla is initializing a defaulted TagCollectionManager
     *
     * @apiNote Internal: For use by TagCollectionManager
     */
    public static ITagCollectionSupplier populateTagCollectionManager(ITagCollection<Block> blockTags, ITagCollection<Item> itemTags, ITagCollection<Fluid> fluidTags, ITagCollection<EntityType<?>> entityTypeTags)
    {
        //Default the tag collections
        resetCachedTagCollections();
        if (!customTagTypes.isEmpty())
        {
            LOGGER.debug("Populated the TagCollectionManager with {} extra types", customTagTypes.size());
        }
        return ITagCollectionSupplier.func_242209_a(blockTags, itemTags, fluidTags, entityTypeTags);
    }

    /**
     * Updates the custom tag types' tags from reloading via NetworkTagManager
     *
     * @apiNote Internal: For use by NetworkTagManager
     */
    public static void updateCustomTagTypes(List<TagCollectionReaderInfo> tagCollectionReaders)
    {
        ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
        for (TagCollectionReaderInfo info : tagCollectionReaders)
        {
            builder.put(info.tagType, info.reader.func_242226_a(info.tagBuilders));
        }
        customTagTypes = builder.build();
    }

    /**
     * Updates the custom tag types' tags from packet
     *
     * @apiNote Internal
     */
    public static void updateCustomTagTypes(SyncCustomTagTypes packet)
    {
        customTagTypes = packet.getCustomTagTypes();
    }

    /**
     * Gets the completable future containing the reload results for all custom tag types.
     *
     * @apiNote Internal: For use by NetworkTagManager
     */
    public static CompletableFuture<List<TagCollectionReaderInfo>> getCustomTagTypeReloadResults(IResourceManager resourceManager, Executor backgroundExecutor, Map<ResourceLocation, TagCollectionReader<?>> readers)
    {
        CompletableFuture<List<TagCollectionReaderInfo>> customResults = CompletableFuture.completedFuture(new ArrayList<>());
        for (Map.Entry<ResourceLocation, TagCollectionReader<?>> entry : readers.entrySet())
        {
            customResults = customResults.thenCombine(entry.getValue().func_242224_a(resourceManager, backgroundExecutor), (results, result) -> {
                results.add(new TagCollectionReaderInfo(entry.getKey(), entry.getValue(), result));
                return results;
            });
        }
        return customResults;
    }

    /**
     * Helper storage class for keeping track of various data for all custom tag types in the NetworkTagReader to make the code easier to read.
     *
     * @apiNote Internal: For use by NetworkTagManager
     */
    public static class TagCollectionReaderInfo
    {

        private final ResourceLocation tagType;
        private final TagCollectionReader<?> reader;
        private final Map<ResourceLocation, ITag.Builder> tagBuilders;

        private TagCollectionReaderInfo(ResourceLocation tagType, TagCollectionReader<?> reader, Map<ResourceLocation, ITag.Builder> tagBuilders)
        {
            this.tagType = tagType;
            this.reader = reader;
            this.tagBuilders = tagBuilders;
        }
    }
}