package net.minecraftforge.event;

import net.minecraft.util.registry.DynamicRegistries;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * <p>Fired after datapacks are loaded, and after the {@link DynamicRegistries} are populated with all their data, but before the server is loaded.</p>
 *
 * <p>Using this event allows mods to interact with datapack worldgen objects, among other things.<br>
 * For example, manually inserting a JSON feature into a vanilla biome.</p>
 *
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable},
 * and does not {@linkplain HasResult have a result}.</p>
 */
public class DynamicRegistriesLoadedEvent extends Event {
    @Nonnull
    private final DynamicRegistries.Impl registries;

    public DynamicRegistriesLoadedEvent(final @Nonnull DynamicRegistries.Impl registries)
    {
        this.registries = registries;
    }

    public @Nonnull DynamicRegistries.Impl getRegistries() { return registries; }

}
