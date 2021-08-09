package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;

public interface IForgeLivingEntity {

    private LivingEntity self()
    {
        return (LivingEntity) this;
    }

    /**
     * Returns whether the {@link LivingEntity} can breathe in the fluid.
     *
     * @param state The current {@link FluidState} the entity is within
     * @return Whether the {@link LivingEntity} can breathe in the fluid
     */
    default boolean canBreatheInFluid(FluidState state)
    {
        return false;
    }
}
