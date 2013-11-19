package net.minecraftforge.client;

import net.minecraft.world.EnumGameType;
import net.minecraft.util.EnumOS;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.common.EnumHelper;

public class EnumHelperClient extends EnumHelper
{

    private static Class[][] clentTypes =
    {
        {EnumGameType.class, int.class, String.class},
        {EnumOptions.class, String.class, boolean.class, boolean.class},
        {EnumOS.class},
        {EnumRarity.class, int.class, String.class}
    };
    
    public static EnumGameType addGameType(String name, int id, String displayName)
    {
        return addEnum(EnumGameType.class, name, id, displayName);
    }
    
    public static EnumOptions addOptions(String name, String langName, boolean isSlider, boolean isToggle)
    {
        return addEnum(EnumOptions.class, name, langName, isSlider, isToggle);
    }
    
    public static EnumOS addOS2(String name)
    {
        return addEnum(EnumOS.class, name);
    }
    
    public static EnumRarity addRarity(String name, int color, String displayName)
    {
        return addEnum(EnumRarity.class, name, color, displayName);
    }

    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        return addEnum(clentTypes, enumType, enumName, paramValues);
    }
}
