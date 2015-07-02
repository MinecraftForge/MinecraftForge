/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Guichaguri - implementation
 */

package net.minecraftforge.fml.common;

import com.google.common.eventbus.EventBus;

public class TweakModContainer extends DummyModContainer
{
    private ModMetadata md;

    public TweakModContainer(ModMetadata md)
    {
        super(md);
        this.md = md;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return true;
    }

    @Override
    public String toString()
    {
        return md != null ? getModId() : "Tweak Container ("+md.modId+") @" + System.identityHashCode(this);
    }

}
