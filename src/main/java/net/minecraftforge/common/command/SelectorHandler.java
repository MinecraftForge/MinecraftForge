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

import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.List;

/**
 * A proxy for {@link EntitySelector} methods. Can be used to customize the parsing behavior<br>
 * <b>Note:</b>  You should generally override all three methods to produce consistent results<br>
 * <b>Note:</b> These methods are called on both client and server<br>
 * <b>Note:</b>  For compatibility reasons, you should not break the key=value pattern
 */
public class SelectorHandler
{
    public <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass)
    {
        return EntitySelector.matchEntitiesDefault(sender, token, targetClass);
    }

    /**
     * This should raise an {@link net.minecraftforge.event.EntitySelectorEvent EntitySelectorEvent}
     */
    public boolean matchesMultiplePlayers(String selectorStr)
    {
        return EntitySelector.matchesMultiplePlayersDefault(selectorStr);
    }

    public boolean hasArguments(String selectorStr)
    {
        return EntitySelector.hasArgumentsDefault(selectorStr);
    }

    private static SelectorHandler handler = new SelectorHandler();
    private static boolean isCustom = false;
    private static String modifyingMod = "Minecraft";

    /**
     * Allows you to override the behavior of EnitySelector by setting this handler to a custom implementation<br>
     * This overrides any previous handlers set<br>
     * Always use {@link net.minecraftforge.event.EntitySelectorEvent EntitySelectorEvent} if possible
     */
    public static void setHandler(SelectorHandler handler)
    {
        if (isCustom)
            FMLLog.bigWarning("SelectorHandler is already overridden" + (modifyingMod == null ? "" : " by '" + modifyingMod + "'"));

        isCustom = true;
        ModContainer container = Loader.instance().activeModContainer();

        if (container == null)
            FMLLog.bigWarning("SelectorHandler is overridden by an unknown mod. Overriding should be done during mod loading");

        modifyingMod = container == null ? null : container.getName();

        SelectorHandler.handler = handler;

        FMLLog.info("SelectorHandler is being overridden" + (modifyingMod == null ? "" : " by '" + modifyingMod + "'"));
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
     * @return <code>null</code> if unknown, "Minecraft" if not custom, and name of modifying mod otherwise
     */
    public static String getModifyingMod()
    {
        return modifyingMod;
    }
}
