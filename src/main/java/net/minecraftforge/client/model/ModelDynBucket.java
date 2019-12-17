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
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.*;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.fluids.FluidUtil;

import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModelDynBucket implements IModelGeometry<ModelDynBucket>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(ForgeVersion.MOD_ID, "dynbucket"), "inventory");

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_COVER = 7.496f / 16f;
    private static final float SOUTH_Z_COVER = 8.504f / 16f;
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    @Nonnull
    private final Fluid fluid;

    private final boolean flipGas;
    private final boolean tint;
    private final boolean coverIsMask;

    public ModelDynBucket(Fluid fluid, boolean flipGas, boolean tint, boolean coverIsMask)
    {
        this.fluid = fluid;
        this.flipGas = flipGas;
        this.tint = tint;
        this.coverIsMask = coverIsMask;
    }

    /**
     * Returns a new ModelDynBucket representing the given fluid, but with the same
     * other properties (flipGas, tint, coverIsMask).
     */
    public ModelDynBucket withFluid(Fluid newFluid)
    {
        return new ModelDynBucket(newFluid, flipGas, tint, coverIsMask);
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

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform sprite, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        Material particleLocation = owner.resolveTexture("particle");
        if (MissingTextureSprite.getLocation().toString().equals(particleLocation))
        {
            particleLocation = null;
        }

        Material baseLocation = owner.resolveTexture("base");
        if (MissingTextureSprite.getLocation().toString().equals(baseLocation))
        {
            baseLocation = null;
        }

        Material fluidMaskLocation = owner.resolveTexture("fluid");
        if (MissingTextureSprite.getLocation().toString().equals(fluidMaskLocation))
        {
            fluidMaskLocation = null;
        }

        Material coverLocation = owner.resolveTexture("cover");
        if (!MissingTextureSprite.getLocation().toString().equals(coverLocation))
        {
            // cover (the actual item around the other two)
            coverLocation = null;
        }

        IModelTransform transformsFromModel = owner.getCombinedTransform();

        IModelTransform state = sprite;
        ImmutableMap<TransformType, TransformationMatrix> transformMap = transformsFromModel != null ?
                        PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(transformsFromModel, state)) :
                        PerspectiveMapWrapper.getTransforms(state);

        TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;

        // if the fluid is lighter than air, will manipulate the initial state to be rotated 180deg to turn it upside down
        if (flipGas && fluid != Fluids.EMPTY && fluid.getAttributes().isLighterThanAir())
        {
            sprite = new ModelTransformComposition(state, new SimpleModelTransform(new TransformationMatrix(null, new Quaternion(0, 0, 1, 0), null, null).blockCenterToCorner()));
            state = sprite;
        }

        TransformationMatrix transform = state.func_225615_b_();

        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? spriteGetter.apply(ForgeHooksClient.getBlockMaterial(fluid.getAttributes().getStillTexture())) : null;

        if (particleSprite == null) particleSprite = fluidSprite;

        Random random = new Random();
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (baseLocation != null)
        {
            // build base (insidest)
            IBakedModel model = new ItemLayerModel(ImmutableList.of(baseLocation)).bake(owner, bakery, spriteGetter, sprite, ItemOverrideList.EMPTY, modelLocation);
            random.setSeed(42);
            builder.addAll(model.getQuads(null, null, random));
        }

        if (fluidMaskLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
            if (templateSprite != null)
            {
                // build liquid layer (inside)
                builder.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.BLOCK, transform, templateSprite, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, tint ? fluid.getAttributes().getColor() : 0xFFFFFFFF, 1));
                builder.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.BLOCK, transform, templateSprite, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, tint ? fluid.getAttributes().getColor() : 0xFFFFFFFF, 1));
            }
        }

        if (coverLocation != null && (!coverIsMask || baseLocation != null))
        {
            // cover (the actual item around the other two)
            TextureAtlasSprite coverSprite = spriteGetter.apply(coverLocation);
            if (coverSprite != null)
            {
                if (coverIsMask)
                {
                    TextureAtlasSprite baseSprite = spriteGetter.apply(baseLocation);
                    builder.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.BLOCK, transform, coverSprite, baseSprite, NORTH_Z_COVER, Direction.NORTH, 0xFFFFFFFF, 1));
                    builder.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.BLOCK, transform, coverSprite, baseSprite, SOUTH_Z_COVER, Direction.SOUTH, 0xFFFFFFFF, 1));
                }
                else
                {
                    builder.add(ItemTextureQuadConverter.genQuad(DefaultVertexFormats.BLOCK, transform, 0, 0, 16, 16, NORTH_Z_COVER, coverSprite, Direction.NORTH, 0xFFFFFFFF, 2));
                    builder.add(ItemTextureQuadConverter.genQuad(DefaultVertexFormats.BLOCK, transform, 0, 0, 16, 16, SOUTH_Z_COVER, coverSprite, Direction.SOUTH, 0xFFFFFFFF, 2));
                    if (particleSprite == null)
                    {
                        particleSprite = coverSprite;
                    }
                }
            }
        }

        return new BakedDynBucket(bakery, owner, this, builder.build(), particleSprite, DefaultVertexFormats.BLOCK, Maps.immutableEnumMap(transformMap), Maps.newHashMap(), transform.isIdentity());
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> texs = Sets.newHashSet();

        texs.add(owner.resolveTexture("particle"));
        texs.add(owner.resolveTexture("base"));
        texs.add(owner.resolveTexture("fluid"));
        texs.add(owner.resolveTexture("cover"));

        return texs;
    }

    public enum LoaderDynBucket2 implements IModelLoader<ModelDynBucket>
    {
        INSTANCE;

        @Override
        public IResourceType getResourceType()
        {
            return VanillaResourceType.MODELS;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // no need to clear cache since we create a new model instance
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
        {
            // no need to clear cache since we create a new model instance
        }

        @Override
        public ModelDynBucket read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            if (!modelContents.has("fluid"))
                throw new RuntimeException("Bucket model requires 'fluid' value.");

            ResourceLocation fluidName = new ResourceLocation(modelContents.get("fluid").getAsString());

            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);

            boolean flip = false;
            if (modelContents.has("flipGas"))
            {
                flip = modelContents.get("flipGas").getAsBoolean();
            }

            boolean tint = true;
            if (modelContents.has("applyTint"))
            {
                tint = modelContents.get("applyTint").getAsBoolean();
            }

            boolean coverIsMask = true;
            if (modelContents.has("coverIsMask"))
            {
                coverIsMask = modelContents.get("coverIsMask").getAsBoolean();
            }

            // create new model with correct liquid
            return new ModelDynBucket(fluid, flip, tint, coverIsMask);
        }
    }

        /* TODO Custom TAS
    private static final class BucketBaseSprite extends TextureAtlasSprite
    {
        private final ResourceLocation bucket = new ResourceLocation("item/bucket");
        private final ImmutableList<ResourceLocation> dependencies = ImmutableList.of(bucket);

        private BucketBaseSprite(ResourceLocation res)
        {
            super(res, getSizeInfo(getResource(new ResourceLocation("textures/item/bucket.png"))), getResource(new ResourceLocation("textures/item/bucket.png")).getMetadata(AnimationMetadataSection.SERIALIZER));
        }

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
        }
    }*/

    /**
     * Creates a bucket cover sprite from the vanilla resource.
     */
        /* TODO Custom TAS
    private static final class BucketCoverSprite extends TextureAtlasSprite
    {
        private final ResourceLocation bucket = new ResourceLocation("item/bucket");
        private final ResourceLocation bucketCoverMask = new ResourceLocation(ForgeVersion.MOD_ID, "item/vanilla_bucket_cover_mask");
        private final ImmutableList<ResourceLocation> dependencies = ImmutableList.of(bucket, bucketCoverMask);

        private BucketCoverSprite(ResourceLocation res)
        {
            super(res, getSizeInfo(getResource(new ResourceLocation("textures/item/bucket.png"))), getResource(new ResourceLocation("textures/item/bucket.png")).getMetadata(AnimationMetadataSection.SERIALIZER));
        }

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
        }
    }*/

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
                        String name = fluid.getRegistryName().toString();

                        if (!model.cache.containsKey(name))
                        {
                            ModelDynBucket parent = model.parent.withFluid(fluid);
                            IBakedModel bakedModel = parent.bake(model.owner, bakery, ModelLoader.defaultTextureGetter(), new SimpleModelTransform(model.transforms), model.getOverrides(), new ResourceLocation("forge:bucket_override"));
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
        private final IModelConfiguration owner;
        private final ModelDynBucket parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final VertexFormat format;

        BakedDynBucket(ModelBakery bakery,
                       IModelConfiguration owner, ModelDynBucket parent,
                       ImmutableList<BakedQuad> quads,
                       TextureAtlasSprite particle,
                       VertexFormat format,
                       ImmutableMap<TransformType, TransformationMatrix> transforms,
                       Map<String, IBakedModel> cache,
                       boolean untransformed)
        {
            super(quads, particle, transforms, new BakedDynBucketOverrideHandler(bakery), untransformed);
            this.owner = owner;
            this.format = format;
            this.parent = parent;
            this.cache = cache;
        }
    }

}
