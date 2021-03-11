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

package net.minecraftforge.client.event;

import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

/**
 * <p>Allows modders to adjust the lightmap colors for a specific {@code Dimension}.</p>
 *
 * <p>While {@code DimensionRenderInfo} could look like the closest solution for this task,
 * its entries are stored in private, immutable map that is specific to a {@code DimensionType}.
 * There can be many {@code Dimension}(s) per {@code DimensionType}, which means
 * {@code DimensionRenderInfo} is limited in both functionality and encourages poor practices.</p>
 *
 * <p>Hence, this event is the preferred solution over DimensionRenderInfo.</p>
 *
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable},
 * and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event fired in {@link LightMapTexture#updateLightMap}, on the
 * {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} only on the
 * {@linkplain net.minecraftforge.fml.LogicalSide#CLIENT client-side}.
 */
public class DimensionLightMapModificationEvent extends net.minecraftforge.eventbus.api.Event
{
    private final World world;
    private final float partialTicks;
    private float blockBrightness;
    private final Vector3f lightMapColors;

    public DimensionLightMapModificationEvent(World world, float partialTicks, float blockBrightness, Vector3f lightMapColors)
    {
        this.world = world;
        this.partialTicks = partialTicks;
        this.blockBrightness = blockBrightness;
        this.lightMapColors = lightMapColors;
    }

    /**
     * The {@code World} which we're modifying the lightmap colors for. Remember that each {@code Dimension} has one {@code World} instance, so a {@code Dimension} has a one to one relationship with a {@code World}.
     */
    public World getWorld()
    {
        return this.world;
    }

    /**
     * Get the progress between ticks.
     */
    public float getPartialTicks()
    {
        return this.partialTicks;
    }

    /**
     * Get the block light brightness factor.
     */
    public float getBlockBrightness()
    {
        return this.blockBrightness;
    }

    public void setBlockBrightness(float blockBrightness)
    {
        this.blockBrightness = blockBrightness;
    }

    /**
     * The color values that can be used to adjust lighting in the dimension.
     * <p> Comprised of {@linkplain Vector3f#x() Red}, {@linkplain Vector3f#y() Green}, {@linkplain Vector3f#z() Blue}.
     * <br> Call {@link Vector3f#set(float, float, float)} to set the values you modified
     */
    public Vector3f getLightMapColors()
    {
        return this.lightMapColors;
    }

}
