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

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a structure loads.
 * This event is fired when the server loads, or when a structure tries to load.
 * The structure will not override structures loaded from the world folder,
 * as these are considered configurations files and should not be modified by mods.
 *
 * Canceling the event will remove the structure.
 *
 */
@Cancelable
public class StructureLoadEvent extends Event
{
    private ResourceLocation name;
    private Template structure;

    public StructureLoadEvent(ResourceLocation name, Template structure)
    {
        this.name = name;
        this.structure = structure;
    }

    public ResourceLocation getName()
    {
        return this.name;
    }
    
    public Template getStructure() {
        return this.structure;
    }

    public void setStructure(Template structure)
    {
        this.structure = structure;
    }
}
