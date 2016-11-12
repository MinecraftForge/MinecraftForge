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

package net.minecraftforge.common.command;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

public class SelectorHandlerManager
{
    private SelectorHandlerManager()
    {
    }

    private static SelectorHandler handler = new SelectorHandler();
    private static boolean isCustom = false;
    private static String modifyingMod = "Minecraft";

    /**
     * Allows you to override the behavior of EnitySelector by setting this handler to a custom implementation<br>
     * This overrides any previous handlers set<br>
     * This method must be called only during mod loading
     * Always use {@link net.minecraftforge.event.EntitySelectorEvent EntitySelectorEvent} if possible
     */
    public static void setHandler(SelectorHandler handler)
    {
        final String newModifyingMod = Loader.instance().activeModContainer().getName();

        FMLLog.info("SelectorHandler is being overridden by '" + newModifyingMod + "'");

        if (isCustom)
            FMLLog.bigWarning("SelectorHandler was already overridden by '" + modifyingMod + "'");

        isCustom = true;
        modifyingMod = newModifyingMod;
        SelectorHandlerManager.handler = handler;
    }

    public static SelectorHandler getHandler()
    {
        return handler;
    }

    public static boolean isCustom()
    {
        return isCustom;
    }

    /**
     * @return "Minecraft" if not custom, and name of modifying mod otherwise
     */
    public static String getModifyingMod()
    {
        return modifyingMod;
    }
}
