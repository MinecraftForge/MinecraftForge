package net.minecraftforge.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Renders registered hooks to render during their appropriate {@link Phase}.
 * Renderer hooks may be registered at any time.
 * Custom Phases are allowed.
 * 
 * @see #register
 */
public class LevelRendererHooks
{
    private static final HashMap<Phase, List<Consumer<RenderContext>>> HOOKS = new HashMap<>();
    private static float partialTicks = 0.0F;

    /**
     * Registers the Consumer passed to the {@link Phase} specified.
     */
    public static void register(Phase phase, Consumer<RenderContext> hook)
    {
        HOOKS.computeIfAbsent(phase, p -> new ArrayList<>()).add(hook);
    }

    /**
     * Renders registered hooks.
     */
    public static void render(Phase phase, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, double camX, double camY, double camZ)
    {
        Minecraft.getInstance().getProfiler().popPush(phase.name.toString());
        List<Consumer<RenderContext>> hooks = HOOKS.get(phase);
        if (hooks != null)
        {
            RenderContext context = new RenderContext(levelRenderer, poseStack, projectionMatrix, ticks, partialTicks, camX, camY, camZ);
            for (Consumer<RenderContext> hook : hooks)
                hook.accept(context);
        }
    }

    /**
     * Renders the registered hooks that correspond to for the renderType passed, if any match.
     */
    public static void render(RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, double camX, double camY, double camZ)
    {
        Phase phase = null;
        if (renderType == RenderType.solid()) phase = Phase.AFTER_SOLID_BLOCKS;
        else if (renderType == RenderType.translucent()) phase = Phase.AFTER_TRANSLUCENT_BLOCKS;

        if (phase != null)
            render(phase, levelRenderer, poseStack, projectionMatrix, ticks, camX, camY, camZ);
    }

    /**
     * @return The current partial ticks obtained from the start of LevelRenderer.renderLevel
     */
    public static float getPartialTicks()
    {
        return LevelRendererHooks.partialTicks;
    }

    /**
     * Sets the stored partial ticks
     */
    public static void setPartialTicks(float partialTicks)
    {
        LevelRendererHooks.partialTicks = partialTicks;
    }

    /**
     * Times during {@link LevelRenderer#renderLevel} to render hooks.
     */
    public static record Phase(ResourceLocation name)
    {
        public static final Phase AFTER_SKY = new Phase("after_sky");
        public static final Phase AFTER_SOLID_BLOCKS = new Phase("after_solid_blocks");
        public static final Phase AFTER_ENTITIES = new Phase("after_entities");
        public static final Phase AFTER_TRANSLUCENT_BLOCKS = new Phase("after_translucent_blocks");
        public static final Phase AFTER_PARTICLES = new Phase("after_particles");
        /** Only renders when clouds render */
        public static final Phase AFTER_CLOUDS = new Phase("after_clouds");
        public static final Phase AFTER_WEATHER = new Phase("after_weather");
        /** May not work with fabulous graphics */
        public static final Phase LAST = new Phase("last");

        private Phase(String name)
        {
            this(new ResourceLocation("forge", name));
        }
    }

    /**
     * Stores data to pass into a render hook
     */
    public static record RenderContext(LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int ticks, float partialTick, double camX, double camY, double camZ) {}
}
