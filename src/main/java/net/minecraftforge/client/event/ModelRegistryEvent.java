/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fired when the {@link ModelLoaderRegistry} is ready to register model loaders
 *
 * @see ModelLoaderRegistry#registerLoader(ResourceLocation, IModelLoader)
 */
public class ModelRegistryEvent extends Event implements IModBusEvent
{
}
