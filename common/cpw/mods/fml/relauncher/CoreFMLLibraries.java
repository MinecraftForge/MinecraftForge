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

package cpw.mods.fml.relauncher;

public class CoreFMLLibraries implements ILibrarySet
{
    private static String[] libraries = { "argo-small-3.2.jar","guava-14.0-rc3.jar","asm-all-4.1.jar", "bcprov-jdk15on-148.jar", FMLInjectionData.debfuscationDataName(), "scala-library.jar" };
    private static String[] checksums = { "58912ea2858d168c50781f956fa5b59f0f7c6b51", "931ae21fa8014c3ce686aaa621eae565fefb1a6a", "054986e962b88d8660ae4566475658469595ef58", "960dea7c9181ba0b17e8bab0c06a43f0a5f04e65", FMLInjectionData.deobfuscationDataHash, "458d046151ad179c85429ed7420ffb1eaf6ddf85" };

    @Override
    public String[] getLibraries()
    {
        return libraries;
    }

    @Override
    public String[] getHashes()
    {
        return checksums;
    }

    @Override
    public String getRootURL()
    {
        return System.getProperty("fml.core.libraries.mirror", "http://files.minecraftforge.net/fmllibs/%s");
    }

}
