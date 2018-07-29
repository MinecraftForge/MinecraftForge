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

package net.minecraftforge.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.WorldSavedData;

public class WorldCapabilityData extends WorldSavedData
{
    public static final String ID = "capabilities";

    private INBTSerializable<NBTTagCompound> serializable;
    private NBTTagCompound capNBT = null;

    public WorldCapabilityData(String name)
    {
        super(name);
    }

    public WorldCapabilityData(INBTSerializable<NBTTagCompound> serializable)
    {
        super(ID);
        this.serializable = serializable;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.capNBT = nbt;
        if (serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (serializable != null)
            nbt = serializable.serializeNBT();
        return nbt;
    }

    @Override
    public boolean isDirty()
    {
        return true;
    }

    public void setCapabilities(WorldProvider provider, INBTSerializable<NBTTagCompound> capabilities)
    {
        this.serializable = capabilities;
        if (this.capNBT != null && serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }
}
