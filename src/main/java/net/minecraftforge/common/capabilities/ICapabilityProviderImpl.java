package net.minecraftforge.common.capabilities;

import javax.annotation.Nullable;

public interface ICapabilityProviderImpl<B extends ICapabilityProviderImpl<B>> extends ICapabilityProvider
{
    boolean areCapsCompatible(CapabilityProvider<B> other);
    boolean areCapsCompatible(@Nullable CapabilityDispatcher other);
    void invalidateCaps();
    void reviveCaps();
}
