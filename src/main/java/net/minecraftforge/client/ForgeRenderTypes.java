/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL30C;

public enum ForgeRenderTypes {
    ITEM_LAYERED_SOLID(()-> getItemLayeredSolid(blockAtlas())),
    ITEM_LAYERED_CUTOUT(()-> getItemLayeredCutout(blockAtlas())),
    ITEM_LAYERED_CUTOUT_MIPPED(()-> getItemLayeredCutoutMipped(blockAtlas())),
    ITEM_LAYERED_TRANSLUCENT(()-> getItemLayeredTranslucent(blockAtlas())),
    ITEM_UNSORTED_TRANSLUCENT(()-> getUnsortedTranslucent(blockAtlas())),
    ITEM_UNLIT_TRANSLUCENT(()-> getUnlitTranslucent(blockAtlas())),
    ITEM_UNSORTED_UNLIT_TRANSLUCENT(()-> getUnlitTranslucent(blockAtlas(), false)),
    TRANSLUCENT_ON_PARTICLES_TARGET(() -> getTranslucentParticlesTarget(blockAtlas()));

    /**
     * Controls the texture filtering state for certain {@link RenderType Render Types}.
     *
     * @see ForgeRenderTypes#getText
     * @see ForgeRenderTypes#getTextIntensity
     * @see ForgeRenderTypes#getTextPolygonOffset
     * @see ForgeRenderTypes#getTextIntensityPolygonOffset
     * @see ForgeRenderTypes#getTextSeeThrough
     * @see ForgeRenderTypes#getTextIntensitySeeThrough
     */
    public static boolean enableTextTextureLinearFiltering = false;

    @SuppressWarnings("deprecation")
    private static ResourceLocation blockAtlas() {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    /**
     * @return A RenderType fit for multi-layer solid item rendering.
     */
    public static RenderType getItemLayeredSolid(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_SOLID.apply(textureLocation);
    }

    /**
     * @return A RenderType fit for multi-layer cutout item item rendering.
     */
    public static RenderType getItemLayeredCutout(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_CUTOUT.apply(textureLocation);
    }

    /**
     * @return A RenderType fit for multi-layer cutout-mipped item rendering.
     */
    public static RenderType getItemLayeredCutoutMipped(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_CUTOUT_MIPPED.apply(textureLocation);
    }

    /**
     * @return A RenderType fit for multi-layer translucent item rendering.
     */
    public static RenderType getItemLayeredTranslucent(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_TRANSLUCENT.apply(textureLocation);
    }

    /**
     * @return A RenderType fit for translucent item/entity rendering, but with depth sorting disabled.
     */
    public static RenderType getUnsortedTranslucent(ResourceLocation textureLocation) {
        return Internal.UNSORTED_TRANSLUCENT.apply(textureLocation);
    }

    /**
     * @return A RenderType fit for translucent item/entity rendering, but with diffuse lighting disabled
     * so that fullbright quads look correct.
     */
    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation) {
        return Internal.UNLIT_TRANSLUCENT_SORTED.apply(textureLocation);
    }

