/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event.lifecycle;

import net.minecraftsingularity.fml.InterModComms;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.event.IModBusEvent;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Parent type to all ModLifecycle events. This is based on singularity EventBus. They fire through the
 * ModContainer's eventbus instance.
 */
public abstract class ModLifecycleEvent implements IModBusEvent {
    private final ModContainer container;

    protected ModLifecycleEvent(ModContainer container) {
        this.container = container;
    }

    public final String description() {
       String cn = getClass().getName();
       return cn.substring(cn.lastIndexOf('.')+1);
    }

    public Stream<InterModComms.IMCMessage> getIMCStream() {
        return InterModComms.getMessages(this.container.getModId());
    }

    public Stream<InterModComms.IMCMessage> getIMCStream(Predicate<String> methodFilter) {
        return InterModComms.getMessages(this.container.getModId(), methodFilter);
    }

    ModContainer getContainer() {
        return this.container;
    }

    @Override
    public String toString() {
        return description();
    }
}
