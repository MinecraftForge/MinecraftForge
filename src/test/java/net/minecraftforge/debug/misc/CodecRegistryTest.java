package net.minecraftforge.debug.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("codec_registry_test")
public class CodecRegistryTest {

    private static final Logger LOGGER = LogManager.getLogger("Codec Registry Test");

    private static final Codec<Pair<Block, Item>> CODEC = RecordCodecBuilder.create(codecInstance -> codecInstance.group(
        ForgeRegistries.BLOCKS.getCodec().fieldOf("block").forGetter(Pair::getFirst),
        ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(Pair::getSecond)
    ).apply(codecInstance, Pair::of));

    public CodecRegistryTest() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        JsonObject json = new JsonObject();
        json.addProperty("block", "minecraft:diamond_block");
        json.addProperty("item", "minecraft:diamond_pickaxe");

        DataResult<Pair<Pair<Block, Item>, JsonElement>> result = CODEC.decode(JsonOps.INSTANCE, json);
        result.resultOrPartial(LOGGER::warn).ifPresent(pair -> LOGGER.info("Successfully decoded a diamond block and a diamond pickaxe from json to Block/Item"));

        Pair<Block, Item> pair = Pair.of(Blocks.DIAMOND_BLOCK, Items.DIAMOND_PICKAXE);
        DataResult<INBT> result2 = CODEC.encodeStart(NBTDynamicOps.INSTANCE, pair);
        result2.resultOrPartial(LOGGER::warn).ifPresent(inbt -> LOGGER.info("Successfully encoded a Pair<Block, Item> to an INBT: " + inbt.getString()));
    }
}
