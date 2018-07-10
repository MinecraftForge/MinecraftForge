/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common.versioning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.relauncher.Side;

public final class DependencyParser
{
    public static class DependencyInfo
    {
        public final Set<ArtifactVersion> requirements = new HashSet<>();
        public final List<ArtifactVersion> dependencies = new ArrayList<>();
        public final List<ArtifactVersion> dependants = new ArrayList<>();
    }

    private static final ImmutableList<String> DEPENDENCY_INSTRUCTIONS = ImmutableList.of("client", "server", "required", "before", "after");
    private static final Splitter DEPENDENCY_INSTRUCTIONS_SPLITTER = Splitter.on("-").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCY_PART_SPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCY_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

    private final String modId;
    private final Side side;

    public DependencyParser(String modId, Side side)
    {
        this.modId = modId;
        this.side = side;
    }

    public DependencyInfo parseDependencies(String dependencyString)
    {
        DependencyInfo info = new DependencyInfo();
        if (dependencyString == null || dependencyString.length() == 0)
        {
            return info;
        }

        for (String dep : DEPENDENCY_SPLITTER.split(dependencyString))
        {
            final List<String> depParts = DEPENDENCY_PART_SPLITTER.splitToList(dep);
            if (depParts.size() != 2)
            {
                throw new DependencyParserException(modId, dep, "Dependency string needs 2 parts.");
            }

            final List<String>  instructions = DEPENDENCY_INSTRUCTIONS_SPLITTER.splitToList(depParts.get(0));
            final String target = depParts.get(1);
            parseDependency(dep, instructions, target, info);
        }
        return info;
    }

    private void parseDependency(String dep, List<String> instructions, String target, DependencyInfo info)
    {
        final boolean targetIsAll = target.startsWith("*");
        final boolean targetIsBounded = target.contains("@");
        if (targetIsAll)
        {
            if (target.length() > 1)
            {
                throw new DependencyParserException(modId, dep, "Cannot have an \"all\" (*) relationship with anything except pure *");
            }
            else if (targetIsBounded)
            {
                throw new DependencyParserException(modId, dep, "You cannot have a versioned dependency on everything (*)");
            }
        }

        Side depSide = null;
        String depOrder = null;
        boolean depRequired = false;

        for (String instruction : instructions)
        {
            if ("client".equals(instruction))
            {
                if (depSide != null)
                {
                    throw new DependencyParserException(modId, dep, "Up to one side (client or server) can be specified.");
                }
                depSide = Side.CLIENT;
            }
            else if ("server".equals(instruction))
            {
                if (depSide != null)
                {
                    throw new DependencyParserException(modId, dep, "Up to one side (client or server) can be specified.");
                }
                depSide = Side.SERVER;
            }
            else if ("required".equals(instruction))
            {
                if (depRequired)
                {
                    throw new DependencyParserException(modId, dep, "'required' can only be specified once.");
                }
                if (targetIsAll)
                {
                    throw new DependencyParserException(modId, dep, "You can't 'require' everything (*)");
                }
                depRequired = true;
            }
            else if ("before".equals(instruction) || "after".equals(instruction))
            {
                if (depOrder != null)
                {
                    throw new DependencyParserException(modId, dep, "'before' or 'after' can only be specified once.");
                }
                depOrder = instruction;
            }
            else
            {
                throw new DependencyParserException(modId, dep, String.format("Found invalid instruction '%s'. Only %s are allowed.", instruction, DEPENDENCY_INSTRUCTIONS));
            }
        }

        ArtifactVersion artifactVersion;
        try
        {
            artifactVersion = VersionParser.parseVersionReference(target);
        }
        catch (RuntimeException e)
        {
            throw new DependencyParserException(modId, dep, "Could not parse version string.", e);
        }

        if (!targetIsAll)
        {
            String depModId = artifactVersion.getLabel();
            sanityCheckModId(modId, dep, depModId);
        }

        if (!depRequired && depOrder == null)
        {
            throw new DependencyParserException(modId, dep, "'required', 'client', or 'server' must be specified.");
        }

        if (depSide == null || depSide == this.side)
        {
            if (depRequired)
            {
                info.requirements.add(artifactVersion);
            }

            if ("before".equals(depOrder))
            {
                info.dependants.add(artifactVersion);
            }
            else if ("after".equals(depOrder))
            {
                info.dependencies.add(artifactVersion);
            }
        }
    }

    // TODO 1.13: throw these exceptions instead of logging them
    /** Based on {@link net.minecraftforge.fml.common.FMLModContainer#sanityCheckModId()} */
    private static void sanityCheckModId(String modId, String dep, String depModId)
    {
        if (Strings.isNullOrEmpty(depModId))
        {
            FMLLog.log.error(new DependencyParserException(modId, dep, "The modId is null or empty").getMessage());
        }
        else if (depModId.length() > 64)
        {
            FMLLog.log.error(new DependencyParserException(modId, dep, String.format("The modId '%s' is longer than the maximum of 64 characters.", depModId)).getMessage());
        }
        else if (!depModId.equals(depModId.toLowerCase(Locale.ENGLISH)))
        {
            FMLLog.log.error(new DependencyParserException(modId, dep, String.format("The modId '%s' must be all lowercase.", depModId)).getMessage());
        }
    }

    private static class DependencyParserException extends LoaderException
    {
        public DependencyParserException(String modId, String dependencyString, String explanation)
        {
            super(formatMessage(modId, dependencyString, explanation));
        }

        public DependencyParserException(String modId, String dependencyString, String explanation, Throwable cause)
        {
            super(formatMessage(modId, dependencyString, explanation), cause);
        }

        public static String formatMessage(String modId, String dependencyString, String explanation)
        {
            return String.format("Unable to parse dependency for mod '%s' with dependency string '%s'. %s", modId, dependencyString, explanation);
        }
    }
}
