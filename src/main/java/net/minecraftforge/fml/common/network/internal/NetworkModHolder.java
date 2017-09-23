/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import javax.annotation.Nullable;

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
                FMLLog.log.error("Error occurred invoking NetworkCheckHandler {} at {}", container, e);
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
        FMLLog.log.debug("The mod {} is using a custom checker {}", container.getModId(), checker.getClass().getName());
    }
    public NetworkModHolder(ModContainer container, Class<?> modClass, @Nullable String acceptableVersionRange, ASMDataTable table)
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
                        FMLLog.log.fatal("Found unexpected method signature for annotation NetworkCheckHandler");
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
                FMLLog.log.warn("The declared version check handler method {} on network mod id {} is not accessible", networkCheckHandlerMethod, container.getModId(), e);
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
                FMLLog.log.warn("Invalid bounded range {} specified for network mod id {}", acceptableVersionRange, container.getModId(), e);
            }
            this.checker = new DefaultNetworkChecker();
        }
        FMLLog.log.trace("Mod {} is using network checker : {}", container.getModId(), this.checker);
        FMLLog.log.trace("Testing mod {} to verify it accepts its own version in a remote connection", container.getModId());
        boolean acceptsSelf = acceptVersion(container.getVersion());
        if (!acceptsSelf)
        {
            FMLLog.log.fatal("The mod {} appears to reject its own version number ({}) in its version handling. This is likely a severe bug in the mod!", container.getModId(), container.getVersion());
        }
        else
        {
            FMLLog.log.trace("The mod {} accepts its own version ({})", container.getModId(), container.getVersion());
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
