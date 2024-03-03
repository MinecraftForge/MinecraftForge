/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;

/**
 * Some default context keys, for easier compatibility
 */
public class ContextKeys
{
    /**
     * BlockPos for interacting, breaking and other permissions
     */
    public static final ContextKey<BlockPos> POS = ContextKey.create("pos", BlockPos.class);

    /**
     * The entity can be anything that gets interacted with - a sheep when you try to dye it, skeleton that you attack, etc.
     */
    public static final ContextKey<Entity> TARGET = ContextKey.create("target", Entity.class);

    public static final ContextKey<Direction> FACING = ContextKey.create("facing", Direction.class);
    public static final ContextKey<AABB> AREA = ContextKey.create("area", AABB.class);
    public static final ContextKey<BlockState> BLOCK_STATE = ContextKey.create("blockstate", BlockState.class);
}
