/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.world.AddBuiltinRegistryEvent;
import net.minecraftforge.fml.ModLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataPackRegistriesHooks
{
   private static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> REGISTRY_ACCESS_REGISTRIES_COPY;

   /** Modders, DO NOT USE. Internal use only */
   @Deprecated
   public static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> addBuiltinRegistriesEvent(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder)
   {
      REGISTRY_ACCESS_REGISTRIES_COPY = new HashMap<>(builder.build());
      return REGISTRY_ACCESS_REGISTRIES_COPY;
   }

   /** Modders, DO NOT USE. Internal use only */
   @Deprecated
   public static void fireAddBuiltinRegistriesEvent()
   {
      AddBuiltinRegistryEvent registryEvent = new AddBuiltinRegistryEvent((resourceKey, registryData) ->
      {
         validateRegistryKey(resourceKey);
         if (!BuiltinRegistries.REGISTRY.containsKey(resourceKey.location()))
         {
            throw new IllegalArgumentException("Data-pack registries must be marked as such via RegistryBuilder#dataPackRegistry");
         }
         REGISTRY_ACCESS_REGISTRIES_COPY.put(resourceKey, registryData);
      });
      ModLoader.get().postEvent(registryEvent);
   }

   private static void validateRegistryKey(ResourceKey<? extends Registry<?>> registryKey)
   {
      var location = registryKey.location();
      var prefix = location.getNamespace() + '/';

      if (!location.getPath().startsWith(prefix))
      {
         throw new IllegalArgumentException(String.format("Registry path must be prefixed with '%s'", prefix));
      }
   }
}