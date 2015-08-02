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

package net.minecraftforge.fml.common.discovery;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraftforge.fml.common.ModContainer;

public interface ITypeDiscoverer
{
    // main class part, followed by an optional $ and an "inner class" part. $ cannot be last, otherwise scala breaks
    public static Pattern classFile = Pattern.compile("[^\\s\\$]+(\\$[^\\s]+)?\\.class$");

    public List<ModContainer> discover(ModCandidate candidate, ASMDataTable table);
}
