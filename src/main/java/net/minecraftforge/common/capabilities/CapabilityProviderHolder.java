package net.minecraftforge.common.capabilities;

import java.util.function.Consumer;

public record CapabilityProviderHolder<B extends ICapabilityProvider>(B provider, Consumer<B> listener) {}
