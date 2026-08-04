/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.function.BiFunction;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
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
