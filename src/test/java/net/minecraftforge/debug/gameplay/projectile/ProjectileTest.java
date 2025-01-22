/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.projectile;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + ProjectileTest.MOD_ID)
@Mod(ProjectileTest.MOD_ID)
public class ProjectileTest extends BaseTestMod {
    public static final String MOD_ID = "projectile_test";

    public ProjectileTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    /* Test that the arrow skips through and entity and hits a block without damaging the entity */
    @GameTest(template = "forge:empty3x3x3")
    public static void impact_skip_entity(GameTestHelper helper) {
        helper.makeFloor();
        var pig = helper.spawnWithNoFreeWill(EntityType.PIG, new BlockPos(1, 0, 1));
        var arrow = helper.spawn(EntityType.ARROW, new BlockPos(1, 2, 1));
        var hitPig = helper.intFlag("hitPig");
        var hitBlock = helper.intFlag("hitBlock");
        var hitBlockPos = helper.<BlockPos>flag("hitBlockPos");

        Consumer<ProjectileImpactEvent> onImpact = (event) -> {
            if (event.getEntity() != arrow)
                return;
            var hit = event.getRayTraceResult();

            if (hit.getType() == HitResult.Type.ENTITY) {
                var entity = ((EntityHitResult)hit).getEntity();
                if (entity == pig) {
                    event.setImpactResult(ImpactResult.SKIP_ENTITY);
                    hitPig.set(helper.getTick());
                }
            } else if (hit.getType() == HitResult.Type.BLOCK) {
                var blockHit = (BlockHitResult)hit;
                var pos = blockHit.getBlockPos().subtract(helper.absolutePos(new BlockPos(0, 0, 0)));
                hitBlock.set(helper.getTick());
                hitBlockPos.set(pos);
            }
        };

        helper.addEventListener(onImpact);

        helper.runAfterDelay(20, () -> {
            //System.out.println("Pig: " + hitPig.value + " Block: " + hitBlock.value + " BlockPos: " + hitBlockPos.value + " Health: " + pig.getHealth());
            helper.assertValueEqual(pig.getHealth(), pig.getMaxHealth(), "Pig Health");
            hitPig.assertEquals(6);
            hitBlock.assertEquals(10);
            hitBlockPos.assertEquals(new BlockPos(1, -1, 1));
            helper.succeed();
        });
    }

    /* Test that the arrow stops at the first entity even if it has piercing, and damages the entity */
    @GameTest(template = "forge:empty3x3x3")
    public static void impact_stop(GameTestHelper helper) {
        helper.makeFloor();
        var arrow = helper.spawn(EntityType.ARROW, new BlockPos(1, 2, 1));
        arrow.setPierceLevel((byte)1);
        var pig = helper.spawnWithNoFreeWill(EntityType.PIG, new BlockPos(1, 0, 1));
        pig.setNoGravity(true);
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, new BlockPos(1, -1, 1));
        cow.setNoGravity(true);

        var hitPig = helper.intFlag("hitPig");
        var hitCow = helper.intFlag("hitCow");
        var hitBlock = helper.intFlag("hitBlock");

        Consumer<ProjectileImpactEvent> onImpact = (event) -> {
            if (event.getEntity() != arrow)
                return;
            var hit = event.getRayTraceResult();

            if (hit.getType() == HitResult.Type.ENTITY) {
                var entity = ((EntityHitResult)hit).getEntity();
                if (entity == pig) {
                    event.setImpactResult(ImpactResult.STOP_AT_CURRENT);
                    hitPig.set(helper.getTick());
                } else if (entity == cow) {
                    hitCow.set(helper.getTick());
                }
            } else if (hit.getType() == HitResult.Type.BLOCK) {
                hitBlock.set(helper.getTick());
            }
        };

        helper.addEventListener(onImpact);

        helper.runAfterDelay(20, () -> {
            //System.out.println("Pig: " + hitPig.get() + " Cow: " + hitCow.get() + " Block: " + hitBlock.get() + " Health: " + pig.getHealth());
            helper.assertValueEqual(pig.getHealth(), 9.0F, "Pig Health");
            hitPig.assertEquals(6);
            hitCow.assertUnset();
            hitBlock.assertUnset();
            helper.succeed();
        });
    }

    /* Test that the arrow stops at the first entity even if it has piercing, without damaging the entity */
    @GameTest(template = "forge:empty3x3x3")
    public static void impact_stop_no_damage(GameTestHelper helper) {
        helper.makeFloor();
        var arrow = helper.spawn(EntityType.ARROW, new BlockPos(1, 2, 1));
        arrow.setPierceLevel((byte)1);
        var pig = helper.spawnWithNoFreeWill(EntityType.PIG, new BlockPos(1, 0, 1));
        pig.setNoGravity(true);
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, new BlockPos(1, -1, 1));
        cow.setNoGravity(true);

        var hitPig = helper.intFlag("hitPig");
        var hitCow = helper.intFlag("hitCow");
        var hitBlock = helper.intFlag("hitBlock");

        Consumer<ProjectileImpactEvent> onImpact = (event) -> {
            if (event.getEntity() != arrow)
                return;
            var hit = event.getRayTraceResult();

            if (hit.getType() == HitResult.Type.ENTITY) {
                var entity = ((EntityHitResult)hit).getEntity();
                if (entity == pig) {
                    event.setImpactResult(ImpactResult.STOP_AT_CURRENT_NO_DAMAGE);
                    hitPig.set(helper.getTick());
                } else if (entity == cow) {
                    hitCow.set(helper.getTick());
                }
            } else if (hit.getType() == HitResult.Type.BLOCK) {
                hitBlock.set(helper.getTick());
            }
        };

        helper.addEventListener(onImpact);

        helper.runAfterDelay(20, () -> {
            //System.out.println("Pig: " + hitPig.get() + " Cow: " + hitCow.get() + " Block: " + hitBlock.get() + " Health: " + pig.getHealth());
            helper.assertValueEqual(pig.getHealth(), pig.getMaxHealth(), "Pig Health");
            hitPig.assertEquals(6);
            hitCow.assertUnset();
            hitBlock.assertUnset();
            helper.succeed();
        });
    }
}