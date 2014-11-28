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

public class WrongMinecraftVersionException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    public ModContainer mod;

    public WrongMinecraftVersionException(ModContainer mod)
    {
        super(String.format("Wrong Minecraft version for %s", mod.getModId()));
        this.mod = mod;
    }

}
