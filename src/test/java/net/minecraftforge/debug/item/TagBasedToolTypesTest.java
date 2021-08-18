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

package net.minecraftforge.debug.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod("tag_based_tool_types")
@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class TagBasedToolTypesTest
{
    public static final Tag.Named<Block> MY_TOOL_TAG = BlockTags.createOptional(new ResourceLocation("tag_based_tool_types:minable/my_tool"));
    public static final Tag.Named<Block> MY_TIER_TAG = BlockTags.createOptional(new ResourceLocation("tag_based_tool_types:needs_my_tier_tool"));
    public static final Tier MY_TIER = TierSortingRegistry.registerTier(
            new ForgeTier(5, 5000, 10, 100, 0, MY_TIER_TAG, () -> Ingredient.of(Items.BEDROCK)),
            new ResourceLocation("tag_based_tool_types:my_tier"),
            List.of(Tiers.DIAMOND), List.of());

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new DiggerItem(1, 1, MY_TIER, MY_TOOL_TAG, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS))
        {
            @Override
            public float getDestroySpeed(ItemStack stack, BlockState state)
            {
                if (state.is(BlockTags.MINEABLE_WITH_AXE)) return speed;
                if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return speed;
                return super.getDestroySpeed(stack, state);
            }

            @Override
            public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
            {
                if (state.is(BlockTags.MINEABLE_WITH_PICKAXE))
                    return TierSortingRegistry.isCorrectTierForDrops(Tiers.WOOD, state);
                if (state.is(BlockTags.MINEABLE_WITH_AXE))
                    return TierSortingRegistry.isCorrectTierForDrops(MY_TIER, state);
                if (state.is(MY_TOOL_TAG))
                    return TierSortingRegistry.isCorrectTierForDrops(MY_TIER, state);
                return false;
            }
        }.setRegistryName("test_item"));
    }
}
