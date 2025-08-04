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
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_set_attack_target_event_test")
public final class LivingSetAttackTargetEventTest {
    public static final boolean ENABLE = true;

    public LivingSetAttackTargetEventTest() {
        if (ENABLE) {
            LivingChangeTargetEvent.BUS.addListener(this::onLivingChangeTargetEvent);
        }
    }

    public boolean onLivingChangeTargetEvent(LivingChangeTargetEvent event) {
        // Prevents the piglin from attacking the player if they hold a stick in their hands.
        if (event.getNewTarget() instanceof Player player && event.getEntity() instanceof AbstractPiglin piglin && player.getMainHandItem().getItem() == Items.STICK) {
            return true;
        }
        return false;
    }
}
