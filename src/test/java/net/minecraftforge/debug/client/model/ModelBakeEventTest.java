/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import com.google.common.primitives.Ints;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = ModelBakeEventTest.MODID, name = "ForgeDebugModelBakeEvent", version = ModelBakeEventTest.VERSION, acceptableRemoteVersions = "*")
public class ModelBakeEventTest
{
    public static final String MODID = "forgedebugmodelbakeevent";
    public static final String VERSION = "1.0";
    public static final int cubeSize = 3;

    private static ResourceLocation blockName = new ResourceLocation(MODID, CustomModelBlock.name);
    private static ModelResourceLocation blockLocation = new ModelResourceLocation(blockName, "normal");
    private static ModelResourceLocation itemLocation = new ModelResourceLocation(blockName, "inventory");

    @ObjectHolder(CustomModelBlock.name)
    public static final Block CUSTOM_BLOCK = null;
    @ObjectHolder(CustomModelBlock.name)
    public static final Block CUSTOM_ITEM = null;

    @SuppressWarnings("unchecked")
    public static final IUnlistedProperty<Integer>[] properties = new IUnlistedProperty[6];

    static
    {
        for (EnumFacing f : EnumFacing.values())
        {
            properties[f.ordinal()] = Properties.toUnlisted(PropertyInteger.create(f.getName(), 0, (1 << (cubeSize * cubeSize)) - 1));
        }
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(new CustomModelBlock());
            GameRegistry.registerTileEntity(CustomTileEntity.class, MODID.toLowerCase() + ":custom_tile_entity");
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ItemBlock(CUSTOM_BLOCK).setRegistryName(CUSTOM_BLOCK.getRegistryName()));
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class BakeEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            Item item = Item.getItemFromBlock(CUSTOM_BLOCK);
            ModelLoader.setCustomModelResourceLocation(item, 0, itemLocation);
            ModelLoader.setCustomStateMapper(CUSTOM_BLOCK, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_)
                {
                    return blockLocation;
                }
            });
        }

        @SubscribeEvent
        public static void onModelBakeEvent(ModelBakeEvent event)
        {
            TextureAtlasSprite base = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/slime");
            TextureAtlasSprite overlay = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/redstone_block");
            IBakedModel customModel = new CustomModel(base, overlay);
            event.getModelRegistry().putObject(blockLocation, customModel);
            event.getModelRegistry().putObject(itemLocation, customModel);
        }

        public static class CustomModel implements IBakedModel
        {
            private final TextureAtlasSprite base, overlay;
            //private boolean hasStateSet = false;

            public CustomModel(TextureAtlasSprite base, TextureAtlasSprite overlay)
            {
                this.base = base;
                this.overlay = overlay;
            }

            // TODO update to builder
            private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v)
            {
                return new int[]{
                    Float.floatToRawIntBits(x),
                    Float.floatToRawIntBits(y),
                    Float.floatToRawIntBits(z),
                    color,
                    Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                    Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                    0
                };
            }

            private BakedQuad createSidedBakedQuad(float x1, float x2, float z1, float z2, float y, TextureAtlasSprite texture, EnumFacing side)
            {
                Vec3d v1 = rotate(new Vec3d(x1 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
                Vec3d v2 = rotate(new Vec3d(x1 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
                Vec3d v3 = rotate(new Vec3d(x2 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
                Vec3d v4 = rotate(new Vec3d(x2 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
                return new BakedQuad(Ints.concat(
                    vertexToInts((float) v1.x, (float) v1.y, (float) v1.z, -1, texture, 0, 0),
                    vertexToInts((float) v2.x, (float) v2.y, (float) v2.z, -1, texture, 0, 16),
                    vertexToInts((float) v3.x, (float) v3.y, (float) v3.z, -1, texture, 16, 16),
                    vertexToInts((float) v4.x, (float) v4.y, (float) v4.z, -1, texture, 16, 0)
                ), -1, side, texture, true, DefaultVertexFormats.BLOCK);
            }

            @Override
            public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
            {
                if (side != null)
                {
                    return ImmutableList.of();
                }
                IExtendedBlockState exState = (IExtendedBlockState) state;
                int len = cubeSize * 5 + 1;
                List<BakedQuad> ret = new ArrayList<BakedQuad>();
                for (EnumFacing f : EnumFacing.values())
                {
                    ret.add(createSidedBakedQuad(0, 1, 0, 1, 1, base, f));
                    if (state != null)
                    {
                        for (int i = 0; i < cubeSize; i++)
                        {
                            for (int j = 0; j < cubeSize; j++)
                            {
                                Integer value = exState.getValue(properties[f.ordinal()]);
                                if (value != null && (value & (1 << (i * cubeSize + j))) != 0)
                                {
                                    ret.add(createSidedBakedQuad((float) (1 + i * 5) / len, (float) (5 + i * 5) / len, (float) (1 + j * 5) / len, (float) (5 + j * 5) / len, 1.0001f, overlay, f));
                                }
                            }
                        }
                    }
                }
                return ret;
            }

            @Override
            public boolean isGui3d()
            {
                return true;
            }

            @Override
            public boolean isAmbientOcclusion()
            {
                return true;
            }

            @Override
            public boolean isBuiltInRenderer()
            {
                return false;
            }

            @Override
            public TextureAtlasSprite getParticleTexture()
            {
                return this.base;
            }

            @Override
            public ItemOverrideList getOverrides()
            {
                return ItemOverrideList.NONE;
            }
        }
    }

    public static class CustomModelBlock extends BlockContainer
    {
        public static final String name = "custom_model_block";

        private CustomModelBlock()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(blockName);
        }

        @Override
        public EnumBlockRenderType getRenderType(IBlockState state)
        {
            return EnumBlockRenderType.MODEL;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new CustomTileEntity();
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof CustomTileEntity)
            {
                CustomTileEntity cte = (CustomTileEntity) te;
                Vec3d vec = revRotate(new Vec3d(hitX - .5, hitY - .5, hitZ - .5), side).addVector(.5, .5, .5);
                IUnlistedProperty<Integer> property = properties[side.ordinal()];
                Integer value = cte.getState().getValue(property);
                if (value == null)
                {
                    value = 0;
                }
                value ^= (1 << (cubeSize * ((int) (vec.x * (cubeSize - .0001))) + ((int) (vec.z * (cubeSize - .0001)))));
                cte.setState(cte.getState().withProperty(property, value));
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
            return true;
        }

        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof CustomTileEntity)
            {
                CustomTileEntity cte = (CustomTileEntity) te;
                return cte.getState();
            }
            return state;
        }

        @Override
        protected BlockStateContainer createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[0], properties);
        }
    }

    public static class CustomTileEntity extends TileEntity
    {
        private IExtendedBlockState state;

        public CustomTileEntity()
        {
        }

        public IExtendedBlockState getState()
        {
            if (state == null)
            {
                state = (IExtendedBlockState) getBlockType().getDefaultState();
            }
            return state;
        }

        public void setState(IExtendedBlockState state)
        {
            this.state = state;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static Vec3d rotate(Vec3d vec, EnumFacing side)
    {
        switch (side)
        {
            case DOWN:
                return new Vec3d(vec.x, -vec.y, -vec.z);
            case UP:
                return new Vec3d(vec.x, vec.y, vec.z);
            case NORTH:
                return new Vec3d(vec.x, vec.z, -vec.y);
            case SOUTH:
                return new Vec3d(vec.x, -vec.z, vec.y);
            case WEST:
                return new Vec3d(-vec.y, vec.x, vec.z);
            case EAST:
                return new Vec3d(vec.y, -vec.x, vec.z);
        }
        throw new IllegalArgumentException("Unknown Side " + side);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static Vec3d revRotate(Vec3d vec, EnumFacing side)
    {
        switch (side)
        {
            case DOWN:
                return new Vec3d(vec.x, -vec.y, -vec.z);
            case UP:
                return new Vec3d(vec.x, vec.y, vec.z);
            case NORTH:
                return new Vec3d(vec.x, -vec.z, vec.y);
            case SOUTH:
                return new Vec3d(vec.x, vec.z, -vec.y);
            case WEST:
                return new Vec3d(vec.y, -vec.x, vec.z);
            case EAST:
                return new Vec3d(-vec.y, vec.x, vec.z);
        }
        throw new IllegalArgumentException("Unknown Side " + side);
    }
}
