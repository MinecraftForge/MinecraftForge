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

package net.minecraftforge.common;

import java.util.function.BiFunction;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * In 1.13.2, Mojang made DimensionType as the unique holder/identifier for each Dimension.
 * We used to be able to create custom Dimensions separate from DimensionType.
 * But now that they are hard linked to a single instance, this is the new class that
 * defines the 'template' for new dimensions.
 */
public abstract class ModDimension extends ForgeRegistryEntry<ModDimension>
{
    public abstract BiFunction<World, DimensionType, ? extends Dimension> getFactory();

    /**
     * Serialize any necessary data, this is called both when saving the world on the server.
     * And when sent over the network to the client.
     *
     * @param buffer The data buffer to write to.
     * @param network true when sent over the network, so you can only data needed by the client.
     */
    public void write(PacketBuffer buffer, boolean network){}

    /**
     * Deserialize any necessary data, this is called both when saving the world on the server.
     * And when sent over the network to the client.
     *
     * @param buffer The data buffer to write to.
     * @param network true when sent over the network, so you can only data needed by the client.
     */
    public void read(PacketBuffer buffer, boolean network){}

    public IBiomeMagnifier getMagnifier() {
        return ColumnFuzzedBiomeMagnifier.INSTANCE;
    }

    /**
     * Convenience method for generating a ModDimension with a specific factory but no extra
     * data handling behaviour. Extend ModDimension to override other methods.
     *
     * @param factory Factory for creating {@link Dimension} instances from DimType and World.
     * @return A custom ModDimension with that factory.
     */
    public static ModDimension withFactory(BiFunction<World, DimensionType, ? extends Dimension> factory) {
        return new ModDimension() {
            @Override
            public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
                return factory;
            }
        };
    }
}
