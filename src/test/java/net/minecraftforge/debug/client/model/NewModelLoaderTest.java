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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.ItemLayersModelBuilder;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.client.model.generators.loaders.SeparatePerspectiveModelBuilder;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Mod(NewModelLoaderTest.MODID)
public class NewModelLoaderTest
{
    public static final String MODID = "new_model_loader_test";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Block> obj_block = BLOCKS.register("obj_block", () ->
            new Block(Block.Properties.of(Material.WOOD).strength(10)) {
                @Override
                protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                {
                    builder.add(BlockStateProperties.HORIZONTAL_FACING);
                }

                @Nullable
                @Override
                public BlockState getStateForPlacement(BlockItemUseContext context)
                {
                    return defaultBlockState().setValue(
                            BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection()
                    );
                }

                @Override
                public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
                {
                    return Block.box(2,2,2,14,14,14);
                }
            }
    );

    public static RegistryObject<Item> obj_item = ITEMS.register("obj_block", () ->
            new BlockItem(obj_block.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)) {
                @Override
                public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity)
                {
                    return armorType == EquipmentSlotType.HEAD;
                }
            }
    );

    public static RegistryObject<Item> custom_transforms = ITEMS.register("custom_transforms", () ->
            new Item(new Item.Properties().tab(ItemGroup.TAB_MISC))
    );

    public static RegistryObject<Item> custom_vanilla_loader = ITEMS.register("custom_vanilla_loader", () ->
            new Item(new Item.Properties().tab(ItemGroup.TAB_MISC))
    );

    public static RegistryObject<Item> custom_loader = ITEMS.register("custom_loader", () ->
            new Item(new Item.Properties().tab(ItemGroup.TAB_MISC))
    );

    public static RegistryObject<Item> item_layers = ITEMS.register("item_layers", () ->
            new Item(new Item.Properties().tab(ItemGroup.TAB_MISC))
    );

    public static RegistryObject<Item> separate_perspective = ITEMS.register("separate_perspective", () ->
            new Item(new Item.Properties().tab(ItemGroup.TAB_MISC))
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
        public void onResourceManagerReload(IResourceManager resourceManager)
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
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
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
            ImmutableList<VertexFormatElement> elements = DefaultVertexFormats.BLOCK.getElements();
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
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
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
                        .perspective(ItemCameraTransforms.TransformType.GUI, nested().parent(getExistingFile(mcLoc("minecraft:item/snowball"))))
                        .perspective(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, nested().parent(getExistingFile(mcLoc("minecraft:item/bone"))))
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
            simpleBlock(NewModelLoaderTest.obj_block.get(), models()
                    .getBuilder(NewModelLoaderTest.obj_block.getId().getPath())
                    .customLoader(OBJLoaderBuilder::begin)
                            .modelLocation(new ResourceLocation("new_model_loader_test:models/item/sugar_glider.obj"))
                            .flipV(true)
                    .end()
                    .texture("qr", "minecraft:block/oak_planks")
                    .texture("particle", "#qr")
            );
        }
    }
}
