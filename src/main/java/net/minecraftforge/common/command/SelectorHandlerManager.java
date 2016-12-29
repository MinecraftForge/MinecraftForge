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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.Loader;

/**
 * Allows registration of custom selector types by assigning a {@link SelectorHandler} to a prefix
 * To check whether a new prefix conflicts with any registered prefix, the methods {@link #getShadowed(String)} and {@link #getShadowing(String)} can be used
 * This class handles calls to the {@link EntitySelector} methods {@link EntitySelector#matchEntities matchEntities},
 * {@link EntitySelector#matchesMultiplePlayers matchesMultiplePlayers} and {@link EntitySelector#isSelector isSelector}.<br>
 * The calls are delegated to the handler with the longest matching prefix
 */
public class SelectorHandlerManager
{
    private SelectorHandlerManager()
    {
    }

    //the ordering is reversed such that longer prefixes appear before their shorter substrings
    private static final NavigableMap<String, SelectorHandler> selectorHandlers = new TreeMap<String, SelectorHandler>(Collections.<String> reverseOrder());
    private static final NavigableMap<String, String> registeringMods = new TreeMap<String, String>(Collections.<String> reverseOrder());

    private static final SelectorHandler vanillaHandler = new SelectorHandler()
    {
        @Override
        public <T extends Entity> List<T> matchEntities(final ICommandSender sender, final String token, final Class<? extends T> targetClass) throws CommandException
        {
            return EntitySelector.matchEntitiesDefault(sender, token, targetClass);
        }

        @Override
        public boolean matchesMultiplePlayers(final String selectorStr) throws CommandException
        {
            return EntitySelector.matchesMultiplePlayersDefault(selectorStr);
        }

        @Override
        public boolean isSelector(final String selectorStr)
        {
            return EntitySelector.isSelectorDefault(selectorStr);
        }
    };

    static
    {
        for (final String prefix : ArrayUtils.toArray("@p", "@a", "@r", "@e"))
        {
            selectorHandlers.put(prefix, vanillaHandler);
            registeringMods.put(prefix, "minecraft");
        }
    }

    /**
     * Returns a collection of all registered prefixes more specific than {@code prefix}
     *
     * @return A {@link Map} mapping the prefixes to the mod ids registering them
     */
    public static Map<String, String> getShadowing(final String prefix)
    {
        final Map<String, String> ret = new HashMap<String, String>();

        for (final Entry<String, String> other : registeringMods.descendingMap().tailMap(prefix, false).entrySet())
        {
            if (other.getKey().startsWith(prefix))
            {
                ret.put(other.getKey(), other.getValue());
            }
            else
            {
                return ret;
            }
        }

        return ret;
    }

    /**
     * Returns a collection of all registered prefixes at most as specific as {@code prefix}
     *
     * @return A {@link Map} mapping the prefixes to the mod ids registering them
     */
    public static Map<String, String> getShadowed(final String prefix)
    {
        if (prefix.isEmpty())
        {
            return Collections.emptyMap();
        }

        final Map<String, String> ret = new HashMap<String, String>();

        for (final Entry<String, String> other : registeringMods.subMap(prefix, true, prefix.substring(0, 1), true).entrySet())
        {
            if (prefix.startsWith(other.getKey()))
            {
                ret.put(other.getKey(), other.getValue());
            }
        }

        return ret;
    }

    /**
     * Registers a new {@link SelectorHandler} for {@code prefix}.
     *
     * @param prefix A non-empty string
     */
    public static void register(final String prefix, final SelectorHandler handler)
    {
        if (prefix.isEmpty())
        {
            throw new IllegalArgumentException("Prefix must not be empty");
        }

        final String modId = Loader.instance().activeModContainer().getModId();

        selectorHandlers.put(prefix, handler);
        registeringMods.put(prefix, modId);
    }

    /**
     * Returns the best matching handler for the given string. Defaults to the vanilla handler if no prefix applies
     */
    public static SelectorHandler getHandler(final String selectorStr)
    {
        if (!selectorStr.isEmpty())
        {
            for (final Entry<String, SelectorHandler> handler : selectorHandlers.subMap(selectorStr, true, selectorStr.substring(0, 1), true).entrySet())
            {
                if (selectorStr.startsWith(handler.getKey()))
                {
                    return handler.getValue();
                }
            }
        }

        return vanillaHandler;
    }

    //These methods are called by the vanilla methods

    public static <T extends Entity> List<T> matchEntities(final ICommandSender sender, final String token, final Class<? extends T> targetClass) throws CommandException
    {
        return getHandler(token).matchEntities(sender, token, targetClass);
    }

    public static boolean matchesMultiplePlayers(final String selectorStr) throws CommandException
    {
        return getHandler(selectorStr).matchesMultiplePlayers(selectorStr);
    }

    public static boolean isSelector(final String selectorStr)
    {
        return getHandler(selectorStr).isSelector(selectorStr);
    }
}
