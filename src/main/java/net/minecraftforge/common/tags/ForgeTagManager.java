package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ForgeTagManager extends NetworkTagManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation FLUID_TAGS = new ResourceLocation("fluids");
    private static final Map<ResourceLocation, TagCollectionFactoryEntry> factories = new HashMap<>();
    private static final List<ResourceLocation> VANILLA_IDS = ImmutableList.of(GameData.BLOCKS, GameData.ITEMS, FLUID_TAGS);
    private static void registerVanillaTags(ResourceLocation id, Consumer<ForgeTagManager> reloadCallback)
    {
        factories.put(id,new TagCollectionFactoryEntry(null,reloadCallback,null,true));
    }

    static {
        registerVanillaTags(GameData.BLOCKS,(manager -> BlockTags.setCollection(manager.getBlocks())));
        registerVanillaTags(GameData.ITEMS,(manager -> ItemTags.setCollection(manager.getItems())));
        registerVanillaTags(FLUID_TAGS,(manager -> FluidTags.setCollection(manager.getFluids())));
    }

    private static int generation = 0;

    public static int getGeneration()
    {
        return generation;
    }

    public static void registerTagCollection(ResourceLocation id, Supplier<ForgeTagCollection<?>> factory)
    {
        registerTagCollection(id,factory,null);
    }

    public static void registerTagCollection(ResourceLocation id, Supplier<ForgeTagCollection<?>> factory, @Nullable Consumer<ForgeTagManager> reloadCallback)
    {
        registerTagCollection(id,factory,reloadCallback,null);
    }

    public static void registerTagCollection(ResourceLocation id, @Nonnull Supplier<ForgeTagCollection<?>> factory, @Nullable Consumer<ForgeTagManager> reloadCallback, @Nullable Predicate<ForgeTagManager> syncPredicate)
    {
        if (factories.containsKey(id))
            throw new IllegalArgumentException("Cannot register " + id + " TagCollection twice!");
        factories.put(id, new TagCollectionFactoryEntry(factory,reloadCallback,syncPredicate,false));
    }

    private Map<ResourceLocation, TagCollectionValueEntry> tagCollections;

    public ForgeTagManager()
    {
        super();
        tagCollections = Maps.newHashMapWithExpectedSize(factories.size());
        for (Map.Entry<ResourceLocation, TagCollectionFactoryEntry> entry : factories.entrySet())
            tagCollections.put(entry.getKey(), entry.getValue().create(entry.getKey(),this));
        tagCollections = Collections.unmodifiableMap(tagCollections);
    }


    public ForgeTagCollection<?> getTagsForId(ResourceLocation id)
    {
        return tagCollections.get(id).getTags();
    }

    @Override
    public ForgeTagCollection<Block> getBlocks()
    {
        return (ForgeTagCollection<Block>) super.getBlocks();
    }

    @Override
    public ForgeTagCollection<Item> getItems()
    {
        return (ForgeTagCollection<Item>) super.getItems();
    }

    @Override
    public ForgeTagCollection<Fluid> getFluids()
    {
        return (ForgeTagCollection<Fluid>) super.getFluids();
    }

    @Override
    public void clear()
    {
        for (Map.Entry<ResourceLocation, TagCollectionValueEntry> entry : tagCollections.entrySet())
            entry.getValue().clear();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        for (Map.Entry<ResourceLocation, TagCollectionValueEntry> entry : tagCollections.entrySet())
        {
            entry.getValue().clear();
            entry.getValue().reload(entry.getKey(), resourceManager,this);
        }
        generation++;
    }

    @Override
    public void write(PacketBuffer buffer)
    {
        this.getBlocks().write(buffer);
        this.getItems().write(buffer);
        this.getFluids().write(buffer);
        //we might wind up sending more data than vanilla needs (and therefore produce excessive Network traffic...)
        //TODO Check if connected to vanilla client and if so don't write ForgeTag Data
        writeForgeTags(buffer);
    }

    private void writeForgeTags(final PacketBuffer buffer)
    {
        tagCollections.entrySet().stream().filter(e -> e.getValue().shouldSync(ForgeTagManager.this)).filter(e -> !e.getValue().isVanilla()).forEach( e-> {
            buffer.writeResourceLocation(e.getKey());
            e.getValue().write(e.getKey(), buffer);
        });
    }

    public void readTags(PacketBuffer buffer)
    {
        this.getBlocks().read(GameData.BLOCKS, buffer);
        this.getItems().read(GameData.ITEMS, buffer);
        this.getFluids().read(FLUID_TAGS, buffer);
        readForgeTags(buffer);
    }

    private void readForgeTags(PacketBuffer buffer)
    {
        while (buffer.isReadable())
        {
            ResourceLocation id = buffer.readResourceLocation();
            //we cannot make any assumptions about the Format an unknown collection might have written into the buffer (custom implementations are allowed...)-> we have to Fail
            //modders who see this: if you want your tags not to be synced: just provide a Predicate!
            ForgeTagCollection<?> tags = Objects.requireNonNull(getTagsForId(id),"Tag List mismatch! Received "+id+" from Server, but it was not registered Client-Side!");
            tags.read(id,buffer);
        }
    }

    public void onRemoteClientReceivedTags()
    {
        for (Map.Entry<ResourceLocation, TagCollectionValueEntry> entry : tagCollections.entrySet())
        {
            entry.getValue().onReloaded(this);
        }
    }

    private static class TagCollectionValueEntry{
        private final ForgeTagCollection<?> collection;
        @Nonnull
        private final Predicate<ForgeTagManager> performSyncPredicate;
        @Nullable
        private final Consumer<ForgeTagManager> reloadCallback;
        private final boolean isVanillaCollection;

        TagCollectionValueEntry(@Nonnull ForgeTagCollection<?> collection, @Nullable Consumer<ForgeTagManager> reloadCallback, @Nonnull Predicate<ForgeTagManager> performSyncPredicate, boolean vanillaCollection)
        {
            this.collection = collection;
            this.reloadCallback = reloadCallback;
            this.performSyncPredicate = performSyncPredicate;
            this.isVanillaCollection = vanillaCollection;
        }


        boolean shouldSync(ForgeTagManager manager)
        {
            return performSyncPredicate.test(manager);
        }


        void clear()
        {
            collection.clear();
        }


        ForgeTagCollection<?> getTags()
        {
            return collection;
        }


        void read(ResourceLocation location, PacketBuffer buffer)
        {
            collection.read(location, buffer);
        }


        void write(ResourceLocation location, PacketBuffer buffer)
        {
            collection.write(location, buffer);
        }


        void reload(ResourceLocation location, IResourceManager resManager, ForgeTagManager tagManager)
        {
            collection.reload(location, resManager);
            onReloaded(tagManager);
        }

        void onReloaded(ForgeTagManager tagManager)
        {
            if (reloadCallback!=null) reloadCallback.accept(tagManager);
        }

        boolean isVanilla() {
            return isVanillaCollection;
        }
    }

    private static class TagCollectionFactoryEntry{
        private final Supplier<ForgeTagCollection<?>> supplier;
        @Nonnull
        private final Predicate<ForgeTagManager> performSyncPredicate;
        private final Consumer<ForgeTagManager> reloadCallback;
        private final boolean isVanillaCollection;

        TagCollectionFactoryEntry(@Nullable Supplier<ForgeTagCollection<?>> collectionSupplier, @Nullable Consumer<ForgeTagManager> reloadCallback,@Nullable Predicate<ForgeTagManager> performSyncPredicate, boolean vanillaCollection)
        {
            this.supplier = collectionSupplier;
            this.reloadCallback = reloadCallback;
            this.performSyncPredicate = performSyncPredicate!=null?performSyncPredicate:(rl->true);
            this.isVanillaCollection = vanillaCollection;
            if (this.supplier == null && !isVanilla()) throw new IllegalArgumentException("A factory for TagCollections is required!");
        }

        TagCollectionValueEntry create(ResourceLocation id, ForgeTagManager manager)
        {
            if (!isVanilla()) return new TagCollectionValueEntry(Objects.requireNonNull(supplier.get()),reloadCallback,performSyncPredicate,false);
            if (id.equals(GameData.ITEMS)) return new TagCollectionValueEntry(manager.getItems(),reloadCallback,performSyncPredicate,true);
            if (id.equals(GameData.BLOCKS)) return new TagCollectionValueEntry(manager.getBlocks(),reloadCallback,performSyncPredicate,true);
            if (id.equals(FLUID_TAGS)) return new TagCollectionValueEntry(manager.getFluids(),reloadCallback,performSyncPredicate,true);
            throw new RuntimeException("Failed to create vanilla TagCollection with id "+id);
        }

        boolean isVanilla() {
            return isVanillaCollection;
        }
    }
}
