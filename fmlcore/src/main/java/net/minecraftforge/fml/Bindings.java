/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Bindings {
    private static final Bindings INSTANCE = new Bindings();

    private final IBindingsProvider provider;

    private Bindings() {
        final var providers = ServiceLoaderUtils.streamServiceLoader(()->ServiceLoader.load(FMLLoader.getGameLayer(), IBindingsProvider.class), sce->{}).toList();
        if (providers.size() != 1) {
            throw new IllegalStateException("Could not find bindings provider");
        }
        this.provider = providers.get(0);
    }

    public static Supplier<IEventBus> getForgeBus() {
        return INSTANCE.provider.getForgeBusSupplier();
    }

    public static Supplier<I18NParser> getMessageParser() {
        return INSTANCE.provider.getMessageParser();
    }

    public static Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return INSTANCE.provider.getConfigConfiguration();
    }
}