    /**
     * @return A RenderType fit for translucent item/entity rendering, but with diffuse lighting disabled
     * so that fullbright quads look correct.
     * @param sortingEnabled If false, depth sorting will not be performed.
     */
    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
        return (sortingEnabled ? Internal.UNLIT_TRANSLUCENT_SORTED : Internal.UNLIT_TRANSLUCENT_UNSORTED).apply(textureLocation);
    }

    /**
     * @return Same as {@link RenderType#entityCutout(ResourceLocation)}, but with mipmapping enabled.
     */
    public static RenderType getEntityCutoutMipped(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_CUTOUT_MIPPED.apply(textureLocation);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return Replacement of {@link RenderType#text(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getText(ResourceLocation locationIn) {
        return Internal.TEXT.apply(locationIn);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return Replacement of {@link RenderType#textIntensity(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getTextIntensity(ResourceLocation locationIn) {
        return Internal.TEXT_INTENSITY.apply(locationIn);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return Replacement of {@link RenderType#textPolygonOffset(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getTextPolygonOffset(ResourceLocation locationIn) {
        return Internal.TEXT_POLYGON_OFFSET.apply(locationIn);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return Replacement of {@link RenderType#textIntensityPolygonOffset(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getTextIntensityPolygonOffset(ResourceLocation locationIn) {
        return Internal.TEXT_INTENSITY_POLYGON_OFFSET.apply(locationIn);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return Replacement of {@link RenderType#textSeeThrough(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getTextSeeThrough(ResourceLocation locationIn) {
        return Internal.TEXT_SEETHROUGH.apply(locationIn);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return Replacement of {@link RenderType#textIntensitySeeThrough(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getTextIntensitySeeThrough(ResourceLocation locationIn) {
        return Internal.TEXT_INTENSITY_SEE_THROUGH.apply(locationIn);
    }

    /**
     * @see #enableTextTextureLinearFiltering
     *
     * @return A variation of {@link RenderType#translucent()} that uses {@link RenderStateShard.OutputStateShard#PARTICLES_TARGET}
     */
    public static RenderType getTranslucentParticlesTarget(ResourceLocation locationIn) {
        return Internal.TRANSLUCENT_PARTICLES_TARGET.apply(locationIn);
    }


    /**
     * Render type for rendering the early loading screen, not for modder consumption.
     *
     * @param window The early loading screen, so we can grab the buffer ID
     */
    @ApiStatus.Internal
    public static RenderType getLoadingOverlay(DisplayWindow window) {
        return Internal.getLoadingOverlay(window);
    }

    // ----------------------------------------
    //  Implementation details below this line
    // ----------------------------------------

    private final NonNullSupplier<RenderType> renderTypeSupplier;

    ForgeRenderTypes(NonNullSupplier<RenderType> renderTypeSupplier) {
        // Wrap in a Lazy<> to avoid running the supplier more than once.
        this.renderTypeSupplier = NonNullLazy.of(renderTypeSupplier);
    }

    public RenderType get() {
        return renderTypeSupplier.get();
    }

    // TODO: [VEN] Note that the names of these render types have been modified to match the names of their container constants,
    //             This was done for the sake of consistency, but it may be wrong. Need validation.
    //             Additionally, I am unsure if I have set all the flags correctly. Most of these don't have full docs on
    //             what they are meant to be used for, so I had to guess how to remap them to the new system.
    private static abstract class Internal extends RenderType {
        // TODO: [VEN] This is really just here because argument names are stripped I guess? Just as a weird doc thing?
        private Internal(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
            throw new IllegalStateException("This class must not be instantiated");
        }

        public static Function<ResourceLocation, RenderType> UNSORTED_TRANSLUCENT = Util.memoize(Internal::unsortedTranslucent);
        private static RenderType unsortedTranslucent(ResourceLocation textureLocation) {
            return create("forge_unsorted_translucent",
                          TRANSIENT_BUFFER_SIZE,
                          true,
                          false,
                          RenderPipelines.ENTITY_TRANSLUCENT,
                          CompositeState.builder()
                                        .setTextureState(new TextureStateShard(textureLocation, false))
                                        .setLightmapState(LIGHTMAP)
                                        .setOverlayState(OVERLAY)
                                        .createCompositeState(true));
        }

        public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_SORTED = Util.memoize(Internal::unlitTranslucentSorted);
        private static RenderType unlitTranslucentSorted(ResourceLocation textureLocation) {
            return create("forge_unlit_translucent_sorted",
                          TRANSIENT_BUFFER_SIZE,
                          true,
                          true,
                          RenderPipelines.ENTITY_TRANSLUCENT,
                          CompositeState.builder()
                                        .setTextureState(new TextureStateShard(textureLocation, false))
                                        .setLightmapState(NO_LIGHTMAP)
                                        .setOverlayState(OVERLAY)
                                        .createCompositeState(true));
        }

        public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_UNSORTED = Util.memoize(Internal::unlitTranslucentUnsorted);
        private static RenderType unlitTranslucentUnsorted(ResourceLocation textureLocation) {
            return create("forge_unlit_translucent_sorted",
                          TRANSIENT_BUFFER_SIZE,
                          true,
                          false,
                          RenderPipelines.ENTITY_TRANSLUCENT,
                          CompositeState.builder()
                                        .setTextureState(new TextureStateShard(textureLocation, false))
                                        .setLightmapState(NO_LIGHTMAP)
                                        .setOverlayState(OVERLAY)
                                        .createCompositeState(true));
        }

        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_SOLID = Util.memoize(Internal::layeredItemSolid);
        private static RenderType layeredItemSolid(ResourceLocation locationIn) {
            return create(
                    "forge_layered_item_soild",
                    TRANSIENT_BUFFER_SIZE,
                    true,
                    false,
                    RenderPipelines.ENTITY_SOLID,
                    CompositeState.builder()
                                  .setTextureState(new TextureStateShard(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .setOverlayState(OVERLAY)
                                  .createCompositeState(true)
            );
        }

        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_CUTOUT = Util.memoize(Internal::layeredItemCutout);
        private static RenderType layeredItemCutout(ResourceLocation locationIn) {
            return create(
                    "forge_layered_item_cutout",
                    TRANSIENT_BUFFER_SIZE,
                    true,
                    false,
                    RenderPipelines.ENTITY_CUTOUT,
                    CompositeState.builder()
                                  .setTextureState(new TextureStateShard(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .setOverlayState(OVERLAY)
                                  .createCompositeState(true)
            );
        }

        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_CUTOUT_MIPPED = Util.memoize(Internal::layeredItemCutoutMipped);
        private static RenderType layeredItemCutoutMipped(ResourceLocation locationIn) {
            return create(
                    "forge_layered_item_cutout_mipped",
                    TRANSIENT_BUFFER_SIZE,
                    true,
                    false,
                    RenderPipelines.ENTITY_SMOOTH_CUTOUT,
                    CompositeState.builder()
                                  .setTextureState(new TextureStateShard(locationIn, true))
                                  .setLightmapState(LIGHTMAP)
                                  .setOverlayState(OVERLAY)
                                  .createCompositeState(true)
            );
        }

        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_TRANSLUCENT = Util.memoize(Internal::layeredItemTranslucent);
        private static RenderType layeredItemTranslucent(ResourceLocation locationIn) {
            return create(
                    "forge_layered_item_translucent",
                    TRANSIENT_BUFFER_SIZE,
                    true,
                    true,
                    RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL,
                    CompositeState.builder()
                                  .setTextureState(new TextureStateShard(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .setOverlayState(OVERLAY)
                                  .createCompositeState(true)
            );
        }

        public static Function<ResourceLocation, RenderType> TEXT = Util.memoize(Internal::getText);
        private static RenderType getText(ResourceLocation locationIn) {
            return create(
                    "forge_text",
                    SMALL_BUFFER_SIZE,
                    false,
                    true,
                    RenderPipelines.TEXT,
                    CompositeState.builder()
                                  .setTextureState(new CustomizableTextureState(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(false)
            );
        }

        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.memoize(Internal::getTextIntensity);
        private static RenderType getTextIntensity(ResourceLocation locationIn) {
            return create(
                    "forge_text_intensity",
                    SMALL_BUFFER_SIZE,
                    false,
                    false,
                    RenderPipelines.TEXT_INTENSITY,
                    CompositeState.builder()
                                  .setTextureState(new CustomizableTextureState(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(false)
            );
        }

        public static Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.memoize(Internal::getTextPolygonOffset);
        private static RenderType getTextPolygonOffset(ResourceLocation locationIn) {
            return create(
                    "forge_text_polygon_offset",
                    TRANSIENT_BUFFER_SIZE,
                    false,
                    true,
                    RenderPipelines.TEXT_POLYGON_OFFSET,
                    CompositeState.builder()
                                  .setTextureState(new CustomizableTextureState(locationIn,  false))
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(false)
            );
        }

        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(Internal::getTextIntensityPolygonOffset);
        private static RenderType getTextIntensityPolygonOffset(ResourceLocation locationIn) {
            return create(
                    "forge_text_intensity_polygon_offset",
                    TRANSIENT_BUFFER_SIZE,
                    false,
                    true,
                    RenderPipelines.TEXT_INTENSITY,
                    CompositeState.builder()
                                  .setTextureState(new CustomizableTextureState(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(false)
            );
        }

        public static Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH = Util.memoize(Internal::getTextSeeThrough);
        /**
         * Use {@link #TEXT_SEE_THROUGH} instead.
         */
        @Deprecated
        public static Function<ResourceLocation, RenderType> TEXT_SEETHROUGH = TEXT_SEE_THROUGH;
        private static RenderType getTextSeeThrough(ResourceLocation locationIn) {
            return create(
                    "forge_text_see_through",
                    TRANSIENT_BUFFER_SIZE,
                    false,
                    false,
                    RenderPipelines.TEXT_SEE_THROUGH,
                    CompositeState.builder()
                                  .setTextureState(new CustomizableTextureState(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(false)
            );
        }

        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(Internal::getTextIntensitySeeThrough);
        /**
         * Use {@link #TEXT_INTENSITY_SEE_THROUGH} instead.
         */
        @SuppressWarnings("unused")
        @Deprecated(forRemoval = true, since = "1.21.5")
        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEETHROUGH = TEXT_INTENSITY_SEE_THROUGH;
        private static RenderType getTextIntensitySeeThrough(ResourceLocation locationIn) {
            return create(
                    "forge_text_intensity_see_through",
                    TRANSIENT_BUFFER_SIZE,
                    false,
                    true,
                    RenderPipelines.TEXT_INTENSITY_SEE_THROUGH,
                    CompositeState.builder()
                                  .setTextureState(new CustomizableTextureState(locationIn, false))
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(false)
            );
        }

        public static Function<ResourceLocation, RenderType> TRANSLUCENT_PARTICLES_TARGET = Util.memoize(Internal::getTranslucentParticlesTarget);
        private static RenderType getTranslucentParticlesTarget(ResourceLocation locationIn) {
            return create(
                    "forge_translucent_particles_target",
                    TRANSIENT_BUFFER_SIZE,
                    true,
                    true,
                    RenderPipelines.TRANSLUCENT_PARTICLE,
                    CompositeState.builder()
                                  .setTextureState(new TextureStateShard(locationIn, false))
                                  .setOutputState(PARTICLES_TARGET)
                                  .setLightmapState(LIGHTMAP)
                                  .createCompositeState(true)
            );
        }

        private static final RenderPipeline LOADING_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                .withLocation("pipeline/forge/loading_overlay")
                .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE))
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withDepthWrite(false)
                .build();

        public static RenderType getLoadingOverlay(DisplayWindow window) {
            return create(
                "forge_loading_overlay",
                TRANSIENT_BUFFER_SIZE,
                false,
                false,
                LOADING_PIPELINE,
                CompositeState.builder()
                    .setTextureState(new LoadingOverlayTextureState(window))
                    .createCompositeState(false)
            );
        }
    }

    private static class CustomizableTextureState extends TextureStateShard {
        private CustomizableTextureState(ResourceLocation resLoc, boolean mipmap) {
            super(resLoc, mipmap);
            this.setupState = () -> {
                TextureManager manager = Minecraft.getInstance().getTextureManager();
                var texture = manager.getTexture(resLoc);
                texture.setFilter(enableTextTextureLinearFiltering, this.mipmap);
                RenderSystem.setShaderTexture(0, texture.getTextureView());
            };
        }
    }

    private static class LoadingOverlayTextureState extends TextureStateShard {
        private static final ResourceLocation LOADING_TEXTURE = ResourceLocation.fromNamespaceAndPath("forge", "loading_overlay");
        private final GpuTexture texture;
        private final GpuTextureView textureView;

        private LoadingOverlayTextureState(DisplayWindow window) {
            super(LOADING_TEXTURE, false);

            GpuDevice gpu = RenderSystem.getDevice();
            this.texture = gpu.createTexture(LOADING_TEXTURE.toString(), 5, TextureFormat.RGBA8,
                    window.context().width(), window.context().height(),
                    1, window.getFramebufferTextureId());
            this.textureView = gpu.createTextureView(this.texture);

            this.setupState = () -> {
                GL30C.glTexParameterIi(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST);
                GL30C.glTexParameterIi(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
                RenderSystem.setShaderTexture(0, this.textureView);
            };
        }
    }
}
