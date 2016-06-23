/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common;

import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class MissingModsException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;
    public final Set<ArtifactVersion> missingMods;
    private final String modName;

    public MissingModsException(Set<ArtifactVersion> missingMods, String id, String name)
    {
        super(String.format("Mod %s (%s) requires %s", id, name, missingMods));
        this.missingMods = missingMods;
        this.modName = name;
    }

    public String getModName()
    {
        return modName;
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
