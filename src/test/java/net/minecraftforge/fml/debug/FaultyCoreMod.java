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

package net.minecraftforge.fml.debug;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Map;

public class FaultyCoreMod implements IFMLLoadingPlugin
{
    public static boolean enabled = false;

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{FaultyTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }

    public static class FaultyTransformer implements IClassTransformer
    {

        @Override
        public byte[] transform(String name, String transformedName, byte[] basicClass)
        {
            if (enabled && name.equals("net.minecraft.client.gui.GuiMainMenu"))
            {
                throw new RuntimeException("Faulty transformer test exception");
            }
            return basicClass;
        }
    }
}
