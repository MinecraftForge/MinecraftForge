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

package cpw.mods.fml.common.network;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

public class ModMissingPacket extends FMLPacket
{

    private List<ModData> missing;
    private List<ModData> badVersion;

    public ModMissingPacket()
    {
        super(Type.MOD_MISSING);
    }

    @Override
    public byte[] generatePacket(Object... data)
    {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();

        List<String> missing = (List<String>) data[0];
        List<String> badVersion = (List<String>) data[1];

        dat.writeInt(missing.size());
        for (String missed : missing)
        {
            ModContainer mc = Loader.instance().getIndexedModList().get(missed);
            dat.writeUTF(missed);
            dat.writeUTF(mc.getVersion());
        }
        dat.writeInt(badVersion.size());
        for (String bad : badVersion)
        {
            ModContainer mc = Loader.instance().getIndexedModList().get(bad);
            dat.writeUTF(bad);
            dat.writeUTF(mc.getVersion());
        }
        return dat.toByteArray();
    }

    private static class ModData
    {
        String modId;
        String modVersion;
    }
    @Override
    public FMLPacket consumePacket(byte[] data)
    {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        int missingLen = dat.readInt();
        missing = Lists.newArrayListWithCapacity(missingLen);
        for (int i = 0; i < missingLen; i++)
        {
            ModData md = new ModData();
            md.modId = dat.readUTF();
            md.modVersion = dat.readUTF();
            missing.add(md);
        }
        int badVerLength = dat.readInt();
        badVersion = Lists.newArrayListWithCapacity(badVerLength);
        for (int i = 0; i < badVerLength; i++)
        {
            ModData md = new ModData();
            md.modId = dat.readUTF();
            md.modVersion = dat.readUTF();
            badVersion.add(md);
        }
        return this;
    }

    @Override
    public void execute(INetworkManager network, FMLNetworkHandler handler, NetHandler netHandler, String userName)
    {
        FMLCommonHandler.instance().getSidedDelegate().displayMissingMods(this);
    }

    public List<ArtifactVersion> getModList()
    {
        Builder<ArtifactVersion> builder = ImmutableList.<ArtifactVersion>builder();
        for (ModData md : missing)
        {
            builder.add(new DefaultArtifactVersion(md.modId, VersionRange.createFromVersion(md.modVersion, null)));
        }
        for (ModData md : badVersion)
        {
            builder.add(new DefaultArtifactVersion(md.modId, VersionRange.createFromVersion(md.modVersion, null)));
        }
        return builder.build();
    }

}
