/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Function;

import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.Nullable;

@Mod(NewModelLoaderTest.MODID)
public class NewModelLoaderTest
{
    public static final String MODID = "new_model_loader_test";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Block> obj_block = BLOCKS.register("obj_block", () ->
            new Block(Block.Properties.of().mapColor(MapColor.WOOD).strength(10)) {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
                {
                    builder.add(BlockStateProperties.HORIZONTAL_FACING);
                }

                @Nullable
                @Override
                public BlockState getStateForPlacement(BlockPlaceContext context)
                {
                    return defaultBlockState().setValue(
                            BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection()
                    );
                }

                @Override
                public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
                {
                    return Block.box(2,2,2,14,14,14);
                }
            }
    );

    public static RegistryObject<Item> obj_item = ITEMS.register("obj_block", () ->
            new BlockItem(obj_block.get(), new Item.Properties()) {
                @Override
                public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity)
                {
                    return armorType == EquipmentSlot.HEAD;
                }
            }
    );

    public static RegistryObject<Item> custom_transforms = ITEMS.register("custom_transforms", () ->
            new Item(new Item.Properties())
    );

    public static RegistryObject<Item> custom_vanilla_loader = ITEMS.register("custom_vanilla_loader", () ->
            new Item(new Item.Properties())
    );

    public static RegistryObject<Item> custom_loader = ITEMS.register("custom_loader", () ->
            new Item(new Item.Properties())
    );

    public static RegistryObject<Item> item_layers = ITEMS.register("item_layers", () ->
            new Item(new Item.Properties())
    );

    public static RegistryObject<Item> separate_perspective = ITEMS.register("separate_perspective", () ->
            new Item(new Item.Properties())
    );

    public NewModelLoaderTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::modelRegistry);
        modEventBus.addListener(this::datagen);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            Arrays.asList(
                obj_item,
                custom_transforms,
                custom_vanilla_loader,
                custom_loader,
                item_layers,
                separate_perspective
            ).forEach(event::accept);
        }
    }

    public void modelRegistry(ModelEvent.RegisterGeometryLoaders event)
    {
        event.register("custom_loader", new TestLoader());
    }

    static class TestLoader implements IGeometryLoader<TestModel>
    {
        @Override
        public TestModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext)
        {
            return new TestModel();
        }
    }

    static class TestModel extends SimpleUnbakedGeometry<TestModel>
    {
        @Override
        protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<net.minecraft.client.resources.model.Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
            TextureAtlasSprite texture = spriteGetter.apply(owner.getMaterial("particle"));

            var quadBaker = new QuadBakingVertexConsumer.Buffered();

            quadBaker.setDirection(Direction.UP);
            quadBaker.setSprite(texture);

            quadBaker.vertex(0, 1, 0.5f).color(255, 255, 255, 255).uv(texture.getU(0), texture.getV(0)).uv2(0).normal(0, 0, 0).endVertex();
            quadBaker.vertex(0, 0, 0.5f).color(255, 255, 255, 255).uv(texture.getU(0), texture.getV(16)).uv2(0).normal(0, 0, 0).endVertex();
            quadBaker.vertex(1, 0, 0.5f).color(255, 255, 255, 255).uv(texture.getU(16), texture.getV(16)).uv2(0).normal(0, 0, 0).endVertex();
            quadBaker.vertex(1, 1, 0.5f).color(255, 255, 255, 255).uv(texture.getU(16), texture.getV(0)).uv2(0).normal(0, 0, 0).endVertex();

            modelBuilder.addUnculledFace(quadBaker.getQuad());
        }
    }

    private void datagen(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        final PackOutput output = gen.getPackOutput();

        // Let blockstate provider see generated item models by passing its existing file helper
        ItemModelProvider itemModels = new ItemModels(output, event.getExistingFileHelper());
        gen.addProvider(event.includeClient(), itemModels);
        gen.addProvider(event.includeClient(), new BlockStates(output, itemModels.existingFileHelper));
    }

    public static class ItemModels extends ItemModelProvider
    {
        public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper)
        {
            super(output, MODID, existingFileHelper);
        }

        @Override
        protected void registerModels()
        {
            withExistingParent(NewModelLoaderTest.item_layers.getId().getPath(), "forge:item/default")
                    .texture("particle", "minecraft:block/red_stained_glass")
                    .texture("layer0", "minecraft:item/coal")
                    .texture("layer1", "minecraft:item/stick")
                    .customLoader(ItemLayerModelBuilder::begin)
                        .emissive(15, 15, 1)
                    .end();
            withExistingParent(NewModelLoaderTest.separate_perspective.getId().getPath(), "forge:item/default")
                    .customLoader(SeparateTransformsModelBuilder::begin)
                        .base(nested().parent(getExistingFile(mcLoc("minecraft:item/coal"))))
                        .perspective(ItemDisplayContext.GUI, nested().parent(getExistingFile(mcLoc("minecraft:item/snowball"))))
                        .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, nested().parent(getExistingFile(mcLoc("minecraft:item/bone"))))
                    .end();
        }
    }

    public static class BlockStates extends BlockStateProvider
    {
        public BlockStates(PackOutput output, ExistingFileHelper exFileHelper)
        {
            super(output, MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            BlockModelBuilder model = models()
                    .getBuilder(NewModelLoaderTest.obj_block.getId().getPath())
                    .customLoader(ObjModelBuilder::begin)
                            .modelLocation(new ResourceLocation("new_model_loader_test:models/item/sugar_glider.obj"))
                            .flipV(true)
                    .end()
                    .texture("qr", "minecraft:block/oak_planks")
                    .texture("particle", "#qr");
            getVariantBuilder(NewModelLoaderTest.obj_block.get())
                    .partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                            .addModels(new ConfiguredModel(model, 0, 90, false))
                    .partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                            .addModels(new ConfiguredModel(model, 0, 270, false))
                    .partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                            .addModels(new ConfiguredModel(model))
                    .partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                            .addModels(new ConfiguredModel(model, 0, 180, false))
                    .partialState();
        }
    }
}
