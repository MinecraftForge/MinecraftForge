/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.entity;

import net.minecraft.world.entity.MobCategory;
import net.minecraftsingularity.fml.common.Mod;

@Mod("create_entity_classification_test")
public class CreateEntityClassificationTest
{
    public static MobCategory test = MobCategory.create("TEST", "test", 1, true, true, 128);
}
