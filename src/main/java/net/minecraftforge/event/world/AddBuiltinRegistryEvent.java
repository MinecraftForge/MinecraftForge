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

public class AddBuiltinRegistryEvent extends Event implements IModBusEvent
{
   private final BiConsumer<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> dataPackRegistryConsumer;

   public AddBuiltinRegistryEvent(BiConsumer<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> dataPackRegistryConsumer)
   {
      this.dataPackRegistryConsumer = dataPackRegistryConsumer;
   }

   public <T> AddBuiltinRegistryEvent addRegistry(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull Codec<T> codec)
   {
      return this.addRegistry(registryKey, codec, null);
   }

   public <T> AddBuiltinRegistryEvent addRegistry(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull Codec<T> codec, @Nullable Codec<T> networkCodec)
   {
      this.dataPackRegistryConsumer.accept(registryKey, new RegistryAccess.RegistryData<>(registryKey, codec, networkCodec));
      return this;
   }
}