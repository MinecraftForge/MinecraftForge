/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

@Mod(TagBasedToolTypesTest.MODID)
public class TagBasedToolTypesTest
{
    static final String MODID = "tag_based_tool_types";

    private static final TagKey<Block> MINEABLE_TAG = BlockTags.create(new ResourceLocation(MODID, "minable/my_tool"));
    private static final TagKey<Block> REQUIRES_TAG = BlockTags.create(new ResourceLocation(MODID, "needs_my_tier_tool"));

    private static final Tier MY_TIER = TierSortingRegistry.registerTier(
        new ForgeTier(5, 5000, 10, 100, 0, REQUIRES_TAG, () -> Ingredient.of(Items.BEDROCK)),
        new ResourceLocation(MODID, "my_tier"),
        List.of(Tiers.DIAMOND), List.of()
    );

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> STONE = BLOCKS.register("test_stone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    @SuppressWarnings("unused")
    private static final RegistryObject<Item> ORE_ITEM = ITEMS.register(STONE.getId().getPath(), () -> new BlockItem(STONE.get(), (new Item.Properties()).tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    private static final RegistryObject<Item> TOOL = ITEMS.register("test_tool", () ->
    {
        return new DiggerItem(1, 1, MY_TIER, MINEABLE_TAG, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS))
        {
            @Override
            public float getDestroySpeed(ItemStack stack, BlockState state)
            {
                if (state.is(BlockTags.MINEABLE_WITH_AXE)) return speed;
                if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return speed;
                return super.getDestroySpeed(stack, state);
            }

            @Override
            public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
            {
                if (state.is(BlockTags.MINEABLE_WITH_PICKAXE))
                    return TierSortingRegistry.isCorrectTierForDrops(Tiers.WOOD, state);
                if (state.is(BlockTags.MINEABLE_WITH_AXE))
                    return TierSortingRegistry.isCorrectTierForDrops(MY_TIER, state);
                if (state.is(MINEABLE_TAG))
                    return TierSortingRegistry.isCorrectTierForDrops(MY_TIER, state);
                return false;
            }
        };
    });

    public TagBasedToolTypesTest()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::gatherData);
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existing = event.getExistingFileHelper();

        if (event.includeServer())
        {
            gen.addProvider(new BlockTagsProvider(gen, MODID, existing)
            {
                @Override
                protected void addTags()
                {
                    this.tag(MINEABLE_TAG).add(STONE.get());
                    this.tag(REQUIRES_TAG).add(STONE.get());
                }
            });

            gen.addProvider(new LootTableProvider(gen)
            {
                @Override
                protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker)
                {
                   map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
                }

                @Override
                protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
                {
                    return ImmutableList.of(Pair.of(() ->
                    {
                        return new BlockLoot()
                        {

                            @Override
                            protected Iterable<Block> getKnownBlocks()
                            {
                                return BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
                            }

                            @Override
                            protected void addTables()
                            {
                                this.dropSelf(STONE.get());
                            }
                        };
                    }, LootContextParamSets.BLOCK));
                }
            });
        }

        if (event.includeClient())
        {
            gen.addProvider(new BlockStateProvider(gen, MODID, existing)
            {
                @Override
                protected void registerStatesAndModels()
                {
                    ModelFile model = models().cubeAll(STONE.get().getRegistryName().getPath(), mcLoc("block/debug"));
                    simpleBlock(STONE.get(), model);
                    simpleBlockItem(STONE.get(), model);
                }
            });
            gen.addProvider(new ItemModelProvider(gen, MODID, existing)
            {
                @Override
                protected void registerModels()
                {
                    getBuilder(TOOL.get().getRegistryName().getPath())
                        .parent(new UncheckedModelFile("item/generated"))
                        .texture("layer0", mcLoc("item/wooden_pickaxe"));
                }
            });
        }
    }
}
