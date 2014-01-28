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

import java.util.Map;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {
                             "cpw.mods.fml.common.asm.transformers.MarkerTransformer",
                             "cpw.mods.fml.common.asm.transformers.SideTransformer",
                             "cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer",
                            };
    }

    @Override
    public String getAccessTransformerClass()
    {
        return "cpw.mods.fml.common.asm.transformers.AccessTransformer";
    }
    @Override
    public String getModContainerClass()
    {
        return "cpw.mods.fml.common.FMLContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "cpw.mods.fml.common.asm.FMLSanityChecker";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // don't care about this data
    }
}