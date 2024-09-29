package net.minecraftforge.common.capabilities;

@FunctionalInterface
public interface ICapabilityFactory<T, B extends ICapabilityProvider> {
    CapabilityProviderHolder<B> create(T obj);
}
