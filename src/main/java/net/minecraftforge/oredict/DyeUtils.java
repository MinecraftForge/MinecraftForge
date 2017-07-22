package net.minecraftforge.oredict;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.OptionalInt;

public class DyeUtils
{
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

    public static OptionalInt metaFromStack(ItemStack stack)
    {
        return Arrays.stream(OreDictionary.getOreIDs(stack))
                .mapToObj(OreDictionary::getOreName)
                .mapToInt(name -> ArrayUtils.indexOf(dyeOredicts, name))
                .filter(id -> id >= 0)
                .findFirst();
    }
}
