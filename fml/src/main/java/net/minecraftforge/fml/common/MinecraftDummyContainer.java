/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class MinecraftDummyContainer extends DummyModContainer
{

    private VersionRange staticRange;
    public MinecraftDummyContainer(String actualMCVersion)
    {
        super(new ModMetadata());
        getMetadata().modId = "Minecraft";
        getMetadata().name = "Minecraft";
        getMetadata().version = actualMCVersion;
        staticRange = VersionParser.parseRange("["+actualMCVersion+"]");
    }


    public VersionRange getStaticVersionRange()
    {
        return staticRange;
    }
}
