package net.minecraftforge.debug.entity.living;

import java.util.Optional;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_set_attack_target_event_test")
public class LivingSetAttackTargetEventTest
{
    public static final boolean ENABLE = true;

    public LivingSetAttackTargetEventTest()
    {
        if(ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
    {
        LivingEntity originalTarget = event.getTarget();
        
        // Make piglins peaceful when the player holds a stick in their hands.
        // Exception: The player hit the piglin
        if (event.getTarget() instanceof Player player && event.getEntity() instanceof AbstractPiglin piglin
                && player.getMainHandItem().getItem() == Items.STICK && event.getTarget() != event.getEntity().getLastHurtByMob())
        {
            event.setTarget(null);
            
            Optional<LivingEntity> newTarget = event.getCurrentTarget();
            
            // Check if LivingSetAttackTargetEvent#getTarget does not return the same result as before and throw an exception if that is the case.
            // If LivingSetAttackTargetEvent#getCurrentTarget does not return an empty optional, we shall throw an exception as well.
            if(originalTarget != event.getTarget() || newTarget.isPresent())
            {
                throw new RuntimeException("Sanity checks for LivingSetAttackTargetEvent did NOT succeed. This indicates that a bug is present.");
            }
        }
    }
}