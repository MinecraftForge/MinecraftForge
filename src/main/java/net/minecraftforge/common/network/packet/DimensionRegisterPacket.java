/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgePacket;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * This class offers the ability for servers to register dimensions on client.
 */
public class DimensionRegisterPacket extends ForgePacket
{

    /** The dimension ID to register on client */
    public int dimensionId;
    /** The provider ID to register with dimension on client */
    public int providerId;

    // nullary constructor required by ForgePacket.make()
    public DimensionRegisterPacket() {}

    public DimensionRegisterPacket(int dimensionId, int providerId)
    {
        this.dimensionId = dimensionId;
        this.providerId = providerId;
    }

    @Override
    public byte[] generatePacket()
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(this.dimensionId);
        dat.writeInt(this.providerId);
        return dat.toByteArray();
    }

    @Override
    public ForgePacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        dimensionId = dat.readInt();
        providerId = dat.readInt();
        return this;
    }

    @Override
    public void execute(INetworkManager network, EntityPlayer player)
    {
        if (!(player instanceof EntityPlayerMP))
        {
            if (!DimensionManager.isDimensionRegistered(dimensionId))
            {
                DimensionManager.registerDimension(dimensionId, providerId);
            }
        }
    }

}