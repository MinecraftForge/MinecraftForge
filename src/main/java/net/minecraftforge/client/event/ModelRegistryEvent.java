/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

/**
 * Fired when the {@link net.minecraftforge.client.model.ModelLoader} is ready to register model loaders
 */
public class ModelRegistryEvent extends Event implements IModBusEvent
{
}
