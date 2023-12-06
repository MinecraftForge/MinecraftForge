/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Bindings {
    private static final Bindings INSTANCE = new Bindings();

    private final IBindingsProvider provider;

    private Bindings() {
        var providers = new ArrayList<IBindingsProvider>(1);
        for (var itr = ServiceLoader.load(FMLLoader.getGameLayer(), IBindingsProvider.class).iterator(); itr.hasNext(); ) {
            try {
                providers.add(itr.next());
            } catch (ServiceConfigurationError sce) {
                sce.printStackTrace();
            }
        }

        if (providers.size() != 1)
            throw new IllegalStateException("Could not find bindings provider: " + providers);

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
