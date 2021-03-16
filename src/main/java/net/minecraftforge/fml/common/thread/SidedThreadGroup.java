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

package net.minecraftforge.fml.common.thread;

import net.minecraftforge.fml.LogicalSide;

import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;

/**
 * A thread group and factory combination which belongs to a {@link LogicalSide}.
 */
public final class SidedThreadGroup extends ThreadGroup implements ThreadFactory
{
    private final LogicalSide side;

    SidedThreadGroup(final LogicalSide side)
    {
        super(side.name());
        this.side = side;
    }

    /**
     * Gets the side this sided thread group belongs to.
     *
     * @return the side
     */
    public LogicalSide getSide()
    {
        return this.side;
    }

    @Override
    public Thread newThread(@Nonnull final Runnable runnable)
    {
        return new Thread(this, runnable);
    }
}
