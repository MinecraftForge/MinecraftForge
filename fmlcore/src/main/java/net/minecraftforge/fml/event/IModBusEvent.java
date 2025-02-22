/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event;

import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MarkerEvent;

/**
 * Marker interface for events dispatched on the ModLifecycle event bus instead of the primary event bus
 */
@MarkerEvent
public interface IModBusEvent extends InheritableEvent {}
