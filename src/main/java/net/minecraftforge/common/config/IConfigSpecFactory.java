package net.minecraftforge.common.config;

import net.minecraftforge.fml.config.ModConfig;

/**
 * The factory that produces a {@link ModConfigSpec}.
 *
 * Mods can provide their own implementation of {@link #create(ModConfig)} to have their specific implementation
 * injected into their config instances.
 */
public interface IConfigSpecFactory<V extends ModConfigSpec>
{
    /**
     * Create a new instance of {@link ModConfigSpec} using the given {@link ModConfig}
     *
     * @param modConfig the {@link ModConfig}
     * @return a new instance of {@link ModConfigSpec}
     */
    V create(ModConfig<V> modConfig);

    /**
     * Create a new, default, instance of {@link ModConfigSpec} using the given {@link ModConfig}
     *
     * @param modConfig the ModConfig to use
     * @return a new instance of {@link ModConfigSpec}
     */
    public static <V extends ModConfigSpec> V createSpec(ModConfig<V> modConfig) {
        return (V) new ModConfigSpec(modConfig);
    }
}
