/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.WeightedVariants;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Demonstrates a model that changes its appearance based on its surroundings, like connected textures do.
 * <p>
 * Placing a {@code connected_block} next to another one turns both from stone bricks into gold blocks. The
 * {@code data_connected_block} does the same based on the {@link ModelData} of its block entity, rather than its neighbors.
 */
@GameTestNamespace("forge")
@Mod(ConnectedModelTest.MODID)
public class ConnectedModelTest extends BaseTestMod {
    public static final String MODID = "connected_model";

    /** Only exists to get both appearances baked from the block state file. It is never set on a block in the level. */
    private static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    private static final ModelProperty<Boolean> FORCE_CONNECTED = new ModelProperty<>();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    private static final RegistryObject<Block> CONNECTED_BLOCK = BLOCKS.register("connected_block", () -> new ConnectedBlock(BlockBehaviour.Properties.of().setId(BLOCKS.key("connected_block"))));
    private static final RegistryObject<Block> DATA_CONNECTED_BLOCK = BLOCKS.register("data_connected_block", () -> new DataConnectedBlock(BlockBehaviour.Properties.of().setId(BLOCKS.key("data_connected_block"))));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    private static final RegistryObject<Item> CONNECTED_ITEM = ITEMS.register("connected_block", () -> new BlockItem(CONNECTED_BLOCK.get(), new Item.Properties().setId(ITEMS.key("connected_block"))));
    private static final RegistryObject<Item> DATA_CONNECTED_ITEM = ITEMS.register("data_connected_block", () -> new BlockItem(DATA_CONNECTED_BLOCK.get(), new Item.Properties().setId(ITEMS.key("data_connected_block"))));

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    private static final RegistryObject<BlockEntityType<DataConnectedBlockEntity>> DATA_CONNECTED_BLOCK_ENTITY = BLOCK_ENTITIES.register("data_connected_block", () -> new BlockEntityType<>(DataConnectedBlockEntity::new, Set.of(DATA_CONNECTED_BLOCK.get())));

    private static final BlockPos POS = new BlockPos(16, 64, 16);

