/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model;

import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.dispatch.ModelState;

/**
 * Simple implementation of {@link ModelState}.
 */
public record SimpleModelState(Transformation transformation) implements ModelState {}
