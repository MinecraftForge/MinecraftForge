/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.property;

import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.data.ModelProperty;

/// @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
public class Properties {
    /**
     * Property indicating if the model should be rendered in the static renderer or in the TESR. AnimationTESR sets it to false.
     */
    public static final BooleanProperty StaticProperty = BooleanProperty.create("static");

    /**
     * Property holding the IModelState used for animating the model in the TESR.
     */
    public static final ModelProperty<ModelState> AnimationProperty = new ModelProperty<ModelState>();
}
