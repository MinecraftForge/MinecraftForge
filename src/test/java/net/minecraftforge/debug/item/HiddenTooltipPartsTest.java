/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Mod(HiddenTooltipPartsTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = HiddenTooltipPartsTest.MOD_ID)
public class HiddenTooltipPartsTest
{
    public static final String MOD_ID = "hidden_tooltip_parts";
    public static final boolean ENABLED = true;
    private static final AttributeModifier MODIFIER = new AttributeModifier(MOD_ID, 10f, Operation.ADDITION);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item", () -> new TestItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public HiddenTooltipPartsTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    static class TestItem extends Item
    {

        public TestItem(Properties properties)
        {
            super(properties);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
        {
            return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                    .put(Attributes.ARMOR, MODIFIER)
                    .build();
        }

        @NotNull
        @Override
        public Set<ItemStack.TooltipPart> getHiddenTooltipParts(@NotNull ItemStack stack)
        {
            return Set.of(ItemStack.TooltipPart.MODIFIERS);
        }
    }
}