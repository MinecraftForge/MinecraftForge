/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.fml.config.IConfigEvent;

import java.util.function.Supplier;

public interface IBindingsProvider {
    Supplier<BusGroup> getForgeBusSupplier();
    Supplier<I18NParser> getMessageParser();
    Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration();
}
