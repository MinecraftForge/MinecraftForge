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
