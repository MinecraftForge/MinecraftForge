/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fired when the {@link ForgeModelBakery} is ready to register model loaders
 */
public class ModelRegistryEvent extends Event implements IModBusEvent
{
}
