/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.gameplay.item;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.GameType;
import net.minecraftsingularity.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftsingularity.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.test.BaseTestMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@GameTestNamespace("singularity")
@Mod(PreventItemDamageTest.MOD_ID)
public class PreventItemDamageTest extends BaseTestMod {
    static final String MOD_ID = "prevent_item_damage";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<FakeShieldItem> FAKE_SHIELD = ITEMS.register("fake_shield", FakeShieldItem::new);

    public PreventItemDamageTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        this.testItem(lookup -> FAKE_SHIELD.get().getDefaultInstance());
    }

    @GameTest
    public static void player_fake_shield_took_modified_damage(GameTestHelper helper) {
        helper.makeFloor();

        // setup player
        var player = helper.makeMockPlayer(GameType.SURVIVAL);

        // setup shield
        var shield = FAKE_SHIELD.get().getDefaultInstance();

        // setup events
        var firedLivingEntityUseItem = helper.boolFlag("fired LivingEntityUseItemEvent");
        var firedPlayerDestroyItem = helper.boolFlag("fired PlayerDestroyItemEvent");
        helper.addEventListener(LivingEntityUseItemEvent.Start.BUS, event -> {
            if (event.getEntity() != player) return;

            helper.assertTrue(event.getItem() == shield, () -> "Player is using an item, but it's not the fake shield! Check the game test impl.");

            // Artificially pass 5 seconds from start of the shield
            // This is because the first 5 ticks, the player is still vulnerable
            firedLivingEntityUseItem.set(true);
            event.setDuration(event.getDuration() - 100);
        });
        helper.addRecordListener(PlayerDestroyItemEvent.BUS, event -> {
            if (event.getEntity() != player) return;

            helper.assertTrue(event.getOriginal() == shield, () -> "Player destroyed an item, but it's not the fake shield! Check the game test impl.");

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
        player.hurtServer(helper.getLevel(), damage, 5.0F);

        // ok, run the tests
        firedLivingEntityUseItem.assertEquals(true);
        firedPlayerDestroyItem.assertEquals(true);
        helper.assertValueEqual(initialDamage + 1, shield.getDamageValue(), "shield damage value", "Fake shield did not take precisely 1 damage! Check IForgeItem#damageItem.");
        helper.assertValueEqual(player.getItemInHand(InteractionHand.MAIN_HAND), shield, "player shield", "Fake shield was removed from player's hand! Check Player#hurtCurrentlyUsedShield.");
        helper.assertValueNotEqual(player.getUseItem(), shield, "player use item", "Player should not be using the shield! The onBreak callback was never invoked. Check FakeShieldItem or IForgeItem#damageItem.");
        helper.succeed();
    }

    @GameTest
    public static void fake_shield_damage_item_impl(GameTestHelper helper) {
        helper.makeFloor();

        // setup player
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        // setup shield
        var shield = FAKE_SHIELD.get().getDefaultInstance();
        int initialDamage = shield.getDamageValue();

        // test hurt and break
        var damaged = helper.<Item>flag("damaged shield");
        shield.hurtAndBreak(1, helper.getLevel(), player, damaged::set);
        damaged.assertEquals(FAKE_SHIELD.get(), "Fake shield was not damaged! Check IForgeItem#damageItem.");
        helper.assertValueEqual(initialDamage + 1, shield.getDamageValue(), "shield damage value", "Fake shield did not take precisely 1 damage! Check IForgeItem#damageItem.");

        // test hurt without breaking
        initialDamage = shield.getDamageValue();
        shield.hurtWithoutBreaking(1, player);
        helper.assertValueEqual(initialDamage, shield.getDamageValue(), "shield damage value", "Fake shield took damage even though hurtWithoutBreak test should set damage taken to 0! Check FakeShieldItem or IForgeItem#damageItem.");

        // finished
        helper.succeed();
    }

    private static final class FakeShieldItem extends ShieldItem {
        public FakeShieldItem() {
            super(new Item.Properties()
                .setId(ITEMS.key("fake_shield"))
                .durability(10)
                .equippableUnswappable(EquipmentSlot.OFFHAND)
                .delayedComponent(
                    DataComponents.BLOCKS_ATTACKS,
                    context -> new BlocksAttacks(
                        0F,
                        0F,
                        List.of(new BlocksAttacks.DamageReduction(360.0F, Optional.empty(), 0.0F, 1.0F)),
                        new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                        Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                        Optional.of(SoundEvents.SHIELD_BLOCK),
                        Optional.of(SoundEvents.SHIELD_BREAK)
                    )
                )
            );
        }

        @Override
        public int damageItem(ItemStack stack, int damage, ServerLevel level, @Nullable ServerPlayer player, boolean canBreak, Consumer<Item> onBroken) {
            if (canBreak) {
                onBroken.accept(this);
                return 1;
            }

            return 0;
        }
    }
}
