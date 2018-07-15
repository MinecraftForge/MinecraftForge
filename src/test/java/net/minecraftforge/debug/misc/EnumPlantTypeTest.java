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

package net.minecraftforge.debug.misc;

import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "enumplanttypetest", name = "EnumPlantTypeTest", version = "1.0", acceptableRemoteVersions = "*")
public class EnumPlantTypeTest
{
    private static Logger logger;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        BiomeType biomeType = null;
        try
        {
            biomeType = BiomeType.getType("FAKE");
        }
        catch (NullPointerException npe)
        {
            logger.warn("EnumHelper in BiomeType is working incorrectly!", npe);
        }
        finally
        {
            if (biomeType == null || !biomeType.name().equals("FAKE"))
            {
                logger.warn("EnumHelper in BiomeType is working incorrectly!");
            }
        }
        EnumPlantType plantType = null;
        try
        {
            plantType = EnumPlantType.getPlantType("FAKE");
        }
        catch (NullPointerException npe)
        {
            logger.warn("EnumHelper in EnumPlantType is working incorrectly!", npe);
        }
        finally
        {
            if (plantType == null || !plantType.name().equals("FAKE"))
            {
                logger.warn("EnumHelper in EnumPlantType is working incorrectly!");
            }
        }
    }
}
