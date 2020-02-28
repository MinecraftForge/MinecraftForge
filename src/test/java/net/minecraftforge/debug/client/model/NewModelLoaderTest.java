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

package net.minecraftforge.debug.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Mod(NewModelLoaderTest.MODID)
public class NewModelLoaderTest
{
    public static final String MODID = "new_model_loader_test";
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Block> obj_block = BLOCKS.register("obj_block", () ->
            new Block(Block.Properties.create(Material.WOOD)) {
                @Override
                protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
                {
                    builder.add(BlockStateProperties.HORIZONTAL_FACING);
                }

                @Nullable
                @Override
                public BlockState getStateForPlacement(BlockItemUseContext context)
                {
                    return getDefaultState().with(
                            BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing()
                    );
                }
            }
    );

    public static RegistryObject<Item> obj_item = ITEMS.register("obj_block", () ->
            new BlockItem(obj_block.get(), new Item.Properties().group(ItemGroup.MISC)) {
                @Override
                public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity)
                {
                    return armorType == EquipmentSlotType.HEAD;
                }
            }
    );

    public static RegistryObject<Item> custom_transforms = ITEMS.register("custom_transforms", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC))
    );

    public static RegistryObject<Item> custom_vanilla_loader = ITEMS.register("custom_vanilla_loader", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC))
    );

    public static RegistryObject<Item> custom_loader = ITEMS.register("custom_loader", () ->
            new Item(new Item.Properties().group(ItemGroup.MISC))
    );

    public NewModelLoaderTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        ModelLoaderRegistry2.registerLoader(new ResourceLocation(MODID, "custom_loader"), new TestLoader());
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
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
        {
            TextureAtlasSprite texture = spriteGetter.apply(new ResourceLocation(owner.resolveTexture("particle")));

            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);

            builder.setTexture(texture);
            builder.setQuadOrientation(Direction.UP);

            putVertex(builder, format, 0,1,0.5f, texture.getInterpolatedU(0), texture.getInterpolatedV(0), 1, 1, 1);
            putVertex(builder, format, 0,0,0.5f, texture.getInterpolatedU(0), texture.getInterpolatedV(16), 1, 1, 1);
            putVertex(builder, format, 1,0,0.5f, texture.getInterpolatedU(16), texture.getInterpolatedV(16), 1, 1, 1);
            putVertex(builder, format, 1,1,0.5f, texture.getInterpolatedU(16), texture.getInterpolatedV(0), 1, 1, 1);

            modelBuilder.addGeneralQuad(builder.build());
        }

        private void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, int x, float y, float z, float u, float v, float red, float green, float blue)
        {
            for(int i=0;i<format.getElementCount();i++)
            {
                switch(format.getElement(i).getUsage())
                {
                    case POSITION:
                        builder.put(i, x, y, z);
                        break;
                    case UV:
                        if (format.getElement(i).getIndex() == 0)
                            builder.put(i, u, v);
                        else
                            builder.put(i);
                        break;
                    case COLOR:
                        builder.put(i, red, green, blue, 1.0f);
                        break;
                    case NORMAL:
                        builder.put(i, 0, 0, 1);
                        break;
                    default:
                        builder.put(i);
                        break;
                }
            }
        }

        @Override
        public Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
        {
            return Collections.singleton(new ResourceLocation(owner.resolveTexture("particle")));
        }
    }
}
