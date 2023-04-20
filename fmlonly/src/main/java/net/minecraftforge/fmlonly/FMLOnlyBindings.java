/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmlonly;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.I18NParser;
import net.minecraftforge.fml.IBindingsProvider;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FMLOnlyBindings implements IBindingsProvider {
    private static final IEventBus DUMMYFORGEBUS = BusBuilder.builder().build();

    @Override
    public Supplier<IEventBus> getForgeBusSupplier() {
        return ()->DUMMYFORGEBUS;
    }

    @Override
    public Supplier<I18NParser> getMessageParser() {
        return ()->new I18NParser() {
            @Override
            public String parseMessage(final String i18nMessage, final Object... args) {
                return i18nMessage + Arrays.stream(args).map(Objects::toString).collect(Collectors.joining(",", "[", "]"));
            }

            @Override
            public String stripControlCodes(final String toStrip) {
                return toStrip;
            }
        };
    }

    @Override
    public Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return ()->new IConfigEvent.ConfigConfig(ModConfigEvent.Loading::new, ModConfigEvent.Reloading::new, ModConfigEvent.Unloading::new);
    }
}
