package cpw.mods.fml.common.network;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.item.Item;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.relauncher.Side;

public class NetworkModHandler
{
    private static Object connectionHandlerDefaultValue;
    private static Object packetHandlerDefaultValue;
    private static Object clientHandlerDefaultValue;
    private static Object serverHandlerDefaultValue;
    private static Object tinyPacketHandlerDefaultValue;

    private static int assignedIds = 1;

    private int localId;
    private int networkId;

    private ModContainer container;
    private NetworkMod mod;
    private Method checkHandler;

    private VersionRange acceptableRange;
    private ITinyPacketHandler tinyPacketHandler;

    public NetworkModHandler(ModContainer container, NetworkMod modAnnotation)
    {
        this.container = container;
        this.mod = modAnnotation;
        this.localId = assignedIds++;
        this.networkId = this.localId;
        // Skip over the map object because it has special network id meaning
        if (Item.field_77744_bd.field_77779_bT == assignedIds)
        {
            assignedIds++;
        }
    }
    public NetworkModHandler(ModContainer container, Class<?> networkModClass, ASMDataTable table)
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

        tryCreatingPacketHandler(container, mod.packetHandler(), mod.channels(), null);
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            if (mod.clientPacketHandlerSpec() != getClientHandlerSpecDefaultValue())
            {
                tryCreatingPacketHandler(container, mod.clientPacketHandlerSpec().packetHandler(), mod.clientPacketHandlerSpec().channels(), Side.CLIENT);
            }
        }
        if (mod.serverPacketHandlerSpec() != getServerHandlerSpecDefaultValue())
        {
            tryCreatingPacketHandler(container, mod.serverPacketHandlerSpec().packetHandler(), mod.serverPacketHandlerSpec().channels(), Side.SERVER);
        }

        if (mod.connectionHandler() != getConnectionHandlerDefaultValue())
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

        if (mod.tinyPacketHandler()!=getTinyPacketHandlerDefaultValue())
        {
            try
            {
                tinyPacketHandler = mod.tinyPacketHandler().newInstance();
            }
            catch (Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "Unable to create tiny packet handler instance %s", mod.tinyPacketHandler().getName());
                throw new FMLNetworkException(e);
            }
        }
    }
    /**
     * @param container
     */
    private void tryCreatingPacketHandler(ModContainer container, Class<? extends IPacketHandler> clazz, String[] channels, Side side)
    {
        if (side!=null && side.isClient() && ! FMLCommonHandler.instance().getSide().isClient())
        {
            return;
        }
        if (clazz!=getPacketHandlerDefaultValue())
        {
            if (channels.length==0)
            {
                FMLLog.log(Level.WARNING, "The mod id %s attempted to register a packet handler without specifying channels for it", container.getModId());
            }
            else
            {
                IPacketHandler instance;
                try
                {
                    instance = clazz.newInstance();
                }
                catch (Exception e)
                {
                    FMLLog.log(Level.SEVERE, e, "Unable to create a packet handler instance %s for mod %s", clazz.getName(), container.getModId());
                    throw new FMLNetworkException(e);
                }

                for (String channel : channels)
                {
                    NetworkRegistry.instance().registerChannel(instance, channel, side);
                }
            }
        }
        else if (channels.length > 0)
        {
            FMLLog.warning("The mod id %s attempted to register channels without specifying a packet handler", container.getModId());
        }
    }
    /**
     * @return the default {@link NetworkMod#connectionHandler()} annotation value
     */
    private Object getConnectionHandlerDefaultValue()
    {
        try {
            if (connectionHandlerDefaultValue == null)
            {
                connectionHandlerDefaultValue = NetworkMod.class.getMethod("connectionHandler").getDefaultValue();
            }
            return connectionHandlerDefaultValue;
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Derp?", e);
        }
    }

    /**
     * @return the default {@link NetworkMod#packetHandler()} annotation value
     */
    private Object getPacketHandlerDefaultValue()
    {
        try {
            if (packetHandlerDefaultValue == null)
            {
                packetHandlerDefaultValue = NetworkMod.class.getMethod("packetHandler").getDefaultValue();
            }
            return packetHandlerDefaultValue;
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Derp?", e);
        }
    }

    /**
     * @return the default {@link NetworkMod#tinyPacketHandler()} annotation value
     */
    private Object getTinyPacketHandlerDefaultValue()
    {
        try {
            if (tinyPacketHandlerDefaultValue == null)
            {
                tinyPacketHandlerDefaultValue = NetworkMod.class.getMethod("tinyPacketHandler").getDefaultValue();
            }
            return tinyPacketHandlerDefaultValue;
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Derp?", e);
        }
    }
    /**
     * @return the {@link NetworkMod#clientPacketHandlerSpec()} default annotation value
     */
    private Object getClientHandlerSpecDefaultValue()
    {
        try {
            if (clientHandlerDefaultValue == null)
            {
                clientHandlerDefaultValue = NetworkMod.class.getMethod("clientPacketHandlerSpec").getDefaultValue();
            }
            return clientHandlerDefaultValue;
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Derp?", e);
        }
    }
    /**
     * @return the default {@link NetworkMod#serverPacketHandlerSpec()} annotation value
     */
    private Object getServerHandlerSpecDefaultValue()
    {
        try {
            if (serverHandlerDefaultValue == null)
            {
                serverHandlerDefaultValue = NetworkMod.class.getMethod("serverPacketHandlerSpec").getDefaultValue();
            }
            return serverHandlerDefaultValue;
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Derp?", e);
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

    public boolean hasTinyPacketHandler()
    {
        return tinyPacketHandler != null;
    }
    public ITinyPacketHandler getTinyPacketHandler()
    {
        return tinyPacketHandler;
    }
}
