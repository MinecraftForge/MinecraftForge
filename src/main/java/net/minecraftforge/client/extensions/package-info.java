/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/**
 * Extension interfaces for {@link net.minecraftforge.api.distmarker.Dist#CLIENT client}-only classes.
 *
 * <p>Extension interfaces allow the convenient addition of methods to the target class, as the target class is patched
 * to implement the interface. Because of this, these interfaces must only be implemented by the target classes, and must
 * not be implemented manually by mods.</p>
 *
 * <p>These interfaces hold at least one method: the {@code self()} method which casts the object into the type of the
 * targeted class, to allow methods in the extension interface to reference methods from the target class. Additional
 * methods are usually {@code default} with an implementation in the interface itself, but methods may be implemented
 * in the target class instead if it requires access to (patched-in or original) fields in the instance.</p>
 */
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
package net.minecraftforge.client.extensions;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;