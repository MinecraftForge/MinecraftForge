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

import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;

public class NetworkModHolder
{
    private static int assignedIds = 1;

    private int localId;
    private int networkId;

    private ModContainer container;
    private NetworkMod mod;
    private Method checkHandler;

    private VersionRange acceptableRange;

    private IPacketHandlerFactory packetHandlerFactory;

    public NetworkModHolder(ModContainer container, NetworkMod modAnnotation)
    {
        this.container = container;
        this.mod = modAnnotation;
        this.localId = assignedIds++;
        this.networkId = this.localId;
    }
    public NetworkModHolder(ModContainer container, Class<?> networkModClass, ASMDataTable table)
    {
        this(container, networkModClass.getAnnotation(NetworkMod.class));
        if (this.mod == null)
        {
            return;
        }

        Set<ASMData> versionCheckHandlers = table.getAnnotationsFor(container).get(NetworkMod.VersionCheckHandler.class.getName());
        String versionCheckHandlerMethod = null;
        for (ASMData vch : versionCheckHandlers)
        {
            if (vch.getClassName().equals(networkModClass.getName()))
            {
                versionCheckHandlerMethod = vch.getObjectName();
                versionCheckHandlerMethod = versionCheckHandlerMethod.substring(0,versionCheckHandlerMethod.indexOf('('));
                break;
            }
        }
        if (versionCheckHandlerMethod != null)
        {
            try
            {
                Method checkHandlerMethod = networkModClass.getDeclaredMethod(versionCheckHandlerMethod, String.class);
                if (checkHandlerMethod.isAnnotationPresent(NetworkMod.VersionCheckHandler.class))
                {
                    this.checkHandler = checkHandlerMethod;
                }
            }
            catch (Exception e)
            {
                FMLLog.log(Level.WARNING, e, "The declared version check handler method %s on network mod id %s is not accessible", versionCheckHandlerMethod, container.getModId());
            }
        }

        configureNetworkMod(container);
    }
    protected void configureNetworkMod(ModContainer container)
    {
        if (this.checkHandler == null)
        {
            String versionBounds = mod.versionBounds();
            if (!Strings.isNullOrEmpty(versionBounds))
            {
                try
                {
                    this.acceptableRange = VersionRange.createFromVersionSpec(versionBounds);
                }
                catch (InvalidVersionSpecificationException e)
                {
                    FMLLog.log(Level.WARNING, e, "Invalid bounded range %s specified for network mod id %s", versionBounds, container.getModId());
                }
            }
        }

        FMLLog.finest("Testing mod %s to verify it accepts its own version in a remote connection", container.getModId());
        boolean acceptsSelf = acceptVersion(container.getVersion());
        if (!acceptsSelf)
        {
            FMLLog.severe("The mod %s appears to reject its own version number (%s) in its version handling. This is likely a severe bug in the mod!", container.getModId(), container.getVersion());
        }
        else
        {
            FMLLog.finest("The mod %s accepts its own version (%s)", container.getModId(), container.getVersion());
        }
    }

    public boolean acceptVersion(String version)
    {
        if (checkHandler != null)
        {
            try
            {
                return (Boolean)checkHandler.invoke(container.getMod(), version);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.WARNING, e, "There was a problem invoking the checkhandler method %s for network mod id %s", checkHandler.getName(), container.getModId());
                return false;
            }
        }

        if (acceptableRange!=null)
        {
            return acceptableRange.containsVersion(new DefaultArtifactVersion(version));
        }

        return container.getVersion().equals(version);
    }

    public int getLocalId()
    {
        return localId;
    }

    public int getNetworkId()
    {
        return networkId;
    }

    public ModContainer getContainer()
    {
        return container;
    }

    public NetworkMod getMod()
    {
        return mod;
    }

    public boolean isNetworkMod()
    {
        return mod != null;
    }

    public void setNetworkId(int value)
    {
        this.networkId = value;
    }

}
