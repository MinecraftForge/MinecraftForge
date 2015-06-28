package net.minecraftforge.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ModelFluid implements IModelCustomData
{
    public static final ModelFluid waterModel = new ModelFluid(FluidRegistry.WATER);
    public static final ModelFluid lavaModel = new ModelFluid(FluidRegistry.LAVA);
    private final Fluid fluid;

    public ModelFluid(Fluid fluid)
    {
        this.fluid = fluid;
    }

    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptySet();
    }

    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableSet.of(fluid.getStill(), fluid.getFlowing());
    }

    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return new BakedFluid(state.apply(this), format, fluid.getColor(), bakedTextureGetter.apply(fluid.getStill()), bakedTextureGetter.apply(fluid.getFlowing()), fluid.isGaseous());
    }

    public IModelState getDefaultState()
    {
        return ModelRotation.X0_Y0;
    }

    public static enum FluidLoader implements ICustomModelLoader
    {
        instance;

        public void onResourceManagerReload(IResourceManager resourceManager) {}

        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals("forge") && (
                modelLocation.getResourcePath().equals("fluid") ||
                modelLocation.getResourcePath().equals("models/block/fluid") ||
                modelLocation.getResourcePath().equals("models/item/fluid"));
        }

        public IModel loadModel(ResourceLocation modelLocation)
        {
            return waterModel;
        }
    }

    public static class BakedFluid implements IFlexibleBakedModel, ISmartBlockModel
    {
        private static final int x[] = { 0, 0, 1, 1 };
        private static final int z[] = { 0, 1, 1, 0 };
        private static final float eps = 1e-3f;

        private final TRSRTransformation transformation;
        private final VertexFormat format;
        private final int color;
        private final TextureAtlasSprite still, flowing;
        private final boolean gas;
        private final Optional<IExtendedBlockState> state;
        private final EnumMap<EnumFacing, List<BakedQuad>> faceQuads;

        public BakedFluid(TRSRTransformation transformation, VertexFormat format, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, boolean gas)
        {
            this(transformation, format, color, still, flowing, gas, Optional.<IExtendedBlockState>absent());
        }

        public BakedFluid(TRSRTransformation transformation, VertexFormat format, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, boolean gas, Optional<IExtendedBlockState> stateOption)
        {
            this.transformation = transformation;
            this.format = format;
            this.color = color;
            this.still = still;
            this.flowing = flowing;
            this.gas = gas;
            this.state = stateOption;

            faceQuads = Maps.newEnumMap(EnumFacing.class);
            for(EnumFacing side : EnumFacing.values())
            {
                faceQuads.put(side, ImmutableList.<BakedQuad>of());
            }

            if(state.isPresent())
            {
                IExtendedBlockState state = this.state.get();
                float[] y = new float[4];
                for(int i = 0; i < 4; i++)
                {
                    if(gas)
                    {
                        y[i] = 1 - state.getValue(BlockFluidBase.LEVEL_CORNERS[i]);
                    }
                    else
                    {
                        y[i] = state.getValue(BlockFluidBase.LEVEL_CORNERS[i]);
                    }
                }

                float flow = state.getValue(BlockFluidBase.FLOW_DIRECTION);

                // top

                TextureAtlasSprite topSprite = flowing;
                float scale = 4;
                if(flow < -999F)
                {
                    flow = 0;
                    scale = 8;
                    topSprite = still;
                }

                float c = MathHelper.cos(flow) * scale;
                float s = MathHelper.sin(flow) * scale;

                EnumFacing side = gas ? EnumFacing.DOWN : EnumFacing.UP;
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(side);
                builder.setQuadColored();
                for(int i = gas ? 3 : 0; i != (gas ? -1 : 4); i+= (gas ? -1 : 1))
                {
                    putVertex(
                        builder, side,
                        x[i], y[i], z[i],
                        topSprite.getInterpolatedU(8 + c * (x[i] * 2 - 1) + s * (z[i] * 2 - 1)),
                        topSprite.getInterpolatedV(8 + c * (x[(i + 1) % 4] * 2 - 1) + s * (z[(i + 1) % 4] * 2 - 1)));
                }
                faceQuads.put(side, ImmutableList.<BakedQuad>of(builder.build()));

                // bottom

                side = side.getOpposite();
                builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(side);
                builder.setQuadColored();
                for(int i = gas ? 3 : 0; i != (gas ? -1 : 4); i+= (gas ? -1 : 1))
                {
                    putVertex(
                        builder, side,
                        z[i], gas ? 1 : 0, x[i],
                        still.getInterpolatedU(z[i] * 16),
                        still.getInterpolatedV(x[i] * 16));
                }
                faceQuads.put(side, ImmutableList.<BakedQuad>of(builder.build()));

                // sides

                for(int i = 0; i < 4; i++)
                {
                    side = EnumFacing.getHorizontal((5 - i) % 4);
                    BakedQuad q[] = new BakedQuad[2];

                    for(int k = 0; k < 2; k++)
                    {
                        builder = new UnpackedBakedQuad.Builder(format);
                        builder.setQuadOrientation(side);
                        builder.setQuadColored();
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
                builder.setQuadColored();
                for(int i = 0; i < 4; i++)
                {
                    putVertex(
                        builder, EnumFacing.UP,
                        z[i], x[i], 0,
                        still.getInterpolatedU(x[i] * 16),
                        still.getInterpolatedV(z[i] * 16));
                }
                faceQuads.put(EnumFacing.SOUTH, ImmutableList.<BakedQuad>of(builder.build()));
            }
        }

        private void putVertex(UnpackedBakedQuad.Builder builder, EnumFacing side, float x, float y, float z, float u, float v)
        {
            for(int e = 0; e < format.getElementCount(); e++)
            {
                // TODO transformation
                switch(format.getElement(e).getUsage())
                {
                case POSITION:
                    builder.put(e, x - side.getDirectionVec().getX() * eps, y, z - side.getDirectionVec().getZ() * eps, 1f);
                    break;
                case COLOR:
                    float d = LightUtil.diffuseLight(side);
                    builder.put(e,
                        d * ((color >> 16) & 0xFF) / 255f,
                        d * ((color >> 8) & 0xFF) / 255f,
                        d * (color & 0xFF) / 255f,
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

        public boolean isAmbientOcclusion()
        {
            return false; // FIXME
        }

        public boolean isGui3d()
        {
            return false;
        }

        public boolean isBuiltInRenderer()
        {
            return false;
        }

        public TextureAtlasSprite getTexture()
        {
            return still;
        }

        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return faceQuads.get(side);
        }

        public List<BakedQuad> getGeneralQuads()
        {
            return ImmutableList.of();
        }

        public VertexFormat getFormat()
        {
            return format;
        }

        public IBakedModel handleBlockState(IBlockState state)
        {
            return new BakedFluid(transformation, format, color, still, flowing, gas, Optional.of((IExtendedBlockState)state));
        }
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
        if(!customData.containsKey("fluid")) return this;

        String fluidStr = customData.get("fluid");
        JsonElement e = new JsonParser().parse(fluidStr);
        String fluid = e.getAsString();
        if(!FluidRegistry.isFluidRegistered(fluid))
        {
            FMLLog.severe("fluid '%s' not found", fluid);
            return waterModel;
        }
        return new ModelFluid(FluidRegistry.getFluid(fluid));
    }
}
