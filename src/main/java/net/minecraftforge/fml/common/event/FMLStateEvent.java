/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.relauncher.Side;

/**
 * The parent of all mod-state changing events
 */
public abstract class FMLStateEvent extends FMLEvent
{
    public FMLStateEvent(Object... data)
    {

    }

    /**
     * The current state of the mod
     * @return The current state of the mod
     */
    public abstract ModState getModState();

    /**
     * The side we're loading on. {@link Side#CLIENT} means we're loading in the client, {@link Side#SERVER} means
     * we're loading in the dedicated server.
     * @return Return which side we're loading on.
     */
    public Side getSide()
    {
        return FMLCommonHandler.instance().getSide();
    }
}
