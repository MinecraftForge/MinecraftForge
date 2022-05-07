package net.minecraftforge.client.event;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fires at various times during LevelRenderer.renderLevel. 
 * Check {@link #stage} to render during the appropriate time for your use case.
 */
public class RenderLevelStageEvent extends Event
{
    public final Stage stage;
    public final LevelRenderer levelRenderer;
    public final PoseStack poseStack;
    public final Matrix4f projectionMatrix;
    public final int ticks;
    public final float partialTick;
    public final double camX, camY, camZ;

    public RenderLevelStageEvent(Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, float partialTick, double camX, double camY, double camZ)
    {
        this.stage = stage;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.ticks = ticks;
        this.partialTick = partialTick;
        this.camX = camX;
        this.camY = camY;
        this.camZ = camZ;
    }

    /**
     * Use to create a custom {@link RenderLevelStageEvent.Stage}s.
     * Fired after the LevelRenderer has been created.
     */
    public static class RegisterStageEvent extends Event implements IModBusEvent
    {
        /**
         * @param name The name of your Stage.
         * @param renderType
         *     If not null, called automatically by LevelRenderer.renderChunkLayer if the RenderType passed into it matches this one.
         *     If null, needs to be called manually by whoever implements it.
         * 
         */
        public Stage register(ResourceLocation name, @Nullable RenderType renderType)
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
         * Called within a fabulous graphics target. 
         * Happens after block entities render. 
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

        private static Stage register(ResourceLocation name, @Nullable RenderType renderType)
        {
            Stage stage = new Stage(name.toString());
            if (renderType != null && RENDER_TYPE_STAGES.put(renderType, stage) != null)
                throw new IllegalArgumentException("Attempted to replace an existing RenderLevelStageEvent.Stage for a RenderType: Stage = " + stage + ", RenderType = " + renderType);
            return stage;
        }

        private static Stage register(String name, @Nullable RenderType renderType)
        {
            return register(new ResourceLocation("forge", name), renderType);
        }

        @Override
        public String toString()
        {
            return this.name;
        }

        /**
         * @return The Stage bound to the RenderType passed or null if no value is present.
         */
        @Nullable
        public static Stage fromRenderType(RenderType renderType)
        {
            return RENDER_TYPE_STAGES.get(renderType);
        }
    }
}
