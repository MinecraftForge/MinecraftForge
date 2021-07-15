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

package net.minecraftforge.debug.entity.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
/**
 * Tests if the various patches made to utilize values associated with ForgeMod.REACH_DISTANCE && ForgeMod.ATTACK_REACH works
 * The Longsword adds a modifier to the user's attack reach, which is by default 3.0D
 * The final attack reach can in fact be made greater than reach distance,
 * and will work to attack from a distance further than the one you can break blocks from
 */
@Mod(PlayerAttackReachTest.MODID)
public class PlayerAttackReachTest
{
    static final String MODID = "player_attack_reach_test";

    static final float ATTACK_REACH = 4.0F; // Change this value for testing - default attack reach attribute value is 3.0D and ranges from 0.0D-1024.0D

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    static RegistryObject<Item> LONGSWORD = ITEMS.register("longsword", () ->
            new LongswordItem(ItemTier.IRON, 3, -2.4F, ATTACK_REACH, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT))
    );

    public PlayerAttackReachTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }

    static class LongswordItem extends SwordItem
    {
        private final float reach;
        private final Multimap<Attribute, AttributeModifier> defaultModifiers = ArrayListMultimap.create(); // initialize as empty
        protected static final UUID BASE_REACH_DISTANCE_UUID = UUID.fromString("f1797f2f-f02c-44f9-951f-27c9e5446916");

        LongswordItem(IItemTier itemTier, int attackDamageIn, float attackSpeedIn, float reachIn, Properties properties)
        {
            super(itemTier, attackDamageIn, attackSpeedIn, properties);
            this.reach = reachIn;
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlotType)
        {
            if(equipmentSlotType == EquipmentSlotType.MAINHAND){
                if(this.defaultModifiers.isEmpty()){
                    Multimap<Attribute, AttributeModifier> oldAttributeModifiers = super.getDefaultAttributeModifiers(equipmentSlotType);
                    this.defaultModifiers.putAll(oldAttributeModifiers);
                    this.defaultModifiers.put(ForgeMod.ATTACK_REACH.get(), new AttributeModifier(BASE_REACH_DISTANCE_UUID, "Weapon modifier", (double)this.reach, AttributeModifier.Operation.ADDITION));
                }
                return this.defaultModifiers;
            }
            else return super.getDefaultAttributeModifiers(equipmentSlotType);
        }
    }
}
