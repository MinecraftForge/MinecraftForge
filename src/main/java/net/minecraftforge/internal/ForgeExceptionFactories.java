/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.internal;

public class ForgeExceptionFactories {

	public static final IllegalCallerException INTERNAL_METHOD = new IllegalCallerException("This is an internal forge method and cannot be used by mods");
}