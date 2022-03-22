package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;

class VanillaRegisterEvent extends Event implements IModBusEvent
{
    @NotNull
    final Registry<?> vanillaRegistry;

    VanillaRegisterEvent(@NotNull Registry<?> vanillaRegistry)
    {
        this.vanillaRegistry = vanillaRegistry;
    }
}
