package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

public class LivingConsumeAirEvent extends LivingEvent 
{
   private int amount;
   public LivingConsumeAirEvent(LivingEntity entity, int amount)
   {
      super(entity);
      this.amount = amount;
   }

   public int getAmount()
   {
      return amount;
   }

   public void setAmount(int amount)
   {
      this.amount = amount;
   }
}
