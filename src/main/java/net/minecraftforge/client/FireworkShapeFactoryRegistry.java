/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.world.item.FireworkRocketItem;

/**
 * Keeps track of custom firework shape types, because Particle is client side only this can't be on the Shape itself.
 * So sometime during your client initalization call register.
 */
public class FireworkShapeFactoryRegistry {
	private static final Map<FireworkRocketItem.Shape, Factory> factories = new HashMap<>();

	public static interface Factory {
		void build(FireworkParticles.Starter starter, boolean trail, boolean flicker, int[] colors, int[] fadecolors);
	}

	public static void register(FireworkRocketItem.Shape shape, Factory factory) {
		factories.put(shape, factory);
	}

	@Nullable
	public static Factory get(FireworkRocketItem.Shape shape) {
		return factories.get(shape);
	}
}
