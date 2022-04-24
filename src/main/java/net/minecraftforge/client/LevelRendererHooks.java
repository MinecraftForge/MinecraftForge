package net.minecraftforge.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Renders registered {@link ILevelRendererHook}s to render during their appropriate times.
 * Register hooks during {@link FMLClientSetupEvent}.
 * 
 * @see #registerHook
 */
public class LevelRendererHooks
{
    private static final EnumMap<Phase, List<ILevelRendererHook>> HOOKS = new EnumMap<>(Phase.class);
    private static float partialTicks = 0.0F;

    /**
     * Registers the {@link ILevelRendererHook} passed to the {@link Phase} specified.
     */
    public static void registerHook(Phase phase, ILevelRendererHook hook)
    {
        HOOKS.computeIfAbsent(phase, p -> new ArrayList<>()).add(hook);
    }

    /**
     * @return The current partial ticks from the start of LevelRenderer.renderLevel
     */
    public static float getPartialTicks()
    {
        return LevelRendererHooks.partialTicks;
    }

    /**
     * Called internally. You should have no reason to call this.
     */
    public static void render(Phase phase, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, double camX, double camY, double camZ)
    {
        Minecraft.getInstance().getProfiler().popPush(phase.profillerLabel());
        List<ILevelRendererHook> hooks = HOOKS.get(phase);
        if (hooks != null)
            for (ILevelRendererHook hook : hooks)
                hook.render(levelRenderer, poseStack, projectionMatrix, partialTicks, camX, camY, camZ);
    }

    /**
     * Called internally. You should have no reason to call this.
     */
    public static void setPartialTicks(float partialTicks)
    {
        LevelRendererHooks.partialTicks = partialTicks;
    }

    /**
     * Times during {@link LevelRenderer#renderLevel} to render {@link ILevelRendererHook}s. 
     * Some phases may not work with fabulous graphics, and have been documented as such.
     */
    public static enum Phase
    {
        /**
         * May not work with fabulous graphics
         */
        BEFORE_SOLID_BLOCKS("before_solid_blocks"),
        AFTER_SOLID_BLOCKS("after_solid_blocks"),
        AFTER_TRANSLUCENT_BLOCKS("after_translucent_blocks"),
        AFTER_PARTICLES("after_particles"),
        AFTER_CLOUDS("after_clouds"),
        AFTER_WEATHER("after_weather"),
        /**
         * May not work with fabulous graphics
         */
        LAST("last");

        final String profillerName;

        Phase(String name)
        {
            this.profillerName = "forge_render_" + name;
        }

        String profillerLabel()
        {
            return this.profillerName;
        }
    }

    /**
     * Used to render things in the world. Registered with {@link LevelRendererHooks#registerHook}
     */
    @FunctionalInterface
    public static interface ILevelRendererHook
    {
        void render(LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ);
    }
}
