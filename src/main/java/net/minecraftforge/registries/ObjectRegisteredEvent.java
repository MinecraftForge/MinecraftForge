package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ObjectRegisteredEvent extends Event implements IModBusEvent
{
    @NotNull
    private final RegisterEvent registerEvent;
    @NotNull
    private final ResourceLocation location;
    public ObjectRegisteredEvent(RegisterEvent registerEvent, ResourceLocation location)
    {
        this.registerEvent = registerEvent;
        this.location = location;
    }

    public @NotNull RegisterEvent getRegisterEvent() {
        return registerEvent;
    }

    public @NotNull ResourceLocation getLocation() {
        return location;
    }
}
