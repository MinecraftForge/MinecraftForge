package net.minecraftforge.common;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
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
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: Store modded tags as static and in this class? And then reference the modded tags via IForgeTagCollectionSupplier but not actually require it
// to have it in each individual instance?
//TODO: We can easily make it so that it "nukes" the cache when reloads happen, but how would we best nuke it when connecting to say a vanilla server after
// having loaded tags, or would that even matter
public class ForgeTagHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static Map<ResourceLocation, TagCollectionReader<?>> createModdedTagReaders()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        ImmutableMap.Builder<ResourceLocation, TagCollectionReader<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : TagRegistryManager.getCustomTagTypes())
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
        for (ResourceLocation registryName : TagRegistryManager.getCustomTagTypes())
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

    public static CompletableFuture<List<Pair<ResourceLocation, Pair<TagCollectionReader<?>, Map<ResourceLocation, ITag.Builder>>>>> getModdedTagReloadResults(IResourceManager resourceManager, Executor backgroundExecutor, Map<ResourceLocation, TagCollectionReader<?>> readers)
    {
        CompletableFuture<List<Pair<ResourceLocation, Pair<TagCollectionReader<?>, Map<ResourceLocation, ITag.Builder>>>>> moddedResults = CompletableFuture.completedFuture(new ArrayList<>());
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