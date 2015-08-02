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

package net.minecraftforge.fml.common.network.internal;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

public class NetworkModHolder
{
    public abstract class NetworkChecker {
        public abstract boolean check(Map<String,String> remoteVersions, Side side);
    }

    private class IgnoredChecker extends NetworkChecker {
        @Override
        public boolean check(Map<String, String> remoteVersions, Side side)
        {
            return true;
        }
        @Override
        public String toString()
        {
            return "No network checking performed";
        }
    }
    private class DefaultNetworkChecker extends NetworkChecker {
        @Override
        public boolean check(Map<String,String> remoteVersions, Side side)
        {
            return remoteVersions.containsKey(container.getModId()) ? acceptVersion(remoteVersions.get(container.getModId())) : side == Side.SERVER;
        }
        @Override
        public String toString()
        {
            return acceptableRange != null ? String.format("Accepting range %s", acceptableRange) : String.format("Accepting version %s", container.getVersion());
        }
    }
    private class MethodNetworkChecker extends NetworkChecker {
        @Override
        public boolean check(Map<String,String> remoteVersions, Side side)
        {
            try
            {
                return (Boolean) checkHandler.invoke(container.getMod(), remoteVersions, side);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "Error occurred invoking NetworkCheckHandler %s at %s", checkHandler.getName(), container);
                return false;
            }
        }
        @Override
        public String toString()
        {
            return String.format("Invoking method %s", checkHandler.getName());
        }
    }
    private static int assignedIds = 1;

    private int localId;
    private int networkId;

    private ModContainer container;
    private Method checkHandler;

    private VersionRange acceptableRange;

    private NetworkChecker checker;

    private boolean acceptsVanillaClient;
    private boolean acceptsVanillaServer;

    public NetworkModHolder(ModContainer container)
    {
        this.container = container;
        this.localId = assignedIds++;
        this.networkId = this.localId;
    }
    public NetworkModHolder(ModContainer container, NetworkChecker checker)
    {
        this(container);
        this.checker = Preconditions.checkNotNull(checker);
        FMLLog.fine("The mod %s is using a custom checker %s", container.getModId(), checker.getClass().getName());
    }
    public NetworkModHolder(ModContainer container, Class<?> modClass, String acceptableVersionRange, ASMDataTable table)
    {
        this(container);
        SetMultimap<String, ASMData> annotationTable = table.getAnnotationsFor(container);
        Set<ASMData> versionCheckHandlers;
        if (annotationTable != null)
        {
            versionCheckHandlers = annotationTable.get(NetworkCheckHandler.class.getName());
        }
        else
        {
            versionCheckHandlers = ImmutableSet.of();
        }
        String networkCheckHandlerMethod = null;
        for (ASMData vch : versionCheckHandlers)
        {
            if (vch.getClassName().equals(modClass.getName()))
            {
                networkCheckHandlerMethod = vch.getObjectName();
                networkCheckHandlerMethod = networkCheckHandlerMethod.substring(0,networkCheckHandlerMethod.indexOf('('));
                break;
            }
        }
        if (versionCheckHandlers.isEmpty())
        {
            for (Method m : modClass.getMethods())
            {
                if (m.isAnnotationPresent(NetworkCheckHandler.class))
                {
                    if (m.getParameterTypes().length == 2 && m.getParameterTypes()[0].equals(Map.class) && m.getParameterTypes()[1].equals(Side.class))
                    {
                        this.checkHandler = m;
                        break;
                    }
                    else
                    {
                        FMLLog.severe("Found unexpected method signature for annotation NetworkCheckHandler");
                    }
                }
            }
        }
        if (networkCheckHandlerMethod != null)
        {
            try
            {
                Method checkHandlerMethod = modClass.getDeclaredMethod(networkCheckHandlerMethod, Map.class, Side.class);
                if (checkHandlerMethod.isAnnotationPresent(NetworkCheckHandler.class))
                {
                    this.checkHandler = checkHandlerMethod;
                }
            }
            catch (Exception e)
            {
                FMLLog.log(Level.WARN, e, "The declared version check handler method %s on network mod id %s is not accessible", networkCheckHandlerMethod, container.getModId());
            }
        }
        if (this.checkHandler != null)
        {
            this.checker = new MethodNetworkChecker();
        }
        else if (!Strings.isNullOrEmpty(acceptableVersionRange) && acceptableVersionRange.equals("*"))
        {
            this.checker = new IgnoredChecker();
        }
        else
        {
            try
            {
                this.acceptableRange = VersionRange.createFromVersionSpec(acceptableVersionRange);
            }
            catch (InvalidVersionSpecificationException e)
            {
                FMLLog.log(Level.WARN, e, "Invalid bounded range %s specified for network mod id %s", acceptableVersionRange, container.getModId());
            }
            this.checker = new DefaultNetworkChecker();
        }
        FMLLog.finer("Mod %s is using network checker : %s", container.getModId(), this.checker);
        FMLLog.finer("Testing mod %s to verify it accepts its own version in a remote connection", container.getModId());
        boolean acceptsSelf = acceptVersion(container.getVersion());
        if (!acceptsSelf)
        {
            FMLLog.severe("The mod %s appears to reject its own version number (%s) in its version handling. This is likely a severe bug in the mod!", container.getModId(), container.getVersion());
        }
        else
        {
            FMLLog.finer("The mod %s accepts its own version (%s)", container.getModId(), container.getVersion());
        }
    }

    public boolean acceptVersion(String version)
    {
        if (acceptableRange!=null)
        {
            return acceptableRange.containsVersion(new DefaultArtifactVersion(version));
        }

        return container.getVersion().equals(version);
    }

    public boolean check(Map<String,String> data, Side side)
    {
        return checker.check(data, side);
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

    public void setNetworkId(int value)
    {
        this.networkId = value;
    }

    public void testVanillaAcceptance() {
        acceptsVanillaClient = check(ImmutableMap.<String,String>of(), Side.CLIENT);
        acceptsVanillaServer = check(ImmutableMap.<String,String>of(), Side.SERVER);
    }
    public boolean acceptsVanilla(Side from) {
        return from == Side.CLIENT ? acceptsVanillaClient : acceptsVanillaServer;
    }

}
