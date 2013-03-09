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

import java.util.Set;

import com.google.common.collect.Sets.SetView;

import cpw.mods.fml.common.versioning.ArtifactVersion;

public class MissingModsException extends RuntimeException
{

    public Set<ArtifactVersion> missingMods;

    public MissingModsException(Set<ArtifactVersion> missingMods)
    {
        this.missingMods = missingMods;
    }
}
