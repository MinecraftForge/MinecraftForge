/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.client.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;
import java.util.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class ModelFluid implements IModel
{
    public static final ModelFluid WATER = new ModelFluid(FluidRegistry.WATER);
    public static final ModelFluid LAVA = new ModelFluid(FluidRegistry.LAVA);
    private final Fluid fluid;

    public ModelFluid(Fluid fluid)
    {
        this.fluid = fluid;
    }

    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableSet.of(fluid.getStill(), fluid.getFlowing());
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
        return new BakedFluid(state.apply(Optional.empty()), map, format, fluid.getColor(), bakedTextureGetter.apply(fluid.getStill()), bakedTextureGetter.apply(fluid.getFlowing()), fluid.isGaseous(), Optional.empty());
    }

    @Override
    public IModelState getDefaultState()
    {
        return ModelRotation.X0_Y0;
    }

    public static enum FluidLoader implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(ForgeVersion.MOD_ID) && (
                modelLocation.getResourcePath().equals("fluid") ||
                modelLocation.getResourcePath().equals("models/block/fluid") ||
                modelLocation.getResourcePath().equals("models/item/fluid"));
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            return WATER;
        }
    }

    private static final class BakedFluid implements IBakedModel
    {
        private static final int x[] = { 0, 0, 1, 1 };
        private static final int z[] = { 0, 1, 1, 0 };
        private static final float eps = 1e-3f;

        private final LoadingCache<Long, BakedFluid> modelCache = CacheBuilder.newBuilder().maximumSize(200).build(new CacheLoader<Long, BakedFluid>()
        {
            @Override
            public BakedFluid load(Long key) throws Exception
            {
                boolean statePresent = (key & 1) != 0;
                key >>>= 1;
                int[] cornerRound = new int[4];
                for(int i = 0; i < 4; i++)
                {
                    cornerRound[i] = (int)(key & 0x3FF);
                    key >>>= 10;
                }
                int flowRound = (int)(key & 0x7FF) - 1024;
                return new BakedFluid(transformation, transforms, format, color, still, flowing, gas, statePresent, cornerRound, flowRound);
            }
        });

        private final Optional<TRSRTransformation> transformation;
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;
        private final VertexFormat format;
        private final int color;
        private final TextureAtlasSprite still, flowing;
        private final boolean gas;
        private final EnumMap<EnumFacing, List<BakedQuad>> faceQuads;

        public BakedFluid(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, boolean gas, Optional<IExtendedBlockState> stateOption)
        {
            this(transformation, transforms, format, color, still, flowing, gas, stateOption.isPresent(), getCorners(stateOption), getFlow(stateOption));
        }

        private static int[] getCorners(Optional<IExtendedBlockState> stateOption)
        {
            int[] cornerRound = new int[]{0, 0, 0, 0};
            if(stateOption.isPresent())
            {
                IExtendedBlockState state = stateOption.get();
                for(int i = 0; i < 4; i++)
                {
                    Float level = state.getValue(BlockFluidBase.LEVEL_CORNERS[i]);
                    cornerRound[i] = Math.round((level == null ? 7f / 8 : level) * 768);
                }
            }
            return cornerRound;
        }

        private static int getFlow(Optional<IExtendedBlockState> stateOption)
        {
            Float flow = -1000f;
            if(stateOption.isPresent())
            {
                flow = stateOption.get().getValue(BlockFluidBase.FLOW_DIRECTION);
                if(flow == null) flow = -1000f;
            }
            int flowRound = (int)Math.round(Math.toDegrees(flow));
            flowRound = MathHelper.clamp(flowRound, -1000, 1000);
            return flowRound;
        }

        public BakedFluid(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, boolean gas, boolean statePresent, int[] cornerRound, int flowRound)
        {
            this.transformation = transformation;
            this.transforms = transforms;
            this.format = format;
            this.color = color;
            this.still = still;
            this.flowing = flowing;
            this.gas = gas;

            faceQuads = Maps.newEnumMap(EnumFacing.class);
            for(EnumFacing side : EnumFacing.values())
            {
                faceQuads.put(side, ImmutableList.of());
            }

            if(statePresent)
            {
                float[] y = new float[4];
                for(int i = 0; i < 4; i++)
                {
                    if(gas)
                    {
                        y[i] = 1 - cornerRound[i] / 768f;
                    }
                    else
                    {
                        y[i] = cornerRound[i] / 768f;
                    }
                }

                float flow = (float)Math.toRadians(flowRound);

                // top

                TextureAtlasSprite topSprite = flowing;
                float scale = 4;
                if(flow < -17F)
                {
                    flow = 0;
                    scale = 8;
                    topSprite = still;
                }

                float c = MathHelper.cos(flow) * scale;
                float s = MathHelper.sin(flow) * scale;

                EnumFacing side = gas ? EnumFacing.DOWN : EnumFacing.UP;
                UnpackedBakedQuad.Builder builder;
                ImmutableList.Builder<BakedQuad> topFaceBuilder = ImmutableList.builder();
                for(int k = 0; k < 2; k++)
                {
                    builder = new UnpackedBakedQuad.Builder(format);
                    builder.setQuadOrientation(side);
                    builder.setTexture(topSprite);
                    builder.setQuadTint(0);
                    for (int i = gas ? 3 : 0; i != (gas ? -1 : 4); i += (gas ? -1 : 1))
                    {
                        int l = (k * 3) + (1 - 2 * k) * i;
                        putVertex(
                            builder, side,
                            x[l], y[l], z[l],
                            topSprite.getInterpolatedU(8 + c * (x[l] * 2 - 1) + s * (z[l] * 2 - 1)),
                            topSprite.getInterpolatedV(8 + c * (x[(l + 1) % 4] * 2 - 1) + s * (z[(l + 1) % 4] * 2 - 1)));
                    }
                    topFaceBuilder.add(builder.build());
                }
                faceQuads.put(side, topFaceBuilder.build());

                // bottom

                side = side.getOpposite();
                builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(side);
                builder.setTexture(still);
                builder.setQuadTint(0);
                for(int i = gas ? 3 : 0; i != (gas ? -1 : 4); i+= (gas ? -1 : 1))
                {
                    putVertex(
                        builder, side,
                        z[i], gas ? 1 : 0, x[i],
                        still.getInterpolatedU(z[i] * 16),
                        still.getInterpolatedV(x[i] * 16));
                }
                faceQuads.put(side, ImmutableList.of(builder.build()));

                // sides

                for(int i = 0; i < 4; i++)
                {
                    side = EnumFacing.getHorizontal((5 - i) % 4);
                    BakedQuad q[] = new BakedQuad[2];

                    for(int k = 0; k < 2; k++)
                    {
                        builder = new UnpackedBakedQuad.Builder(format);
                        builder.setQuadOrientation(side);
                        builder.setTexture(flowing);
                        builder.setQuadTint(0);
                        for(int j = 0; j < 4; j++)
                        {
                            int l = (k * 3) + (1 - 2 * k) * j;
                            float yl = z[l] * y[(i + x[l]) % 4];
                            if(gas && z[l] == 0) yl = 1;
                            putVertex(
                                builder, side,
                                x[(i + x[l]) % 4], yl, z[(i + x[l]) % 4],
                                flowing.getInterpolatedU(x[l] * 8),
                                flowing.getInterpolatedV((gas ? yl : 1 - yl) * 8));
                        }
                        q[k] = builder.build();
                    }
                    faceQuads.put(side, ImmutableList.of(q[0], q[1]));
                }
            }
            else
            {
                // 1 quad for inventory
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(EnumFacing.UP);
                builder.setTexture(still);
                builder.setQuadTint(0); //I dont know if we also need this in inventory, but now it should be possible to color it here as well
                for(int i = 0; i < 4; i++)
                {
                    putVertex(
                        builder, EnumFacing.UP,
                        z[i], x[i], 0,
                        still.getInterpolatedU(z[i] * 16),
                        still.getInterpolatedV(x[i] * 16));
                }
                faceQuads.put(EnumFacing.SOUTH, ImmutableList.of(builder.build()));
            }
        }

        private void putVertex(UnpackedBakedQuad.Builder builder, EnumFacing side, float x, float y, float z, float u, float v)
        {
            for(int e = 0; e < format.getElementCount(); e++)
            {
                switch(format.getElement(e).getUsage())
                {
                case POSITION:
                    float[] data = new float[]{ x - side.getDirectionVec().getX() * eps, y, z - side.getDirectionVec().getZ() * eps, 1 };
                    if(transformation.isPresent() && !transformation.get().isIdentity())
                    {
                        Vector4f vec = new Vector4f(data);
                        transformation.get().getMatrix().transform(vec);
                        vec.get(data);
                    }
                    builder.put(e, data);
                    break;
                case COLOR:
                    builder.put(e,
                        ((color >> 16) & 0xFF) / 255f,
                        ((color >> 8) & 0xFF) / 255f,
                        (color & 0xFF) / 255f,
                        ((color >> 24) & 0xFF) / 255f);
                    break;
                case UV: if(format.getElement(e).getIndex() == 0)
                {
                    builder.put(e, u, v, 0f, 1f);
                    break;
                }
                case NORMAL:
                    builder.put(e, (float)side.getFrontOffsetX(), (float)side.getFrontOffsetY(), (float)side.getFrontOffsetZ(), 0f);
                    break;
                default:
                    builder.put(e);
                    break;
                }
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return true;
        }

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
        public TextureAtlasSprite getParticleTexture()
        {
            return still;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            BakedFluid model = this;
            if(state instanceof IExtendedBlockState)
            {
                IExtendedBlockState exState = (IExtendedBlockState)state;
                int[] cornerRound = getCorners(Optional.of(exState));
                int flowRound = getFlow(Optional.of(exState));
                long key = flowRound + 1024;
                for(int i = 3; i >= 0; i--)
                {
                    key <<= 10;
                    key |= cornerRound[i];
                }
                key <<= 1;
                key |= 1;
                model = modelCache.getUnchecked(key);
            }
            if(side == null) return ImmutableList.of();
            return model.faceQuads.get(side);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.NONE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type)
        {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, type);
        }
    }

    @Override
    public ModelFluid process(ImmutableMap<String, String> customData)
    {
        if(!customData.containsKey("fluid")) return this;

        String fluidStr = customData.get("fluid");
        JsonElement e = new JsonParser().parse(fluidStr);
        String fluid = e.getAsString();
        if(!FluidRegistry.isFluidRegistered(fluid))
        {
            FMLLog.log.fatal("fluid '{}' not found", fluid);
            return WATER;
        }
        return new ModelFluid(FluidRegistry.getFluid(fluid));
    }
}
