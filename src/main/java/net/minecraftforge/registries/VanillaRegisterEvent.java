/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intermediary event for registering to vanilla registries from both {@link Registry} and {@link BuiltinRegistries}.
 * This is a stopgap measure until 1.19 comes with the rework of forge registries, at which point it will be deleted.
 * Internal for forge-uses only, see {@link DeferredRegister#vanillaRegister(VanillaRegisterEvent)} for how it is used.
 */
@Deprecated(forRemoval = true, since = "1.18.2")
class VanillaRegisterEvent extends Event implements IModBusEvent
{
    @NotNull
    final Registry<?> vanillaRegistry;

    VanillaRegisterEvent(@NotNull Registry<?> vanillaRegistry)
    {
        this.vanillaRegistry = vanillaRegistry;
    }
}
