/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
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
        if (!(event.getTarget() instanceof AbstractFish clicked)) return;
        if (event.getItemStack().getItem() != Items.GOLD_INGOT) return;
        ServerLevel level = (ServerLevel) event.getLevel();

        ResourceLocation name = new ResourceLocation(MODID, "fish_yeeter");
        event.getLevel().requestRepeatingTask(name, 1 + 20, 10, (task, shared) -> {
            LOGGER.info("count: " + level.getServer().getTickCount());
            if (shared[0] == 10) {
                LOGGER.info("done");
                Vec3 position = clicked.position();
                event.getLevel().explode(event.getTarget(), position.x, position.y, position.z, 4F, Level.ExplosionInteraction.TNT);
                task.cancel();
            } else {
                shared[0]++;
                LOGGER.info("shared: " + shared[0]);
            }
        }, new int[] {0});
    }
}
