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

public interface IScheduledTickHandler extends ITickHandler
{
    /**
     * Return the number of actual ticks that will pass
     * before your next tick will fire. This will be called
     * just after your last tick fired to compute the next delay.
     * @return Time until next tick
     */
    public int nextTickSpacing();
}
