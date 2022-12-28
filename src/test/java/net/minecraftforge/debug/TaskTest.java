/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TaskTest.MODID)
public class TaskTest {
    public static final String MODID = "task_scheduler_test";
    private static Logger LOGGER = LogManager.getLogger(PotionEventTest.class);

    public TaskTest() {
        MinecraftForge.EVENT_BUS.addListener(TaskTest::yeetFish);
    }

    private static void yeetFish(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) return;
        if (!(event.getTarget() instanceof AbstractFish fish)) return;
        if (event.getItemStack().getItem() != Items.GOLD_INGOT) return;

        int gameTime = ServerLifecycleHooks.getCurrentServer().getTickCount();
        TickTask task = new TickTask(gameTime, () -> fish.addDeltaMovement(new Vec3(0.0, 1.0, 0.0))) {
            int counter = 5;

            @Override
            public boolean shouldRun() {
                counter--;
                return counter != 0;
            }
        };
    }
}
