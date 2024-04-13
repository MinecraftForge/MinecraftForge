/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigScreenHandler
{
    /**
     * @param screenFunction A function that takes the {@link Minecraft} client instance and the mods screen as
     *                       arguments and returns your config screen to show when the player clicks the config button
     *                       for your mod on the mods screen.
     *                       <p>You should call {@link Minecraft#setScreen(Screen)} with the provided client instance
     *                       and mods screen for the action of your close button.</p>
     */
    public record ConfigScreenFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) implements IExtensionPoint<ConfigScreenFactory> {
        /**
         * @param screenFunction A function that takes the mods screen as an argument and returns your config screen to
         *                       show when the player clicks the config button for your mod on the mods screen.
         *                       <p>You should call {@link Minecraft#setScreen(Screen)} with the provided mods screen
         *                       for the action of your close button, using {@link Screen#minecraft} to get the client
         *                       instance.</p>
         */
        public ConfigScreenFactory(Function<Screen, Screen> screenFunction) {
            this((mcClient, modsScreen) -> screenFunction.apply(modsScreen));
        }
    }

    public static Optional<BiFunction<Minecraft, Screen, Screen>> getScreenFactoryFor(IModInfo selectedMod)
    {
        return ModList.get().getModContainerById(selectedMod.getModId()).
                flatMap(mc -> mc.getCustomExtension(ConfigScreenFactory.class).map(ConfigScreenFactory::screenFunction));
    }
}
