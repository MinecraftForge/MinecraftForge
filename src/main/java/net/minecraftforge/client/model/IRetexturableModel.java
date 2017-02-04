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

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;

public interface IRetexturableModel extends IModel
{
    /**
     * Applies new textures to the model.
     * The returned model should be independent of the accessed one,
     * as a model should be able to be retextured multiple times producing
     * a separate model each time.
     *
     * The input map MAY map to an empty string "" which should be used
     * to indicate the texture was removed. Handling of that is up to
     * the model itself. Such as using default, missing texture, or
     * removing vertices.
     *
     * The input should be considered a DIFF of the old textures, not a
     * replacement as it may not contain everything.
     *
     * @param textures New
     * @return Model with textures applied.
     */
    IModel retexture(ImmutableMap<String, String> textures);
}
