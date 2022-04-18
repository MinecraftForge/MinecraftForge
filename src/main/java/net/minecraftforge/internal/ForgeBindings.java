/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.internal;

import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.I18NParser;
import net.minecraftforge.fml.IBindingsProvider;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.function.Supplier;

public class ForgeBindings implements IBindingsProvider {
    @Override
    public Supplier<IEventBus> getForgeBusSupplier() {
        return ()-> MinecraftForge.EVENT_BUS;
    }

    @Override
    public Supplier<I18NParser> getMessageParser() {
        return ()->new I18NParser() {
            @Override
            public String parseMessage(final String i18nMessage, final Object... args) {
                return ForgeI18n.parseMessage(i18nMessage, args);
            }

            @Override
            public String stripControlCodes(final String toStrip) {
                return ForgeI18n.stripControlCodes(toStrip);
            }
        };
    }

    @Override
    public Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return ()->new IConfigEvent.ConfigConfig(ModConfigEvent.Loading::new, ModConfigEvent.Reloading::new);
    }
}
