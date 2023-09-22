/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraftforge.event.AttachCapabilitiesEvent;

public interface ICapabilityEventProvider {
    <T> AttachCapabilitiesEvent<T> createAttachCapabilitiesEvent(T obj);
}
