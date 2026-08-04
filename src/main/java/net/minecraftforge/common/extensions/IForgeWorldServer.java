/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.server.ServerWorld;

public interface IForgeWorldServer extends IForgeWorld
{
    default ServerWorld getWorldServer() { return (ServerWorld) this; }
}
