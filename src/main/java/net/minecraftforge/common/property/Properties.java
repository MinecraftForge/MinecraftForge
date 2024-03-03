/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.property;

import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.data.ModelProperty;

public class Properties
{
    /**
     * Property indicating if the model should be rendered in the static renderer or in the TESR. AnimationTESR sets it to false.
     */
    public static final BooleanProperty StaticProperty = BooleanProperty.create("static");

    /**
     * Property holding the IModelState used for animating the model in the TESR.
     */
    public static final ModelProperty<ModelState> AnimationProperty = new ModelProperty<ModelState>();
}
