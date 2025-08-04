/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_conversion_event_test")
public class LivingConversionEventTest {
    public LivingConversionEventTest() {
        LivingConversionEvent.Pre.BUS.addListener(this::canLivingConversion);
        LivingConversionEvent.Post.BUS.addListener(this::onLivingConversion);
    }

    public boolean canLivingConversion(LivingConversionEvent.Pre event) {
        if (event.getEntity() instanceof Piglin) {
            event.setConversionTimer(0);
            return true;
        }
        return false;
    }

    public void onLivingConversion(LivingConversionEvent.Post event) {
        if (event.getEntity() instanceof Villager)
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.LUCK, 20));
    }
}
