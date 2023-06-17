/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.config.widgets;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A value manager which processes the values entered and validated by the user.
 *
 * @param initial A callback to get the initial value of the configuration entry. In-case the entry is set back to its initial value.
 * @param getter A callback to get the current value of the configuration entry. In-case it needs to be read.
 * @param setter A callback to set the new value of the configuration entry.
 */
public record ValueManager(Supplier<Object> initial, Supplier<Object> getter, Consumer<Object> setter)
{
}
