/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.oredict;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Utility class for working with ore dictionary dyes.
 */
public class DyeUtils
{
/*
    private static final String[] dyeOredicts = new String[]
    {
        "dyeWhite",
        "dyeOrange",
        "dyeMagenta",
        "dyeLightBlue",
        "dyeYellow",
        "dyeLime",
        "dyePink",
        "dyeGray",
        "dyeLightGray",
        "dyeCyan",
        "dyePurple",
        "dyeBlue",
        "dyeBrown",
        "dyeGreen",
        "dyeRed",
        "dyeBlack"
    };

    /**
     * Check if an item stack is a dye.
     * @param stack the item stack
     * @return whether the stack is a dye
     * /
    public static boolean isDye(ItemStack stack)
    {
        return metaFromStack(stack).isPresent();
    }

    /**
     * Get the dye metadata from the stack, which can be passed into {@link EnumDyeColor#byMetadata(int)}.
     * @param stack the item stack
     * @return an {@link OptionalInt} holding the dye metadata for a dye, or an empty {@link OptionalInt} otherwise
     * /
    public static OptionalInt metaFromStack(ItemStack stack)
    {
        if (stack.isEmpty()) return OptionalInt.empty();
        return Arrays.stream(OreDictionary.getOreIDs(stack))
                .mapToObj(OreDictionary::getOreName)
                .mapToInt(name -> ArrayUtils.indexOf(dyeOredicts, name))
                .filter(id -> id >= 0)
                .findFirst();
    }

    /**
     * Similar to {@link #metaFromStack(ItemStack)}, except that it returns the raw integer (with a {@code -1} sentinel);
     * this follows vanilla conventions.
     * @param stack the item stack
     * @return the dye metadata for a dye, or {@code -1} otherwise
     * /
    public static int rawMetaFromStack(ItemStack stack)
    {
        return metaFromStack(stack).orElse(-1);
    }

    /**
     * Get the dye damage from the stack, which can be passed into {@link EnumDyeColor#byDyeDamage(int)}.
     * @param stack the item stack
     * @return an {@link OptionalInt} holding the dye damage for a dye, or an empty {@link OptionalInt} otherwise
     * /
    public static OptionalInt dyeDamageFromStack(ItemStack stack)
    {
        final OptionalInt meta = metaFromStack(stack);
        return meta.isPresent() ? OptionalInt.of(0xF - meta.getAsInt()) : OptionalInt.empty();
    }

    /**
     * Similar to {@link #dyeDamageFromStack(ItemStack)}, except that it returns the raw integer (with a {@code -1} sentinel);
     * this follows vanilla conventions.
     * @param stack the item stack
     * @return the dye damage for a dye, or {@code -1} otherwise
     * /
    public static int rawDyeDamageFromStack(ItemStack stack)
    {
        return dyeDamageFromStack(stack).orElse(-1);
    }

    /**
     * Get a dye's color.
     * @param stack the item stack
     * @return an {@link Optional} holding the dye color if present, or an empty {@link Optional} otherwise
     * /
    public static Optional<EnumDyeColor> colorFromStack(ItemStack stack)
    {
        final OptionalInt meta = metaFromStack(stack);
        return meta.isPresent() ? Optional.of(EnumDyeColor.byMetadata(meta.getAsInt())) : Optional.empty();
    }
    */
}
