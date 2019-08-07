package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when you should call {@link net.minecraft.client.particle.ParticleManager#registerFactory}.
 * Note that your {@code ParticleType}s should still be registered during the usual registry events, this
 * is only for the factories.
 */
public class ParticleRegistryEvent extends Event {
}
