package cpw.mods.fml.common.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;

import org.objectweb.asm.Type;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;

public class NetworkModHandler
{
    private static int assignedIds = 1;

    private int localId;
    private int networkId;

    private ModContainer container;
    private NetworkMod mod;
    private Method checkHandler;

    private VersionRange acceptableRange;

    public NetworkModHandler(ModContainer container, Class<?> networkModClass, ASMDataTable table)
    {
        this.container = container;
        this.mod = networkModClass.getAnnotation(NetworkMod.class);
        if (this.mod == null)
        {
            return;
        }

        this.localId = assignedIds++;
        this.networkId = this.localId;

        Set<ASMData> versionCheckHandlers = table.getAnnotationsFor(container).get(NetworkMod.VersionCheckHandler.class.getName());
        String versionCheckHandlerMethod = null;
        for (ASMData vch : versionCheckHandlers)
        {
            if (vch.getClassName().equals(networkModClass.getName()))
            {
                versionCheckHandlerMethod = vch.getObjectName();
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

        if (!mod.packetHandler().getName().equals(NetworkMod.NULL.class.getName()))
        {
            IPacketHandler instance;
            try
            {
                instance = mod.packetHandler().newInstance();
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "Unable to create packet handler instance %s", mod.packetHandler().getName());
                throw new FMLNetworkException(e);
            }

            for (String channel : mod.channels())
            {
                NetworkRegistry.instance().registerChannel(instance, channel);
            }
        }
        else if (mod.channels().length > 0)
        {
            FMLLog.warning("The mod id %s attempted to register channels without specifying a valid packet handler", container.getModId());
        }

        if (!mod.connectionHandler().getName().equals(NetworkMod.NULL.class.getName()))
        {
            IConnectionHandler instance;
            try
            {
                instance = mod.connectionHandler().newInstance();
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "Unable to create connection handler instance %s", mod.connectionHandler().getName());
                throw new FMLNetworkException(e);
            }

            NetworkRegistry.instance().registerConnectionHandler(instance);
        }
    }

    public boolean requiresClientSide()
    {
        return mod.clientSideRequired();
    }

    public boolean requiresServerSide()
    {
        return mod.serverSideRequired();
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
