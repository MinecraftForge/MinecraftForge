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

import java.io.File;
import java.util.Map.Entry;

import com.google.common.collect.SetMultimap;

public class DuplicateModsFoundException extends LoaderException {
    private static final long serialVersionUID = 1L;
    public SetMultimap<ModContainer,File> dupes;

    public DuplicateModsFoundException(SetMultimap<ModContainer, File> dupes) {
        this.dupes = dupes;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        stream.println("Duplicate Mods:");
        for (Entry<ModContainer, File> e : dupes.entries())
        {
            stream.println(String.format("\t%s : %s", e.getKey().getModId(), e.getValue().getAbsolutePath()));
        }
        stream.println("");
    }
}
