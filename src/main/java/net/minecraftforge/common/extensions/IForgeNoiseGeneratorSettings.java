/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ForgeNoiseGeneratorType;

public interface IForgeNoiseGeneratorSettings
{
    public static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * Mods' noise generators that extend the vanilla class must override this method.
     * @return The noise generator type for this class (which provides the codec for reserializing)
     */
    default ForgeNoiseGeneratorType<? extends DimensionSettings> getType()
    {
        Class<? extends IForgeNoiseGeneratorSettings> thisClass = this.getClass();
        if (thisClass != DimensionSettings.class)
        {
            LOGGER.error("Incomplete class: Non-vanilla {} must override getType!", thisClass); // TODO make this a throw in 1.17 beta
        }
        return ForgeMod.VANILLA_NOISE_GENERATOR_TYPE.get();
    }
}
