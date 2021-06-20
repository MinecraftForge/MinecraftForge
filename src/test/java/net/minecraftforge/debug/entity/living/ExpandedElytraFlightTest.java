/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod(ExpandedElytraFlightTest.MOD_ID)
public class ExpandedElytraFlightTest
{
    public static final String MOD_ID = "expanded_elytra_flight_test";
    public static final boolean ENABLE_INTRINSIC_BOOST_FOR_EFFECT = true;
    public static final double FLIGHT_EFFECT_MODIFIER_VALUE_ADDITION = 1.0D;
    public static final double FLIGHT_ENCHANTMENT_MODIFIER_VALUE_ADDITION = 1.0D;
    public static final double FLIGHT_SPEED_EFFECT_MODIFIER_VALUE_ADDITION = 1.0D;
    public static final double FLIGHT_SPEED_ENCHANTMENT_MODIFIER_VALUE_ADDITION = 1.0D;

    // Deferred Registers
    private static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, MOD_ID);
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);

    // Registry Utilities
    private static final String FLIGHT_EFFECT_MODIFIER_UUID_STRING = "53992b1d-4f7c-4479-a00b-f2fdf57dfcdf";
    private static final String FLIGHT_SPEED_EFFECT_MODIFIER_UUID_STRING = "41fddd0b-1ff1-4f24-87a9-b3f23e420187";
    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    // Effects
    private static final RegistryObject<Effect> FLIGHT_EFFECT = EFFECTS.register("flight_effect",
            () -> new FallFlightEffect(EffectType.BENEFICIAL, 0x0000FF)
                    .addAttributeModifier(ForgeMod.FALL_FLIGHT.get(), FLIGHT_EFFECT_MODIFIER_UUID_STRING, FLIGHT_EFFECT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION)
    );

    // Potions
    private static final RegistryObject<Potion> FLIGHT_POTION = POTIONS.register("flight_potion", () -> new Potion(new EffectInstance(FLIGHT_EFFECT.get(), 180 * 20)));

    // Enchantments
    private static final RegistryObject<Enchantment> FLIGHT_ENCHANTMENT = ENCHANTMENTS.register("flight",
            () -> new FlightEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR, ARMOR_SLOTS)
    );

    private static final RegistryObject<Enchantment> FLIGHT_SPEED_ENCHANTMENT = ENCHANTMENTS.register("flight_speed",
            () -> new FlightEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR, ARMOR_SLOTS)
    );

    public ExpandedElytraFlightTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EFFECTS.register(modEventBus);
        POTIONS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
    }

    public static class FallFlightEffect extends Effect
    {
        public FallFlightEffect(EffectType effectType, int color)
        {
            super(effectType, color);
            if(ENABLE_INTRINSIC_BOOST_FOR_EFFECT)
            {
                this.addAttributeModifier(ForgeMod.FALL_FLYING_SPEED.get(), FLIGHT_SPEED_EFFECT_MODIFIER_UUID_STRING, FLIGHT_SPEED_EFFECT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION);
            }
        }

    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class FlightEnchantment extends Enchantment
    {
        private static final UUID FLIGHT_ENCHANTMENT_MODIFIER_UUID = UUID.fromString("f0d64484-f532-4786-963f-e9456ba20d41");
        private static final AttributeModifier ELYTRA_ENCHANTMENT_MODIFIER = new AttributeModifier(FLIGHT_ENCHANTMENT_MODIFIER_UUID, "Flight Enchantment", FLIGHT_ENCHANTMENT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION);


        private static final UUID FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID = UUID.fromString("80135ea1-998a-44ec-a60b-a685adc52b4e");
        private static final AttributeModifier ELYTRA_SPEED_ENCHANTMENT_MODIFIER = new AttributeModifier(FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID, "Flight Speed Enchantment", FLIGHT_SPEED_ENCHANTMENT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION);

        protected FlightEnchantment(Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] slotTypes)
        {
            super(rarity, enchantmentType, slotTypes);
        }

        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
        {
            LivingEntity living = event.getEntityLiving();
            if(living.level.isClientSide) return;

            int elytraEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(FLIGHT_ENCHANTMENT.get(), living);

            ModifiableAttributeInstance fallFlyingAttribute = living.getAttribute(ForgeMod.FALL_FLIGHT.get());
            if (fallFlyingAttribute != null)
            {
                if (fallFlyingAttribute.getModifier(FLIGHT_ENCHANTMENT_MODIFIER_UUID) != null)
                {
                    fallFlyingAttribute.removeModifier(FLIGHT_ENCHANTMENT_MODIFIER_UUID);
                }
                if(elytraEnchantmentLevel > 0)
                {
                    fallFlyingAttribute.addTransientModifier(ELYTRA_ENCHANTMENT_MODIFIER);
                }
            }

            int elytraSpeedEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(FLIGHT_SPEED_ENCHANTMENT.get(), living);
            ModifiableAttributeInstance fallFlyingSpeedAttribute = living.getAttribute(ForgeMod.FALL_FLYING_SPEED.get());
            if (fallFlyingSpeedAttribute != null)
            {
                if (fallFlyingSpeedAttribute.getModifier(FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID) != null)
                {
                    fallFlyingSpeedAttribute.removeModifier(FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID);
                }
                if(elytraSpeedEnchantmentLevel > 0)
                {
                    fallFlyingSpeedAttribute.addTransientModifier(ELYTRA_SPEED_ENCHANTMENT_MODIFIER);
                }
            }
        }
    }
}
