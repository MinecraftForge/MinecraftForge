/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A dynamic fluid container model, capable of re-texturing itself at runtime to match the contained fluid.
 * <p>
 * Composed of a base layer, a fluid layer (applied with a mask) and a cover layer (optionally applied with a mask).
 * The entire model may optionally be flipped if the fluid is gaseous, and the fluid layer may glow if light-emitting.
 * <p>
 * Fluid tinting requires registering a separate {@link ItemColor}. An implementation is provided in {@link Colors}.
 *
 * @see Colors
 */
public class DynamicFluidContainerModel implements UnbakedGeometry {
    // Depth offsets to prevent Z-fighting
    private static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());
    private static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.004f), new Quaternionf());

    private final Fluid fluid;
    private final boolean flipGas;
    private final boolean coverIsMask;
    private final boolean applyFluidLuminosity;

    private DynamicFluidContainerModel(Fluid fluid, boolean flipGas, boolean coverIsMask, boolean applyFluidLuminosity) {
        this.fluid = fluid;
        this.flipGas = flipGas;
        this.coverIsMask = coverIsMask;
        this.applyFluidLuminosity = applyFluidLuminosity;
    }

    public static RenderTypeGroup getLayerRenderTypes(boolean unlit) {
        return new RenderTypeGroup(ChunkSectionLayer.TRANSLUCENT, unlit ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
    }

    @SuppressWarnings("deprecation")
    private static Material getMaterial(ResourceLocation texture) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, texture);
    }

    /**
     * Returns a new ModelDynBucket representing the given fluid, but with the same
     * other properties (flipGas, tint, coverIsMask).
     */
    public DynamicFluidContainerModel withFluid(Fluid newFluid) {
        return new DynamicFluidContainerModel(newFluid, flipGas, coverIsMask, applyFluidLuminosity);
    }

    @Override
    public QuadCollection bake(TextureSlots slots, ModelBaker baker, ModelState state, ModelDebugName name) {
        return bake(slots, baker, state, name, StandaloneGeometryBakingContext.INSTANCE);
    }

    @Override
    public QuadCollection bake(TextureSlots textures, ModelBaker baker, ModelState state, ModelDebugName name, IGeometryBakingContext context) {
        Material fluidMaskLocation = textures.getMaterial("fluid");
        Material stillMaterial = null;

        if (fluid != Fluids.EMPTY) {
            var stillTexture = IClientFluidTypeExtensions.of(fluid).getStillTexture();
            stillMaterial = getMaterial(stillTexture);
        }

        var sprites = baker.sprites();
        var baseSprite = sprites.resolveSlot(textures, "base", name);
        var fluidSprite = stillMaterial == null ? null : sprites.get(stillMaterial, name);
        var coverSprite = sprites.resolveSlot(textures, "cover", name);
        /*
         var particleSprite = sprites.resolveSlot(textures, "particle", name);

        if (particleSprite == null) particleSprite = fluidSprite;
        if (particleSprite == null) particleSprite = baseSprite;
        if (particleSprite == null && !coverIsMask) particleSprite = coverSprite;
         */

        var transformation = state.transformation();

        // TODO: [Forge][Rendering] See if we can get rid of SimpleModelState and wrap transforms completely
        // If the fluid is lighter than air, rotate 180deg to turn it upside down
        if (flipGas && fluid != Fluids.EMPTY && fluid.getFluidType().isLighterThanAir())
            transformation = transformation.compose(new Transformation(null, new Quaternionf(0, 0, 1, 0), null, null));

        var buf = new QuadCollection.Builder();

        if (baseSprite != null) {
            // Base texture
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite.contents());
            UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, state, buf);
        }

        if (fluidMaskLocation != null && fluidSprite != null) {
            TextureAtlasSprite templateSprite = sprites.get(fluidMaskLocation, name);
            if (templateSprite != null) {
                // Fluid layer
                var transformedState = new SimpleModelState(transformation.compose(FLUID_TRANSFORM));
                var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite.contents()); // Use template as mask

                var emissive = applyFluidLuminosity && fluid.getFluidType().getLightLevel() > 0;
                var transformer = emissive ? QuadTransformers.settingMaxEmissivity() : QuadTransformers.empty();

                UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState, transformer, buf); // Bake with fluid texture
            }
        }

        if (coverSprite != null) {
            var sprite = coverIsMask ? baseSprite : coverSprite;
            if (sprite != null) {
                // Cover/overlay
                var transformedState = new SimpleModelState(transformation.compose(COVER_TRANSFORM));
                var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite.contents()); // Use cover as mask
                UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, transformedState, buf); // Bake with selected texture
            }
        }


        return buf.build();
    }

    public static final class Loader implements IGeometryLoader {
        public static final Loader INSTANCE = new Loader();

        private Loader() { }

        @Override
        public UnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            var fluidName = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "fluid"));

            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);

            boolean flip = GsonHelper.getAsBoolean(jsonObject, "flip_gas", false);
            boolean coverIsMask = GsonHelper.getAsBoolean(jsonObject, "cover_is_mask", true);
            boolean applyFluidLuminosity = GsonHelper.getAsBoolean(jsonObject, "apply_fluid_luminosity", true);

            // create new model with correct liquid
            return new DynamicFluidContainerModel(fluid, flip, coverIsMask, applyFluidLuminosity);
        }
    }
}
