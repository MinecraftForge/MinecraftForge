/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

/**
 * Fired when you should call {@link net.minecraft.client.particle.ParticleManager#registerFactory}.
 * Note that your {@code ParticleType}s should still be registered during the usual registry events, this
 * is only for the factories.
 */
public class ParticleFactoryRegisterEvent extends Event implements IModBusEvent {}
