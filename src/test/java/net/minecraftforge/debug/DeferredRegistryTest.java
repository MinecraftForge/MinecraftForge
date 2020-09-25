/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
@Mod(DeferredRegistryTest.MODID)
public class DeferredRegistryTest {
    static final String MODID = "deferred_registry_test";
    private static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Custom> CUSTOMS = DeferredRegister.create(Custom.class, MODID);

    private static final RegistryObject<Block> BLOCK = BLOCKS.register("test", () -> new Block(Block.Properties.create(Material.ROCK)));
    private static final RegistryObject<Item>  ITEM  = ITEMS .register("test", () -> new BlockItem(BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    private static final RegistryObject<Custom> CUSTOM = CUSTOMS.register("test", () -> new Custom(){});

    private static final Supplier<IForgeRegistry<Custom>> CUSTOM_REG = CUSTOMS.makeRegistry("test_registry", () ->
        new RegistryBuilder<Custom>().disableSaving().setMaxID(Integer.MAX_VALUE - 1)
            .onAdd((owner, stage, id, obj, old) -> LOGGER.info("Custom Added: " + id + " " + obj.foo()))
    );

    public DeferredRegistryTest() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        CUSTOMS.register(modBus);
        modBus.addListener(this::gatherData);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new BlockStateProvider(gen, MODID, event.getExistingFileHelper()) {
                @Override
                protected void registerStatesAndModels() {
                    ModelFile model = models().cubeAll(BLOCK.get().getRegistryName().getPath(), mcLoc("block/furnace_top"));
                    simpleBlock(BLOCK.get(), model);
                    simpleBlockItem(BLOCK.get(), model);
                }
            });
        }
    }

    public static class Custom extends ForgeRegistryEntry<Custom> {
        public String foo() {
            return this.getClass().getName();
        }
    }
}
