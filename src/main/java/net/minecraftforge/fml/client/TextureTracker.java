/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.client;

import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class TextureTracker
{
    private static final SetMultimap<String,ResourceLocation> missingTextures = HashMultimap.create();
    private static final Set<String> badTextureDomains = Sets.newHashSet();
    private static final Table<String, String, Set<ResourceLocation>> brokenTextures = HashBasedTable.create();

    public static void trackMissingTexture(ResourceLocation resourceLocation)
    {
        badTextureDomains.add(resourceLocation.getNamespace());
        missingTextures.put(resourceLocation.getNamespace(),resourceLocation);
    }

    public static void trackBrokenTexture(ResourceLocation resourceLocation, String error)
    {
        badTextureDomains.add(resourceLocation.getNamespace());
        Set<ResourceLocation> badType = brokenTextures.get(resourceLocation.getNamespace(), error);
        if (badType == null)
        {
            badType = Sets.newHashSet();
            brokenTextures.put(resourceLocation.getNamespace(), MoreObjects.firstNonNull(error, "Unknown error"), badType);
        }
        badType.add(resourceLocation);
    }
}
