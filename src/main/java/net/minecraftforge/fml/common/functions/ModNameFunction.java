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

package net.minecraftforge.fml.common.functions;

import net.minecraftforge.fml.common.ModContainer;

import com.google.common.base.Function;

public class ModNameFunction implements Function<ModContainer, String>
{
    @Override
    public String apply(ModContainer input)
    {
        return input.getName();
    }

}
