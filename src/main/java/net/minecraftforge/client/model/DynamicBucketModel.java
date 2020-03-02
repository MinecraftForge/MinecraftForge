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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class DynamicBucketModel implements IModelGeometry<DynamicBucketModel>
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

    public DynamicBucketModel(Fluid fluid, boolean flipGas, boolean tint, boolean coverIsMask)
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
    public DynamicBucketModel withFluid(Fluid newFluid)
    {
        return new DynamicBucketModel(newFluid, flipGas, tint, coverIsMask);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        Material particleLocation = owner.resolveTexture("particle");
        if (MissingTextureSprite.getLocation().equals(particleLocation.getTextureLocation()))
        {
            particleLocation = null;
        }

        Material baseLocation = owner.resolveTexture("base");
        if (MissingTextureSprite.getLocation().equals(baseLocation.getTextureLocation()))
        {
            baseLocation = null;
        }

        Material fluidMaskLocation = owner.resolveTexture("fluid");
        if (MissingTextureSprite.getLocation().equals(fluidMaskLocation.getTextureLocation()))
        {
            fluidMaskLocation = null;
        }

        Material coverLocation = owner.resolveTexture("cover");
        if (!MissingTextureSprite.getLocation().equals(coverLocation.getTextureLocation()))
        {
            // cover (the actual item around the other two)
            coverLocation = null;
        }

        IModelTransform transformsFromModel = owner.getCombinedTransform();

        ImmutableMap<TransformType, TransformationMatrix> transformMap = transformsFromModel != null ?
                        PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(transformsFromModel, modelTransform)) :
                        PerspectiveMapWrapper.getTransforms(modelTransform);

        TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;

        // if the fluid is lighter than air, will manipulate the initial state to be rotated 180deg to turn it upside down
        if (flipGas && fluid != Fluids.EMPTY && fluid.getAttributes().isLighterThanAir())
        {
            modelTransform = new ModelTransformComposition(modelTransform, new SimpleModelTransform(new TransformationMatrix(null, new Quaternion(0, 0, 1, 0), null, null)));
        }

        TransformationMatrix transform = modelTransform.getRotation();

        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? spriteGetter.apply(ForgeHooksClient.getBlockMaterial(fluid.getAttributes().getStillTexture())) : null;

        if (particleSprite == null) particleSprite = fluidSprite;

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (baseLocation != null)
        {
            // build base (insidest)
            builder.addAll(ItemLayerModel.getQuadsForSprites(ImmutableList.of(baseLocation), transform, spriteGetter));
        }

        if (fluidMaskLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
            if (templateSprite != null)
            {
                // build liquid layer (inside)
                builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, tint ? fluid.getAttributes().getColor() : 0xFFFFFFFF, 1));
                builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, tint ? fluid.getAttributes().getColor() : 0xFFFFFFFF, 1));
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
                    builder.addAll(ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, NORTH_Z_COVER, Direction.NORTH, 0xFFFFFFFF, 1));
                    builder.addAll(ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, SOUTH_Z_COVER, Direction.SOUTH, 0xFFFFFFFF, 1));
                }
                else
                {
                    builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, NORTH_Z_COVER, coverSprite, Direction.NORTH, 0xFFFFFFFF, 2));
                    builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, SOUTH_Z_COVER, coverSprite, Direction.SOUTH, 0xFFFFFFFF, 2));
                    if (particleSprite == null)
                    {
                        particleSprite = coverSprite;
                    }
                }
            }
        }

        return new BakedModel(bakery, owner, this, builder.build(), particleSprite, Maps.immutableEnumMap(transformMap), Maps.newHashMap(), transform.isIdentity(), modelTransform, owner.isSideLit());
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

    public enum Loader implements IModelLoader<DynamicBucketModel>
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
        public DynamicBucketModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
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
            return new DynamicBucketModel(fluid, flip, tint, coverIsMask);
        }
    }

    private static final class ContainedFluidOverrideHandler extends ItemOverrideList
    {
        private final ModelBakery bakery;
        
        private ContainedFluidOverrideHandler(ModelBakery bakery)
        {
            this.bakery = bakery;
        }

        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity)
        {
            return FluidUtil.getFluidContained(stack)
                    .map(fluidStack -> {
                        BakedModel model = (BakedModel)originalModel;

                        Fluid fluid = fluidStack.getFluid();
                        String name = fluid.getRegistryName().toString();

                        if (!model.cache.containsKey(name))
                        {
                            DynamicBucketModel parent = model.parent.withFluid(fluid);
                            IBakedModel bakedModel = parent.bake(model.owner, bakery, ModelLoader.defaultTextureGetter(), model.originalTransform, model.getOverrides(), new ResourceLocation("forge:bucket_override"));
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
    private static final class BakedModel extends BakedItemModel
    {
        private final IModelConfiguration owner;
        private final DynamicBucketModel parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final IModelTransform originalTransform;
        private final boolean isSideLit;

        BakedModel(ModelBakery bakery,
                   IModelConfiguration owner, DynamicBucketModel parent,
                   ImmutableList<BakedQuad> quads,
                   TextureAtlasSprite particle,
                   ImmutableMap<TransformType, TransformationMatrix> transforms,
                   Map<String, IBakedModel> cache,
                   boolean untransformed,
                   IModelTransform originalTransform, boolean isSideLit)
        {
            super(quads, particle, transforms, new ContainedFluidOverrideHandler(bakery), untransformed, isSideLit);
            this.owner = owner;
            this.parent = parent;
            this.cache = cache;
            this.originalTransform = originalTransform;
            this.isSideLit = isSideLit;
        }
    }

}
