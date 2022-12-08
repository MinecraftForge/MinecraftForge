/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class DataPackRegistryEvent extends Event implements IModBusEvent
{
    @ApiStatus.Internal
    public DataPackRegistryEvent() {}

    /**
     * Fired when datapack registries can be registered.
     * Datapack registries are registries which can only load entries through JSON files from datapacks.
     * <p>
     * Data JSONs will be loaded from {@code data/<datapack_namespace>/modid/registryname/}, where {@code modid} is the namespace of the registry key.
     * <p>
     * This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * on both {@linkplain LogicalSide logical sides}.
     */
    public static final class NewRegistry extends DataPackRegistryEvent
    {
        private final List<DataPackRegistryData<?>> registryDataList = new ArrayList<>();

        @ApiStatus.Internal
        public NewRegistry() {}

        /**
         * Registers the given registry key as an unsynced datapack registry, which will cause data to be loaded from
         * a datapack folder based on the registry's name. The datapack registry is not required to be present
         * on clients when connecting to servers with the mod/registry.
         * <p>
         * Data JSONs will be loaded from {@code data/<datapack_namespace>/modid/registryname/}, where {@code modid} is the namespace of the registry key.
         *
         * @param registryKey the root registry key of the new datapack registry
         * @param codec the codec to be used for loading data from datapacks on servers
         * @see #dataPackRegistry(ResourceKey, Codec, Codec)
         */
        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec)
        {
            this.dataPackRegistry(registryKey, codec, null);
        }

        /**
         * Registers the registry key as a datapack registry, which will cause data to be loaded from
         * a datapack folder based on the registry's name.
         * <p>
         * Data JSONs will be loaded from {@code data/<datapack_namespace>/modid/registryname/}, where {@code modid} is the namespace of the registry key.
         *
         * @param registryKey the root registry key of the new datapack registry
         * @param codec the codec to be used for loading data from datapacks on servers
         * @param networkCodec the codec to be used for syncing loaded data to clients.
         * If {@code networkCodec} is null, data will not be synced, and clients are not required to have this
         * datapack registry to join a server.
         * <p>
         * If {@code networkCodec} is not null, clients must have this datapack registry/mod
         * when joining a server that has this datapack registry/mod.
         * The data will be synced using the network codec and accessible via {@link ClientPacketListener#registryAccess()}.
         * @see #dataPackRegistry(ResourceKey, Codec)
         */
        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec)
        {
            this.registryDataList.add(new DataPackRegistryData<>(new RegistryDataLoader.RegistryData<>(registryKey, codec), networkCodec));
        }

        void process()
        {
            for (DataPackRegistryData<?> registryData : this.registryDataList)
            {
                DataPackRegistriesHooks.addRegistryCodec(registryData);
            }
        }
    }

    record DataPackRegistryData<T>(RegistryDataLoader.RegistryData<T> loaderData, @Nullable Codec<T> networkCodec) {}
}
