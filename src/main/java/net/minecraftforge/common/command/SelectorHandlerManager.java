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
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

/**
 * Allows registration of custom selector types by assigning a {@link SelectorHandler} to a prefix
 * To check whether a new prefix conflicts with any registered prefix, the methods {@link #isShadowed} and {@link #isShadowing} can be used
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
    private static final SortedMap<String, SelectorHandler> selectorHandlers = new TreeMap<String, SelectorHandler>(Collections.<String> reverseOrder());
    private static final SortedMap<String, String> registeringMods = new TreeMap<String, String>(Collections.<String> reverseOrder());

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
            registeringMods.put(prefix, "Minecraft");
        }
    }

    /**
     * Returns whether there is already a more specific prefix registered
     */
    public static boolean isShadowed(final String prefix)
    {
        for (final String other : registeringMods.keySet())
            if (prefix.startsWith(other))
                return true;

        return false;
    }

    /**
     * Returns whether there is already a less specific prefix registered
     */
    public static boolean isShadowing(final String prefix)
    {
        for (final String other : registeringMods.keySet())
            if (other.startsWith(prefix))
                return true;

        return false;
    }

    /**
     * Registers a new {@link SelectorHandler} for the given prefix.
     * Warnings are emitted when {@link #isShadowing} or {@link #isShadowed} would return {@code true}
     */
    public static void register(final String prefix, final SelectorHandler handler)
    {
        final String modName = Loader.instance().activeModContainer().getName();

        for (final Entry<String, String> prefixData : registeringMods.entrySet())
        {
            final String otherPrefix = prefixData.getKey();

            if (prefix.startsWith(otherPrefix))
                FMLLog.info("Selector prefix '%s' of mod '%s' is shadowing prefix '%s' of mod '%s'", prefix, modName, otherPrefix, prefixData.getValue());
            else if (otherPrefix.startsWith(prefix))
                FMLLog.info("Selector prefix '%s' of mod '%s' is shadowed by prefix '%s' of mod '%s'", prefix, modName, otherPrefix, prefixData.getValue());
        }

        selectorHandlers.put(prefix, handler);
        registeringMods.put(prefix, modName);
    }

    /**
     * Returns the best matching handler for the given string. Defaults to the vanilla handler if no prefix applies
     */
    public static SelectorHandler getHandler(final String selectorStr)
    {
        for (final Entry<String, SelectorHandler> handler : selectorHandlers.entrySet())
            if (selectorStr.startsWith(handler.getKey()))
                return handler.getValue();

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
