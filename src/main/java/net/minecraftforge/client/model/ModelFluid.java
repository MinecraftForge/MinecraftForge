package net.minecraftforge.client.model;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;

import org.lwjgl.BufferUtils;

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

            ByteBuffer buf = BufferUtils.createByteBuffer(4 * format.getNextOffset());
            int[] data;

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
                buf.clear();
                for(int i = gas ? 3 : 0; i != (gas ? -1 : 4); i+= (gas ? -1 : 1))
                {
                    putVertex(
                        buf, side,
                        x[i], y[i], z[i],
                        topSprite.getInterpolatedU(8 + c * (x[i] * 2 - 1) + s * (z[i] * 2 - 1)),
                        topSprite.getInterpolatedV(8 + c * (x[(i + 1) % 4] * 2 - 1) + s * (z[(i + 1) % 4] * 2 - 1)));
                }
                buf.flip();
                data = new int[4 * format.getNextOffset() / 4];
                buf.asIntBuffer().get(data);
                faceQuads.put(side, ImmutableList.<BakedQuad>of(new IColoredBakedQuad.ColoredBakedQuad(data, -1, side)));

                // bottom

                side = side.getOpposite();
                buf.clear();
                for(int i = gas ? 3 : 0; i != (gas ? -1 : 4); i+= (gas ? -1 : 1))
                {
                    putVertex(
                        buf, side,
                        z[i], gas ? 1 : 0, x[i],
                        still.getInterpolatedU(z[i] * 16),
                        still.getInterpolatedV(x[i] * 16));
                }
                buf.flip();
                data = new int[4 * format.getNextOffset() / 4];
                buf.asIntBuffer().get(data);
                faceQuads.put(side, ImmutableList.<BakedQuad>of(new IColoredBakedQuad.ColoredBakedQuad(data, -1, side)));

                // sides

                for(int i = 0; i < 4; i++)
                {
                    side = EnumFacing.getHorizontal((5 - i) % 4);
                    BakedQuad q[] = new BakedQuad[2];

                    for(int k = 0; k < 2; k++)
                    {
                        buf.clear();
                        for(int j = 0; j < 4; j++)
                        {
                            int l = (k * 3) + (1 - 2 * k) * j;
                            float yl = z[l] * y[(i + x[l]) % 4];
                            if(gas && z[l] == 0) yl = 1;
                            putVertex(
                                buf, side,
                                x[(i + x[l]) % 4], yl, z[(i + x[l]) % 4],
                                flowing.getInterpolatedU(x[l] * 8),
                                flowing.getInterpolatedV((gas ? yl : 1 - yl) * 8));
                        }
                        buf.flip();
                        data = new int[4 * format.getNextOffset() / 4];
                        buf.asIntBuffer().get(data);
                        q[k] = new IColoredBakedQuad.ColoredBakedQuad(data, -1, side);
                    }
                    faceQuads.put(side, ImmutableList.of(q[0], q[1]));
                }
            }
            else
            {
                // 1 quad for inventory

                buf.clear();
                for(int i = 0; i < 4; i++)
                {
                    putVertex(
                        buf, EnumFacing.UP,
                        z[i], x[i], 0,
                        still.getInterpolatedU(x[i] * 16),
                        still.getInterpolatedV(z[i] * 16));
                }
                buf.flip();
                data = new int[4 * format.getNextOffset() / 4];
                buf.asIntBuffer().get(data);
                faceQuads.put(EnumFacing.SOUTH, ImmutableList.<BakedQuad>of(new IColoredBakedQuad.ColoredBakedQuad(data, -1, EnumFacing.UP)));
            }
        }

        private void put(ByteBuffer buf, VertexFormatElement e, Float... fs)
        {
            Attributes.put(buf, e, true, 0f, fs);
        }

        private float diffuse(EnumFacing side)
        {
            switch(side)
            {
            case DOWN:
                return .5f;
            case UP:
                return 1f;
            case NORTH:
            case SOUTH:
                return .8f;
            default:
                return .6f;
            }
        }

        private void putVertex(ByteBuffer buf, EnumFacing side, float x, float y, float z, float u, float v)
        {
            for(VertexFormatElement e : (List<VertexFormatElement>)format.getElements())
            {
                // TODO transformation
                switch(e.getUsage())
                {
                case POSITION:
                    put(buf, e, x - side.getDirectionVec().getX() * eps, y, z - side.getDirectionVec().getZ() * eps, 1f);
                    break;
                case COLOR:
                    // temporarily add diffuse lighting
                    float d = diffuse(side);
                    put(buf, e,
                        d * ((color >> 16) & 0xFF) / 255f,
                        d * ((color >> 8) & 0xFF) / 255f,
                        d * (color & 0xFF) / 255f,
                        ((color >> 24) & 0xFF) / 255f);
                    break;
                case UV:
                    put(buf, e, u, v, 0f, 1f);
                    break;
                case NORMAL:
                    put(buf, e, (float)side.getFrontOffsetX(), (float)side.getFrontOffsetX(), (float)side.getFrontOffsetX(), 0f);
                    break;
                default:
                    put(buf, e);
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
