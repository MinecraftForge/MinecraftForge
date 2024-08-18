package net.minecraftforge.common.extensions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public interface IForgeResourceKey<T> {
    private ResourceKey<T> self() {
        return (ResourceKey<T>) this;
    }

    /**
     * @param registryAccessHolder of {@link IForgeRegistryAccessHolder}
     * @return {@link Holder<T>} of the Object that this {@link ResourceKey<T>} is registered to
     */
    default Holder<T> getOrThrow(IForgeRegistryAccessHolder registryAccessHolder) {
        return registryAccessHolder.getRegistryAccess().orElseThrow().registryOrThrow(self().registryKey()).getHolderOrThrow(self());
    }
}
