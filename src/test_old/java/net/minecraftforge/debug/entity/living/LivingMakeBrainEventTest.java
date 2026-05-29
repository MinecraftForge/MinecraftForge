/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.entity.living;

import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.common.util.BrainBuilder;
import net.minecraftsingularity.event.entity.living.LivingMakeBrainEvent;
import net.minecraftsingularity.eventtest.internal.EventTest;
import net.minecraftsingularity.eventtest.internal.TestHolder;

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
