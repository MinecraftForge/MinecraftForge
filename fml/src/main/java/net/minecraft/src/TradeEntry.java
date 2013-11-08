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

package net.minecraft.src;

/**
 * Compatibility class for ModLoader -- do not use
 *
 * @author cpw
 *
 */
@Deprecated
public class TradeEntry
{
    @Deprecated
    public final int id;
    @Deprecated
    public float chance;
    @Deprecated
    public boolean buying;
    @Deprecated
    public int min = 0;
    @Deprecated
    public int max = 0;

    @Deprecated
    public TradeEntry(int id, float chance, boolean buying, int min, int max)
    {
        this.id = id;
        this.chance = chance;
        this.buying = buying;
        this.min = min;
        this.max = max;
    }

    @Deprecated
    public TradeEntry(int id, float chance, boolean buying)
    {
        this(id, chance, buying, 0, 0);
    }
}
