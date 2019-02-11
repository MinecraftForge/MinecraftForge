/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fluids;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Handles Fluid registrations. Fluids MUST be registered in order to function.
 */
public abstract class FluidRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FLUIDS = MarkerManager.getMarker("FLUIDS");

    static boolean universalBucketEnabled = false;

    /**
     * Enables the universal bucket in forge.
     * Has to be called before pre-initialization.
     * Actually just call it statically in your mod class.
     */
    public static void enableUniversalBucket()
    {
        universalBucketEnabled = true;
    }

    public static boolean isUniversalBucketEnabled()
    {
        return universalBucketEnabled;
    }
}
