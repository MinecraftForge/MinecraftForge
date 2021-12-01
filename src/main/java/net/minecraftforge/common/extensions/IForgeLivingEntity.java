package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface IForgeLivingEntity
{

    private LivingEntity self() { return (LivingEntity) this; }

    /**
     * Returns the experience value of the entity after the LivingExperienceDropEvent is fired.
     * Returns 0 if the entity should not drop experience.
     *
     * @param player The attacking player involved in obtaining this experience, typically lastHurtByPlayer.
     * @return The amount of experience
     */
    int getTotalExperienceValue(Player player);
}
