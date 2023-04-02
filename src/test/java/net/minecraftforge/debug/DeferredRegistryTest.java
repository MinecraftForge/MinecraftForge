/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
@Mod(DeferredRegistryTest.MODID)
public class DeferredRegistryTest {
    static final String MODID = "deferred_registry_test";
    private static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Custom> CUSTOMS = DeferredRegister.create(new ResourceLocation(MODID, "test_registry"), MODID);
    private static final DeferredRegister<Object> DOESNT_EXIST_REG = DeferredRegister.createOptional(new ResourceLocation(MODID, "doesnt_exist"), MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    // Vanilla Registry - filled directly after all RegistryEvent.Register events are fired
    private static final DeferredRegister<PosRuleTestType<?>> POS_RULE_TEST_TYPES = DeferredRegister.create(Registries.POS_RULE_TEST, MODID);

    private static final RegistryObject<Block> BLOCK = BLOCKS.register("test", () -> new Block(Block.Properties.of().mapColor(MapColor.STONE)));
    private static final RegistryObject<Item>  ITEM  = ITEMS .register("test", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    private static final RegistryObject<Custom> CUSTOM = CUSTOMS.register("test", () -> new Custom(){});
    // Should never be created as the registry doesn't exist - this should silently fail and remain empty
    private static final RegistryObject<Object> DOESNT_EXIST = DOESNT_EXIST_REG.register("test", Object::new);
    private static final RegistryObject<RecipeType<?>> RECIPE_TYPE = RECIPE_TYPES.register("test", () -> new RecipeType<>() {});
    private static final RegistryObject<PosRuleTestType<?>> POS_RULE_TEST_TYPE = POS_RULE_TEST_TYPES.register("test", () -> () -> null);

    private static final TagKey<Custom> CUSTOM_TAG_KEY = CUSTOMS.createOptionalTagKey("test_tag", Set.of(CUSTOM));
    private static final Supplier<IForgeRegistry<Custom>> CUSTOM_REG = CUSTOMS.makeRegistry(() ->
        new RegistryBuilder<Custom>().disableSaving().setMaxID(Integer.MAX_VALUE - 1).hasTags()
            .onAdd((owner, stage, id, key, obj, old) -> LOGGER.info("Custom Added: " + id + " " + obj.foo()))
    );

    public DeferredRegistryTest() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        CUSTOMS.register(modBus);
        RECIPE_TYPES.register(modBus);
        POS_RULE_TEST_TYPES.register(modBus);
        modBus.addListener(this::gatherData);

        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(ITEM);
    }

    public void serverStarted(ServerStartedEvent event)
    {
        // Validate all the RegistryObjects are filled / not filled
        BLOCK.get();
        ITEM.get();
        CUSTOM.get();
        if (DOESNT_EXIST.isPresent())
            throw new IllegalStateException("DeferredRegistryTest#DOESNT_EXIST should not be present!");
        RECIPE_TYPE.get();
        //POS_RULE_TEST_TYPE.get();
        //PLACED_FEATURE.get();
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeClient(), new BlockStateProvider(gen.getPackOutput(), MODID, event.getExistingFileHelper()) {
            @Override
            protected void registerStatesAndModels() {
                simpleBlockWithItem(BLOCK.get(), models().cubeAll(BLOCK.getId().getPath(), mcLoc("block/furnace_top")));
            }
        });
    }

    public static class Custom {
        public String foo() {
            return this.getClass().getName();
        }
    }
}
