/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.relauncher;

import java.util.Map;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {
                             "net.minecraftforge.fml.common.asm.transformers.SideTransformer",
                             "net.minecraftforge.fml.common.asm.transformers.EventSubscriptionTransformer",
                             "net.minecraftforge.fml.common.asm.transformers.EventSubscriberTransformer",
                             "net.minecraftforge.fml.common.asm.transformers.SoundEngineFixTransformer"
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
