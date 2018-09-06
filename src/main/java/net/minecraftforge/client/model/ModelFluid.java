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

package net.minecraftforge.client.model;

import java.util.function.Function;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class ModelFluid implements IModel
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelFluid WATER = new ModelFluid(FluidRegistry.WATER);
    public static final ModelFluid LAVA = new ModelFluid(FluidRegistry.LAVA);

    private final Fluid fluid;

    public ModelFluid(Fluid fluid)
    {
        this.fluid = fluid;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return fluid.getOverlay() != null
                ? ImmutableSet.of(fluid.getStill(), fluid.getFlowing(), fluid.getOverlay())
                : ImmutableSet.of(fluid.getStill(), fluid.getFlowing());
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return new CachingBakedFluid(
                state.apply(Optional.empty()),
                PerspectiveMapWrapper.getTransforms(state),
                format,
                fluid.getColor(),
                bakedTextureGetter.apply(fluid.getStill()),
                bakedTextureGetter.apply(fluid.getFlowing()),
                Optional.ofNullable(fluid.getOverlay()).map(bakedTextureGetter),
                fluid.isLighterThanAir(),
                Optional.empty()
        );
    }

    public enum FluidLoader implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public void func_195410_a(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getNamespace().equals(ForgeVersion.MOD_ID) && (
                modelLocation.getPath().equals("fluid") ||
                modelLocation.getPath().equals("models/block/fluid") ||
                modelLocation.getPath().equals("models/item/fluid"));
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            return WATER;
        }
    }

    private static final class CachingBakedFluid extends BakedFluid
    {
        private final LoadingCache<Long, BakedFluid> modelCache = CacheBuilder.newBuilder().maximumSize(200).build(new CacheLoader<Long, BakedFluid>()
        {
            @Override
            public BakedFluid load(Long key)
            {
                boolean statePresent = (key & 1) != 0;
                key >>>= 1;
                int[] cornerRound = new int[4];
                for (int i = 0; i < 4; i++)
                {
                    cornerRound[i] = (int) (key & 0x3FF);
                    key >>>= 10;
                }
                int flowRound = (int) (key & 0x7FF) - 1024;
                key >>>= 11;
                boolean[] overlaySides = new boolean[4];
                for (int i = 0; i < 4; i++)
                {
                    overlaySides[i] = (key & 1) != 0;
                    key >>>= 1;
                }
                return new BakedFluid(transformation, transforms, format, color, still, flowing, overlay, gas, statePresent, cornerRound, flowRound, overlaySides);
            }
        });

        public CachingBakedFluid(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, Optional<TextureAtlasSprite> overlay, boolean gas, Optional<IExtendedBlockState> stateOption)
        {
            super(transformation, transforms, format, color, still, flowing, overlay, gas, stateOption.isPresent(), getCorners(stateOption), getFlow(stateOption), getOverlay(stateOption));
        }

        /**
         * Gets the quantized fluid levels for each corner.
         *
         * Each value is packed into 10 bits of the model key, so max range is [0,1024).
         * The value is currently stored/interpreted as the closest multiple of 1/864.
         * The divisor is chosen here to allows likely flow values to be exactly representable
         * while also providing good use of the available value range.
         * (For fluids with default quanta, this evenly divides the per-block intervals of 1/9 by 96)
         */
        private static int[] getCorners(Optional<IExtendedBlockState> stateOption)
        {
            int[] cornerRound = {0, 0, 0, 0};
            if (stateOption.isPresent())
            {
                IExtendedBlockState state = stateOption.get();
                for (int i = 0; i < 4; i++)
                {
                    Float level = state.getValue(BlockFluidBase.LEVEL_CORNERS[i]);
                    cornerRound[i] = Math.round((level == null ? 8f / 9f : level) * 864);
                }
            }
            return cornerRound;
        }

        /**
         * Gets the quantized flow direction of the fluid.
         *
         * This value comprises 11 bits of the model key, and is signed, so the max range is [-1024,1024).
         * The value is currently stored as the angle rounded to the nearest degree.
         * A value of -1000 is used to signify no flow.
         */
        private static int getFlow(Optional<IExtendedBlockState> stateOption)
        {
            Float flow = -1000f;
            if (stateOption.isPresent())
            {
                flow = stateOption.get().getValue(BlockFluidBase.FLOW_DIRECTION);
                if (flow == null) flow = -1000f;
            }
            int flowRound = (int) Math.round(Math.toDegrees(flow));
            flowRound = MathHelper.clamp(flowRound, -1000, 1000);
            return flowRound;
        }

        /**
         * Gets the overlay texture flag for each side.
         *
         * This value determines if the fluid "overlay" texture should be used for that side,
         * instead of the normal "flowing" texture (if applicable for that fluid).
         * The sides are stored here by their regular horizontal index.
         */
        private static boolean[] getOverlay(Optional<IExtendedBlockState> stateOption)
        {
            boolean[] overlaySides = new boolean[4];
            if (stateOption.isPresent())
            {
                IExtendedBlockState state = stateOption.get();
                for (int i = 0; i < 4; i++)
                {
                    Boolean overlay = state.getValue(BlockFluidBase.SIDE_OVERLAYS[i]);
                    if (overlay != null) overlaySides[i] = overlay;
                }
            }
            return overlaySides;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            if (side != null && state instanceof IExtendedBlockState)
            {
                Optional<IExtendedBlockState> exState = Optional.of((IExtendedBlockState)state);

                int[] cornerRound = getCorners(exState);
                int flowRound = getFlow(exState);
                boolean[] overlaySides = getOverlay(exState);

                long key = 0L;
                for (int i = 3; i >= 0; i--)
                {
                    key <<= 1;
                    key |= overlaySides[i] ? 1 : 0;
                }
                key <<= 11;
                key |= flowRound + 1024;
                for (int i = 3; i >= 0; i--)
                {
                    key <<= 10;
                    key |= cornerRound[i];
                }
                key <<= 1;
                key |= 1;

                return modelCache.getUnchecked(key).getQuads(state, side, rand);
            }

            return super.getQuads(state, side, rand);
        }
    }

    private static class BakedFluid implements IBakedModel
    {
        private static final int x[] = { 0, 0, 1, 1 };
        private static final int z[] = { 0, 1, 1, 0 };
        private static final float eps = 1e-3f;

        protected final Optional<TRSRTransformation> transformation;
        protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
        protected final VertexFormat format;
        protected final int color;
        protected final TextureAtlasSprite still, flowing;
        protected final Optional<TextureAtlasSprite> overlay;
        protected final boolean gas;
        protected final ImmutableMap<EnumFacing, ImmutableList<BakedQuad>> faceQuads;

        public BakedFluid(Optional<TRSRTransformation> transformation, ImmutableMap<TransformType, TRSRTransformation> transforms, VertexFormat format, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, Optional<TextureAtlasSprite> overlay, boolean gas, boolean statePresent, int[] cornerRound, int flowRound, boolean[] sideOverlays)
        {
            this.transformation = transformation;
            this.transforms = transforms;
            this.format = format;
            this.color = color;
            this.still = still;
            this.flowing = flowing;
            this.overlay = overlay;
            this.gas = gas;
            this.faceQuads = buildQuads(statePresent, cornerRound, flowRound, sideOverlays);
        }

        private ImmutableMap<EnumFacing, ImmutableList<BakedQuad>> buildQuads(boolean statePresent, int[] cornerRound, int flowRound, boolean[] sideOverlays)
        {
            EnumMap<EnumFacing, ImmutableList<BakedQuad>> faceQuads = new EnumMap<>(EnumFacing.class);
            for (EnumFacing side : EnumFacing.values())
            {
                faceQuads.put(side, ImmutableList.of());
            }

            if (statePresent)
            {
                // y levels
                float[] y = new float[4];
                boolean fullVolume = true;
                for (int i = 0; i < 4; i++)
                {
                    float value = cornerRound[i] / 864f;
                    if (value < 1f) fullVolume = false;
                    y[i] = gas ? 1f - value : value;
                }

                // flow
                boolean isFlowing = flowRound > -1000;

                float flow = isFlowing ? (float) Math.toRadians(flowRound) : 0f;
                TextureAtlasSprite topSprite = isFlowing ? flowing : still;
                float scale = isFlowing ? 4f : 8f;

                float c = MathHelper.cos(flow) * scale;
                float s = MathHelper.sin(flow) * scale;

                // top
                EnumFacing top = gas ? EnumFacing.DOWN : EnumFacing.UP;

                // base uv offset for flow direction
                VertexParameter uv = i -> c * (x[i] * 2 - 1) + s * (z[i] * 2 - 1);

                VertexParameter topX = i -> x[i];
                VertexParameter topY = i -> y[i];
                VertexParameter topZ = i -> z[i];
                VertexParameter topU = i -> 8 + uv.get(i);
                VertexParameter topV = i -> 8 + uv.get((i + 1) % 4);

                {
                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

                    builder.add(buildQuad(top, topSprite, gas, false, topX, topY, topZ, topU, topV));
                    if (!fullVolume) builder.add(buildQuad(top, topSprite, !gas, true, topX, topY, topZ, topU, topV));

                    faceQuads.put(top, builder.build());
                }

                // bottom
                EnumFacing bottom = top.getOpposite();
                faceQuads.put(bottom, ImmutableList.of(
                        buildQuad(bottom, still, gas, false,
                                i -> z[i],
                                i -> gas ? 1 : 0,
                                i -> x[i],
                                i -> z[i] * 16,
                                i -> x[i] * 16
                        )
                ));

                // sides
                for (int i = 0; i < 4; i++)
                {
                    EnumFacing side = EnumFacing.byHorizontalIndex((5 - i) % 4); // [W, S, E, N]
                    boolean useOverlay = overlay.isPresent() && sideOverlays[side.getHorizontalIndex()];
                    int si = i; // local var for lambda capture

                    VertexParameter sideX = j -> x[(si + x[j]) % 4];
                    VertexParameter sideY = j -> z[j] == 0 ? (gas ? 1 : 0) : y[(si + x[j]) % 4];
                    VertexParameter sideZ = j -> z[(si + x[j]) % 4];
                    VertexParameter sideU = j -> x[j] * 8;
                    VertexParameter sideV = j -> (gas ? sideY.get(j) : 1 - sideY.get(j)) * 8;

                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

                    if (!useOverlay) builder.add(buildQuad(side, flowing, gas, true, sideX, sideY, sideZ, sideU, sideV));
                    builder.add(buildQuad(side, useOverlay ? overlay.get() : flowing, !gas, false, sideX, sideY, sideZ, sideU, sideV));

                    faceQuads.put(side, builder.build());
                }
            }
            else
            {
                // inventory
                faceQuads.put(EnumFacing.SOUTH, ImmutableList.of(
                        buildQuad(EnumFacing.UP, still, false, false,
                                i -> z[i],
                                i -> x[i],
                                i -> 0,
                                i -> z[i] * 16,
                                i -> x[i] * 16
                        )
                ));
            }

            return ImmutableMap.copyOf(faceQuads);
        }

        // maps vertex index to parameter value
        private interface VertexParameter
        {
            float get(int index);
        }

        private BakedQuad buildQuad(EnumFacing side, TextureAtlasSprite texture, boolean flip, boolean offset, VertexParameter x, VertexParameter y, VertexParameter z, VertexParameter u, VertexParameter v)
        {
            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
            builder.setQuadOrientation(side);
            builder.setTexture(texture);
            builder.setQuadTint(0);

            for (int i = 0; i < 4; i++)
            {
                int vertex = flip ? 3 - i : i;
                putVertex(
                    builder, side, offset,
                    x.get(vertex), y.get(vertex), z.get(vertex),
                    texture.getInterpolatedU(u.get(vertex)),
                    texture.getInterpolatedV(v.get(vertex))
                );
            }

            return builder.build();
        }

        private void putVertex(UnpackedBakedQuad.Builder builder, EnumFacing side, boolean offset, float x, float y, float z, float u, float v)
        {
            for(int e = 0; e < format.getElementCount(); e++)
            {
                switch(format.getElement(e).getUsage())
                {
                case POSITION:
                    float dx = offset ? side.getDirectionVec().getX() * eps : 0f;
                    float dy = offset ? side.getDirectionVec().getY() * eps : 0f;
                    float dz = offset ? side.getDirectionVec().getZ() * eps : 0f;
                    float[] data = { x - dx, y - dy, z - dz, 1f };
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
            return side == null ? ImmutableList.of() : faceQuads.get(side);
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
            LOGGER.fatal("fluid '{}' not found", fluid);
            return WATER;
        }
        return new ModelFluid(FluidRegistry.getFluid(fluid));
    }
}
