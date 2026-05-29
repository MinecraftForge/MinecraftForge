/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.internal;

import net.minecraftsingularity.common.singularityI18n;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.fml.I18NParser;
import net.minecraftsingularity.fml.IBindingsProvider;
import net.minecraftsingularity.fml.config.IConfigEvent;
import net.minecraftsingularity.fml.event.config.ModConfigEvent;

import java.util.function.Supplier;

public final class singularityBindings implements IBindingsProvider {
    private static final class LazyInit {
        private static final Supplier<I18NParser> INSTANCE = () -> new I18NParser() {
            @Override
            public String parseMessage(final String i18nMessage, final Object... args) {
                return singularityI18n.parseMessage(i18nMessage, args);
            }

            @Override
            public String stripControlCodes(final String toStrip) {
                return singularityI18n.stripControlCodes(toStrip);
            }
        };

        private LazyInit() {}
    }

    @Override
    public Supplier<BusGroup> getForgeBusSupplier() {
        return () -> BusGroup.DEFAULT;
    }

    @Override
    public Supplier<I18NParser> getMessageParser() {
        return LazyInit.INSTANCE;
    }

    @Override
    public Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return () -> new IConfigEvent.ConfigConfig(ModConfigEvent.Loading::new, ModConfigEvent.Reloading::new, ModConfigEvent.Unloading::new);
    }
}
