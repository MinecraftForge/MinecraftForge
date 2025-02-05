/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

@Mod(ModelRenderLayerTest.MODID)
@GameTestHolder("forge." + ModelRenderLayerTest.MODID)
public class ModelRenderLayerTest extends BaseTestMod {
    public static final String MODID = "model_render_type";
    private static final String BLOCK_NAME = "cutout_block";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final RegistryObject<Block> BLOCK = BLOCKS.register(BLOCK_NAME,
        () -> new SaplingBlock(
            TreeGrower.ACACIA,
            BlockBehaviour.Properties.of()
                .setId(BLOCKS.key(BLOCK_NAME))
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY)
        )
    );

    private static final String OLD_LEAVES_NAME = "old_leaves";
    private static final BlockBehaviour.StatePredicate FALSE = (state, level, pos) -> false;
    public static final RegistryObject<LeavesBlock> OLD_LEAVES = BLOCKS.register(OLD_LEAVES_NAME,
        () -> new LeavesBlock(
            BlockBehaviour.Properties.of()
                .setId(BLOCKS.key(OLD_LEAVES_NAME))
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isSuffocating(FALSE)
                .isViewBlocking(FALSE)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(FALSE)
        )
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final RegistryObject<Item> ITEM = ITEMS.register(BLOCK_NAME, () -> new BlockItem(BLOCK.get(), new Item.Properties().setId(ITEMS.key(BLOCK_NAME))));
    public static final RegistryObject<BlockItem> OLD_LEAVES_ITEM = ITEMS.register(OLD_LEAVES_NAME, () -> new BlockItem(OLD_LEAVES.get(), new Item.Properties().setId(ITEMS.key(OLD_LEAVES_NAME))));

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public ModelRenderLayerTest(FMLJavaModLoadingContext context) {
        super(context);
        testItem(lookup -> new ItemStack(ITEM.get()));
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(event.includeClient(), new ModelProvider(out));
    }

    @SuppressWarnings("resource")
    @GameTest(template = "forge:empty3x3x3")
    public static void type_from_model(GameTestHelper helper) {
        var manager = Minecraft.getInstance().getModelManager();

        var key = new ModelResourceLocation(rl(BLOCK_NAME), "stage=0");
        var model = manager.getModel(key);
        if (model == manager.getMissingModel())
            helper.fail("Failed to retreive block model for" + key);

        var state = BLOCK.get().defaultBlockState();
        var random = helper.getLevel().random;
        boolean originalRenderState = ItemBlockRenderTypes.isFancy();
        helper.addCleanup(passed -> ItemBlockRenderTypes.setFancy(originalRenderState));

        ItemBlockRenderTypes.setFancy(true);
        var layer = model.getRenderTypes(state, random, ModelData.EMPTY);
        helper.assertTrue(layer.contains(RenderType.cutout()), "Block model does not contain the correct render type for fancy graphics. Expected: cutout");

        ItemBlockRenderTypes.setFancy(false);
        layer = model.getRenderTypes(state, random, ModelData.EMPTY);
        helper.assertTrue(layer.contains(RenderType.solid()), "Block model does not contain the correct render type for fast graphics. Expected: solid");

        helper.succeed();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void old_leaves_render_type(GameTestHelper helper) {
        var manager = Minecraft.getInstance().getModelManager();

        var key = rl(OLD_LEAVES_NAME);
        var models = manager.getModels().entrySet().stream().filter(ml -> ml.getKey().id().equals(key)).map(Map.Entry::getValue).findAny();
        var model = models.orElseThrow(() -> new GameTestAssertException("Failed to retrieve any block models for " + key));

        var state = OLD_LEAVES.get().defaultBlockState();
        var random = helper.getLevel().random;
        boolean originalRenderState = ItemBlockRenderTypes.isFancy();
        helper.addCleanup(passed -> ItemBlockRenderTypes.setFancy(originalRenderState));

        ItemBlockRenderTypes.setFancy(true);
        var layer = model.getRenderTypes(state, random, ModelData.EMPTY);
        helper.assertTrue(layer.contains(RenderType.cutoutMipped()), "Block model does not contain the correct render type for fancy graphics. Expected: cutout_mipped");

        ItemBlockRenderTypes.setFancy(false);
        layer = model.getRenderTypes(state, random, ModelData.EMPTY);
        helper.assertTrue(layer.contains(RenderType.solid()), "Block model does not contain the correct render type for fast graphics. Expected: solid");

        helper.succeed();
    }

    public class ModelProvider extends net.minecraft.client.data.models.ModelProvider {
        public ModelProvider(PackOutput output) {
            super(output);
        }

        protected Stream<Block> getKnownBlocks() {
            return Stream.of(BLOCK.get());
        }

        protected Stream<Item> getKnownItems() {
            return Stream.of(ITEM.get());
        }

        protected BlockModelGenerators getBlockModelGenerators(BlockStateGeneratorCollector blocks, ItemInfoCollector items, SimpleModelCollector models) {
            return new BlockModelGenerators(blocks, items, models) {
                @Override
                public void run() {
                    // CUTOUT BLOCK
                    var textures = PlantType.NOT_TINTED.getTextureMapping(Blocks.ACACIA_SAPLING);
                    var cutout = PlantType.NOT_TINTED.getCross().create(BLOCK.get(), textures, (name, model) -> {
                        var json = model.get().getAsJsonObject();
                        json.addProperty("render_type", "minecraft:cutout");
                        json.addProperty("render_type_fast", "minecraft:solid");
                        this.modelOutput.accept(name, () -> json);
                    });
                    this.blockStateOutput.accept(createSimpleBlock(BLOCK.get(), cutout));

                    var item = ModelTemplates.FLAT_ITEM.create(ITEM.get(), TextureMapping.layer0(Blocks.ACACIA_SAPLING), this.modelOutput);
                    this.itemModelOutput.accept(ITEM.get(), ItemModelUtils.plainModel(item));


                    // OLD LEAVES
                    var oldLeavesCutout = ModelTemplates.LEAVES.create(OLD_LEAVES.get(), TextureMapping.cube(Blocks.OAK_LEAVES), (name, model) -> {
                        var json = model.get().getAsJsonObject();
                        json.addProperty("render_type", "minecraft:cutout_mipped");
                        this.modelOutput.accept(name, () -> json);
                    });
                    this.blockStateOutput.accept(createSimpleBlock(OLD_LEAVES.get(), oldLeavesCutout));

                    this.itemModelOutput.accept(OLD_LEAVES_ITEM.get(), ItemModelUtils.plainModel(oldLeavesCutout));
                }
            };
        }

        protected ItemModelGenerators getItemModelGenerators(ItemInfoCollector items, SimpleModelCollector models) {
            return new ItemModelGenerators(items, models) {
                public void run() {
                }
            };
        }

        static <T> CompletableFuture<?> saveAll(CachedOutput p_378816_, Function<T, Path> p_377914_, Map<T, ? extends Supplier<JsonElement>> p_377836_) {
            return DataProvider.saveAll(p_378816_, Supplier::get, p_377914_, p_377836_);
        }
    }
}
