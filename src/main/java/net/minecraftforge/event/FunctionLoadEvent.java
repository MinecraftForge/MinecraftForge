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

import net.minecraft.command.FunctionObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event fired when a FunctionObject tries to load.
 * This event is fired when the server loads, or when the /reload command runs.
 * This event will not be fired for functions loaded from the world folder, as these
 * are considered configurations files and should not be modified by mods.
 */
public class FunctionLoadEvent extends Event
{
	private Map<ResourceLocation, FunctionObject> functions;

    public FunctionLoadEvent(Map<ResourceLocation, FunctionObject> functions)
    {
        this.functions = functions;
    }

    public Map<ResourceLocation, FunctionObject> getFunctions()
    {
        return this.functions;
    }

    public void addFunction(ResourceLocation name, FunctionObject function)
    {
        this.functions.put(name, function);
    }
}
