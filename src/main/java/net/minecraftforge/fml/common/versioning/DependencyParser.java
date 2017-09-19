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

package net.minecraftforge.fml.common.versioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.relauncher.Side;

public final class DependencyParser
{
    public static class DependencyInfo
    {
        public final Set<ArtifactVersion> requirements = new HashSet<>();
        public final Set<ArtifactVersion> softRequirements = new HashSet<>();
        public final List<ArtifactVersion> dependencies = new ArrayList<>();
        public final List<ArtifactVersion> dependants = new ArrayList<>();
    }

    private static final ImmutableList<String> DEPENDENCY_INSTRUCTIONS = ImmutableList.of("client", "server", "required", "before", "after");
    private static final Splitter DEPENDENCY_INSTRUCTIONS_SPLITTER = Splitter.on("-").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCY_PART_SPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCY_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

    private final Side side;

    public DependencyParser(Side side)
    {
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
            final List<String> instructions;
            final String target;

            final List<String> depParts = DEPENDENCY_PART_SPLITTER.splitToList(dep);
            if (depParts.size() == 1)
            {
                instructions = Collections.emptyList();
                target = depParts.get(0);
            }
            else if (depParts.size() == 2)
            {
                instructions = DEPENDENCY_INSTRUCTIONS_SPLITTER.splitToList(depParts.get(0));
                target = depParts.get(1);
            }
            else
            {
                throw new DependencyParserException(dep, "Dependency string needs 1 or 2 parts.");
            }

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
                throw new DependencyParserException(dep, "Cannot have an \"all\" (*) relationship with anything except pure *");
            }
            else if (targetIsBounded)
            {
                throw new DependencyParserException(dep, "You cannot have a versioned dependency on everything (*)");
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
                    throw new DependencyParserException(dep, "Up to one side (client or server) can be specified.");
                }
                depSide = Side.CLIENT;
            }
            else if ("server".equals(instruction))
            {
                if (depSide != null)
                {
                    throw new DependencyParserException(dep, "Up to one side (client or server) can be specified.");
                }
                depSide = Side.SERVER;
            }
            else if ("required".equals(instruction))
            {
                if (depRequired)
                {
                    throw new DependencyParserException(dep, "'required' can only be specified once.");
                }
                if (targetIsAll)
                {
                    throw new DependencyParserException(dep, "You can't 'require' everything (*)");
                }
                depRequired = true;
            }
            else if ("before".equals(instruction) || "after".equals(instruction))
            {
                if (depOrder != null)
                {
                    throw new DependencyParserException(dep, "'before' or 'after' can only be specified once.");
                }
                depOrder = instruction;
            }
            else
            {
                throw new DependencyParserException(dep, String.format("Found invalid instruction '%s'. Only %s are allowed.", instruction, DEPENDENCY_INSTRUCTIONS));
            }
        }

        if (depSide != null && depSide != this.side)
        {
            return;
        }

        ArtifactVersion artifactVersion;
        try
        {
            artifactVersion = VersionParser.parseVersionReference(target);
        }
        catch (RuntimeException e)
        {
            throw new DependencyParserException(dep, "Could not parse version string.", e);
        }

        // TODO: enable this in 1.13
//        if (!targetIsAll)
//        {
//            String modId = artifactVersion.getLabel();
//            sanityCheckModId(dep, modId);
//        }

        if (!depRequired && depOrder == null && !targetIsBounded)
        {
            throw new DependencyParserException(dep, "Soft dependencies must have a version restriction specified.");
        }

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
        else if (!depRequired && depOrder == null)
        {
            info.softRequirements.add(artifactVersion);
        }
    }

    /** Based on {@link net.minecraftforge.fml.common.FMLModContainer#sanityCheckModId()} */
    private static void sanityCheckModId(String dep, String modId)
    {
        if (Strings.isNullOrEmpty(modId))
        {
            throw new DependencyParserException(dep, "The modId is null or empty");
        }
        if (modId.length() > 64)
        {
            throw new DependencyParserException(dep, String.format("The modId %s is longer than the maximum of 64 characters.", modId));
        }
        if (!modId.equals(modId.toLowerCase(Locale.ENGLISH)))
        {
            throw new DependencyParserException(dep, String.format("The modId %s must be all lowercase.", modId));
        }
    }

    private static class DependencyParserException extends LoaderException
    {
        public DependencyParserException(String dependencyString, String explanation)
        {
            super(formatMessage(dependencyString, explanation));
        }

        public DependencyParserException(String dependencyString, String explanation, Throwable cause)
        {
            super(formatMessage(dependencyString, explanation), cause);
        }

        private static String formatMessage(String dependencyString, String explanation)
        {
            return String.format("Unable to parse dependency string %s, cause - \"%s\"", dependencyString, explanation);
        }
    }
}
