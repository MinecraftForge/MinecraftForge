package net.minecraftforge.common.extensions;

import net.minecraft.core.RegistryAccess;

import java.util.Optional;

public interface IForgeRegistryAccessHolder {
    /**
     * @return Optional to {@link RegistryAccess} due to it being possible to be not present/null
     */
    Optional<RegistryAccess> getRegistryAccess();
}
