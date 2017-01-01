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

package net.minecraftforge.client;

import net.minecraft.util.Util.EnumOS;
import net.minecraft.world.GameType;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nullable;

public class EnumHelperClient extends EnumHelper
{
    private static Class<?>[][] clientTypes =
    {
        {GameType.class, int.class, String.class, String.class},
        {Options.class, String.class, boolean.class, boolean.class},
        {EnumOS.class}
    };

    @Nullable
    public static GameType addGameType(String name, int id, String displayName, String shortName)
    {
        return addEnum(GameType.class, name, id, displayName, shortName);
    }

    @Nullable
    public static Options addOptions(String name, String langName, boolean isSlider, boolean isToggle)
    {
        return addEnum(Options.class, name, langName, isSlider, isToggle);
    }

    @Nullable
    public static Options addOptions(String name, String langName, boolean isSlider, boolean isToggle, float valMin, float valMax, float valStep)
    {
        return addEnum(Options.class, name,
                new Class<?>[]{String.class, boolean.class, boolean.class, float.class, float.class, float.class},
                langName, isSlider, isToggle, valMin, valMax, valStep);
    }

    @Nullable
    public static EnumOS addOS2(String name)
    {
        return addEnum(EnumOS.class, name);
    }

    @Nullable
    private static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        return addEnum(clientTypes, enumType, enumName, paramValues);
    }
}