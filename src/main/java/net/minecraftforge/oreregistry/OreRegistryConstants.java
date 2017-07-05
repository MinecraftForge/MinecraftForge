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

package net.minecraftforge.oreregistry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

/**
 * A list of predefined {@link OreMaterial}s and {@link OreShape} for convenience.
 */
public final class OreRegistryConstants
{
    /* Vanilla materials */
    public static final OreMaterial IRON = OreRegistry.addMaterial("iron");
    public static final OreMaterial GOLD = OreRegistry.addMaterial("gold");
    public static final OreMaterial DIAMOND = OreRegistry.addMaterial("diamond");
    public static final OreMaterial LAPIS = OreRegistry.addMaterial("lapis");
    public static final OreMaterial REDSTONE = OreRegistry.addMaterial("redstone");
    public static final OreMaterial QUARTZ = OreRegistry.addMaterial("quartz");
    public static final OreMaterial COAL = OreRegistry.addMaterial("coal");
    public static final OreMaterial EMERALD = OreRegistry.addMaterial("emerald");

    /* Common modded materials */
    public static final OreMaterial ALUMINIUM = OreRegistry.addMaterial("aluminium");
    public static final OreMaterial BRASS = OreRegistry.addMaterial("brass");
    public static final OreMaterial BRONZE = OreRegistry.addMaterial("bronze");
    public static final OreMaterial COPPER = OreRegistry.addMaterial("copper");
    public static final OreMaterial LEAD = OreRegistry.addMaterial("lead");
    public static final OreMaterial NICKEL = OreRegistry.addMaterial("nickel");
    public static final OreMaterial SILVER = OreRegistry.addMaterial("silver");
    public static final OreMaterial STEEL = OreRegistry.addMaterial("steel");
    public static final OreMaterial TIN = OreRegistry.addMaterial("tin");
    public static final OreMaterial ZINC = OreRegistry.addMaterial("zinc");

    /* Common Shapes */
    public static final OreShape ORE = OreRegistry.addShape("ore");
    public static final OreShape INGOT = OreRegistry.addShape("ingot");
    public static final OreShape DUST = OreRegistry.addShape("dust");
    public static final OreShape POWDER = OreRegistry.addShape("powder");
    public static final OreShape NUGGET = OreRegistry.addShape("nugget");
    public static final OreShape GEM = OreRegistry.addShape("gem");
    public static final OreShape CRYSTAL = OreRegistry.addShape("crystal");
    public static final OreShape BLOCK = OreRegistry.addShape("block");
    public static final OreShape GEAR = OreRegistry.addShape("gear");
    public static final OreShape PLATE = OreRegistry.addShape("plate");

    private OreRegistryConstants() {}

    public static void initVanillaEntries()
    {
        OreRegistry.addOre(IRON, INGOT, new ItemStack(Items.IRON_INGOT));
        OreRegistry.addOre(IRON, NUGGET, new ItemStack(Items.IRON_NUGGET));
        OreRegistry.addOre(IRON, BLOCK, new ItemStack(Blocks.IRON_BLOCK));
        OreRegistry.addOre(IRON, ORE, new ItemStack(Blocks.IRON_ORE));

        OreRegistry.addOre(GOLD, INGOT, new ItemStack(Items.GOLD_INGOT));
        OreRegistry.addOre(GOLD, NUGGET, new ItemStack(Items.GOLD_NUGGET));
        OreRegistry.addOre(GOLD, BLOCK, new ItemStack(Blocks.GOLD_BLOCK));
        OreRegistry.addOre(GOLD, ORE, new ItemStack(Blocks.GOLD_ORE));

        OreRegistry.addOre(EMERALD, GEM, new ItemStack(Items.EMERALD));
        OreRegistry.addOre(EMERALD, BLOCK, new ItemStack(Blocks.EMERALD_BLOCK));
        OreRegistry.addOre(EMERALD, ORE, new ItemStack(Blocks.EMERALD_ORE));

        OreRegistry.addOre(DIAMOND, GEM, new ItemStack(Items.DIAMOND));
        OreRegistry.addOre(DIAMOND, BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK));
        OreRegistry.addOre(DIAMOND, ORE, new ItemStack(Blocks.DIAMOND_ORE));

        OreRegistry.addOre(LAPIS, GEM, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
        OreRegistry.addOre(LAPIS, BLOCK, new ItemStack(Blocks.LAPIS_BLOCK));
        OreRegistry.addOre(LAPIS, ORE, new ItemStack(Blocks.LAPIS_ORE));

        OreRegistry.addOre(REDSTONE, DUST, new ItemStack(Items.REDSTONE));
        OreRegistry.addOre(REDSTONE, BLOCK, new ItemStack(Blocks.REDSTONE_BLOCK));
        OreRegistry.addOre(REDSTONE, ORE, new ItemStack(Blocks.REDSTONE_ORE));

        OreRegistry.addOre(QUARTZ, GEM, new ItemStack(Items.QUARTZ));
        OreRegistry.addOre(QUARTZ, BLOCK, new ItemStack(Blocks.QUARTZ_BLOCK));
        OreRegistry.addOre(QUARTZ, ORE, new ItemStack(Blocks.QUARTZ_ORE));

        OreRegistry.addOre(COAL, GEM, new ItemStack(Items.COAL));
        OreRegistry.addOre(COAL, BLOCK, new ItemStack(Blocks.COAL_BLOCK));
        OreRegistry.addOre(COAL, ORE, new ItemStack(Blocks.COAL_ORE));
    }
}
