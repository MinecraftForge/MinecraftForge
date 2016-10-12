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

public class WrongMinecraftVersionException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;
    public ModContainer mod;
    private String mcVersion;

    public WrongMinecraftVersionException(ModContainer mod, String mcver)
    {
        super(String.format("Wrong Minecraft version for %s", mod.getModId()));
        this.mod = mod;
        this.mcVersion = mcver;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream) {
        stream.println("Wrong Minecraft Versions!");
        stream.println("Mod: " + mod.getModId());
        stream.println("Location: " + mod.getSource().toString());
        stream.println("Expected: " + mod.acceptableMinecraftVersionRange().toString());
        stream.println("Current: " + mcVersion);
        stream.println("");
    }

}
