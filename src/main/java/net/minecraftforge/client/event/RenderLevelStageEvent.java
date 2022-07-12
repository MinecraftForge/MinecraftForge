/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fires at various times during LevelRenderer.renderLevel. 
 * Check {@link #getStage} to render during the appropriate time for your use case.
 * 
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 * 
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
public class RenderLevelStageEvent extends Event
{
    private final Stage stage;
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final Matrix4f projectionMatrix;
    private final int renderTick;
    private final float partialTick;
    private final Camera camera;
    private final Frustum frustum;

    public RenderLevelStageEvent(Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTick, Camera camera, Frustum frustum)
    {
        this.stage = stage;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.partialTick = partialTick;
        this.camera = camera;
        this.frustum = frustum;
    }

    /**
     * {@return the current {@linkplain Stage stage} that is being rendered. Check this before doing rendering to ensure
     * that rendering happens at the appropriate time.}
     */
    public Stage getStage()
    {
        return stage;
    }

    /**
     * {@return the level renderer}
     */
    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the projection matrix}
     */
    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    /**
     * {@return the current "ticks" value in the {@linkplain LevelRenderer level renderer}}
     */
    public int getRenderTick()
    {
        return renderTick;
    }

    /**
     * {@return the current partialTick value used for rendering}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the camera}
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * {@return the frustum}
     */
    public Frustum getFrustum()
    {
        return frustum;
    }

    /**
     * Use to create a custom {@linkplain RenderLevelStageEvent.Stage stages}.
     * Fired after the LevelRenderer has been created.
     * 
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
     * 
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class RegisterStageEvent extends Event implements IModBusEvent
    {
        /**
         * @param name The name of your Stage.
         * @param renderType
         *     If not null, called automatically by LevelRenderer.renderChunkLayer if the RenderType passed into it matches this one.
         *     If null, needs to be called manually by whoever implements it.
         *
         * @throws IllegalArgumentException if the RenderType passed is already mapped to a Stage.
         */
        public Stage register(ResourceLocation name, @Nullable RenderType renderType) throws IllegalArgumentException
        {
            return Stage.register(name, renderType);
        }
    }

    /**
     * A time during level rendering for you to render custom things into the world.
     * @see RegisterStageEvent
     */
    public static class Stage
    {
        private static final Map<RenderType, Stage> RENDER_TYPE_STAGES = new HashMap<>();

        /**
         * Use this to render custom objects into the skybox.
         * Called regardless of if they sky actually renders or not.
         */
        public static final Stage AFTER_SKY = register("after_sky", null);
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_SOLID_BLOCKS = register("after_solid_blocks", RenderType.solid());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS = register("after_cutout_mipped_blocks", RenderType.cutoutMipped());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_CUTOUT_BLOCKS = register("after_cutout_blocks", RenderType.cutout());
        /**
         * Use this to render custom block-like geometry into the world.
         * Due to how transparency sorting works, this stage may not work properly with translucency. If you intend to render translucency,
         * try using {@link #AFTER_TRIPWIRE_BLOCKS} or {@link #AFTER_PARTICLES}.
         * Although this is called within a fabulous graphics target, it does not function properly in many cases.
         */
        public static final Stage AFTER_TRANSLUCENT_BLOCKS = register("after_translucent_blocks", RenderType.translucent());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_TRIPWIRE_BLOCKS = register("after_tripwire_blocks", RenderType.tripwire());
        /**
         * Use this to render custom effects into the world, such as custom entity-like objects or special rendering effects.
         * Called within a fabulous graphics target.
         * Happens after entities render.
         * 
         * @see ForgeRenderTypes#TRANSLUCENT_ON_PARTICLES_TARGET
         */
        public static final Stage AFTER_PARTICLES = register("after_particles", null);
        /**
         * Use this to render custom weather effects into the world.
         * Called within a fabulous graphics target.
         */
        public static final Stage AFTER_WEATHER = register("after_weather", null);

        private final String name;

        private Stage(String name)
        {
            this.name = name;
        }

        private static Stage register(ResourceLocation name, @Nullable RenderType renderType) throws IllegalArgumentException
        {
            Stage stage = new Stage(name.toString());
            if (renderType != null && RENDER_TYPE_STAGES.putIfAbsent(renderType, stage) != null)
                throw new IllegalArgumentException("Attempted to replace an existing RenderLevelStageEvent.Stage for a RenderType: Stage = " + stage + ", RenderType = " + renderType);
            return stage;
        }

        private static Stage register(String name, @Nullable RenderType renderType) throws IllegalArgumentException
        {
            return register(new ResourceLocation(name), renderType);
        }

        @Override
        public String toString()
        {
            return this.name;
        }

        /**
         * {@return the {@linkplain Stage stage} bound to the {@linkplain RenderType render type}, or null if no value is present}
         */
        @Nullable
        public static Stage fromRenderType(RenderType renderType)
        {
            return RENDER_TYPE_STAGES.get(renderType);
        }
    }
}
