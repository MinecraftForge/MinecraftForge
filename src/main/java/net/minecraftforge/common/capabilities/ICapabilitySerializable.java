/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

//Just a mix of the two, useful in patches to lower the size.
public interface ICapabilitySerializable<T extends Tag> extends ICapabilityProvider, INBTSerializable<T>{}
