package net.minecraftforge.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Renders registered hooks to render during their appropriate {@link Phase}.
 * Renderer hooks may be registered at any time.
 * 
 * @see #register
 */
public class LevelRendererHooks
{
    private static final HashMap<Phase, List<Consumer<RenderContext>>> HOOKS = new HashMap<>();
    private static final HashMap<RenderType, Phase> RENDER_TYPE_PHASES = new HashMap<>();
    public static float partialTicks = 0.0F;

    /**
     * Registers the Consumer passed to the {@link Phase} specified.
     */
    public static void register(Phase phase, Consumer<RenderContext> hook)
    {
        HOOKS.computeIfAbsent(phase, p -> new ArrayList<>()).add(hook);
    }

    /**
     * Renders registered hooks for the {@link Phase} passed.
     */
    public static void render(Phase phase, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, double camX, double camY, double camZ)
    {
        List<Consumer<RenderContext>> hooks = HOOKS.get(phase);
        if (hooks != null)
        {
            Minecraft.getInstance().getProfiler().popPush(phase.toString());
            for (Consumer<RenderContext> hook : hooks)
                hook.accept(new RenderContext(levelRenderer, poseStack, projectionMatrix, ticks, partialTicks, camX, camY, camZ));
        }
    }

    /**
     * Renders the registered for the {@link Phase} that matches the RenderType passed, if a Phase exists.
     */
    public static void render(RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, double camX, double camY, double camZ)
    {
        Phase phase = RENDER_TYPE_PHASES.get(renderType);
        if (phase != null)
            render(phase, levelRenderer, poseStack, projectionMatrix, ticks, camX, camY, camZ);
    }

    /**
     * Times during {@link LevelRenderer#renderLevel} to render hooks.
     */
    public static class Phase
    {
        /** May render even if the sky doesn't render. */
        public static final Phase AFTER_SKY = create("after_sky", null);
        public static final Phase AFTER_SOLID_BLOCKS = create("after_solid_blocks", RenderType.solid());
        public static final Phase AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS = create("after_cutout_mipped_blocks", RenderType.cutoutMipped());
        public static final Phase AFTER_CUTOUT_BLOCKS = create("after_cutout_blocks", RenderType.cutout());
        public static final Phase AFTER_ENTITIES = create("after_entities", null);
        /** Also happens after block entities */
        public static final Phase AFTER_TRANSLUCENT_BLOCKS = create("after_translucent_blocks", RenderType.translucent());
        public static final Phase AFTER_TRIPWIRE_BLOCKS = create("after_tripwire_blocks", RenderType.tripwire());
        public static final Phase AFTER_PARTICLES = create("after_particles", null);
        /** Only renders when clouds actually render. */
        public static final Phase AFTER_CLOUDS = create("after_clouds", null);
        public static final Phase AFTER_WEATHER = create("after_weather", null);
        /** May not work with fabulous graphics. */
        public static final Phase LAST = create("last", null);

        private ResourceLocation name;

        private Phase(ResourceLocation name)
        {
            this.name = name;
        }

        /**
         * @param name The name of your Phase. Used for profiling.
         * @param chunkLayer If your Phase should render during LevelRenderer.renderChunkLayer, supply a RenderType. If not, pass null.
         */
        public static Phase create(ResourceLocation name, @Nullable RenderType chunkLayer)
        {
            Phase phase = new Phase(name);
            if (chunkLayer != null && RENDER_TYPE_PHASES.put(chunkLayer, phase) != null)
                throw new IllegalArgumentException("Attempted to replace an existing LevelRendererHooks.Phase for a RenderType: Phase = " + name + ", RenderType = " + chunkLayer.toString());
            return phase;
        }

        private static Phase create(String name, @Nullable RenderType chunkLayer)
        {
            return create(new ResourceLocation("forge", name), chunkLayer);
        }

        @Override
        public String toString()
        {
            return this.name.toString();
        }
    }

    /**
     * Stores data to pass into a render hook
     */
    public static record RenderContext(LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, float partialTick, double camX, double camY, double camZ) {}
}
