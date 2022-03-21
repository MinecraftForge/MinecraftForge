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

public class ConfigGuiHandler
{
    public record ConfigGuiFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) implements IExtensionPoint<ConfigGuiFactory> {}
    public static Optional<BiFunction<Minecraft, Screen, Screen>> getGuiFactoryFor(IModInfo selectedMod)
    {
        return ModList.get().getModContainerById(selectedMod.getModId()).
                flatMap(mc -> mc.getCustomExtension(ConfigGuiFactory.class).map(ConfigGuiFactory::screenFunction));
    }
}
