package net.minecraftforge.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeTagHandler
{

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<IForgeRegistry<?>> registries = new HashSet<>();

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
        //TODO: Don't allow creating "duplicates" of the vanilla tag types
        if (registries.contains(reg))
        {
            //TODO: Change logging level if we keep it at all
            LOGGER.info("Another mod has already added this registry to be a tag!");
            return (TagRegistry<T>) TagRegistryManager.get(reg.getRegistryName());
        }
        registries.add(reg);
        return TagRegistryManager.func_242196_a(reg.getRegistryName(), itcs -> (ITagCollection<T>) itcs.modded().get(reg.getRegistryName()));
    }

    public static Map<ResourceLocation, TagCollectionReader<?>> createModdedTagReaders()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        //TODO: Adjust this so that the tags/thing is plural? Like blocks vs block
        return registries.stream().collect(Collectors.toMap(
              IForgeRegistry::getRegistryName,
              reg -> new TagCollectionReader<>(rl -> Optional.ofNullable(reg.getValue(rl)), "tags/" + reg.getRegistryName().getPath(), reg.getRegistryName().getPath())
        ));
    }

    /**
     * The {@link ITagCollectionSupplier} can be retrieved through {@link World#getTags()} or {@link TagCollectionManager#func_242178_a()} if context is unavailable, when
     * serializing and deserializing something tag based for instance.
     */
    public static ITagCollectionSupplier getModdedTagCollectionSupplier(ITagCollection<Block> blockTags, ITagCollection<Item> itemTags, ITagCollection<Fluid> fluidTags,
          ITagCollection<EntityType<?>> entityTypeTags, Map<ResourceLocation, ITagCollection<?>> modded)
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

    public static ITagCollectionSupplier populateTagCollectionManager(ITagCollection<Block> blockTags, ITagCollection<Item> itemTags, ITagCollection<Fluid> fluidTags,
          ITagCollection<EntityType<?>> entityTypeTags)
    {
        Map<ResourceLocation, ITagCollection<?>> modded = new HashMap<>();
        for (IForgeRegistry<?> reg : registries)
        {
            TagRegistry<?> tagRegistry = TagRegistryManager.get(reg.getRegistryName());
            if (tagRegistry != null)
            {
                modded.put(reg.getRegistryName(), ITagCollection.func_242202_a(collectTags(tagRegistry)));
            }
        }
        if (!modded.isEmpty())
        {
            LOGGER.debug("Populated the TagCollectionManager with {} extra types", modded.size());
        }
        return getModdedTagCollectionSupplier(blockTags, itemTags, fluidTags, entityTypeTags, modded);
    }

    private static <T> Map<ResourceLocation, ITag<T>> collectTags(TagRegistry<T> tagRegistry)
    {
        return tagRegistry.func_241288_c_().stream().collect(Collectors.toMap(INamedTag::func_230234_a_, namedTag -> namedTag, (a, b) -> b));
    }
}