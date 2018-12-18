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

package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IRenderHandler;

public interface IForgeDimension
{
    default Dimension getDimension()
    {
        return (Dimension) this;
    }

    /**
     * Called from {@link World#initCapabilities()}, to gather capabilities for this
     * world. It's safe to access world here since this is called after world is
     * registered.
     *
     * On server, called directly after mapStorage and world data such as Scoreboard
     * and VillageCollection are initialized. On client, called when world is
     * constructed, just before world load event is called. Note that this method is
     * always called before the world load event.
     *
     * @return initial holder for capabilities on the world
     */
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities()
    {
        return null;
    }

    /**
     * The dimension's movement factor. Whenever a player or entity changes
     * dimension from world A to world B, their coordinates are multiplied by
     * worldA.provider.getMovementFactor() / worldB.provider.getMovementFactor()
     * Example: Overworld factor is 1, nether factor is 8. Traveling from overworld
     * to nether multiplies coordinates by 1/8.
     *
     * @return The movement factor
     */
    default double getMovementFactor()
    {
        if (getDimension() instanceof NetherDimension)
        {
            return 8.0;
        }
        return 1.0;
    }

    /**
     * Sets the providers current dimension ID, used in default getSaveFolder()
     * Added to allow default providers to be registered for multiple dimensions.
     * This is to denote the exact dimension ID opposed to the 'type' in WorldType
     *
     * @param id Dimension ID
     */
    void setId(int id);

    int getId();

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getSkyRenderer();

    @OnlyIn(Dist.CLIENT)
    void setSkyRenderer(IRenderHandler skyRenderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getCloudRenderer();

    @OnlyIn(Dist.CLIENT)
    void setCloudRenderer(IRenderHandler renderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getWeatherRenderer();

    @OnlyIn(Dist.CLIENT)
    void setWeatherRenderer(IRenderHandler renderer);

    void resetRainAndThunder();

    default boolean canDoLightning(Chunk chunk)
    {
        return true;
    }

    default boolean canDoRainSnowIce(Chunk chunk)
    {
        return true;
    }

    /**
     * Called on the client to get the music type to play when in this world type.
     * At the time of calling, the client player and world are guaranteed to be non-null
     * @return null to use vanilla logic, otherwise a MusicType to play in this world
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    default MusicTicker.MusicType getMusicType()
    {
        return null;
    }

    default Biome getBiome(BlockPos pos)
    {
       return getDimension().getWorld().getBiomeBody(pos);
    }

    default boolean isDaytime()
    {
        return getDimension().getWorld().getSkylightSubtracted() < 4;
    }

    /**
     * The current sun brightness factor for this dimension.
     * 0.0f means no light at all, and 1.0f means maximum sunlight.
     * This will be used for the "calculateSkylightSubtracted"
     * which is for Sky light value calculation.
     *
     * @return The current brightness factor
     **/
    default float getSunBrightnessFactor(float partialTicks)
    {
        return getDimension().getWorld().getSunBrightnessFactor(partialTicks);
    }

    /**
     * Gets the Sun Brightness for rendering sky.
     * */
    @OnlyIn(Dist.CLIENT)
    default float getSunBrightness(float partialTicks)
    {
        return getDimension().getWorld().getSunBrightnessBody(partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    default Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
        return getDimension().getWorld().getSkyColorBody(cameraEntity, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    default Vec3d getCloudColor(float partialTicks)
    {
        return getDimension().getWorld().getCloudColorBody(partialTicks);
    }

    /**
     * Calculates the current moon phase factor.
     * This factor is effective for slimes.
     * (This method do not affect the moon rendering)
     * */
    default float getCurrentMoonPhaseFactor(long time)
    {
        return Dimension.MOON_PHASE_FACTORS[this.getDimension().getMoonPhase(time)];
    }

    /**
     * Gets the Star Brightness for rendering sky.
     * */
    @OnlyIn(Dist.CLIENT)
    default float getStarBrightness(float partialTicks)
    {
        float f = getDimension().getWorld().getCelestialAngle(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
    }

    default void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) { }

    default void calculateInitialWeather()
    {
        getDimension().getWorld().calculateInitialWeatherBody();
    }

    default void updateWeather()
    {
        getDimension().getWorld().updateWeatherBody();
    }

    default long getSeed()
    {
        return getDimension().getWorld().getWorldInfo().getSeed();
    }

    default long getWorldTime()
    {
        return getDimension().getWorld().getWorldInfo().getDayTime();
    }

    default void setWorldTime(long time)
    {
        getDimension().getWorld().getWorldInfo().setDayTime(time);
    }

    default BlockPos getSpawnPoint()
    {
        WorldInfo info = getDimension().getWorld().getWorldInfo();
        return new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
    }

    default void setSpawnPoint(BlockPos pos)
    {
        getDimension().getWorld().getWorldInfo().setSpawn(pos);
    }

    default boolean canMineBlock(EntityPlayer player, BlockPos pos)
    {
        return getDimension().getWorld().canMineBlockBody(player, pos);
    }

    default boolean isHighHumidity(BlockPos pos)
    {
        return getDimension().getWorld().getBiome(pos).isHighHumidity();
    }

    default int getHeight()
    {
        return 256;
    }

    default int getActualHeight()
    {
        return getDimension().isNether() ? 128 : 256;
    }

    default double getHorizon()
    {
        return getDimension().getWorld().getWorldInfo().getTerrainType().getHorizon(getDimension().getWorld());
    }

    default String getSaveFolder()
    {
        return getId() == 0 ? null : "DIM" + getId();
    }

    /**
     * Determine if the cursor on the map should 'spin' when rendered, like it does for the player in the nether.
     *
     * @param entity The entity holding the map, playername, or frame-ENTITYID
     * @param x X Position
     * @param z Z Position
     * @param rotation the regular rotation of the marker
     * @return True to 'spin' the cursor
     */
    default boolean shouldMapSpin(String entity, double x, double z, double rotation)
    {
        return getId() < 0;
    }
}
