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

package net.minecraftforge.debug;

import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created to host any custom codecs Forge may be adding.
 */
@Mod("forge_codecs_test")
public class CodecsTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    public CodecsTest()
    {
        testFluidStackCodec();
    }

    /**
     * Makes sure FluidStack.CODEC produces the same data as FluidStack.write
     */
    private void testFluidStackCodec()
    {
        FluidStack noTag = new FluidStack(Fluids.WATER, 10);
        FluidStack withTag = new FluidStack(Fluids.LAVA, 10);
        withTag.getOrCreateChildTag("test").putString("value", "This is a test");

        CompoundNBT noTag_write = noTag.writeToNBT(new CompoundNBT());
        CompoundNBT withTag_write = withTag.writeToNBT(new CompoundNBT());

        CompoundNBT noTag_encode = (CompoundNBT)FluidStack.CODEC.encodeStart(NBTDynamicOps.INSTANCE, noTag).getOrThrow(false, error -> {
            LOGGER.error("Error encoding noTag: {}", error);
        });
        CompoundNBT withTag_encode = (CompoundNBT)FluidStack.CODEC.encodeStart(NBTDynamicOps.INSTANCE, withTag).getOrThrow(false, error -> {
            LOGGER.error("Error encoding withTag: {}", error);
        });

        if (!noTag_write.equals(noTag_encode))
            throw new IllegalStateException("Encoded noTag does not match");
        if (!withTag_write.equals(withTag_encode))
            throw new IllegalStateException("Encoded withTag does not match");

        FluidStack noTag_decode = FluidStack.CODEC.decode(NBTDynamicOps.INSTANCE, noTag_encode).getOrThrow(false, error -> {
            LOGGER.error("Error decoding noTag: {}", error);
        }).getFirst();
        FluidStack withTag_decode = FluidStack.CODEC.decode(NBTDynamicOps.INSTANCE, withTag_encode).getOrThrow(false, error -> {
            LOGGER.error("Error decoding withTag: {}", error);
        }).getFirst();

        if (!noTag.equals(noTag_decode))
            throw new IllegalStateException("Decoded noTag does not match");
        if (!withTag.equals(withTag_decode))
            throw new IllegalStateException("Decoded withTag does not match");
    }
}
