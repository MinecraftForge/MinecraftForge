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

import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class MissingModsException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;
    public final Set<ArtifactVersion> missingMods;

    public MissingModsException(Set<ArtifactVersion> missingMods, String id, String name)
    {
        super(String.format("Mod %s (%s) requires %s", id, name, missingMods));
        this.missingMods = missingMods;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println("Missing Mods:");
        for (ArtifactVersion v : missingMods)
        {
            stream.println(String.format("\t%s : %s", v.getLabel(), v.getRangeString()));
        }
        stream.println("");
    }
}
