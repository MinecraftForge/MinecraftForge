/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftsingularity.client.singularityRenderTypes;
import net.minecraftsingularity.client.RenderTypeGroup;
import net.minecraftsingularity.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftsingularity.client.model.geometry.IGeometryBakingContext;
import net.minecraftsingularity.client.model.geometry.IGeometryLoader;
import net.minecraftsingularity.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftsingularity.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftsingularity.registries.singularityRegistries;

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
        return new RenderTypeGroup(ChunkSectionLayer.TRANSLUCENT, unlit ? singularityRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : singularityRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
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
        Material coverLocation = textures.getMaterial("cover");
        Material stillMaterial = null;

        if (fluid != Fluids.EMPTY) {
            var stillTexture = IClientFluidTypeExtensions.of(fluid).getStillTexture();
            if (stillTexture != null) {
                // Models can no longer have textures across atlases, so redirect to the item model of the same name.
                // Modders may need to make clones of their textures in the item atlas, or provide a custom atlas alias
                if (stillTexture.getPath().startsWith("block/"))
                    stillTexture = stillTexture.withPath(path -> "item/" + path.substring(6));
                stillMaterial = new Material(stillTexture);
            }
        }


        var materials = baker.materials();
        var baseMaterial = materials.resolveSlot(textures, "base", name);
        var fluidMaterial = stillMaterial == null ? null : materials.get(stillMaterial, name);
        var coverMaterial = coverLocation == null ? null : materials.get(coverLocation, name);

        /*
         var particleSprite = sprites.resolveSlot(textures, "particle", name);

        if (particleSprite == null) particleSprite = fluidSprite;
        if (particleSprite == null) particleSprite = baseSprite;
        if (particleSprite == null && !coverIsMask) particleSprite = coverSprite;
         */

        var transformation = state.transformation();

        // TODO: [singularity][Rendering] See if we can get rid of SimpleModelState and wrap transforms completely
        // If the fluid is lighter than air, rotate 180deg to turn it upside down
        if (flipGas && fluid != Fluids.EMPTY && fluid.getFluidType().isLighterThanAir()) {
            transformation = transformation.compose(new Transformation(null, new Quaternionf(0, 0, 1, 0), null, null));
            state = new SimpleModelState(transformation);
        }

        var buf = new QuadCollection.Builder();

        if (baseMaterial != null)
            ItemModelGenerator.bakeExtrudedSprite(buf, baker.interner(), state, info(baker, baseMaterial, 0));

        // Fluid layer
        if (fluidMaskLocation != null && fluidMaterial != null) {
            var templateMaterial = materials.get(fluidMaskLocation, name);
            if (templateMaterial != null) {
                var transformedState = new SimpleModelState(transformation.compose(FLUID_TRANSFORM));
                UnbakedGeometryHelper.bakeMaskedSprite(buf, baker.interner(), transformedState, info(baker, fluidMaterial, 1), info(baker, templateMaterial, 1));
            }
        }

        // Cover/overlay
        if (coverMaterial != null) {
            var sprite = coverIsMask ? baseMaterial : coverMaterial;
            if (sprite != null) {
                var transformedState = new SimpleModelState(transformation.compose(COVER_TRANSFORM));
                UnbakedGeometryHelper.bakeMaskedSprite(buf, baker.interner(), transformedState, info(baker, coverMaterial, 2), info(baker, sprite, 2));
            }
        }


        return buf.build();
    }

    private static MaterialInfo info(final ModelBaker baker, final Material.Baked material, final int tintIndex) {
        return baker.interner().materialInfo(
            MaterialInfo.of(material, material.sprite().transparency(), tintIndex, true, 0)
        );
    }

    public static final class Loader implements IGeometryLoader {
        public static final Loader INSTANCE = new Loader();

        private Loader() { }

        @Override
        public UnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            var fluidName = Identifier.parse(GsonHelper.getAsString(jsonObject, "fluid"));

            Fluid fluid = singularityRegistries.FLUIDS.getValue(fluidName);

            boolean flip = GsonHelper.getAsBoolean(jsonObject, "flip_gas", false);
            boolean coverIsMask = GsonHelper.getAsBoolean(jsonObject, "cover_is_mask", true);
            boolean applyFluidLuminosity = GsonHelper.getAsBoolean(jsonObject, "apply_fluid_luminosity", true);

            // create new model with correct liquid
            return new DynamicFluidContainerModel(fluid, flip, coverIsMask, applyFluidLuminosity);
        }
    }
}
