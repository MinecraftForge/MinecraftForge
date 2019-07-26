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
 *//*


package net.minecraftforge.debug.client.model;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
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
import net.minecraftforge.registries.ObjectHolder;

@Mod(ModelDataTest.MODID)
public class ModelDataTest
{
    public static final String MODID = "forgedebugmodeldata";
    public static final String VERSION = "1.0";

    private static final ModelProperty<Boolean> MAGIC_PROP = new ModelProperty<Boolean>();
    private static final String BLOCK_NAME = "block";
    private static final String BLOCK_REGNAME = MODID + ":" + BLOCK_NAME;

    @ObjectHolder(BLOCK_REGNAME)
    public static final Block MY_BLOCK = null;
    @ObjectHolder(BLOCK_REGNAME)
    public static final TileEntityType<Tile> TILE_TYPE = null;

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
        event.getModelRegistry().put(new ModelResourceLocation(BLOCK_REGNAME, ""), new IDynamicBakedModel()
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
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData)
            {
                return modelData.getData(MAGIC_PROP) ? stone.getQuads(state, side, rand, modelData) : dirt.getQuads(state, side, rand, modelData);
            }

            @Override
            public TextureAtlasSprite getParticleTexture()
            {
                return MissingTextureSprite.func_217790_a();
            }

            @Override
            public ItemOverrideList getOverrides()
            {
                return null;
            }

            @Override
            @Nonnull
            public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
            {
                if (world.getBlockState(pos.down()).getBlock() == Blocks.AIR)
                {
                    tileData.setData(MAGIC_PROP, !tileData.getData(MAGIC_PROP));
                }
                return tileData;
            }
        });
    }


    private static class Tile extends TileEntity implements ITickableTileEntity
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
                world.markForRerender(getPos());
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
        event.getRegistry().register(new ContainerBlock(Block.Properties.create(Material.ROCK))
        {
            @Override
            @Nullable
            public TileEntity createNewTileEntity(IBlockReader worldIn)
            {
                return new Tile();
            }

            @Override
            public BlockRenderType getRenderType(BlockState state)
            {
                return BlockRenderType.MODEL;
            }
        }.setRegistryName(MODID, BLOCK_NAME));
    }

    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(TileEntityType.Builder.func_223042_a(Tile::new, MY_BLOCK).build(null).setRegistryName(MODID, BLOCK_NAME));
    }
}
*/
