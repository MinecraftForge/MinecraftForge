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

/*package net.minecraftforge.event.world;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager.SavedEntry;
import net.minecraftforge.eventbus.api.Event;

/**
 * Register all of your custom ModDimensons here, fired during server loading when
 * dimension data is read from the world file.
 *
 * Contains a list of missing entries. Registering an entry with the DimensionManger
 * will remove the matching entry from the missing list.
 * /
public class RegisterDimensionsEvent extends Event
{
    private final Map<ResourceLocation, SavedEntry> missing;
    private final Set<ResourceLocation> keys;

    public RegisterDimensionsEvent(Map<ResourceLocation, SavedEntry> missing)
    {
        this.missing = missing;
        this.keys = Collections.unmodifiableSet(this.missing.keySet());
    }

    public Set<ResourceLocation> getMissingNames()
    {
        return keys;
    }

    @Nullable
    public SavedEntry getEntry(ResourceLocation key)
    {
        return missing.get(key);
    }
}*/
