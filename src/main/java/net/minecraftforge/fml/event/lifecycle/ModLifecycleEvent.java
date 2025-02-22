/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.event.MarkerEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Parent type to all ModLifecycle events. This is based on Forge EventBus. They fire through the
 * ModContainer's eventbus instance.
 */
@MarkerEvent
public sealed interface ModLifecycleEvent extends IModBusEvent permits ParallelDispatchEvent {
    default String description() {
       String cn = getClass().getName();
       return cn.substring(cn.lastIndexOf('.') + 1);
    }

    default Stream<InterModComms.IMCMessage> getIMCStream() {
        return InterModComms.getMessages(container().getModId());
    }

    default Stream<InterModComms.IMCMessage> getIMCStream(Predicate<String> methodFilter) {
        return InterModComms.getMessages(container().getModId(), methodFilter);
    }

    ModContainer container();
}
