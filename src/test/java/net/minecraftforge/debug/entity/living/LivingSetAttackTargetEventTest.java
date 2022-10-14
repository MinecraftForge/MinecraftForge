/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
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
        // Make piglins die when they start attacking a player who is not holding a stick in their main hand.
        if (event.getTarget() instanceof Player player && event.getEntity() instanceof AbstractPiglin piglin && player.getMainHandItem().getItem() != Items.STICK)
        {
            event.getEntity().kill();
        }
    }

    @SubscribeEvent
    public void onLivingChangeTargetEvent(LivingChangeTargetEvent event)
    {
        // Prevents the piglin from attacking the player if they hold a stick in their hands.
        if(event.getNewTarget() instanceof Player player && event.getEntity() instanceof AbstractPiglin piglin && player.getMainHandItem().getItem() == Items.STICK)
        {
            event.setCanceled(true);
        }
    }
}
