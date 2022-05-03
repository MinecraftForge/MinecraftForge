/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.Optional;
import java.util.function.BiFunction;

public class ConfigGuiHandler
{
    public static Optional<BiFunction<Minecraft, Screen, Screen>> getGuiFactoryFor(ModInfo selectedMod)
    {
        return ModList.get().getModContainerById(selectedMod.getModId()).
                flatMap(mc -> mc.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY));
    }
}
