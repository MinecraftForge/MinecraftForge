/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * <p>
 *    This event is fired on the Mod event bus at the time of FMLLoadCompleteEvent
 * </p>
 * <p>
 *    This event is used to register the Codec to a data-pack-registry. All Registries passed here should be marked as data-pack-registries
 *    with RegistryBuilder#dataPackRegistry.
 * </p>
 * <p>
 *    You should avoid the usage of {link AddBuiltinRegistryEvent#addRegistry(ResourceKey, Codec, Codec)},
 *    because clients missing the required network codec can cause strange issues
 * </p>
 */
public class AddBuiltinRegistryEvent extends Event implements IModBusEvent
{
   private final BiConsumer<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> dataPackRegistryConsumer;

   public AddBuiltinRegistryEvent(BiConsumer<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> dataPackRegistryConsumer)
   {
      this.dataPackRegistryConsumer = dataPackRegistryConsumer;
   }

   /**
    * @param registryKey The passed ResourceKey should belong to a Registry marked as data-pack-registry with RegistryBuilder#dataPackRegistry
    * @param codec this codec is used to serialize elements from data-packs to the registry
    */
   public <T> AddBuiltinRegistryEvent addRegistry(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull Codec<T> codec)
   {
      return this.addRegistry(registryKey, codec, null);
   }

   /**
    * @param registryKey The passed ResourceKey should belong to a Registry marked as data-pack-registry with RegistryBuilder#dataPackRegistry
    * @param codec this codec is used to serialize elements from data-packs to the registry
    * @param networkCodec the network codec is used to serialize all registry elements for client-server sync. If you want to disable sync, pass {@code null}
    */
   public <T> AddBuiltinRegistryEvent addRegistry(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull Codec<T> codec, @Nullable Codec<T> networkCodec)
   {
      this.dataPackRegistryConsumer.accept(registryKey, new RegistryAccess.RegistryData<>(registryKey, codec, networkCodec));
      return this;
   }
}