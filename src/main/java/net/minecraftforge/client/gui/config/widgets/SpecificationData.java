/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.config.widgets;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * The config specification data.
 * @param configSpec The configuration specification.
 * @param isSynced True if the configuration is synced, false if it is not. WHen true then the config entry should be read only.
 */
public record SpecificationData(ForgeConfigSpec configSpec, boolean isSynced)
{
}
