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

/**
 * Interface for certain core plugins to register libraries to
 * be loaded in by the FML class loader at launch time
 * Deprecated without replacement.
 *
 * @author cpw
 *
 */
@Deprecated
public interface ILibrarySet
{
    /**
     * Return a list of libraries available from a common location
     *
     * @return a list of libraries available from a common location
     */
    String[] getLibraries();
    /**
     * Return the string encoded sha1 hash for each library in the returned list
     *
     * @return the string encoded sha1 hash for each library in the returned list
     */
    String[] getHashes();
    /**
     * Return the root URL format string from which this library set can be obtained
     * There needs to be a single %s string substitution which is the library name
     * @return the root URL format string from which this library set can be obtained
     */
    String getRootURL();
}
