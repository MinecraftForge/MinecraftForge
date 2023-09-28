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
public class LivingConversionEventTest
{
    public LivingConversionEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::canLivingConversion);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingConversion);
    }

    public void canLivingConversion(LivingConversionEvent.Pre event)
    {
        if (event.getEntity() instanceof Piglin)
        {
            event.setCanceled(true);
            event.setConversionTimer(0);
        }
    }

    public void onLivingConversion(LivingConversionEvent.Post event)
    {
        if (event.getEntity() instanceof Villager)
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.LUCK, 20));
    }
}
