/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.model;

import java.util.Optional;

/*
 * Represents the dynamic information associated with the model.
 * Common use case is (possibly interpolated) animation frame.
 */
public interface IModelState
{
    /*
     * Returns the transformation that needs to be applied to the specific part of the model.
     * Coordinate system is determined by the part type.
     * if no part is provided, global model transformation is returned.
     */
    Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part);
}