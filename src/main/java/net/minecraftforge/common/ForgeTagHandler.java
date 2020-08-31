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
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: Nuke the cached customTagTypes map when connecting to say a vanilla server after having loaded tags, as we won't have received a packet to update them
//TODO: keep track of generation index, both of vanilla tags and of custom tags and make sure that they match
public class ForgeTagHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Map<ResourceLocation, ITagCollection<?>> customTagTypes = Collections.emptyMap();
    private static Set<ResourceLocation> customTagTypeNames = Collections.emptySet();
    private static boolean tagTypesSet = false;

    public static void setCustomTagTypes(Set<ResourceLocation> customTagTypes)
    {
        if (tagTypesSet) throw new RuntimeException("Custom tag types have already been set, this method should only be called by forge, and after registries are initialized");
        tagTypesSet = true;
        customTagTypeNames = ImmutableSet.copyOf(customTagTypes);
        TagRegistry.performDelayedAdd();
    }

    public static Set<ResourceLocation> getCustomTagTypeNames()
    {
        return customTagTypeNames;
    }

    public static Map<ResourceLocation, ITagCollection<?>> getCustomTagTypes() {
        return customTagTypes;
    }

    public static void updateCustomTagTypes(Map<ResourceLocation, ITagCollection<?>> customTagTypes) {
        //TODO: Implement this in a slightly less "public" API way
        ForgeTagHandler.customTagTypes = customTagTypes;
    }

    @Nullable
    private static <T extends IForgeRegistryEntry<T>> TagRegistry<T> getTagRegistry(IForgeRegistry<T> registry)
    {
        return (TagRegistry<T>) TagRegistryManager.get(registry.getRegistryName());
    }

    private static void validateRegistrySupportsTags(IForgeRegistry<?> registry) {
        //Note: We also check against getTagRegistry in case someone decides to use the helpers for tag creation for types supported by vanilla
        if (getTagRegistry(registry) == null && (!(registry instanceof ForgeRegistry) || ((ForgeRegistry<?>) registry).getTagFolder() == null))
        {
            throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
        }
    }

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

    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(IForgeRegistry<T> registry, ResourceLocation name)
    {
        return createOptionalTag(registry, name, null);
    }

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

    //TODO: State in java docs for these three methods that they are helpers for making tags for custom registries, and that they don't do validation initially
    // to ensure that the registry type does actually support having tags, and will explode slightly later down the line once the registry is created and it doesn't
    // support them
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

    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(ResourceLocation registryName, ResourceLocation name)
    {
        return createOptionalTag(registryName, name, null);
    }

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

    public static Map<ResourceLocation, TagCollectionReader<?>> createCustomTagTypeReaders()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        ImmutableMap.Builder<ResourceLocation, TagCollectionReader<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : customTagTypeNames)
        {
            ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry != null)
            {
                String tagFolder = registry.getTagFolder();
                if (tagFolder != null)
                {
                    builder.put(registryName, new TagCollectionReader<>(rl -> Optional.ofNullable(registry.getValue(rl)), "tags/" + tagFolder, registryName.getPath()));
                }
            }
        }
        return builder.build();
    }

    //TODO: Re-evaluate this method
    public static ITagCollectionSupplier populateTagCollectionManager(ITagCollection<Block> blockTags, ITagCollection<Item> itemTags, ITagCollection<Fluid> fluidTags, ITagCollection<EntityType<?>> entityTypeTags)
    {
        ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : customTagTypeNames)
        {
            TagRegistry<?> tagRegistry = TagRegistryManager.get(registryName);
            if (tagRegistry != null)
            {
                builder.put(registryName, ITagCollection.func_242202_a(tagRegistry.func_241288_c_().stream().collect(Collectors.toMap(INamedTag::func_230234_a_, namedTag -> namedTag))));
            }
        }
        Map<ResourceLocation, ITagCollection<?>> modded = builder.build();
        if (!modded.isEmpty())
        {
            LOGGER.debug("Populated the TagCollectionManager with {} extra types", modded.size());
        }
        updateCustomTagTypes(modded);
        return ITagCollectionSupplier.func_242209_a(blockTags, itemTags, fluidTags, entityTypeTags);
    }

    //Update from NetworkTagManager
    public static void updateCustomTagTypes(List<TagCollectionReaderInfo> tagCollectionReaders) {
        ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
        for (TagCollectionReaderInfo info : tagCollectionReaders)
        {
            builder.put(info.tagType, info.reader.func_242226_a(info.tagBuilders));
        }
        updateCustomTagTypes(builder.build());
    }

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