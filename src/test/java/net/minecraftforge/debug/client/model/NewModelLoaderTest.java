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

package net.minecraftforge.debug.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.ItemLayersModelBuilder;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.client.model.generators.loaders.SeparatePerspectiveModelBuilder;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;

@Mod(NewModelLoaderTest.MODID)
public class NewModelLoaderTest
{
    public static final String MODID = "new_model_loader_test";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Block> obj_block = BLOCKS.register("obj_block", () ->
            new Block(Block.Properties.of(Material.WOOD).strength(10)) {
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
            new BlockItem(obj_block.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)) {
                @Override
                public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity)
                {
                    return armorType == EquipmentSlot.HEAD;
                }
            }
    );

    public static RegistryObject<Item> custom_transforms = ITEMS.register("custom_transforms", () ->
            new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public static RegistryObject<Item> custom_vanilla_loader = ITEMS.register("custom_vanilla_loader", () ->
            new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public static RegistryObject<Item> custom_loader = ITEMS.register("custom_loader", () ->
            new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public static RegistryObject<Item> item_layers = ITEMS.register("item_layers", () ->
            new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public static RegistryObject<Item> separate_perspective = ITEMS.register("separate_perspective", () ->
            new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public NewModelLoaderTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::modelRegistry);
        modEventBus.addListener(this::datagen);
    }

    public void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "custom_loader"), new TestLoader());
    }

    static class TestLoader implements IModelLoader<TestModel>
    {
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager)
        {
        }

        @Override
        public TestModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            return new TestModel();
        }
    }

    static class TestModel implements ISimpleModelGeometry<TestModel>
    {
        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function< net.minecraft.client.resources.model.Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
        {
            TextureAtlasSprite texture = spriteGetter.apply(owner.resolveTexture("particle"));

            BakedQuadBuilder builder = new BakedQuadBuilder();

            builder.setTexture(texture);
            builder.setQuadOrientation(Direction.UP);

            putVertex(builder, 0,1,0.5f, texture.getU(0), texture.getV(0), 1, 1, 1);
            putVertex(builder, 0,0,0.5f, texture.getU(0), texture.getV(16), 1, 1, 1);
            putVertex(builder, 1,0,0.5f, texture.getU(16), texture.getV(16), 1, 1, 1);
            putVertex(builder, 1,1,0.5f, texture.getU(16), texture.getV(0), 1, 1, 1);

            modelBuilder.addGeneralQuad(builder.build());
        }

        private void putVertex(BakedQuadBuilder builder, int x, float y, float z, float u, float v, float red, float green, float blue)
        {
            ImmutableList<VertexFormatElement> elements = DefaultVertexFormat.BLOCK.getElements();
            for(int i=0;i<elements.size();i++)
            {
                switch(elements.get(i).getUsage())
                {
                    case POSITION:
                        builder.put(i, x, y, z);
                        break;
                    case UV:
                        if (elements.get(i).getIndex() == 0)
                            builder.put(i, u, v);
                        else
                            builder.put(i);
                        break;
                    case COLOR:
                        builder.put(i, red, green, blue, 1.0f);
                        break;
                    default:
                        builder.put(i);
                        break;
                }
            }
        }

        @Override
        public Collection< net.minecraft.client.resources.model.Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            return Collections.singleton(owner.resolveTexture("particle"));
        }
    }

    private void datagen(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient())
        {
            // Let blockstate provider see generated item models by passing its existing file helper
            ItemModelProvider itemModels = new ItemModels(gen, event.getExistingFileHelper());
            gen.addProvider(itemModels);
            gen.addProvider(new BlockStates(gen, itemModels.existingFileHelper));
        }
    }

    public static class ItemModels extends ItemModelProvider
    {
        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
        {
            super(generator, MODID, existingFileHelper);
        }

        @Override
        protected void registerModels()
        {
            withExistingParent(NewModelLoaderTest.item_layers.getId().getPath(), "forge:item/default")
                    .texture("layer0", "minecraft:item/coal")
                    .texture("layer1", "minecraft:item/stick")
                    .customLoader(ItemLayersModelBuilder::begin)
                        .fullbright(1)
                    .end();
            withExistingParent(NewModelLoaderTest.separate_perspective.getId().getPath(), "forge:item/default")
                    .customLoader(SeparatePerspectiveModelBuilder::begin)
                        .base(nested().parent(getExistingFile(mcLoc("minecraft:item/coal"))))
                        .perspective(ItemTransforms.TransformType.GUI, nested().parent(getExistingFile(mcLoc("minecraft:item/snowball"))))
                        .perspective(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, nested().parent(getExistingFile(mcLoc("minecraft:item/bone"))))
                    .end();
        }
    }

    public static class BlockStates extends BlockStateProvider
    {
        public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper)
        {
            super(gen, MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            BlockModelBuilder model = models()
                    .getBuilder(NewModelLoaderTest.obj_block.getId().getPath())
                    .customLoader(OBJLoaderBuilder::begin)
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
