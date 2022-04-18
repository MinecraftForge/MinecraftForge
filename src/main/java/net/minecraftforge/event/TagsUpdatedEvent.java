/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.core.RegistryAccess;
import net.minecraftforge.eventbus.api.Event;

public class TagsUpdatedEvent extends Event
{
    private final RegistryAccess registries;

     public TagsUpdatedEvent(RegistryAccess registries)
     {
         this.registries = registries;
     }

     /**
      * @return The dynamic registries that have had their tags rebound.
      */
     public RegistryAccess getTagManager()
     {
         return registries;
     }
}
