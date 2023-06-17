/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.config.widgets;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory definition for making config entry widgets.
 */
@FunctionalInterface
public interface ConfigGuiWidgetFactory
{
    /**
     * Creates a new widget instance for the given config entry.
     *
     * @param value The config value to create the widget for.
     * @param valueSpec The value specification for the config value.
     * @param valueManager The value manager for the config value (contains a setter, getter and initial value supplier to manage the value entered by the user, don't use the config value directly since this prevents resetting and error checking.)
     * @param spec The config specification data for the config value.
     * @param name The name of the configuration entry.
     * @return A config gui widget or null if not supported.
     */
    @Nullable
    ConfigGuiWidget create(@NotNull final ForgeConfigSpec.ConfigValue<?> value, @NotNull final ForgeConfigSpec.ValueSpec valueSpec, @NotNull final ValueManager valueManager, @NotNull final SpecificationData spec, @NotNull Component name);
}
