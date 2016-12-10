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

import javax.annotation.Nonnull;

import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * DifficultyChangeEvent is fired when difficulty is changing. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onDifficultyChange(EnumDifficulty, EnumDifficulty)}.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result. {@link HasResult}<br>
 * ALLOW: final difficulty is given by subscribers.<br>
 * DEFAULT: final difficulty is the same as difficulty in argument.<br>
 * DENY: no change.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@HasResult
public class DifficultyChangeEvent extends Event
{
    private EnumDifficulty difficulty;
    private final EnumDifficulty defaultNewDifficulty;
    private final EnumDifficulty oldDifficulty;

    public DifficultyChangeEvent(EnumDifficulty difficulty, EnumDifficulty oldDifficulty)
    {
        this.difficulty = difficulty;
        this.defaultNewDifficulty = difficulty;
        this.oldDifficulty = oldDifficulty;
    }

    public EnumDifficulty getDifficulty()
    {
        return difficulty;
    }

    public void setDifficulty(@Nonnull EnumDifficulty difficulty)
    {
        this.difficulty = difficulty;
    }

    public EnumDifficulty getDefaultNewDifficulty()
    {
        return defaultNewDifficulty;
    }

    public EnumDifficulty getOldDifficulty()
    {
        return oldDifficulty;
    }
}