package net.minecraftforge.common.capabilities;

import java.util.Optional;

public final class CapabilityReference<T>
{

    private Optional<Capability<T>> capability = Optional.empty();

    private CapabilityReference()
    {
    }

    public static <T> CapabilityReference<T> create()
    {
        return new CapabilityReference<>();
    }

    public Optional<Capability<T>> resolve()
    {
        return capability;
    }

    void inject(Capability<T> capability)
    {
        this.capability = Optional.of(capability);
    }

}
