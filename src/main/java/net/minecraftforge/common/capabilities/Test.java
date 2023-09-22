/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraftforge.event.AttachCapabilitiesEvent;

public class Test implements ICapabilityEventProvider {
    @Override
    public <T> AttachCapabilitiesEvent<T> createAttachCapabilitiesEvent(T obj) {
        return new AttachEvent<>(obj);
    }

    public static class AttachEvent<T> extends AttachCapabilitiesEvent<T> {
        public AttachEvent(T obj) {
            super(obj);
        }
    }
}
