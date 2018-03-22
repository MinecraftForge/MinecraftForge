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

package net.minecraftforge.event;

import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when advancements load.
 * This event is fired when the server loads, or when the /reload command runs.
 * The advancements will not override advancements loaded from the world folder,
 * as these are considered configurations files and should not be modified by mods.
 */
public class AdvancementLoadEvent extends Event
{
    private Map<ResourceLocation, Advancement.Builder> advancements;

    public AdvancementLoadEvent(Map<ResourceLocation, Advancement.Builder> advancements)
    {
        this.advancements = advancements;
    }

    public Map<ResourceLocation, Advancement.Builder> getAdvancements()
    {
        return this.advancements;
    }

    public void addAdvancement(ResourceLocation name, Advancement.Builder builder)
    {
        this.advancements.put(name, builder);
    }
}
