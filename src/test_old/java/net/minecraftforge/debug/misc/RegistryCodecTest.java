/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This test mod show a few example usages of {@link IForgeRegistry#getCodec()} to serialize and deserialize registry entries to JSON or NBT.
 * There are 4 tested cases :
 * 1. json -> Pair
 * 2. Pair -> nbt
 * 3. Pair -> compressed json
 * 4. compressed json -> Pair
 * For each test the result will be logged.
 */
@Mod("registry_codec_test")
public class RegistryCodecTest
{

    private static final Logger LOGGER = LogManager.getLogger("Codec Registry Test");

    /**
     * This Codec can serialize and deserialize a {@code Pair<Item, Block>}.
     * The resulting JSON (or NBT equivalent) will have this structure:
     * <pre>{@code
     * {
     *     "block": "block_registry_name",
     *     "item": "item_registry_name"
     * }
     * }</pre>
     */
    private static final Codec<Pair<Block, Item>> CODEC = RecordCodecBuilder.create(codecInstance -> codecInstance.group(
            ForgeRegistries.BLOCKS.getCodec().fieldOf("block").forGetter(Pair::getFirst),
            ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(Pair::getSecond)
    ).apply(codecInstance, Pair::of));

    public RegistryCodecTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event)
    {
        //Create our Json to decode
        JsonObject json = new JsonObject();
        json.addProperty("block", "minecraft:diamond_block");
        json.addProperty("item", "minecraft:diamond_pickaxe");

        //Decode our Json and log an info in case of success or a warning in case of error
        DataResult<Pair<Pair<Block, Item>, JsonElement>> result = CODEC.decode(JsonOps.INSTANCE, json);
        result.resultOrPartial(LOGGER::warn).ifPresent(pair -> LOGGER.info("Successfully decoded a diamond block and a diamond pickaxe from json to Block/Item"));

        //Create a Pair<Block, Item> to test the serialization of our codec
        Pair<Block, Item> pair = Pair.of(Blocks.DIAMOND_BLOCK, Items.DIAMOND_PICKAXE);

        //Serialize the Pair to NBT, and log an info in case of success or a warning in case of error
        DataResult<Tag> result2 = CODEC.encodeStart(NbtOps.INSTANCE, pair);
        result2.resultOrPartial(LOGGER::warn).ifPresent(tag -> LOGGER.info("Successfully encoded a Pair<Block, Item> to a nbt tag: {}", tag));

        //Serialize the Pair to JSON using the COMPRESSED JsonOps, this will use the int registry id instead of the ResourceLocation one,
        //This is not recommended because int IDs can change, so you should not rely on them
        DataResult<JsonElement> result3 = CODEC.encodeStart(JsonOps.COMPRESSED, pair);
        result3.resultOrPartial(LOGGER::warn).ifPresent(compressedJson -> LOGGER.info("Successfully encoded a Pair<Block, Item> to a compressed json: {}", compressedJson));

        //Create a json to decode using numerical IDs, to be decoded by JsonOps.COMPRESSED
        JsonArray jsonCompressed = new JsonArray();
        jsonCompressed.add(((ForgeRegistry<Block>) ForgeRegistries.BLOCKS).getID(Blocks.DIAMOND_BLOCK));
        jsonCompressed.add(((ForgeRegistry<Item>) ForgeRegistries.ITEMS).getID(Items.DIAMOND_PICKAXE));

        //Decode a compressed json to the corresponding Pair<Block, Item>, this time using Codec#parse
        DataResult<Pair<Block, Item>> result4 = CODEC.parse(JsonOps.COMPRESSED, jsonCompressed);
        result4.resultOrPartial(LOGGER::warn).ifPresent(pair2 -> LOGGER.info("Successfully decoded a diamond block and a diamond pickaxe from compressed json to Block/Item"));
    }
}