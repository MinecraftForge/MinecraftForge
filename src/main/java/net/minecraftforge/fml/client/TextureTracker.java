/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
