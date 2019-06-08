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

package net.minecraftforge.client.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.Quat4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;

import java.util.function.Function;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModelDynBucket implements IUnbakedModel
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(ForgeVersion.MOD_ID, "dynbucket"), "inventory");

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_COVER = 7.496f / 16f;
    private static final float SOUTH_Z_COVER = 8.504f / 16f;
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final IUnbakedModel MODEL = new ModelDynBucket();

    @Nullable
    private final ResourceLocation baseLocation;
    @Nullable
    private final ResourceLocation liquidLocation;
    @Nullable
    private final ResourceLocation coverLocation;
    @Nullable
    private final Fluid fluid;

    private final boolean flipGas;
    private final boolean tint;

    public ModelDynBucket()
    {
        this(null, null, null, null, false, true);
    }

    public ModelDynBucket(@Nullable ResourceLocation baseLocation, @Nullable ResourceLocation liquidLocation, @Nullable ResourceLocation coverLocation, @Nullable Fluid fluid, boolean flipGas, boolean tint)
    {
        this.baseLocation = baseLocation;
        this.liquidLocation = liquidLocation;
        this.coverLocation = coverLocation;
        this.fluid = fluid;
        this.flipGas = flipGas;
        this.tint = tint;
    }
    
    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        if (baseLocation != null)
            builder.add(baseLocation);
        if (liquidLocation != null)
            builder.add(liquidLocation);
        if (coverLocation != null)
            builder.add(coverLocation);
        if (fluid != null)
            builder.add(fluid.getStill());

        return builder.build();
    }
    
    @Override
    public Collection<ResourceLocation> getDependencies() 
    {
    	return Collections.emptyList();
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
    {
        IModelState state = sprite.getState();
        ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

        // if the fluid is lighter than air, will manipulate the initial state to be rotated 180° to turn it upside down
        if (flipGas && fluid != null && fluid.isLighterThanAir())
        {
            sprite = new ModelStateComposition(state, TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, new Quat4f(0, 0, 1, 0), null, null)));
            state = sprite.getState();
        }

        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite fluidSprite = null;
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if(fluid != null) {
            fluidSprite = spriteGetter.apply(fluid.getStill());
        }

        Random random = new Random();
        if (baseLocation != null)
        {
            // build base (insidest)
            IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(bakery, spriteGetter, sprite, format);
            random.setSeed(42);
            builder.addAll(model.getQuads(null, null, random));
            particleSprite = model.getParticleTexture();
        }
        if (liquidLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite liquid = spriteGetter.apply(liquidLocation);
            // build liquid layer (inside)
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, tint ? fluid.getColor() : 0xFFFFFFFF, 1));
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, tint ? fluid.getColor() : 0xFFFFFFFF, 1));
            particleSprite = fluidSprite;
        }
        if (coverLocation != null)
        {
            // cover (the actual item around the other two)
            TextureAtlasSprite cover = spriteGetter.apply(coverLocation);
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_COVER, cover, Direction.NORTH, 0xFFFFFFFF, 2));
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_COVER, cover, Direction.SOUTH, 0xFFFFFFFF, 2));
            if (particleSprite == null)
            {
                particleSprite = cover;
            }
        }

        return new BakedDynBucket(bakery, this, builder.build(), particleSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap(), transform.isIdentity());
    }

    /**
     * Sets the fluid in the model.
     * "fluid" - Name of the fluid in the fluid registry.
     * "flipGas" - If "true" the model will be flipped upside down if the fluid is lighter than air. If "false" it won't.
     * "applyTint" - If "true" the model will tint the fluid quads according to the fluid's base color.
     * <p/>
     * If the fluid can't be found, water is used.
     */
    @Override
    public ModelDynBucket process(ImmutableMap<String, String> customData)
    {
        String fluidName = customData.get("fluid");
        Fluid fluid = null; // TODO fluids FluidRegistry.getFluid(fluidName);

        if (fluid == null) fluid = this.fluid;

        boolean flip = flipGas;
        if (customData.containsKey("flipGas"))
        {
            String flipStr = customData.get("flipGas");
            if (flipStr.equals("true")) flip = true;
            else if (flipStr.equals("false")) flip = false;
            else
                throw new IllegalArgumentException(String.format("DynBucket custom data \"flipGas\" must have value \'true\' or \'false\' (was \'%s\')", flipStr));
        }

        boolean tint = this.tint;
        if (customData.containsKey("applyTint"))
        {
            String string = customData.get("applyTint");
            switch (string)
            {
                case "true":  tint = true;  break;
                case "false": tint = false; break;
                default: throw new IllegalArgumentException(String.format("DynBucket custom data \"applyTint\" must have value \'true\' or \'false\' (was \'%s\')", string));
            }
        }

        // create new model with correct liquid
        return new ModelDynBucket(baseLocation, liquidLocation, coverLocation, fluid, flip, tint);
    }

    /**
     * Allows to use different textures for the model.
     * There are 3 layers:
     * base - The empty bucket/container
     * fluid - A texture representing the liquid portion. Non-transparent = liquid
     * cover - An overlay that's put over the liquid (optional)
     * <p/>
     * If no liquid is given a hardcoded variant for the bucket is used.
     */
    @Override
    public ModelDynBucket retexture(ImmutableMap<String, String> textures)
    {

        ResourceLocation base = baseLocation;
        ResourceLocation liquid = liquidLocation;
        ResourceLocation cover = coverLocation;

        if (textures.containsKey("base"))
            base = new ResourceLocation(textures.get("base"));
        if (textures.containsKey("fluid"))
            liquid = new ResourceLocation(textures.get("fluid"));
        if (textures.containsKey("cover"))
            cover = new ResourceLocation(textures.get("cover"));

        return new ModelDynBucket(base, liquid, cover, fluid, flipGas, tint);
    }
    
    @Nullable
    protected static IResource getResource(ResourceLocation resourceLocation)
    {
        try
        {
            return Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        }
        catch (IOException ignored)
        {
            return null;
        }
    }
    
    @Nullable
    protected static PngSizeInfo getSizeInfo(IResource resource)
    {
        try
        {
            return new PngSizeInfo(resource.toString(), resource.getInputStream());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public enum LoaderDynBucket implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getNamespace().equals(ForgeVersion.MOD_ID) && modelLocation.getPath().contains("forgebucket");
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation)
        {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // no need to clear cache since we create a new model instance
        }

        public void register(AtlasTexture map)
        {
            // only create these textures if they are not added by a resource pack

        	IResource res;
            if (getResource(new ResourceLocation(ForgeVersion.MOD_ID, "textures/items/bucket_cover.png")) == null)
            {
                ResourceLocation bucketCover = new ResourceLocation(ForgeVersion.MOD_ID, "items/bucket_cover");
                BucketCoverSprite bucketCoverSprite = new BucketCoverSprite(bucketCover);
//                map.setTextureEntry(bucketCoverSprite);
            }

            if (getResource(new ResourceLocation(ForgeVersion.MOD_ID, "textures/items/bucket_base.png")) == null)
            {
                ResourceLocation bucketBase = new ResourceLocation(ForgeVersion.MOD_ID, "items/bucket_base");
                BucketBaseSprite bucketBaseSprite = new BucketBaseSprite(bucketBase);
//                map.setTextureEntry(bucketBaseSprite);
            }
        }
    }

    private static final class BucketBaseSprite extends TextureAtlasSprite
    {
        private final ResourceLocation bucket = new ResourceLocation("item/bucket");
        private final ImmutableList<ResourceLocation> dependencies = ImmutableList.of(bucket);

        private BucketBaseSprite(ResourceLocation res)
        {
            super(res, getSizeInfo(getResource(new ResourceLocation("textures/item/bucket.png"))), getResource(new ResourceLocation("textures/item/bucket.png")).getMetadata(AnimationMetadataSection.SERIALIZER));
        }

        /* TODO Custom TAS
        @Override
        public boolean hasCustomLoader(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location)
        {
            return true;
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            return dependencies;
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location, @Nonnull Function<ResourceLocation, TextureAtlasSprite> textureGetter)
        {
            final TextureAtlasSprite sprite = textureGetter.apply(bucket);
            // TODO custom sprites are gonna be a PITA, these are final
            width = sprite.getIconWidth();
            height = sprite.getIconHeight();
            // TODO No easy way to dump pixels of one sprite into another without n^2 for loop, investigate patch?
            final int[][] pixels = sprite.getFrameTextureData(0);
            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }*/
    }

    /**
     * Creates a bucket cover sprite from the vanilla resource.
     */
    private static final class BucketCoverSprite extends TextureAtlasSprite
    {
        private final ResourceLocation bucket = new ResourceLocation("item/bucket");
        private final ResourceLocation bucketCoverMask = new ResourceLocation(ForgeVersion.MOD_ID, "item/vanilla_bucket_cover_mask");
        private final ImmutableList<ResourceLocation> dependencies = ImmutableList.of(bucket, bucketCoverMask);

        private BucketCoverSprite(ResourceLocation res)
        {
            super(res, getSizeInfo(getResource(new ResourceLocation("textures/item/bucket.png"))), getResource(new ResourceLocation("textures/item/bucket.png")).getMetadata(AnimationMetadataSection.SERIALIZER));
        }

        /* TODO Custom TAS
        @Override
        public boolean hasCustomLoader(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location)
        {
            return true;
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            return dependencies;
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location, @Nonnull Function<ResourceLocation, TextureAtlasSprite> textureGetter)
        {
            final TextureAtlasSprite sprite = textureGetter.apply(bucket);
            final TextureAtlasSprite alphaMask = textureGetter.apply(bucketCoverMask);
            width = sprite.getIconWidth();
            height = sprite.getIconHeight();
            final int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[width * height];

            try (
                 IResource empty = getResource(new ResourceLocation("textures/items/bucket_empty.png"));
                 IResource mask = getResource(new ResourceLocation(ForgeVersion.MOD_ID, "textures/items/vanilla_bucket_cover_mask.png"))
            ) {
                // use the alpha mask if it fits, otherwise leave the cover texture blank
                if (empty != null && mask != null && Objects.equals(empty.getPackName(), mask.getPackName()) &&
                        alphaMask.getIconWidth() == width && alphaMask.getIconHeight() == height)
                {
                    final int[][] oldPixels = sprite.getFrameTextureData(0);
                    final int[][] alphaPixels = alphaMask.getFrameTextureData(0);

                    for (int p = 0; p < width * height; p++)
                    {
                        final int alphaMultiplier = alphaPixels[0][p] >>> 24;
                        final int oldPixel = oldPixels[0][p];
                        final int oldPixelAlpha = oldPixel >>> 24;
                        final int newAlpha = oldPixelAlpha * alphaMultiplier / 0xFF;
                        pixels[0][p] = (oldPixel & 0xFFFFFF) + (newAlpha << 24);
                    }
                }
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to close resource", e);
            }

            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }*/
    }

    private static final class BakedDynBucketOverrideHandler extends ItemOverrideList
    {
        private final ModelBakery bakery;
        
        private BakedDynBucketOverrideHandler(ModelBakery bakery)
        {
            this.bakery = bakery;
        }

        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity)
        {
            return FluidUtil.getFluidContained(stack)
                    .map(fluidStack -> {
                        BakedDynBucket model = (BakedDynBucket)originalModel;

                        Fluid fluid = fluidStack.getFluid();
                        String name = fluid.getName();

                        if (!model.cache.containsKey(name))
                        {
                            IUnbakedModel parent = model.parent.process(ImmutableMap.of("fluid", name));
                            Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                            textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());

                            IBakedModel bakedModel = parent.bake(bakery, textureGetter, new SimpleModelState(model.transforms), model.format);
                            model.cache.put(name, bakedModel);
                            return bakedModel;
                        }

                        return model.cache.get(name);
                    })
                    // not a fluid item apparently
                    .orElse(originalModel); // empty bucket
        }
    }

    // the dynamic bucket is based on the empty bucket
    private static final class BakedDynBucket extends BakedItemModel
    {
        private final ModelDynBucket parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final VertexFormat format;

        BakedDynBucket(ModelBakery bakery,
                       ModelDynBucket parent,
                       ImmutableList<BakedQuad> quads,
                       TextureAtlasSprite particle,
                       VertexFormat format,
                       ImmutableMap<TransformType, TRSRTransformation> transforms,
                       Map<String, IBakedModel> cache,
                       boolean untransformed)
        {
            super(quads, particle, transforms, new BakedDynBucketOverrideHandler(bakery), untransformed);
            this.format = format;
            this.parent = parent;
            this.cache = cache;
        }
    }

}
