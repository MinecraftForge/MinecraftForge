package net.minecraftforge.common;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag.Builder;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.tags.TagRegistry;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeTagHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<ResourceLocation> registryNames = new HashSet<>();

    /**
     * Creates and "registers" (or gets) a TagRegistry based on the passed in IForgeRegistry. This has to be called after RegistryEvents are done.
     *
     * @param reg The registry behind the tag type
     * @param <T> The Tag type
     *
     * @return A TagRegistry used to create any tags in code for this type
     */
    public synchronized static <T extends IForgeRegistryEntry<T>> TagRegistry<T> createTagType(IForgeRegistry<T> reg)
    {
        ResourceLocation registryName = reg.getRegistryName();
        if (TagRegistryManager.contains(registryName))
        {
            //TODO: Log that it already existed? (Use different than old one though in case they try to call it on the blocks registry or something)
            //LOGGER.info("Another mod has already added this registry to be a tag!");
            return (TagRegistry<T>) TagRegistryManager.get(registryName);
        }
        registryNames.add(registryName);
        return TagRegistryManager.func_242196_a(registryName, tagCollectionSupplier -> (ITagCollection<T>) tagCollectionSupplier.modded().get(registryName));
    }

    public static Map<ResourceLocation, TagCollectionReader<?>> createModdedTagReaders()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        ImmutableMap.Builder<ResourceLocation, TagCollectionReader<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : registryNames)
        {
            IForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry != null)
            {
                //TODO: Adjust this so that the tags/thing is plural? Like blocks vs block
                builder.put(registryName, new TagCollectionReader<>(rl -> Optional.ofNullable(registry.getValue(rl)), "tags/" + registryName.getPath(), registryName.getPath()));
            }
        }
        return builder.build();
    }

    //TODO: Update docs and maybe move this to IForgeTagCollectionSupplier?
    /**
     * The {@link ITagCollectionSupplier} can be retrieved through {@link World#getTags()} or {@link TagCollectionManager#func_242178_a()} if context is unavailable, when
     * serializing and deserializing something tag based for instance.
     */
    public static ITagCollectionSupplier getModdedTagCollectionSupplier(ITagCollection<Block> blockTags, ITagCollection<Item> itemTags, ITagCollection<Fluid> fluidTags, ITagCollection<EntityType<?>> entityTypeTags, Map<ResourceLocation, ITagCollection<?>> modded)
    {
        return new ITagCollectionSupplier()
        {
            @Override
            public ITagCollection<Block> func_241835_a()
            {
                return blockTags;
            }

            @Override
            public ITagCollection<Item> func_241836_b()
            {
                return itemTags;
            }

            @Override
            public ITagCollection<Fluid> func_241837_c()
            {
                return fluidTags;
            }

            @Override
            public ITagCollection<EntityType<?>> func_241838_d()
            {
                return entityTypeTags;
            }

            @Override
            public Map<ResourceLocation, ITagCollection<?>> modded()
            {
                return modded;
            }
        };
    }

    public static ITagCollectionSupplier populateTagCollectionManager(ITagCollection<Block> blockTags, ITagCollection<Item> itemTags, ITagCollection<Fluid> fluidTags, ITagCollection<EntityType<?>> entityTypeTags)
    {
        ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : registryNames)
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
        return getModdedTagCollectionSupplier(blockTags, itemTags, fluidTags, entityTypeTags, modded);
    }

    public static CompletableFuture<List<Pair<ResourceLocation, Pair<TagCollectionReader<?>, Map<ResourceLocation, Builder>>>>> getModdedTagReloadResults(IResourceManager resourceManager, Executor backgroundExecutor, Map<ResourceLocation, TagCollectionReader<?>> readers)
    {
        CompletableFuture<List<Pair<ResourceLocation, Pair<TagCollectionReader<?>, Map<ResourceLocation, Builder>>>>> moddedResults = CompletableFuture.completedFuture(new ArrayList<>());
        for (Map.Entry<ResourceLocation, TagCollectionReader<?>> entry : readers.entrySet())
        {
            TagCollectionReader<?> reader = entry.getValue();
            moddedResults = moddedResults.thenCombine(reader.func_242224_a(resourceManager, backgroundExecutor), (results, result) -> {
                results.add(new Pair<>(entry.getKey(), new Pair<>(reader, result)));
                return results;
            });
        }
        return moddedResults;
    }
}