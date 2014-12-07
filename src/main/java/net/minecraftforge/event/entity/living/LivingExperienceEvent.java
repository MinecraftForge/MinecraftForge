package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class LivingExperienceEvent extends LivingEvent 
{
    public final int originalExperience;
    public final EntityPlayer attackingPlayer;
    
    public int experience;
    
    public LivingExperienceEvent(EntityLivingBase entity, EntityPlayer attackingPlayer, int originalExperience)
    {
        super(entity);
        
        this.originalExperience = this.experience = originalExperience;
        this.attackingPlayer = attackingPlayer;
    }
}
