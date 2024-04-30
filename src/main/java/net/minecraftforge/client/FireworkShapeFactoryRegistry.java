/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.component.FireworkExplosion;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.particle.FireworkParticles;

/**
 * Keeps track of custom firework shape types, because Particle is client side only this can't be on the Shape itself.
 * So sometime during your client initalization call register.
 */
public class FireworkShapeFactoryRegistry {
	private static final Map<FireworkExplosion.Shape, Factory> factories = new HashMap<>();

	public interface Factory {
		void build(FireworkParticles.Starter starter, boolean trail, boolean flicker, int[] colors, int[] fadecolors);
	}

	public static void register(FireworkExplosion.Shape shape, Factory factory) {
		factories.put(shape, factory);
	}

	@Nullable
	public static Factory get(FireworkExplosion.Shape shape) {
		return factories.get(shape);
	}
}
