package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event fires when all registries are ready to have modded objects registered.
 * All registries will be ready and unfrozen when this event fires.
 * <p>
 * Fired on the {@link IModBusEvent mod bus}.
 *
 * @see #register(ResourceKey, ResourceLocation, Object)
 */
public class RegisterEvent extends Event implements IModBusEvent
{
    @NotNull
    private final ResourceKey<? extends Registry<?>> registryKey;
    @Nullable
    private final ForgeRegistry<?> forgeRegistry;
    @Nullable
    private final Registry<?> vanillaRegistry;

    RegisterEvent(@NotNull ResourceKey<? extends Registry<?>> registryKey, @Nullable ForgeRegistry<?> forgeRegistry, @Nullable Registry<?> vanillaRegistry)
    {
        this.registryKey = registryKey;
        this.forgeRegistry = forgeRegistry;
        this.vanillaRegistry = vanillaRegistry;
    }

    /**
     * Registers the value with the given name to the stored registry if the provided registry key matches this event's registry key.
     *
     * @param registryKey The key of the registry to register the value to
     * @param name The name of the object to register as its key
     * @param value The object value
     * @param <T> The type of the registry
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation name, T value)
    {
        if (this.registryKey.equals(registryKey))
        {
            if (this.forgeRegistry != null)
                ((IForgeRegistry) this.forgeRegistry).register(name, value);
            else if (this.vanillaRegistry != null)
                Registry.register((Registry) this.vanillaRegistry, name, value);
        }
    }

    /**
     * @return The registry key linked to this event
     */
    @NotNull
    public ResourceKey<? extends Registry<?>> getRegistryKey()
    {
        return registryKey;
    }

    /**
     * @return The forge registry for the given registry key, or {@code null} if the registry is not a forge registry
     */
    @Nullable
    public ForgeRegistry<?> getForgeRegistry()
    {
        return forgeRegistry;
    }

    /**
     * @return The vanilla registry for the given registry key, or {@code null} if the registry is not a vanilla registry
     */
    @Nullable
    public Registry<?> getVanillaRegistry()
    {
        return vanillaRegistry;
    }

    @Override
    public String toString()
    {
        return "RegisterEvent";
    }
}
