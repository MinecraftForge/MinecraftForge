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

package net.minecraftforge.debug.gameplay.loot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod(GlobalLootModifiersTest.MODID)
public class GlobalLootModifiersTest {
    public static final String MODID = "global_loot_test";
    public static final boolean ENABLE = true;
    @ObjectHolder(value = MODID)
    public static final Enchantment smelt = null;
    public GlobalLootModifiersTest() { }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class EventHandlers {
        @SubscribeEvent
        public static void registerEnchantments(@Nonnull final RegistryEvent.Register<Enchantment> event) {
            if (ENABLE) {
                event.getRegistry().register(new SmelterEnchantment(Rarity.UNCOMMON, EnchantmentType.DIGGER, new EquipmentSlotType[] {EquipmentSlotType.MAINHAND}).setRegistryName(new ResourceLocation(MODID,"smelt")));
            }
        }

        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            if (ENABLE) {
                event.getRegistry().register(
                        new WheatSeedsConverterModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"wheat_harvest"))
                );
                event.getRegistry().register(new SmeltingEnchantmentModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"smelting")));
                event.getRegistry().register(new SilkTouchTestModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"silk_touch_bamboo")));
            }
        }
    }

    private static class SmelterEnchantment extends Enchantment {
        protected SmelterEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
            super(rarityIn, typeIn, slots);
        }
    }

    /**
     * The smelting enchantment causes this modifier to be invoked, via the smelting loot_modifier json
     *
     */
    private static class SmeltingEnchantmentModifier extends LootModifier {
        public SmeltingEnchantmentModifier(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Nonnull
        @Override
        public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
            generatedLoot.forEach((stack) -> ret.add(smelt(stack, context)));
            return ret;
        }

        private static ItemStack smelt(ItemStack stack, LootContext context) {
            return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
                    .map(FurnaceRecipe::getRecipeOutput)
                    .filter(itemStack -> !itemStack.isEmpty())
                    .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
                    .orElse(stack);
        }

        private static class Serializer extends GlobalLootModifierSerializer<SmeltingEnchantmentModifier> {
            @Override
            public SmeltingEnchantmentModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
                return new SmeltingEnchantmentModifier(conditionsIn);
            }
        }
    }

    /**
     * When harvesting blocks with bamboo, this modifier is invoked, via the silk_touch_bamboo loot_modifier json
     *
     */
    private static class SilkTouchTestModifier extends LootModifier {
        public SilkTouchTestModifier(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Nonnull
        @Override
        public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            ItemStack ctxTool = context.get(LootParameters.TOOL);
            //return early if silk-touch is already applied (otherwise we'll get stuck in an infinite loop).
            if(EnchantmentHelper.getEnchantments(ctxTool).containsKey(Enchantments.SILK_TOUCH)) return generatedLoot;
            ItemStack fakeTool = ctxTool.copy();
            fakeTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
            LootContext.Builder builder = new LootContext.Builder(context);
            builder.withParameter(LootParameters.TOOL, fakeTool);
            LootContext ctx = builder.build(LootParameterSets.BLOCK);
            LootTable loottable = context.getWorld().getServer().getLootTableManager().getLootTableFromLocation(context.get(LootParameters.BLOCK_STATE).getBlock().getLootTable());
            return loottable.generate(ctx);
        }

        private static class Serializer extends GlobalLootModifierSerializer<SilkTouchTestModifier> {
            @Override
            public SilkTouchTestModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
                return new SilkTouchTestModifier(conditionsIn);
            }
        }
    }

    /**
     * When harvesting wheat with shears, this modifier is invoked via the wheat_harvest loot_modifier json<br/>
     * This modifier checks how many seeds were harvested and turns X seeds into Y wheat (3:1)
     *
     */
    private static class WheatSeedsConverterModifier extends LootModifier {
        private final int numSeedsToConvert;
        private final Item itemToCheck;
        private final Item itemReward;
        public WheatSeedsConverterModifier(ILootCondition[] conditionsIn, int numSeeds, Item itemCheck, Item reward) {
            super(conditionsIn);
            numSeedsToConvert = numSeeds;
            itemToCheck = itemCheck;
            itemReward = reward;
        }

        @Nonnull
        @Override
        public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            //
            // Additional conditions can be checked, though as much as possible should be parameterized via JSON data.
            // It is better to write a new ILootCondition implementation than to do things here.
            //
            int numSeeds = 0;
            for(ItemStack stack : generatedLoot) {
                if(stack.getItem() == itemToCheck)
                    numSeeds+=stack.getCount();
            }
            if(numSeeds >= numSeedsToConvert) {
                generatedLoot.removeIf(x -> x.getItem() == itemToCheck);
                generatedLoot.add(new ItemStack(itemReward, (numSeeds/numSeedsToConvert)));
                numSeeds = numSeeds%numSeedsToConvert;
                if(numSeeds > 0)
                    generatedLoot.add(new ItemStack(itemToCheck, numSeeds));
            }
            return generatedLoot;
        }

        private static class Serializer extends GlobalLootModifierSerializer<WheatSeedsConverterModifier> {

            @Override
            public WheatSeedsConverterModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
                int numSeeds = JSONUtils.getInt(object, "numSeeds");
                Item seed = ForgeRegistries.ITEMS.getValue(new ResourceLocation((JSONUtils.getString(object, "seedItem"))));
                Item wheat = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getString(object, "replacement")));
                return new WheatSeedsConverterModifier(conditionsIn, numSeeds, seed, wheat);
            }
        }
    }
}
