package net.minecraftforge.client;

import net.minecraft.util.Util.EnumOS;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.client.settings.GameSettings.Options; 
import net.minecraftforge.common.util.EnumHelper;

public class EnumHelperClient extends EnumHelper
{
    @SuppressWarnings("rawtypes")
    private static Class[][] clentTypes =
    {
        {GameType.class, int.class, String.class},
        {Options.class, String.class, boolean.class, boolean.class},
        {EnumOS.class}
    };
    
    public static GameType addGameType(String name, int id, String displayName)
    {
        return addEnum(GameType.class, name, id, displayName);
    }
    
    public static Options addOptions(String name, String langName, boolean isSlider, boolean isToggle)
    {
        return addEnum(Options.class, name, langName, isSlider, isToggle);
    }
    
    public static EnumOS addOS2(String name)
    {
        return addEnum(EnumOS.class, name);
    }

    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        return addEnum(clentTypes, enumType, enumName, paramValues);
    }
}
