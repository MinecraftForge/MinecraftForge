/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.config.IConfigEvent;

import java.util.function.Supplier;

public interface IBindingsProvider {
    Supplier<BusGroup> getForgeBusSupplier();
    Supplier<I18NParser> getMessageParser();
    Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration();
}
