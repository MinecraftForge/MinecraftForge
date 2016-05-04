package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Event for when an entity drops experience on its death, can be used to change
 * the amount of experience points dropped or completely prevent dropping of experience
 * by canceling the event.
 */
@Cancelable
public class LivingExperienceDropEvent extends LivingEvent 
{
    private final EntityPlayer attackingPlayer;
    private final int originalExperiencePoints;

    private int droppedExperiencePoints;

    public LivingExperienceDropEvent(EntityLivingBase entity, EntityPlayer attackingPlayer, int originalExperience)
    {
        super(entity);

        this.attackingPlayer = attackingPlayer;
        this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
    }

    public int getDroppedExperience()
    {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int droppedExperience)
    {
        this.droppedExperiencePoints = droppedExperience;
    }

    public EntityPlayer getAttackingPlayer()
    {
        return attackingPlayer;
    }

    public int getOriginalExperience()
    {
        return originalExperiencePoints;
    }
}
