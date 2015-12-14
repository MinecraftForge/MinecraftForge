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

package net.minecraftforge.fml.relauncher;

import java.util.Map;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {
                             "net.minecraftforge.fml.common.asm.transformers.BlamingTransformer",
                             "net.minecraftforge.fml.common.asm.transformers.SideTransformer",
                             "net.minecraftforge.fml.common.asm.transformers.EventSubscriptionTransformer",
                             "net.minecraftforge.fml.common.asm.transformers.EventSubscriberTransformer",
                            };
    }

    @Override
    public String getAccessTransformerClass()
    {
        return "net.minecraftforge.fml.common.asm.transformers.AccessTransformer";
    }
    @Override
    public String getModContainerClass()
    {
        return "net.minecraftforge.fml.common.FMLContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "net.minecraftforge.fml.common.asm.FMLSanityChecker";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // don't care about this data
    }
}
