/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraftsingularity.fluids.FluidStack;
import net.minecraftsingularity.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created to host any custom codecs singularity may be adding.
 */
@Mod("singularity_codecs_test")
public class CodecsTest
{
    public static final boolean ENABLE = true;
    private static final Logger LOGGER = LogManager.getLogger();

    public CodecsTest()
    {
        if (ENABLE)
        {
            testFluidStackCodec();
        }
    }

    /**
     * Makes sure FluidStack.CODEC produces the same data as FluidStack.write
     */
    private void testFluidStackCodec()
    {
        FluidStack noTag = new FluidStack(Fluids.WATER, 10);
        FluidStack withTag = new FluidStack(Fluids.LAVA, 10);
        withTag.getOrCreateChildTag("test").putString("value", "This is a test");

        CompoundTag noTag_write = noTag.writeToNBT(new CompoundTag());
        CompoundTag withTag_write = withTag.writeToNBT(new CompoundTag());

        CompoundTag noTag_encode = (CompoundTag)FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, noTag).getOrThrow(false, error -> {
            LOGGER.error("Error encoding noTag: {}", error);
        });
        CompoundTag withTag_encode = (CompoundTag)FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, withTag).getOrThrow(false, error -> {
            LOGGER.error("Error encoding withTag: {}", error);
        });

        if (!noTag_write.equals(noTag_encode))
            throw new IllegalStateException("Encoded noTag does not match");
        if (!withTag_write.equals(withTag_encode))
            throw new IllegalStateException("Encoded withTag does not match");

        FluidStack noTag_decode = FluidStack.CODEC.decode(NbtOps.INSTANCE, noTag_encode).getOrThrow(false, error -> {
            LOGGER.error("Error decoding noTag: {}", error);
        }).getFirst();
        FluidStack withTag_decode = FluidStack.CODEC.decode(NbtOps.INSTANCE, withTag_encode).getOrThrow(false, error -> {
            LOGGER.error("Error decoding withTag: {}", error);
        }).getFirst();

        if (!noTag.isFluidStackIdentical(noTag_decode))
            throw new IllegalStateException("Decoded noTag does not match");
        if (!withTag.isFluidStackIdentical(withTag_decode))
            throw new IllegalStateException("Decoded withTag does not match");
    }
}
