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
   public static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> addBuiltinRegistriesEvent(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder)
   {
      REGISTRY_ACCESS_REGISTRIES_COPY = new HashMap<>(builder.build());
      return REGISTRY_ACCESS_REGISTRIES_COPY;
   }

   /** Modders, DO NOT USE. Internal use only */
   public static void fireAddBuiltinRegistriesEvent()
   {
      AddBuiltinRegistryEvent registryEvent = new AddBuiltinRegistryEvent((resourceKey, registryData) ->
      {
         validateRegistryKey(resourceKey);
         if (!BuiltinRegistries.REGISTRY.containsKey(resourceKey.location()))
         {
            throw new IllegalArgumentException("DataPack registry must be marked as DataPack Registry in RegistryBuilder");
         }
         REGISTRY_ACCESS_REGISTRIES_COPY.put(resourceKey, registryData);
      });
      ModLoader.get().postEvent(registryEvent);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation location)
   {
      String namespace = location.getNamespace();
      String path = location.getPath();
      String prefix = getPathPrefix(namespace);
      if (!path.startsWith(prefix))
      {
         path = prefix + path;
      }
      return ResourceKey.createRegistryKey(new ResourceLocation(namespace, path));
   }

   private static String getPathPrefix(String namespace)
   {
      return namespace + '/';
   }

   private static void validateRegistryKey(ResourceKey<? extends Registry<?>> registryKey)
   {
      var location = registryKey.location();
      var prefix = getPathPrefix(location.getNamespace());

      if (!location.getPath().startsWith(prefix))
      {
         throw new IllegalArgumentException(String.format("Registry path must be prefixed with '%s'", prefix));
      }
   }

   public static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> createDataPackRegistry(ResourceKey<? extends Registry<T>> registryKey, String modid, Class<T> baseClass)
   {
      DeferredRegister<T> deferredRegister = DeferredRegister.create(registryKey, modid);
      deferredRegister.makeRegistry(baseClass, () -> new RegistryBuilder<T>().disableSync().disableSaving().isDataPackRegistry());
      return deferredRegister;
   }

   @SuppressWarnings("unchecked")
   public static <T> Optional<Holder<T>> getHolder(ResourceKey<T> key) {
      if (key != null && registryExists(key.registry()))
      {
         ResourceLocation registryName = key.registry();
         Registry<T> registry = (Registry<T>) Registry.REGISTRY.get(registryName);
         if (registry == null)
            registry = (Registry<T>) BuiltinRegistries.REGISTRY.get(registryName);

         if (registry != null)
            return Optional.of(registry.getOrCreateHolder(key));
      }

      return Optional.empty();
   }

   private static boolean registryExists(ResourceLocation registryName)
   {
      return RegistryManager.ACTIVE.getRegistry(registryName) != null
            || Registry.REGISTRY.containsKey(registryName)
            || BuiltinRegistries.REGISTRY.containsKey(registryName);
   }
}