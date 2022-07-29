package net.minecraftforge.debug.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
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
        // Make piglins peaceful when the player holds a stick in their hands.
        // Exception: The player hit the piglin
        if (event.isCausedByBehavior() && event.getTarget() instanceof Player player && event.getEntity() instanceof AbstractPiglin piglin
                && player.getMainHandItem().getItem() == Items.STICK && event.getTarget() != event.getEntityLiving().getLastHurtByMob())
        {
            piglin.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, (LivingEntity)null);
        }
    }
}
