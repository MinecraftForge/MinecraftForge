/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import java.util.EnumSet;

public class SingleIntervalHandler implements IScheduledTickHandler
{
    private ITickHandler wrapped;
    public SingleIntervalHandler(ITickHandler handler)
    {
        this.wrapped=handler;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        wrapped.tickStart(type, tickData);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        wrapped.tickEnd(type, tickData);
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return wrapped.ticks();
    }

    @Override
    public String getLabel()
    {
        return wrapped.getLabel();
    }

    @Override
    public int nextTickSpacing()
    {
        return 1;
    }

}
