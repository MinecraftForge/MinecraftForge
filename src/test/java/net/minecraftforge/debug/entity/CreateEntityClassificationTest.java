/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraftforge.fml.common.Mod;

@Mod("create_entity_classification_test")
public class CreateEntityClassificationTest
{
    public static EntityClassification test = EntityClassification.create("TEST", "test", 1, true, true, 128);
}
