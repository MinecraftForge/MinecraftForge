package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;

public interface ICapabilityFactory<T> {
    ICapabilityProvider create(T obj);
}
