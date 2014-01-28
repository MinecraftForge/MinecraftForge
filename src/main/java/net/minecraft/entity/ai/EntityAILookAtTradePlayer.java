package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookAtTradePlayer extends EntityAIWatchClosest
{
    private final EntityVillager theMerchant;
    private static final String __OBFID = "CL_00001593";

    public EntityAILookAtTradePlayer(EntityVillager par1EntityVillager)
    {
        super(par1EntityVillager, EntityPlayer.class, 8.0F);
        this.theMerchant = par1EntityVillager;
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        if (this.theMerchant.isTrading())
        {
            this.closestEntity = this.theMerchant.getCustomer();
            return true;
        }
        else
        {
            return false;
        }
    }
}