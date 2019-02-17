/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class FortuneEnchantBonus extends LootFunction
{
    private final int multiplier, offset;

    protected FortuneEnchantBonus(LootCondition[] conditions, int multiplier, int offset)
    {
        super(conditions);
        this.multiplier = multiplier;
        this.offset = offset;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context)
    {
        Entity player = context.getKillerPlayer();
        if (player instanceof EntityPlayer)
        {
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ((EntityPlayer) player).getHeldItemMainhand());
            if (fortune > 0 && this.multiplier > 0 && this.offset > 0)
            {
                stack.grow(rand.nextInt(fortune * this.multiplier + this.offset));
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<FortuneEnchantBonus>
    {

        public Serializer()
        {
            super(new ResourceLocation("forge", "fortune_enchant"), FortuneEnchantBonus.class);
        }

        @Override
        public void serialize(JsonObject object, FortuneEnchantBonus theFunction, JsonSerializationContext context)
        {
            object.addProperty("multiplier", theFunction.multiplier);
            object.addProperty("offset", theFunction.offset);
        }

        @Override
        public FortuneEnchantBonus deserialize(JsonObject object, JsonDeserializationContext context, LootCondition[] conditions)
        {
            return new FortuneEnchantBonus(conditions, object.get("multiplier").getAsInt(), object.get("offset").getAsInt());
        }
    }
}
