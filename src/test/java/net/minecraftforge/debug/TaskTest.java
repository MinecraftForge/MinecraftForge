/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.resources.ResourceLocation;
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
        if (!(event.getTarget() instanceof AbstractFish)) return;
        if (event.getItemStack().getItem() != Items.GOLD_INGOT) return;

        ResourceLocation name = new ResourceLocation(MODID, "fish_yeeter");
        event.getLevel().requestRepeatingTask(name, TickEvent.Phase.END, 20, 10, (task, shared) -> {
            LOGGER.info(shared.repeatCount + " repeat");
            if (shared.repeatCount == 5) task.done();

            shared.clicked.addDeltaMovement(new Vec3(0.0, 0.7, 0.0));
            shared.clicked.hurtMarked = true;
            shared.repeatCount++;

            return shared;
        }, (task, shared) -> {
            LOGGER.info("done");
            if (shared.clicked instanceof Salmon) {
                Vec3 position = shared.clicked.position();
                event.getLevel().explode(event.getTarget(), position.x, position.y, position.z, 4F, Level.ExplosionInteraction.TNT);
            }
        }, new SharedInfo((AbstractFish) event.getTarget()));
    }

    private static final class SharedInfo {
        private int repeatCount = 0;
        private AbstractFish clicked;

        private SharedInfo(AbstractFish clicked) {
            this.clicked = clicked;
        }
    }
}
