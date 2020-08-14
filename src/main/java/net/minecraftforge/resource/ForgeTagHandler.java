package net.minecraftforge.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ForgeTagHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<IForgeRegistry<?>> registries = new HashSet<>();

    /**
     * Creates and "registers" (or gets) a TagRegistry based on the passed in IForgeRegistry.
     * This has to be called after RegistryEvents are done.
     *
     * @param   reg The registry behind the tag type
     * @param   <T> The Tag type
     * @return A TagRegistry used to create any tags in code for this type
     */
    public synchronized static <T extends IForgeRegistryEntry<T>> TagRegistry<T> createTagType(IForgeRegistry<T> reg)
    {
        if(registries.contains(reg))
        {
            LOGGER.info("Another mod has already added this registry to be a tag!");
            return (TagRegistry<T>) TagRegistryManager.get(reg.getRegistryName());
        }
        registries.add(reg);
        return TagRegistryManager.func_242196_a(reg.getRegistryName(), itcs -> (ITagCollection<T>) itcs.modded().get(reg.getRegistryName()));
    }

    public static Map<ResourceLocation, TagCollectionReader<?>> createModdedTagTypes()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        return registries.stream()
                .collect(Collectors.toMap(
                        IForgeRegistry::getRegistryName,
                        reg -> new TagCollectionReader<>(rl -> Optional.ofNullable(reg.getValue(rl)), "tags/" + reg.getRegistryName().getPath(), reg.getRegistryName().getPath())
                ));
    }

    /**
     * The {@link ITagCollectionSupplier} can be retrieved through {@link World#getTags()} or
     * {@link TagCollectionManager#func_242178_a()} if context is unavailable, when serializing and deserializing something tag based for instance.
     */
    public static ITagCollectionSupplier getModdedTagCollectionSupplier(ITagCollectionSupplier base, Map<ResourceLocation, ITagCollection<?>> modded)
    {
        return new ITagCollectionSupplier()
        {
            @Override
            public ITagCollection<Block> func_241835_a()
            {
                return base.func_241835_a();
            }

            @Override
            public ITagCollection<Item> func_241836_b()
            {
                return base.func_241836_b();
            }

            @Override
            public ITagCollection<Fluid> func_241837_c()
            {
                return base.func_241837_c();
            }

            @Override
            public ITagCollection<EntityType<?>> func_241838_d()
            {
                return base.func_241838_d();
            }

            @Override
            public Map<ResourceLocation, ITagCollection<?>> modded()
            {
                return modded;
            }
        };
    }

    public static ITagCollectionSupplier populateTagCollectionManager(ITagCollectionSupplier base)
    {
        Map<ResourceLocation, ITagCollection<?>> modded = registries
                .stream()
                .collect(Collectors.toMap(
                        IForgeRegistry::getRegistryName,
                        reg -> ITagCollection.func_242202_a((Map) //HACKS?
                                TagRegistryManager.get(reg.getRegistryName()).func_241288_c_()
                                        .stream()
                                        .collect(Collectors.toMap(ITag.INamedTag::func_230234_a_, Function.identity())))));
        if(!modded.isEmpty())
            LOGGER.debug("Populated the TagCollectionManager with {} extra types", modded.size());
        return getModdedTagCollectionSupplier(base, modded);
    }

    public static void moddedCollectionWrite(PacketBuffer buffer, Map<ResourceLocation, ITagCollection<?>> modded)
    {
        buffer.writeInt(modded.size());
        modded.forEach((rl, tags) -> forgeTagCollectionWrite(buffer, rl, tags));
    }

    public static Map<ResourceLocation, ITagCollection<?>> moddedCollectionRead(PacketBuffer buffer)
    {
        int size = buffer.readVarInt();
        ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
        for(int i = 0; i < size; i ++)
        {
            ResourceLocation name = buffer.readResourceLocation();
            builder.put(name, forgeTagCollectionRead(buffer, name));
        }
        return builder.build();
    }

    //mostly copied from ITagCollection.
    private static <T> void forgeTagCollectionWrite(PacketBuffer buffer, ResourceLocation regName, ITagCollection<T> modded)
    {
        IForgeRegistry<?> reg = RegistryManager.ACTIVE.getRegistry(regName);
        buffer.writeResourceLocation(regName);
        Map<ResourceLocation, ITag<T>> map = modded.func_241833_a();
        buffer.writeVarInt(map.size());

        try
        {
            for(Map.Entry<ResourceLocation, ITag<T>> entry : map.entrySet())
            {
                buffer.writeResourceLocation(entry.getKey());
                buffer.writeVarInt(entry.getValue().func_230236_b_().size());

                for(T t : entry.getValue().func_230236_b_())
                {
                    buffer.writeResourceLocation(reg.getKey(get(t)));
                }
            }
        }
        catch (NullPointerException e)
        {
            throw new RuntimeException("Could not find an object in registry: " + reg.getRegistryName() + " for tag: " + map.keySet(), e);
        }
        catch (ClassCastException e)
        {
            throw new RuntimeException("Invalid tag type, was not an IForgeRegistryEntry!", e);
        }
    }

    private static <T> ITagCollection<T> forgeTagCollectionRead(PacketBuffer buffer, ResourceLocation regName)
    {
        IForgeRegistry<?> reg = RegistryManager.ACTIVE.getRegistry(regName);
        Map<ResourceLocation, ITag<T>> map = Maps.newHashMap();
        int i = buffer.readVarInt();

        try
        {
            for(int j = 0; j < i; ++j)
            {
                ResourceLocation resourcelocation = buffer.readResourceLocation();
                int k = buffer.readVarInt();
                ImmutableSet.Builder<T> builder = ImmutableSet.builder();

                for(int l = 0; l < k; ++l)
                {
                    builder.add((T) reg.getValue(buffer.readResourceLocation()));
                }
                map.put(resourcelocation, ITag.func_232946_a_(builder.build()));
            }
        }
        catch (NullPointerException e)
        {
            throw new RuntimeException("Could not find an object in registry: " + reg.getRegistryName() + " for tag: " + map.keySet(), e);
        }
        catch (ClassCastException e)
        {
            throw new RuntimeException("Invalid tag type, was not an IForgeRegistryEntry!", e);
        }
        return ITagCollection.func_242202_a(map);
    }

    public static <T> CompletableFuture<T>[] hackArray(Collection<CompletableFuture<T>> some, CompletableFuture<T> ... others){
        int size = others.length + some.size();
        ArrayList<CompletableFuture<T>> l = Lists.newArrayList(some);
        CompletableFuture<T>[] array = Arrays.copyOf(others, size);
        for(int i = others.length; i < size; i++)
            array[i] = l.get(i-others.length);
        return array;
    }

    private static <V extends IForgeRegistryEntry<V>> V get(Object t) { return (V) t; }

}
