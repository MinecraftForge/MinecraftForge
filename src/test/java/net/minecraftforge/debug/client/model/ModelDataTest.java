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

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModelDataTest.MODID)
public class ModelDataTest
{
    public static final String MODID = "forgedebugmodeldata";
    public static final String VERSION = "1.0";

    private static final ModelProperty<Boolean> MAGIC_PROP = new ModelProperty<Boolean>();

    public ModelDataTest()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(TileEntityType.class, this::registerTileEntities);
        MinecraftForge.EVENT_BUS.addListener(this::modelBake);
    }

    public void modelBake(ModelBakeEvent event)
    {
        final IBakedModel stone = event.getModelRegistry().get(new ModelResourceLocation("minecraft:stone"));
        final IBakedModel dirt = event.getModelRegistry().get(new ModelResourceLocation("minecraft:dirt"));
        final IBakedModel old = event.getModelRegistry().get(new ModelResourceLocation("forge:modeltest"));
        event.getModelRegistry().put(new ModelResourceLocation("forge:modeltest"), new IDynamicBakedModel()
        {
            @Override
            public boolean isGui3d()
            {
                return false;
            }

            @Override
            public boolean isBuiltInRenderer()
            {
                return false;
            }

            @Override
            public boolean isAmbientOcclusion()
            {
                return true;
            }

            @Override
            public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand, IModelData modelData)
            {
                return modelData.getData(MAGIC_PROP) ? stone.getQuads(state, side, rand, modelData) : dirt.getQuads(state, side, rand, modelData);
            }

            @Override
            public TextureAtlasSprite getParticleTexture()
            {
                return MissingTextureSprite.getSprite();
            }

            @Override
            public ItemOverrideList getOverrides()
            {
                return null;
            }

            @Override
            @Nonnull
            public IModelData getModelData(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull IModelData tileData)
            {
                if (world.getBlockState(pos.down()).getBlock() == Blocks.AIR)
                {
                    tileData.setData(MAGIC_PROP, !tileData.getData(MAGIC_PROP));
                }
                return tileData;
            }
        });
    }

    private static final TileEntityType<Tile> TILE_TYPE = TileEntityType.Builder.create(Tile::new).build(null);

    private static class Tile extends TileEntity implements ITickable
    {

        public Tile()
        {
            super(TILE_TYPE);
        }

        private int counter;

        @Override
        public void tick()
        {
            if (world.isRemote && counter++ == 100)
            {
                ModelDataManager.requestModelDataRefresh(this);
                world.markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }

        @Override
        public IModelData getModelData()
        {
            return new ModelDataMap.Builder().withInitial(MAGIC_PROP, counter < 100).build();
        }
    }

    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockContainer(Block.Properties.create(Material.ROCK))
        {

            @Override
            @Nullable
            public TileEntity createNewTileEntity(IBlockReader worldIn)
            {
                return new Tile();
            }

            @Override
            public EnumBlockRenderType getRenderType(IBlockState state)
            {
                return EnumBlockRenderType.MODEL;
            }
        }.setRegistryName("forge:modeltest"));
    }

    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(TILE_TYPE.setRegistryName("forge:modeltest"));
    }
}
