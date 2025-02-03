/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.item;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.util.function.Consumer;

@Mod(PreventItemDamageTest.MOD_ID)
@GameTestHolder("forge." + PreventItemDamageTest.MOD_ID)
public class PreventItemDamageTest extends BaseTestMod {
    static final String MOD_ID = "prevent_item_damage";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<FakeShieldItem> FAKE_SHIELD = ITEMS.register("fake_shield", FakeShieldItem::new);

    public PreventItemDamageTest() {
        this.testItem(() -> FAKE_SHIELD.get().getDefaultInstance());
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void player_fake_shield_took_modified_damage(GameTestHelper helper) {
        helper.makeFloor();

        // setup player
        var player = helper.makeMockPlayer(GameType.SURVIVAL);

        // setup shield
        var shield = FAKE_SHIELD.get().getDefaultInstance();

        // setup events
        var firedLivingEntityUseItem = helper.boolFlag("fired LivingEntityUseItemEvent");
        var firedPlayerDestroyItem = helper.boolFlag("fired PlayerDestroyItemEvent");
        helper.<LivingEntityUseItemEvent.Start>addEventListener(event -> {
            if (event.getEntity() != player) return;

            if (event.getItem() != shield)
                helper.fail("Player is using an item, but it's not the fake shield! Check the game test impl.");

            // Artificially pass 5 seconds from start of the shield
            // This is because the first 5 ticks, the player is still vulnerable
            firedLivingEntityUseItem.set(true);
            event.setDuration(event.getDuration() - 100);
        });
        helper.<PlayerDestroyItemEvent>addEventListener(event -> {
            if (event.getEntity() != player) return;

            if (event.getOriginal() != shield)
                helper.fail("Player destroyed an item, but it's not the fake shield! Check the game test impl.");

            firedPlayerDestroyItem.set(true);
        });

        // start using shield
        player.setItemInHand(InteractionHand.MAIN_HAND, shield);
        player.startUsingItem(InteractionHand.MAIN_HAND);
        int initialDamage = shield.getDamageValue();

        // setup enemy
        var enemy = helper.spawnWithNoFreeWill(EntityType.HUSK, new BlockPos(2, 0, 2));
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

        // ok, run the tests
        firedLivingEntityUseItem.assertEquals(true);
        firedPlayerDestroyItem.assertEquals(true);
        helper.assertValueEqual(initialDamage + 1, shield.getDamageValue(), "shield damage value", "Fake shield did not take precisely 1 damage! Check IForgeItem#damageItem.");
        helper.assertValueEqual(player.getItemInHand(InteractionHand.MAIN_HAND), shield, "player shield", "Fake shield was removed from player's hand! Check Player#hurtCurrentlyUsedShield.");
        helper.succeed();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void fake_shield_damage_item_impl(GameTestHelper helper) {
        helper.makeFloor();

        // setup player
        var player = helper.makeMockServerPlayer();

        // setup shield
        var shield = FAKE_SHIELD.get().getDefaultInstance();
        int initialDamage = shield.getDamageValue();

        // test hurt and break
        var damaged = helper.<ServerPlayer>flag("damaged shield");
        shield.hurtAndBreak(1, player, damaged::set);
        damaged.assertEquals(player, "Fake shield was not damaged! Check IForgeItem#damageItem.");
        helper.assertValueEqual(initialDamage + 1, shield.getDamageValue(), "shield damage value", "Fake shield did not take precisely 1 damage! Check IForgeItem#damageItem.");

        // HURT WITHOUT BREAK WAS ADDED IN 1.21.3
        /*
        // test hurt without breaking
        initialDamage = shield.getDamageValue();
        shield.hurtWithoutBreaking(1, player);
        helper.assertValueEqual(initialDamage, shield.getDamageValue(), "shield damage value", "Fake shield took damage even though hurtWithoutBreak test should set damage taken to 0! Check FakeShieldItem or IForgeItem#damageItem.");
        */

        // finished
        helper.succeed();
    }

    private static final class FakeShieldItem extends ShieldItem {
        public FakeShieldItem() {
            super(new Item.Properties().durability(10));
        }

        @Override
        public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
            onBroken.accept(entity);
            return 1;
        }
    }
}
