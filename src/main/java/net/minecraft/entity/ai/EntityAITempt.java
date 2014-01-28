package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityAITempt extends EntityAIBase
{
    // JAVADOC FIELD $$ field_75284_a
    private EntityCreature temptedEntity;
    private double field_75282_b;
    // JAVADOC FIELD $$ field_75283_c
    private double targetX;
    // JAVADOC FIELD $$ field_75280_d
    private double targetY;
    // JAVADOC FIELD $$ field_75281_e
    private double targetZ;
    private double field_75278_f;
    private double field_75279_g;
    // JAVADOC FIELD $$ field_75289_h
    private EntityPlayer temptingPlayer;
    // JAVADOC FIELD $$ field_75290_i
    private int delayTemptCounter;
    // JAVADOC FIELD $$ field_75287_j
    private boolean isRunning;
    private Item field_151484_k;
    // JAVADOC FIELD $$ field_75285_l
    private boolean scaredByPlayerMovement;
    private boolean field_75286_m;
    private static final String __OBFID = "CL_00001616";

    public EntityAITempt(EntityCreature p_i45316_1_, double p_i45316_2_, Item p_i45316_4_, boolean p_i45316_5_)
    {
        this.temptedEntity = p_i45316_1_;
        this.field_75282_b = p_i45316_2_;
        this.field_151484_k = p_i45316_4_;
        this.scaredByPlayerMovement = p_i45316_5_;
        this.setMutexBits(3);
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        if (this.delayTemptCounter > 0)
        {
            --this.delayTemptCounter;
            return false;
        }
        else
        {
            this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0D);

            if (this.temptingPlayer == null)
            {
                return false;
            }
            else
            {
                ItemStack itemstack = this.temptingPlayer.getCurrentEquippedItem();
                return itemstack == null ? false : itemstack.getItem() == this.field_151484_k;
            }
        }
    }

    // JAVADOC METHOD $$ func_75253_b
    public boolean continueExecuting()
    {
        if (this.scaredByPlayerMovement)
        {
            if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0D)
            {
                if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D)
                {
                    return false;
                }

                if (Math.abs((double)this.temptingPlayer.rotationPitch - this.field_75278_f) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.field_75279_g) > 5.0D)
                {
                    return false;
                }
            }
            else
            {
                this.targetX = this.temptingPlayer.posX;
                this.targetY = this.temptingPlayer.posY;
                this.targetZ = this.temptingPlayer.posZ;
            }

            this.field_75278_f = (double)this.temptingPlayer.rotationPitch;
            this.field_75279_g = (double)this.temptingPlayer.rotationYaw;
        }

        return this.shouldExecute();
    }

    // JAVADOC METHOD $$ func_75249_e
    public void startExecuting()
    {
        this.targetX = this.temptingPlayer.posX;
        this.targetY = this.temptingPlayer.posY;
        this.targetZ = this.temptingPlayer.posZ;
        this.isRunning = true;
        this.field_75286_m = this.temptedEntity.getNavigator().getAvoidsWater();
        this.temptedEntity.getNavigator().setAvoidsWater(false);
    }

    // JAVADOC METHOD $$ func_75251_c
    public void resetTask()
    {
        this.temptingPlayer = null;
        this.temptedEntity.getNavigator().clearPathEntity();
        this.delayTemptCounter = 100;
        this.isRunning = false;
        this.temptedEntity.getNavigator().setAvoidsWater(this.field_75286_m);
    }

    // JAVADOC METHOD $$ func_75246_d
    public void updateTask()
    {
        this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float)this.temptedEntity.getVerticalFaceSpeed());

        if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25D)
        {
            this.temptedEntity.getNavigator().clearPathEntity();
        }
        else
        {
            this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.field_75282_b);
        }
    }

    // JAVADOC METHOD $$ func_75277_f
    public boolean isRunning()
    {
        return this.isRunning;
    }
}