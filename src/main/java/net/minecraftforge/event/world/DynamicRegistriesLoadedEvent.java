package net.minecraftforge.event.world;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.IBiomeParameters;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * Fires after dynamic registries (biomes, configuredfeatures, etc) have loaded
 * from datapacks, and after dimensions/chunkgenerators/biomeproviders load
 * from datapacks. Allows modification of biomes in-place using
 * dynamicregistries context (allowing e.g. adding features from datapacks to a
 * set of biomes specified by datapacks).
 * 
 * Attempting to *replace* things in the registries at this point or at server
 * runtime will break everything as value-key lookups are used in many places,
 * so don't do that.
 *
 * In order to maintain the most compatibility possible with other mods'
 * modifications to a biome, the event should be assigned a
 * {@link net.minecraftforge.eventbus.api.EventPriority} as follows:
 *
 * - Additions to any list/map contained in a biome : {@link EventPriority#HIGH}
 * - Removals to any list/map contained in a biome : {@link EventPriority#NORMAL}
 * - Any other modification : {@link EventPriority#LOW}
 * 
 * Mods that add worldgen features or spawning mobs and wish to add their
 * features to biomes can do so via this event. Custom biomes should declare any
 * vanilla features in their biome json.
 * 
 * This event is guaranteed to provide a unique and unmodified set of dynamic
 * registry objects each time the event fires.
 */
public class DynamicRegistriesLoadedEvent extends Event
{
    private final @Nonnull DynamicRegistries dataRegistries;
    private final @Nonnull Map<RegistryKey<Biome>, IBiomeParameters> biomeModifiers;

    public DynamicRegistriesLoadedEvent(final @Nonnull DynamicRegistries dataRegistries, final @Nonnull Map<RegistryKey<Biome>, IBiomeParameters> biomeModifiers)
    {
        this.dataRegistries = dataRegistries;
        this.biomeModifiers = biomeModifiers;
    }

    /**
     * Access to the data registries containing the biomes being modified. Contains
     * biomes, features, dimensiontypes, etc. These have been fully loaded and
     * pasted over any builtin vanilla objects.
     * 
     * @return The registries containing the builtin WorldGenRegistries objects as
     *         well as any datapack objects that have been added to the registries
     *         or have replaced builtin objects in them. No objects in this
     *         registries instance have been provided to a previous
     *         DynamicRegistriesLoadedEvent.
     */
    public DynamicRegistries getDataRegistries()
    {
        return this.dataRegistries;
    }

    /**
     * Gets the map of biome modifiers. Any modifications made by event listeners to
     * these parameters will be applied to biomes at the end of the event.
     * 
     * These parameters are initially in the same state as the original biome loaded
     * from builtin registries or from datapacks, but may have been modified by
     * other event listeners before being given to your own listener.
     * 
     * Add features to your biome by adding them to the generation builder via this,
     * it's nicer than copying the biome's immutable feature lists
     * 
     * @return biome modifiers by biome key
     */
    public Map<RegistryKey<Biome>, IBiomeParameters> getBiomeModifiers()
    {
        return this.biomeModifiers;
    }
}