    public ConnectedModelTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        this.testItem(_ -> CONNECTED_ITEM.get().getDefaultInstance());
        this.testItem(_ -> DATA_CONNECTED_ITEM.get().getDefaultInstance());
        BuildCreativeModeTabContentsEvent.BUS.addListener(this::onCreativeModeTabBuildContents);
        ModelEvent.ModifyBakingResult.BUS.addListener(this::onModifyBakingResult);
        GatherDataEvent.getBus(this.modBus).addListener(this::onGatherData);
    }

    private void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var models = event.getResults().blockStateModels();
        for (var block : List.of(CONNECTED_BLOCK.get(), DATA_CONNECTED_BLOCK.get())) {
            var state = block.defaultBlockState();
            models.put(state, new ConnectedModel(models.get(state), models.get(state.setValue(CONNECTED, true))));
        }
    }

    private void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new ModelProvider(event.getGenerator().getPackOutput()));
    }

    @GameTest
    public static void is_regular_without_context(GameTestHelper helper) {
        // The no-context collectParts overload returns the regular parts
        var model = connectedModel(helper);
        helper.assertValueEqual(collect(model.regular()), collect(model), Component.literal("Parts without level context"));
        helper.succeed();
    }

    @GameTest
    public static void is_regular_without_neighbors(GameTestHelper helper) {
        // A level with no neighboring blocks or model data gives the regular parts
        var model = connectedModel(helper);
        helper.assertValueEqual(collect(model.regular()), collect(model, new TestLevel(Map.of(), Map.of())), Component.literal("Parts without neighbors"));
        helper.succeed();
    }

    @GameTest
    public static void connects_to_neighbor(GameTestHelper helper) {
        // A neighbor of the same block in any direction gives the connected parts
        var model = connectedModel(helper);
        for (var direction : Direction.values()) {
            var level = new TestLevel(Map.of(POS.relative(direction), CONNECTED_BLOCK.get().defaultBlockState()), Map.of());
            helper.assertValueEqual(collect(model.connected()), collect(model, level), Component.literal("Parts with neighbor in direction " + direction));
        }
        helper.succeed();
    }

    @GameTest
    public static void ignores_other_neighbors(GameTestHelper helper) {
        // A neighbor of a different block still gives the regular parts
        var model = connectedModel(helper);
        var level = new TestLevel(Map.of(POS.above(), Blocks.STONE.defaultBlockState()), Map.of());
        helper.assertValueEqual(collect(model.regular()), collect(model, level), Component.literal("Parts with unrelated neighbor"));
        helper.succeed();
    }

    @GameTest
    public static void connects_from_model_data(GameTestHelper helper) {
        // Model data from the level can select the connected parts without any neighbors
        var model = connectedModel(helper);
        var level = new TestLevel(Map.of(), Map.of(POS, ModelData.builder().with(FORCE_CONNECTED, true).build()));
        helper.assertValueEqual(collect(model.connected()), collect(model, level), Component.literal("Parts with model data"));
        helper.succeed();
    }

    @GameTest
    public static void particle_uses_context(GameTestHelper helper) {
        // The level-aware particleMaterial overload follows the same selection as the parts
        var model = connectedModel(helper);
        var state = CONNECTED_BLOCK.get().defaultBlockState();
        var level = new TestLevel(Map.of(POS.above(), state), Map.of());
        helper.assertValueEqual(model.connected().particleMaterial(), model.particleMaterial(level, POS, state), Component.literal("Particle material"));
        helper.succeed();
    }

    @GameTest
    public static void weighted_variants_pass_context(GameTestHelper helper) {
        // WeightedVariants forwards the level context to the model it selects
        var model = connectedModel(helper);
        var weighted = new WeightedVariants(WeightedList.of(model));
        var level = new TestLevel(Map.of(POS.above(), CONNECTED_BLOCK.get().defaultBlockState()), Map.of());
        helper.assertValueEqual(collect(model.connected()), collect(weighted, level), Component.literal("Parts of weighted model"));
        helper.succeed();
    }

    @GameTest
    public static void multipart_passes_context(GameTestHelper helper) {
        // MultiPartModel forwards the level context to each of the models it selects
        var model = connectedModel(helper);
        var state = CONNECTED_BLOCK.get().defaultBlockState();
        var selector = new MultiPartModel.Selector<BlockStateModel.Unbaked>(_ -> true, new PrebakedModel(model));
        var multipart = new MultiPartModel.Unbaked(List.of(selector, selector)).bake(state, new TestBaker());
        var level = new TestLevel(Map.of(POS.above(), state), Map.of());
        var expected = collect(model.connected());
        expected.addAll(collect(model.connected()));
        helper.assertValueEqual(expected, collect(multipart, level), Component.literal("Parts of multipart model"));
        helper.succeed();
    }

    private static ConnectedModel connectedModel(GameTestHelper helper) {
        var model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(CONNECTED_BLOCK.get().defaultBlockState());
        if (model instanceof ConnectedModel connected)
            return connected;
        throw helper.assertionException(Component.literal("Expected a connected model, but found " + model));
    }

    @SuppressWarnings("deprecation")
    private static List<BlockStateModelPart> collect(BlockStateModel model) {
        var parts = new ArrayList<BlockStateModelPart>();
        model.collectParts(RandomSource.create(), parts);
        return parts;
    }

    private static List<BlockStateModelPart> collect(BlockStateModel model, BlockAndTintGetter level) {
        var parts = new ArrayList<BlockStateModelPart>();
        model.collectParts(level, POS, CONNECTED_BLOCK.get().defaultBlockState(), RandomSource.create(), parts);
        return parts;
    }

    private static class ConnectedBlock extends Block {
        private ConnectedBlock(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(CONNECTED, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(CONNECTED);
        }
    }

    private static class DataConnectedBlock extends ConnectedBlock implements EntityBlock {
        private DataConnectedBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new DataConnectedBlockEntity(pos, state);
        }
    }

    private static class DataConnectedBlockEntity extends BlockEntity {
        private DataConnectedBlockEntity(BlockPos pos, BlockState state) {
            super(DATA_CONNECTED_BLOCK_ENTITY.get(), pos, state);
        }

        @Override
        public ModelData getModelData() {
            return ModelData.builder().with(FORCE_CONNECTED, true).build();
        }
    }

    @SuppressWarnings("deprecation")
    private record ConnectedModel(BlockStateModel regular, BlockStateModel connected) implements BlockStateModel {
        private BlockStateModel select(BlockAndTintGetter level, BlockPos pos, BlockState state) {
            if (Boolean.TRUE.equals(level.getModelData(pos).get(FORCE_CONNECTED)))
                return this.connected;

            for (var direction : Direction.values()) {
                if (level.getBlockState(pos.relative(direction)).is(state.getBlock()))
                    return this.connected;
            }
            return this.regular;
        }

        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
            this.regular.collectParts(random, output);
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> output) {
            this.select(level, pos, state).collectParts(level, pos, state, random, output);
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.regular.particleMaterial();
        }

        @Override
        public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
            return this.select(level, pos, state).particleMaterial(level, pos, state);
        }

        @Override
        public @BakedQuad.MaterialFlags int materialFlags() {
            return this.regular.materialFlags() | this.connected.materialFlags();
        }
    }

    private record PrebakedModel(BlockStateModel model) implements BlockStateModel.Unbaked {
        @Override
        public BlockStateModel bake(ModelBaker modelBakery) {
            return this.model;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
        }
    }

    private static class ModelProvider extends net.minecraft.client.data.models.ModelProvider {
        private ModelProvider(PackOutput output) {
            super(output);
        }

        @Override
        protected Stream<Block> getKnownBlocks() {
            return Stream.of(CONNECTED_BLOCK.get(), DATA_CONNECTED_BLOCK.get());
        }

        @Override
        protected Stream<Item> getKnownItems() {
            return Stream.of(CONNECTED_ITEM.get(), DATA_CONNECTED_ITEM.get());
        }

        @Override
        protected BlockModelGenerators getBlockModelGenerators(BlockStateGeneratorCollector blocks, ItemInfoCollector items, SimpleModelCollector models) {
            return new BlockModelGenerators(blocks, items, models) {
                @Override
                public void run() {
                    var regular = ModelLocationUtils.getModelLocation(Blocks.STONE_BRICKS);
                    var connected = ModelLocationUtils.getModelLocation(Blocks.GOLD_BLOCK);
                    for (var block : List.of(CONNECTED_BLOCK.get(), DATA_CONNECTED_BLOCK.get())) {
                        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(createBooleanModelDispatch(CONNECTED, plainVariant(connected), plainVariant(regular))));
                        this.registerSimpleItemModel(block, regular);
                    }
                }
            };
        }

        @Override
        protected ItemModelGenerators getItemModelGenerators(ItemInfoCollector items, SimpleModelCollector models) {
            return new ItemModelGenerators(items, models) {
                @Override
                public void run() {
                    // The item models are already registered together with the blocks
                }
            };
        }
    }

    private static class TestBaker implements ModelBaker {
        @Override
        public <T> T compute(ModelBaker.SharedOperationKey<T> key) {
            return key.compute(this);
        }

        @Override
        public ResolvedModel getModel(Identifier location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BlockStateModelPart missingBlockModelPart() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MaterialBaker materials() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ModelBaker.Interner interner() {
            throw new UnsupportedOperationException();
        }
    }

    private record TestLevel(Map<BlockPos, BlockState> blocks, Map<BlockPos, ModelData> modelData) implements BlockAndTintGetter {
        @Override
        public ModelData getModelData(BlockPos pos) {
            return this.modelData.getOrDefault(pos, ModelData.EMPTY);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return this.blocks.getOrDefault(pos, Blocks.AIR.defaultBlockState());
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.getBlockState(pos).getFluidState();
        }

        @Override
        public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
            return EMPTY.getBlockEntity(pos);
        }

        @Override
        public CardinalLighting cardinalLighting() {
            return EMPTY.cardinalLighting();
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return EMPTY.getLightEngine();
        }

        @Override
        public int getBlockTint(BlockPos pos, ColorResolver color) {
            return EMPTY.getBlockTint(pos, color);
        }

        @Override
        public int getHeight() {
            return EMPTY.getHeight();
        }

        @Override
        public int getMinY() {
            return EMPTY.getMinY();
        }
    }
}
