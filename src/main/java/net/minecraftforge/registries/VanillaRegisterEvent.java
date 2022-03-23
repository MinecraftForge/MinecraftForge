/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;

class VanillaRegisterEvent extends Event implements IModBusEvent
{
    @NotNull
    final Registry<?> vanillaRegistry;

    VanillaRegisterEvent(@NotNull Registry<?> vanillaRegistry)
    {
        this.vanillaRegistry = vanillaRegistry;
    }
}
