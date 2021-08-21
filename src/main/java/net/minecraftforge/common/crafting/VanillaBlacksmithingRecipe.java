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

package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents the vanilla anvil behavior
 */
public record VanillaBlacksmithingRecipe(ResourceLocation id) implements IBlacksmithingRecipe
{
    @Override
    public boolean matches(final ContainerWrapper container, final Level p_44003_)
    {
        ItemStack itemstack = container.getItem(0);
        ItemStack itemstack2 = container.getItem(1);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
        int i = 0;
        int j = itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
        int k = 0;
        boolean flag = false;

        if (!itemstack2.isEmpty())
        {
            if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(container.getMenu(), itemstack, itemstack2, container.getResultSlots(), container.getItemName(), j, container.getPlayer()))
            {
                return false;
            }
            flag = itemstack2.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
            if (itemstack.isDamageableItem() && itemstack.getItem().isValidRepairItem(itemstack, itemstack2))
            {
                int l2 = Math.min(itemstack.getDamageValue(), itemstack.getMaxDamage() / 4);
                if (l2 <= 0)
                {
                    return false;
                }
            }
            else
            {
                if (!flag && (!itemstack.is(itemstack2.getItem()) || !itemstack.isDamageableItem()))
                {
                    return false;
                }

                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                boolean flag2 = false;
                boolean flag3 = false;

                for (Enchantment enchantment1 : map1.keySet())
                {
                    if (enchantment1 != null)
                    {
                        int i2 = map.getOrDefault(enchantment1, 0);
                        int j2 = map1.get(enchantment1);
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = enchantment1.canEnchant(itemstack);
                        if (container.getPlayer().getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK))
                        {
                            flag1 = true;
                        }

                        for (Enchantment enchantment : map.keySet())
                        {
                            if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment))
                            {
                                flag1 = false;
                                ++i;
                            }
                        }

                        if (!flag1)
                        {
                            flag3 = true;
                        }
                        else
                        {
                            flag2 = true;
                            if (j2 > enchantment1.getMaxLevel())
                            {
                                j2 = enchantment1.getMaxLevel();
                            }

                            map.put(enchantment1, j2);
                            int k3 = 0;
                            switch (enchantment1.getRarity())
                            {
                                case COMMON:
                                    k3 = 1;
                                    break;
                                case UNCOMMON:
                                    k3 = 2;
                                    break;
                                case RARE:
                                    k3 = 4;
                                    break;
                                case VERY_RARE:
                                    k3 = 8;
                            }

                            if (flag)
                            {
                                k3 = Math.max(1, k3 / 2);
                            }

                            i += k3 * j2;
                            if (itemstack.getCount() > 1)
                            {
                                i = 40;
                            }
                        }
                    }
                }

                if (flag3 && !flag2)
                {
                    return false;
                }
            }
        }

        if (StringUtils.isBlank(container.getItemName()))
        {
            if (itemstack.hasCustomHoverName())
            {
                k = 1;
                i += k;
            }
        }
        else if (!container.getItemName().equals(itemstack.getHoverName().getString()))
        {
            k = 1;
            i += k;
        }
        if (flag && !itemstack.isBookEnchantable(itemstack2))
        {
            return false;
        }

        int cost = j + i;
        if (i <= 0)
        {
            return false;
        }

        if (k == i && cost >= 40)
        {
            cost = 39;
        }

        return cost < 40 || container.getPlayer().getAbilities().instabuild;
    }

    @Override
    public ItemStack assemble(final ContainerWrapper container)
    {
        ItemStack itemstack = container.getItem(0);
        int i = 0;
        int j = 0;
        int k = 0;
        ItemStack itemstack1 = itemstack.copy();
        ItemStack itemstack2 = container.getItem(1);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
        j = j + itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
        container.setItemCost(1, 0);
        boolean flag = false;

        if (!itemstack2.isEmpty())
        {
            flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
            if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2))
            {
                int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                if (l2 <= 0)
                {
                    return ItemStack.EMPTY;
                }

                int i3;
                for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3)
                {
                    int j3 = itemstack1.getDamageValue() - l2;
                    itemstack1.setDamageValue(j3);
                    ++i;
                    l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                }

                container.setItemCost(1, i3);
            }
            else
            {
                if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem()))
                {
                    return ItemStack.EMPTY;
                }

                if (itemstack1.isDamageableItem() && !flag)
                {
                    int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                    int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                    int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                    int k1 = l + j1;
                    int l1 = itemstack1.getMaxDamage() - k1;
                    if (l1 < 0)
                    {
                        l1 = 0;
                    }

                    if (l1 < itemstack1.getDamageValue())
                    {
                        itemstack1.setDamageValue(l1);
                        i += 2;
                    }
                }

                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                boolean flag2 = false;
                boolean flag3 = false;

                for (Enchantment enchantment1 : map1.keySet())
                {
                    if (enchantment1 != null)
                    {
                        int i2 = map.getOrDefault(enchantment1, 0);
                        int j2 = map1.get(enchantment1);
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = enchantment1.canEnchant(itemstack);
                        if (container.getPlayer().getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK))
                        {
                            flag1 = true;
                        }

                        for (Enchantment enchantment : map.keySet())
                        {
                            if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment))
                            {
                                flag1 = false;
                                ++i;
                            }
                        }

                        if (!flag1)
                        {
                            flag3 = true;
                        }
                        else
                        {
                            flag2 = true;
                            if (j2 > enchantment1.getMaxLevel())
                            {
                                j2 = enchantment1.getMaxLevel();
                            }

                            map.put(enchantment1, j2);
                            int k3 = switch (enchantment1.getRarity())
                                    {
                                        case COMMON -> 1;
                                        case UNCOMMON -> 2;
                                        case RARE -> 4;
                                        case VERY_RARE -> 8;
                                    };

                            if (flag)
                            {
                                k3 = Math.max(1, k3 / 2);
                            }

                            i += k3 * j2;
                            if (itemstack.getCount() > 1)
                            {
                                i = 40;
                            }
                        }
                    }
                }

                if (flag3 && !flag2)
                {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (StringUtils.isBlank(container.getItemName()))
        {
            if (itemstack.hasCustomHoverName())
            {
                k = 1;
                i += k;
                itemstack1.resetHoverName();
            }
        }
        else if (!container.getItemName().equals(itemstack.getHoverName().getString()))
        {
            k = 1;
            i += k;
            itemstack1.setHoverName(new TextComponent(container.getItemName()));
        }
        if (flag && !itemstack1.isBookEnchantable(itemstack2))
        {
            itemstack1 = ItemStack.EMPTY;
        }

        container.setXpCost(j + i);
        if (i <= 0)
        {
            itemstack1 = ItemStack.EMPTY;
        }

        if (k == i && k > 0 && container.getXpCost() >= 40)
        {
            container.setXpCost(39);
        }

        if (container.getXpCost() >= 40 && !container.getPlayer().getAbilities().instabuild)
        {
            itemstack1 = ItemStack.EMPTY;
        }

        if (!itemstack1.isEmpty())
        {
            int k2 = itemstack1.getBaseRepairCost();
            if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost())
            {
                k2 = itemstack2.getBaseRepairCost();
            }

            if (k != i || k == 0)
            {
                k2 = AnvilMenu.calculateIncreasedRepairCost(k2);
            }

            itemstack1.setRepairCost(k2);
            EnchantmentHelper.setEnchantments(map, itemstack1);
        }

        return itemstack1;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ForgeMod.VANILLA_BLACKSMITHING_SERIALIZER.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<VanillaBlacksmithingRecipe>
    {
        @Override
        public VanillaBlacksmithingRecipe fromJson(final ResourceLocation rl, final JsonObject json)
        {
            return new VanillaBlacksmithingRecipe(rl);
        }

        @Nullable
        @Override
        public VanillaBlacksmithingRecipe fromNetwork(final ResourceLocation rl, final FriendlyByteBuf buf)
        {
            return new VanillaBlacksmithingRecipe(rl);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buf, final VanillaBlacksmithingRecipe recipe)
        {
        }
    }
}
