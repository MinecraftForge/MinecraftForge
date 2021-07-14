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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
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
    public static final boolean ENABLE = true;

    static final boolean ENABLE_INTRINSIC_BOOST_FOR_EFFECT = true;
    static final boolean ENABLE_INTRINSIC_BOOST_FOR_ARMOR = true;

    static final double FLIGHT_ARMOR_MODIFIER_VALUE_ADDITION = 1.0D;
    static final double FLIGHT_EFFECT_MODIFIER_VALUE_ADDITION = 1.0D;
    static final double FLIGHT_ENCHANTMENT_MODIFIER_VALUE_ADDITION = 1.0D;

    static final double FLIGHT_SPEED_ARMOR_MODIFIER_VALUE_ADDITION = 1.0D;
    static final double FLIGHT_SPEED_EFFECT_MODIFIER_VALUE_ADDITION = 1.0D;
    static final double FLIGHT_SPEED_ENCHANTMENT_MODIFIER_VALUE_ADDITION = 1.0D;

    static final int FLIGHT_POTION_EFFECT_DURATION = 180 * 20;

    // Deferred Registers
    private static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, MOD_ID);
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // Registry Utilities
    private static final String FLIGHT_EFFECT_MODIFIER_UUID_STRING = "53992b1d-4f7c-4479-a00b-f2fdf57dfcdf";
    private static final String NO_FLIGHT_EFFECT_MODIFIER_UUID_STRING = "333764b4-2106-49b1-94fd-2362cb6e2d79";
    private static final String ABSOLUTELY_NO_FLIGHT_EFFECT_MODIFIER_UUID_STRING = "ee75a567-e301-4aa2-a2cd-dd1eca06094d";

    private static final String FLIGHT_SPEED_EFFECT_MODIFIER_UUID_STRING = "41fddd0b-1ff1-4f24-87a9-b3f23e420187";

    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    // Effects
    private static final RegistryObject<Effect> FLIGHT_EFFECT = EFFECTS.register("flight",
            () -> new FallFlightEffect(EffectType.BENEFICIAL, 0x0000FF)
                    .addAttributeModifier(ForgeMod.FALL_FLIGHT.get(), FLIGHT_EFFECT_MODIFIER_UUID_STRING, FLIGHT_EFFECT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION)
    );

    private static final RegistryObject<Effect> NO_FLIGHT_EFFECT = EFFECTS.register("no_flight",
            () -> new FallFlightEffect(EffectType.HARMFUL, 0xFF0000)
                    .addAttributeModifier(ForgeMod.FALL_FLIGHT.get(), NO_FLIGHT_EFFECT_MODIFIER_UUID_STRING, -FLIGHT_EFFECT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION)
    );

    private static final RegistryObject<Effect> ABSOLUTELY_NO_FLIGHT_EFFECT = EFFECTS.register("absolutely_no_flight",
            () -> new FallFlightEffect(EffectType.HARMFUL, 0xFF0000)
                    .addAttributeModifier(ForgeMod.FALL_FLIGHT.get(), ABSOLUTELY_NO_FLIGHT_EFFECT_MODIFIER_UUID_STRING, -FLIGHT_EFFECT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );

    // Potions
    private static final RegistryObject<Potion> FLIGHT_POTION = POTIONS.register("flight",
            () -> new Potion(new EffectInstance(FLIGHT_EFFECT.get(), FLIGHT_POTION_EFFECT_DURATION))
    );

    private static final RegistryObject<Potion> NO_FLIGHT_POTION = POTIONS.register("no_flight",
            () -> new Potion(new EffectInstance(NO_FLIGHT_EFFECT.get(), FLIGHT_POTION_EFFECT_DURATION))
    );

    private static final RegistryObject<Potion> ABSOLUTELY_NO_FLIGHT_POTION = POTIONS.register("absolutely_no_flight",
            () -> new Potion(new EffectInstance(ABSOLUTELY_NO_FLIGHT_EFFECT.get(), FLIGHT_POTION_EFFECT_DURATION))
    );

    // Enchantments
    private static final RegistryObject<Enchantment> FLIGHT_ENCHANTMENT = ENCHANTMENTS.register("flight",
            () -> new FlightEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR, ARMOR_SLOTS)
    );

    private static final RegistryObject<Enchantment> FLIGHT_SPEED_ENCHANTMENT = ENCHANTMENTS.register("flight_speed",
            () -> new FlightEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR, ARMOR_SLOTS)
    );

    // Items
    private static final RegistryObject<Item> FLIGHT_BOOTS = ITEMS.register("flight_boots",
            () -> new FlightArmorItem(ArmorMaterial.IRON, EquipmentSlotType.FEET, true, false, FLIGHT_SPEED_ARMOR_MODIFIER_VALUE_ADDITION, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

    private static final RegistryObject<Item> NO_FLIGHT_LEGGINGS = ITEMS.register("no_flight_leggings",
            () -> new FlightArmorItem(ArmorMaterial.IRON, EquipmentSlotType.LEGS, false, false,0, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

    private static final RegistryObject<Item> ABSOLUTELY_NO_FLIGHT_HELMET = ITEMS.register("absolutely_no_flight_helmet",
            () -> new FlightArmorItem(ArmorMaterial.IRON, EquipmentSlotType.HEAD, false, true, 0, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

    public ExpandedElytraFlightTest()
    {
        if(ENABLE){
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            EFFECTS.register(modEventBus);
            POTIONS.register(modEventBus);
            ENCHANTMENTS.register(modEventBus);
            ITEMS.register(modEventBus);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        LivingEntity living = event.getEntityLiving();
        if(living.level.isClientSide) return;

        int flightEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(FLIGHT_ENCHANTMENT.get(), living);

        ModifiableAttributeInstance fallFlyingAttribute = living.getAttribute(ForgeMod.FALL_FLIGHT.get());
        if (fallFlyingAttribute != null)
        {
            if (fallFlyingAttribute.getModifier(FlightEnchantment.FLIGHT_ENCHANTMENT_MODIFIER_UUID) != null)
            {
                fallFlyingAttribute.removeModifier(FlightEnchantment.FLIGHT_ENCHANTMENT_MODIFIER_UUID);
            }
            if(flightEnchantmentLevel > 0)
            {
                fallFlyingAttribute.addTransientModifier(FlightEnchantment.ELYTRA_ENCHANTMENT_MODIFIER);
            }
        }

        int flightSpeedEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(FLIGHT_SPEED_ENCHANTMENT.get(), living);
        ModifiableAttributeInstance fallFlyingSpeedAttribute = living.getAttribute(ForgeMod.FALL_FLYING_SPEED.get());
        if (fallFlyingSpeedAttribute != null)
        {
            if (fallFlyingSpeedAttribute.getModifier(FlightEnchantment.FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID) != null)
            {
                fallFlyingSpeedAttribute.removeModifier(FlightEnchantment.FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID);
            }
            if(flightSpeedEnchantmentLevel > 0)
            {
                fallFlyingSpeedAttribute.addTransientModifier(FlightEnchantment.ELYTRA_SPEED_ENCHANTMENT_MODIFIER);
            }
        }
    }

    static class FallFlightEffect extends Effect
    {
        public FallFlightEffect(EffectType effectType, int color)
        {
            super(effectType, color);
            if(ENABLE_INTRINSIC_BOOST_FOR_EFFECT && effectType == EffectType.BENEFICIAL)
            {
                this.addAttributeModifier(ForgeMod.FALL_FLYING_SPEED.get(), FLIGHT_SPEED_EFFECT_MODIFIER_UUID_STRING, FLIGHT_SPEED_EFFECT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION);
            }
        }

    }

    static class FlightEnchantment extends Enchantment
    {
        private static final UUID FLIGHT_ENCHANTMENT_MODIFIER_UUID = UUID.fromString("f0d64484-f532-4786-963f-e9456ba20d41");
        private static final AttributeModifier ELYTRA_ENCHANTMENT_MODIFIER = new AttributeModifier(FLIGHT_ENCHANTMENT_MODIFIER_UUID, "Flight Enchantment", FLIGHT_ENCHANTMENT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION);


        private static final UUID FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID = UUID.fromString("80135ea1-998a-44ec-a60b-a685adc52b4e");
        private static final AttributeModifier ELYTRA_SPEED_ENCHANTMENT_MODIFIER = new AttributeModifier(FLIGHT_SPEED_ENCHANTMENT_MODIFIER_UUID, "Flight Speed Enchantment", FLIGHT_SPEED_ENCHANTMENT_MODIFIER_VALUE_ADDITION, AttributeModifier.Operation.ADDITION);

        protected FlightEnchantment(Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] slotTypes)
        {
            super(rarity, enchantmentType, slotTypes);
        }
    }

    static class FlightArmorItem extends ArmorItem
    {
        private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
        private final Multimap<Attribute, AttributeModifier> defaultModifiers = ArrayListMultimap.create(); // initialize as empty
        private final boolean canFly;
        private final boolean absolute;
        private final double flySpeed;
        public FlightArmorItem(IArmorMaterial armorMaterial, EquipmentSlotType slotType, boolean canFly, boolean absolute, double flySpeedIn, Properties properties) {
            super(armorMaterial, slotType, properties);
            this.canFly = canFly;
            this.absolute = absolute;
            this.flySpeed = flySpeedIn;
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlotType)
        {
            if(equipmentSlotType == this.slot){
                if(this.defaultModifiers.isEmpty()){
                    Multimap<Attribute, AttributeModifier> oldAttributeModifiers = super.getDefaultAttributeModifiers(equipmentSlotType);
                    this.defaultModifiers.putAll(oldAttributeModifiers);
                    UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlotType.getIndex()];
                    this.defaultModifiers.put(ForgeMod.FALL_FLIGHT.get(),
                            new AttributeModifier(uuid, "Armor fall flight modifier",
                                    this.canFly ? FLIGHT_ARMOR_MODIFIER_VALUE_ADDITION : -FLIGHT_ARMOR_MODIFIER_VALUE_ADDITION,
                                    this.absolute ? AttributeModifier.Operation.MULTIPLY_TOTAL : AttributeModifier.Operation.ADDITION));
                    if(ENABLE_INTRINSIC_BOOST_FOR_ARMOR && this.canFly && this.flySpeed > 0)
                    {
                        this.defaultModifiers.put(ForgeMod.FALL_FLYING_SPEED.get(), new AttributeModifier(uuid, "Armor fall flight speed modifier", this.flySpeed, AttributeModifier.Operation.ADDITION));
                    }
                }
                return this.defaultModifiers;
            }
            else return super.getDefaultAttributeModifiers(equipmentSlotType);
        }
    }
}
