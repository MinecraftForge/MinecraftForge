/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BrainBuilder;
import net.minecraftforge.event.entity.living.LivingMakeBrainEvent;
import net.minecraftforge.eventtest.internal.EventTest;
import net.minecraftforge.eventtest.internal.TestHolder;

@TestHolder("LivingMakeBrain")
public class LivingMakeBrainEventTest extends EventTest
{
    public static final boolean ENABLE = true;

    @Override
    public void registerEvents() {
        if(ENABLE)
        {
            MinecraftForge.EVENT_BUS.addListener(this::livingMakeBrainEventListener);
        }
    }

    public void livingMakeBrainEventListener(LivingMakeBrainEvent event)
    {
        if(event.getEntity() instanceof Piglin piglin)
        {
            // lets us test in the Overworld
            piglin.setImmuneToZombification(true);
            // add the ability to swim to Piglins
            BrainBuilder<Piglin> brainBuilder = event.getTypedBrainBuilder(piglin);
            brainBuilder.addBehaviorToActivityByPriority(0, Activity.CORE, new Swim(0.8F));
            this.pass();
        }
    }
}
