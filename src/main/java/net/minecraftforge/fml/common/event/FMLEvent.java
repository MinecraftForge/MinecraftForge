/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.event;

import net.minecraftforge.fml.common.ModContainer;

/**
 * Parent type to all FML events. This is based on Guava EventBus. Event Subscription isn't using the Guava annotation
 * however, it's using a custom annotation specific to FML {@link net.minecraftforge.fml.common.Mod.EventHandler}
 */
public class FMLEvent
{
    public final String getEventType()
    {
        return getClass().getSimpleName();
    }
    public final String description()
    {
       String cn = getClass().getName();
       return cn.substring(cn.lastIndexOf('.')+4,cn.length()-5);
    }
    public void applyModContainer(ModContainer activeContainer) {
        // NO OP
    }
}
