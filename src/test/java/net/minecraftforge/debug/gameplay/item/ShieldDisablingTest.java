/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.item;

import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

import java.util.function.Function;

@Mod(ShieldDisablingTest.MOD_ID)
@GameTestHolder("forge." + ShieldDisablingTest.MOD_ID)
public final class ShieldDisablingTest extends BaseTestMod {
    public static final String MOD_ID = "shield_disabling";

    public ShieldDisablingTest() { }

    @GameTest(template = "forge:empty3x3x3")
    public static void player_shield_disabled_by_axe(GameTestHelper helper) {
        player_shield_disabled_common(helper, h -> Util.make(
            h.spawnWithNoFreeWill(EntityType.HUSK, new BlockPos(2, 0, 2)),
            enemy -> enemy.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE))
        ));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void player_shield_disabled_by_warden(GameTestHelper helper) {
        player_shield_disabled_common(helper, h -> h.spawnWithNoFreeWill(EntityType.WARDEN, new BlockPos(2, 0, 2)));
    }

    private static void player_shield_disabled_common(GameTestHelper helper, Function<GameTestHelper, LivingEntity> enemyGetter) {
        helper.makeFloor();

        // setup player
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.<LivingEntityUseItemEvent.Start>addEventListener(event -> {
            // Artificially pass 5 seconds from start of the shield
            // This is because the first 5 ticks, the player is still vulnerable
            if (event.getEntity() == player) {
                event.setDuration(event.getDuration() - 100);
            }
        });

        // start using shield
        var shield = Items.SHIELD.getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, shield);
        player.startUsingItem(InteractionHand.MAIN_HAND);

        // setup enemy
        var enemy = enemyGetter.apply(helper);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, enemy.position());

        // hit the player
        var attack = helper.registryLookup(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.MOB_ATTACK);
        var damage = new DamageSource(attack, enemy) {
            @Override
            public boolean scalesWithDifficulty() {
                return false;
            }
        };
        player.hurt(damage, 5.0F);

        // shield on cooldown?
        helper.assertTrue(player.getCooldowns().isOnCooldown(shield.getItem()), "shield should be on cooldown");
        helper.succeed();
    }
}
