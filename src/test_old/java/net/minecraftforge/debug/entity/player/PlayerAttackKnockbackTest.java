/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

/**
 * Tests if the patch to PlayerEntity to make it utilize Attributes.ATTACK_KNOCKBACK works
 * The Knockback Sword adds a modifier to the user's attack knockback
 * The goal of this patch is to allow modders to more easily add equipment or potion effects that affect the attack knockback of players
 * As well as allow command users to modify this attribute for a player
 */
@Mod(PlayerAttackKnockbackTest.MODID)
public class PlayerAttackKnockbackTest {

    static final String MODID = "player_attack_knockback_test";

    static final float ATTACK_KNOCKBACK_VALUE = 2.0F; // Change this value for testing, but note that the attribute is capped at 5.0D

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    static RegistryObject<Item> KNOCKBACK_SWORD = ITEMS.register("knockback_sword", () ->
            new KnockbackSwordItem(Tiers.IRON, 3, -2.4F, ATTACK_KNOCKBACK_VALUE, (new Item.Properties()))
    );

    public PlayerAttackKnockbackTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
            event.accept(KNOCKBACK_SWORD);
    }

    static class KnockbackSwordItem extends SwordItem {
        private final float attackKnockback;
        private final Multimap<Attribute, AttributeModifier> defaultModifiers = ArrayListMultimap.create(); // initialize as empty
        protected static final UUID BASE_ATTACK_KNOCKBACK_UUID = UUID.fromString("01efce91-ab3a-4163-b464-5c7bd1ae5496");

        KnockbackSwordItem(Tier itemTier, int attackDamageIn, float attackSpeedIn, float attackKnockbackIn, Properties properties) {
            super(itemTier, attackDamageIn, attackSpeedIn, properties);
            this.attackKnockback = attackKnockbackIn;
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlotType) {
            if(equipmentSlotType == EquipmentSlot.MAINHAND){
                if(this.defaultModifiers.isEmpty()){
                    Multimap<Attribute, AttributeModifier> oldAttributeModifiers = super.getDefaultAttributeModifiers(equipmentSlotType);
                    this.defaultModifiers.putAll(oldAttributeModifiers);
                    this.defaultModifiers.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_UUID, "Weapon modifier", (double)this.attackKnockback, AttributeModifier.Operation.ADDITION));
                }
                return this.defaultModifiers;
            }
            else return super.getDefaultAttributeModifiers(equipmentSlotType);
        }
    }
}
