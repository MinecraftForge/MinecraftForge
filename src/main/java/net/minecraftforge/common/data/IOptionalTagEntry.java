/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.tags.Tag.ITagEntry;

/**
 * Marker class used by Forge to make a tag entry optional at runtime
 */
public interface IOptionalTagEntry<T> extends ITagEntry<T> {}
