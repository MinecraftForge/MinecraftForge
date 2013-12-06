/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.network.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class OpenGuiPacket extends FMLOldPacket
{
    int windowId;
    int networkId;
    int modGuiId;
    int x;
    int y;
    int z;

    @Override
    byte[] generatePacketData(Object... data)
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt((Integer) data[0]); // windowId
        dat.writeInt((Integer) data[1]); // networkId
        dat.writeInt((Integer) data[2]); // modGuiId
        dat.writeInt((Integer) data[3]); // x
        dat.writeInt((Integer) data[4]); // y
        dat.writeInt((Integer) data[5]); // z
        return dat.toByteArray();
    }

    @Override
    public FMLOldPacket consumePacketData(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        windowId = dat.readInt();
        networkId = dat.readInt();
        modGuiId = dat.readInt();
        x = dat.readInt();
        y = dat.readInt();
        z = dat.readInt();
        return this;
    }


}
