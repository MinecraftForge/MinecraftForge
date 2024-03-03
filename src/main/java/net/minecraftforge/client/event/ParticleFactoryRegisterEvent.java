/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fired when you should call {@link ParticleEngine#register(ParticleType, ParticleProvider)}.
 * Note that your {@code ParticleType}s should still be registered during the usual registry events, this
 * is only for the factories.
 */
public class ParticleFactoryRegisterEvent extends Event implements IModBusEvent {}
