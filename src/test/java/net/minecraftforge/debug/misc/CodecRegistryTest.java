/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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

    /**
     *     This Codec can serialize and deserialize a Pair<Item, Block>
     *     The result Json (or nbt equivalent) will have this structure :
     *     {
     *         "block": "block_registry_name",
     *         "item": "item_registry_name"
     *     }
     */
    private static final Codec<Pair<Block, Item>> CODEC = RecordCodecBuilder.create(codecInstance -> codecInstance.group(
        ForgeRegistries.BLOCKS.getCodec().fieldOf("block").forGetter(Pair::getFirst),
        ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(Pair::getSecond)
    ).apply(codecInstance, Pair::of));

    public CodecRegistryTest() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        //Create our Json to decode
        JsonObject json = new JsonObject();
        json.addProperty("block", "minecraft:diamond_block");
        json.addProperty("item", "minecraft:diamond_pickaxe");

        //Decode our Json and log an info in case of succes or a warning in case of error
        DataResult<Pair<Pair<Block, Item>, JsonElement>> result = CODEC.decode(JsonOps.INSTANCE, json);
        result.resultOrPartial(LOGGER::warn).ifPresent(pair -> LOGGER.info("Successfully decoded a diamond block and a diamond pickaxe from json to Block/Item"));

        //Create a Pair<Block, Item> to test the serialization of our codec
        Pair<Block, Item> pair = Pair.of(Blocks.DIAMOND_BLOCK, Items.DIAMOND_PICKAXE);

        //Serialize the Pair to NBT, and log an info in case of success or a warning in case of error
        DataResult<INBT> result2 = CODEC.encodeStart(NBTDynamicOps.INSTANCE, pair);
        result2.resultOrPartial(LOGGER::warn).ifPresent(inbt -> LOGGER.info("Successfully encoded a Pair<Block, Item> to an INBT: " + inbt));

        //Serialize the Pair to JSON using the COMPRESSED JsonOps, this will use the int registry id instead of the ResourceLocation one,
        // this is not recommended because int IDs can change so you should not rely on them
        DataResult<JsonElement> result3 = CODEC.encodeStart(JsonOps.COMPRESSED, pair);
        result3.resultOrPartial(LOGGER::warn).ifPresent(compressedJson -> LOGGER.info("Successfuly encoded a Pair<Block, Item> to a compressed json: " + compressedJson));
    }
}
