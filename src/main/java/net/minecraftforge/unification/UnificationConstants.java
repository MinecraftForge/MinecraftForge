/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.unification;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

/**
 * Provides a list of predefined {@link UnifiedMaterial} and {@link UnifiedForm} for convenience.
 */
public final class UnificationConstants
{
    public static final UnifiedMaterial IRON = UnificationManager.getMaterial("iron");
    public static final UnifiedMaterial GOLD = UnificationManager.getMaterial("gold");
    public static final UnifiedMaterial DIAMOND = UnificationManager.getMaterial("diamond");
    public static final UnifiedMaterial LAPIS = UnificationManager.getMaterial("lapis");
    public static final UnifiedMaterial REDSTONE = UnificationManager.getMaterial("redstone");
    public static final UnifiedMaterial QUARTZ = UnificationManager.getMaterial("quartz");
    public static final UnifiedMaterial COAL = UnificationManager.getMaterial("coal");
    public static final UnifiedMaterial EMERALD = UnificationManager.getMaterial("emerald");

    public static final UnifiedForm ORE = UnificationManager.getForm("ore");
    public static final UnifiedForm INGOT = UnificationManager.getForm("ingot");
    public static final UnifiedForm DUST = UnificationManager.getForm("dust");
    public static final UnifiedForm NUGGET = UnificationManager.getForm("nugget");
    public static final UnifiedForm GEM = UnificationManager.getForm("gem");
    public static final UnifiedForm BLOCK = UnificationManager.getForm("block");

    private static boolean hasInit = false;

    private UnificationConstants() {}

    /**
     * This should only be called by Forge
     */
    public static void initVanillaEntries()
    {
        if (hasInit)
        {
            throw new IllegalStateException("tried to init vanilla entries twice");
        }
        hasInit = true;

        Unifier<ItemStack> items = UnificationManager.getItemStackUnifier();
        Unifier<IBlockState> blocks = UnificationManager.getBlockStateUnifier();

        items.add(IRON, INGOT, new ItemStack(Items.IRON_INGOT));
        items.add(IRON, NUGGET, new ItemStack(Items.IRON_NUGGET));
        items.add(IRON, BLOCK, new ItemStack(Blocks.IRON_BLOCK));
        items.add(IRON, ORE, new ItemStack(Blocks.IRON_ORE));
        blocks.add(IRON, BLOCK, Blocks.IRON_BLOCK.getDefaultState());
        blocks.add(IRON, ORE, Blocks.IRON_ORE.getDefaultState());

        items.add(GOLD, INGOT, new ItemStack(Items.GOLD_INGOT));
        items.add(GOLD, NUGGET, new ItemStack(Items.GOLD_NUGGET));
        items.add(GOLD, BLOCK, new ItemStack(Blocks.GOLD_BLOCK));
        items.add(GOLD, ORE, new ItemStack(Blocks.GOLD_ORE));
        blocks.add(GOLD, BLOCK, Blocks.GOLD_BLOCK.getDefaultState());
        blocks.add(GOLD, ORE, Blocks.GOLD_ORE.getDefaultState());

        items.add(EMERALD, GEM, new ItemStack(Items.EMERALD));
        items.add(EMERALD, BLOCK, new ItemStack(Blocks.EMERALD_BLOCK));
        items.add(EMERALD, ORE, new ItemStack(Blocks.EMERALD_ORE));
        blocks.add(EMERALD, BLOCK, Blocks.EMERALD_BLOCK.getDefaultState());
        blocks.add(EMERALD, ORE, Blocks.EMERALD_ORE.getDefaultState());

        items.add(DIAMOND, GEM, new ItemStack(Items.DIAMOND));
        items.add(DIAMOND, BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK));
        items.add(DIAMOND, ORE, new ItemStack(Blocks.DIAMOND_ORE));
        blocks.add(DIAMOND, BLOCK, Blocks.DIAMOND_BLOCK.getDefaultState());
        blocks.add(DIAMOND, ORE, Blocks.DIAMOND_ORE.getDefaultState());

        items.add(LAPIS, GEM, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
        items.add(LAPIS, BLOCK, new ItemStack(Blocks.LAPIS_BLOCK));
        items.add(LAPIS, ORE, new ItemStack(Blocks.LAPIS_ORE));
        blocks.add(LAPIS, BLOCK, Blocks.LAPIS_BLOCK.getDefaultState());
        blocks.add(LAPIS, ORE, Blocks.LAPIS_ORE.getDefaultState());

        items.add(REDSTONE, DUST, new ItemStack(Items.REDSTONE));
        items.add(REDSTONE, BLOCK, new ItemStack(Blocks.REDSTONE_BLOCK));
        items.add(REDSTONE, ORE, new ItemStack(Blocks.REDSTONE_ORE));
        blocks.add(REDSTONE, BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState());
        blocks.add(REDSTONE, ORE, Blocks.REDSTONE_ORE.getDefaultState());

        items.add(QUARTZ, GEM, new ItemStack(Items.QUARTZ));
        items.add(QUARTZ, BLOCK, new ItemStack(Blocks.QUARTZ_BLOCK));
        items.add(QUARTZ, ORE, new ItemStack(Blocks.QUARTZ_ORE));
        blocks.add(QUARTZ, BLOCK, Blocks.QUARTZ_BLOCK.getDefaultState());
        blocks.add(QUARTZ, ORE, Blocks.QUARTZ_ORE.getDefaultState());

        items.add(COAL, GEM, new ItemStack(Items.COAL));
        items.add(COAL, BLOCK, new ItemStack(Blocks.COAL_BLOCK));
        items.add(COAL, ORE, new ItemStack(Blocks.COAL_ORE));
        blocks.add(COAL, BLOCK, Blocks.COAL_BLOCK.getDefaultState());
        blocks.add(COAL, ORE, Blocks.COAL_ORE.getDefaultState());
    }
}
