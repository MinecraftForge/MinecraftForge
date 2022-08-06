/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

/** Implement this interface in a {@link net.minecraft.world.level.levelgen.structure.StructurePiece} class extension to modify its {@link net.minecraft.world.level.levelgen.Beardifier} behavior. */
public interface PieceBeardifierModifier {
    BoundingBox getBeardifierBox();

    TerrainAdjustment getTerrainAdjustment();

    int getGroundLevelDelta();
}
