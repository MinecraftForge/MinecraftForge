/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.property;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.state.BooleanProperty;
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
    public static final ModelProperty<IModelTransform> AnimationProperty = new ModelProperty<IModelTransform>();
}
