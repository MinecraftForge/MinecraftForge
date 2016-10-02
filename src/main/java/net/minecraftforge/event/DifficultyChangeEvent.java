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

import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * DifficultyChangeEvent is fired when difficulty is changing. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onDifficultyChange(EnumDifficulty, EnumDifficulty)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the difficulty is not changed.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class DifficultyChangeEvent extends Event
{
    private final EnumDifficulty oldDifficulty;
    private EnumDifficulty newDifficulty;
    public DifficultyChangeEvent(EnumDifficulty oldDifficulty, EnumDifficulty newDifficulty)
    {
        this.oldDifficulty = oldDifficulty;
        this.newDifficulty = newDifficulty;
    }

    public EnumDifficulty getOldDifficulty() { return oldDifficulty; }

    public EnumDifficulty getNewDifficulty() { return newDifficulty; }

    public void setNewDifficulty(EnumDifficulty newDifficulty)
    {
        if (newDifficulty == null) return;
        this.newDifficulty = newDifficulty;
    }
}
