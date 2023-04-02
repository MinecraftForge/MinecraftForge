/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;

/**
 * Test mod that demos most Forge-provided model loaders in a single block + item, as well as in-JSON render states
 * and the refactored immutable ModelData managed by the client level. The block can be found in the decoration tab.
 * <p>
 * Additionally, some fields in the JSON have deprecated names, so those MUST be updated in 1.20, or the model will
 * break. They have all been annotated accordingly.
 * <ul>
 *     <li>As a block: Composite loader, using 3 child element models, each with a different render type,
 *     some using vanilla's elements loader, and some Forge's</li>
 *     <li>In the right hand: Fluid container with lava (emissive)</li>
 *     <li>In the left hand: Multi-layer item with chainmail chestplate + emissive bow</li>
 * </ul>
 * <p>
 * Clicking on the upper half of the block will make the model move up by a bit, and clicking on the lower half will
 * move it down.
 */
@Mod(MegaModelTest.MOD_ID)
public class MegaModelTest
{
    public static final String MOD_ID = "mega_model_test";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    private static final String REG_NAME = "test_block";
    public static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register(REG_NAME, TestBlock::new);
    public static final RegistryObject<Item> TEST_BLOCK_ITEM = ITEMS.register(REG_NAME, () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<?>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register(REG_NAME, () -> new BlockEntityType<>(
            TestBlock.Entity::new, Set.of(TEST_BLOCK.get()), null
    ));

    public MegaModelTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(TEST_BLOCK_ITEM);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents
    {

        @SubscribeEvent
        public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event)
        {
            var name = new ModelResourceLocation(MOD_ID, REG_NAME, "");
            event.getModels().computeIfPresent(name, (n, m) -> new TransformingModelWrapper(m));
        }

    }

    private static class TestBlock extends Block implements EntityBlock
    {
        public TestBlock()
        {
            super(Properties.of().mapColor(MapColor.STONE));
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new Entity(pos, state);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            var entity = level.getBlockEntity(pos);
            if (entity instanceof Entity e)
            {
                e.y += Mth.sign(hit.getLocation().y - pos.getY() - 0.5);
                e.requestModelDataUpdate();
                level.sendBlockUpdated(pos, state, state, 8);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            return super.use(state, level, pos, player, hand, hit);
        }

        public static class Entity extends BlockEntity
        {
            public int y = 0;

            public Entity(BlockPos pos, BlockState state)
            {
                super(TEST_BLOCK_ENTITY.get(), pos, state);
            }

            @Override
            public @NotNull ModelData getModelData()
            {
                return ModelData.builder().with(TestData.PROPERTY, new TestData(new Transformation(
                        new Vector3f(0, y * 0.2f, 0),
                        new Quaternionf(1f, 1f, 1f, 1f),
                        Transformation.identity().getScale(),
                        new Quaternionf(1f, 1f, 1f, 1f)
                ))).build();
            }
        }
    }

    private record TestData(Transformation transform)
    {
        public static final ModelProperty<TestData> PROPERTY = new ModelProperty<>();
    }

    private static class TransformingModelWrapper extends BakedModelWrapper<BakedModel>
    {
        public TransformingModelWrapper(BakedModel originalModel)
        {
            super(originalModel);
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
        {
            var quads = super.getQuads(state, side, rand, data, renderType);
            if (!data.has(TestData.PROPERTY))
                return quads;
            return QuadTransformers.applying(data.get(TestData.PROPERTY).transform()).process(quads);
        }
    }
}
