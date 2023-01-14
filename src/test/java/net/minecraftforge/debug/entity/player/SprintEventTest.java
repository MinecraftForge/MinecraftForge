/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntitySprintEvent;
import net.minecraftforge.client.event.ShouldSprint;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(SprintEventTest.MODID)
@Mod.EventBusSubscriber
public class SprintEventTest {
    public static final String MODID = "sprint_event_test";
    @SubscribeEvent
    public static void onPlayerSprint(ShouldSprint event) {
        Item item = event.getEntity().getMainHandItem().getItem();
        if (item == Items.OBSIDIAN) {
            if (!event.willSprint) return;
            event.setCanceled(true); // Disallow running
        }
        if (item == Items.FEATHER) {
            if (event.willSprint) return;
            event.setCanceled(true); // Forcefully keep running state
        }
    }

    @SubscribeEvent
    public static void onEntitySprint(EntitySprintEvent event) {
        if (event.getEntity() instanceof Ocelot ocelot && !ocelot.level.isClientSide && ocelot.getActiveEffects().isEmpty()) {
            ocelot.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 4)); // Makes ocelots run incredibly fast
        }
    }
}
