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

package net.minecraftforge.fml.common;

import java.util.List;

public class Java8VersionException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;
    private final List<ModContainer> mods;

    public Java8VersionException(List<ModContainer> mods)
    {
        super("Mods require Java 8");
        this.mods = mods;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println("Mods requiring Java 8:");
        for (ModContainer mc : mods)
        {
            stream.println(String.format("\t%s : %s", mc.getName(), mc.getModId()));
        }
        stream.println("");
    }

    public List<ModContainer> getMods()
    {
        return mods;
    }
}
