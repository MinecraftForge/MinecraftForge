package net.minecraftforge.client;

import net.minecraft.util.Util.EnumOS;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.common.util.EnumHelper;

public class EnumHelperClient extends EnumHelper
{
    private static Class<?>[][] clientTypes =
    {
        {GameType.class, int.class, String.class, String.class},
        {Options.class, String.class, boolean.class, boolean.class},
        {EnumOS.class}
    };

    public static GameType addGameType(String name, int id, String displayName, String shortName)
    {
        return addEnum(GameType.class, name, id, displayName, shortName);
    }

    public static Options addOptions(String name, String langName, boolean isSlider, boolean isToggle)
    {
        return addEnum(Options.class, name, langName, isSlider, isToggle);
    }

    public static Options addOptions(String name, String langName, boolean isSlider, boolean isToggle, float valMin, float valMax, float valStep)
    {
        return addEnum(Options.class, name,
                new Class<?>[]{String.class, boolean.class, boolean.class, float.class, float.class, float.class},
                langName, isSlider, isToggle, valMin, valMax, valStep);
    }

    public static EnumOS addOS2(String name)
    {
        return addEnum(EnumOS.class, name);
    }

    private static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        return addEnum(clientTypes, enumType, enumName, paramValues);
    }
}