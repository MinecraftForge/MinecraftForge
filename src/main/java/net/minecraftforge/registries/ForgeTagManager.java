package net.minecraftforge.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.*;

import javax.annotation.Nonnull;

public class ForgeTagManager extends NetworkTagManager {
    private static int generation = 0;

    public static int getGeneration() {
        return generation;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        clear();
        getBlockTags().onReload(resourceManager);
        getItemTags().onReload(resourceManager);
        getFluids().reload(resourceManager);
        BlockTags.setCollection(getBlocks());
        ItemTags.setCollection(getItems());
        FluidTags.setCollection(getFluids());
        //everything non-vanilla:
        RegistryManager.ACTIVE.onTagReload(resourceManager);
        generation++;
    }

    @Nonnull
    private static TagDelegate<Block> getBlockTags() {
        assert ForgeRegistries.BLOCKS.getTagDelegate()!=null;
        return ForgeRegistries.BLOCKS.getTagDelegate();
    }

    @Nonnull
    private static TagDelegate<Item> getItemTags() {
        assert ForgeRegistries.ITEMS.getTagDelegate()!=null;
        return ForgeRegistries.ITEMS.getTagDelegate();
    }

    @Override
    @Nonnull
    public NetworkTagCollection<Block> getBlocks() {
        return getBlockTags().asTagCollection();
    }

    @Override
    @Nonnull
    public NetworkTagCollection<Item> getItems() {
        return getItemTags().asTagCollection();
    }

    @Override
    public void clear() { //vanilla only needs to clear vanilla...
        TagDelegate<Block> blockTagDelegate = getBlockTags();
        TagDelegate<Item> itemTagDelegate = getItemTags();
        blockTagDelegate.clear();
        itemTagDelegate.clear();
        getFluids().clear();
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer) {
        TagDelegate<Block> blockTagDelegate = getBlockTags();
        TagDelegate<Item> itemTagDelegate = getItemTags();
        blockTagDelegate.onWriteVanillaPacket(buffer);
        itemTagDelegate.onWriteVanillaPacket(buffer);
        getFluids().write(buffer);
    }

    private void readInternal(PacketBuffer buffer) {
        TagDelegate<Block> blockTagDelegate = getBlockTags();
        TagDelegate<Item> itemTagDelegate = getItemTags();
        blockTagDelegate.onReadVanillaPacket(buffer);
        itemTagDelegate.onReadVanillaPacket(buffer);
        getFluids().read(buffer);
    }

    @Nonnull
    public static ForgeTagManager read(@Nonnull PacketBuffer buffer) {
        ForgeTagManager tagManager = new ForgeTagManager();
        tagManager.readInternal(buffer);
        return tagManager;
    }
}
