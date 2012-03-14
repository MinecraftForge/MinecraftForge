package net.minecraft.src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ModLoaderMp
{
    public static final String NAME = "ModLoaderMP";
    public static final String VERSION = "1.2.3v3";
    private static boolean hasInit = false;
    private static boolean packet230Received = false;
    private static Map netClientHandlerEntityMap = new HashMap();
    private static Map guiModMap = new HashMap();

    public static void initialize()
    {
        if (!hasInit)
        {
            initializePrivate();
        }
    }

    public static void handleAllPackets(Packet230ModLoader var0)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        packet230Received = true;

        if (var0.modId == "ModLoaderMP".hashCode())
        {
            switch (var0.packetType)
            {
                case 0:
                    handleModCheck(var0);
                    break;

                case 1:
                    handleTileEntityPacket(var0);
            }
        }
        else if (var0.modId == "Spawn".hashCode())
        {
            NetClientHandlerEntity var1 = handleNetClientHandlerEntities(var0.packetType);

            if (var1 != null && ISpawnable.class.isAssignableFrom(var1.entityClass))
            {
                try
                {
                    Entity var2 = (Entity)var1.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {ModLoader.getMinecraftInstance().theWorld});
                    ((ISpawnable)var2).spawn(var0);
                    ((WorldClient)ModLoader.getMinecraftInstance().theWorld).addEntityToWorld(var2.entityId, var2);
                }
                catch (Exception var4)
                {
                    ModLoader.getLogger().throwing("ModLoader", "handleCustomSpawn", var4);
                    ModLoader.throwException(String.format("Error initializing entity of type %s.", new Object[] {Integer.valueOf(var0.packetType)}), var4);
                    return;
                }
            }
        }
        else
        {
            for (int var5 = 0; var5 < ModLoader.getLoadedMods().size(); ++var5)
            {
                BaseMod var6 = (BaseMod)ModLoader.getLoadedMods().get(var5);

                if (var6 instanceof BaseModMp)
                {
                    BaseModMp var3 = (BaseModMp)var6;

                    if (var3.getId() == var0.modId)
                    {
                        var3.handlePacket(var0);
                        break;
                    }
                }
            }
        }
    }

    public static NetClientHandlerEntity handleNetClientHandlerEntities(int var0)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        return netClientHandlerEntityMap.containsKey(Integer.valueOf(var0)) ? (NetClientHandlerEntity)netClientHandlerEntityMap.get(Integer.valueOf(var0)) : null;
    }

    public static void sendPacket(BaseModMp var0, Packet230ModLoader var1)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var0 == null)
        {
            IllegalArgumentException var2 = new IllegalArgumentException("baseModMp cannot be null.");
            ModLoader.getLogger().throwing("ModLoaderMp", "SendPacket", var2);
            ModLoader.throwException("baseModMp cannot be null.", var2);
        }
        else
        {
            var1.modId = var0.getId();
            sendPacket(var1);
        }
    }

    public static void registerGUI(BaseModMp var0, int var1)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (guiModMap.containsKey(Integer.valueOf(var1)))
        {
            log("RegisterGUI error: inventoryType already registered.");
        }
        else
        {
            guiModMap.put(Integer.valueOf(var1), var0);
        }
    }

    public static void handleGUI(Packet100OpenWindow var0)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        BaseModMp var1 = (BaseModMp)guiModMap.get(Integer.valueOf(var0.inventoryType));
        GuiScreen var2 = var1.handleGUI(var0.inventoryType);

        if (var2 != null)
        {
            ModLoader.openGUI(ModLoader.getMinecraftInstance().thePlayer, var2);
            ModLoader.getMinecraftInstance().thePlayer.craftingInventory.windowId = var0.windowId;
        }
    }

    public static void registerNetClientHandlerEntity(Class var0, int var1)
    {
        registerNetClientHandlerEntity(var0, false, var1);
    }

    public static void registerNetClientHandlerEntity(Class var0, boolean var1, int var2)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var2 > 255)
        {
            log("RegisterNetClientHandlerEntity error: entityId cannot be greater than 255.");
        }
        else if (netClientHandlerEntityMap.containsKey(Integer.valueOf(var2)))
        {
            log("RegisterNetClientHandlerEntity error: entityId already registered.");
        }
        else
        {
            if (var2 > 127)
            {
                var2 -= 256;
            }

            netClientHandlerEntityMap.put(Integer.valueOf(var2), new NetClientHandlerEntity(var0, var1));
        }
    }

    public static void sendKey(BaseModMp var0, int var1)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var0 == null)
        {
            IllegalArgumentException var2 = new IllegalArgumentException("baseModMp cannot be null.");
            ModLoader.getLogger().throwing("ModLoaderMp", "SendKey", var2);
            ModLoader.throwException("baseModMp cannot be null.", var2);
        }
        else
        {
            Packet230ModLoader var3 = new Packet230ModLoader();
            var3.modId = "ModLoaderMP".hashCode();
            var3.packetType = 1;
            var3.dataInt = new int[] {var0.getId(), var1};
            sendPacket(var3);
        }
    }

    public static void log(String var0)
    {
        System.out.println(var0);
        ModLoader.getLogger().fine(var0);
    }

    private static void initializePrivate()
    {
        hasInit = true;

        try
        {
            Method var0;

            try
            {
                var0 = Packet.class.getDeclaredMethod("a", new Class[] {Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Class.class});
            }
            catch (NoSuchMethodException var2)
            {
                var0 = Packet.class.getDeclaredMethod("addIdClassMapping", new Class[] {Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Class.class});
            }

            var0.setAccessible(true);
            var0.invoke((Object)null, new Object[] {Integer.valueOf(230), Boolean.valueOf(true), Boolean.valueOf(true), Packet230ModLoader.class});
        }
        catch (IllegalAccessException var3)
        {
            ModLoader.getLogger().throwing("ModLoaderMp", "init", var3);
            ModLoader.throwException("An impossible error has occurred!", var3);
        }
        catch (IllegalArgumentException var4)
        {
            ModLoader.getLogger().throwing("ModLoaderMp", "init", var4);
            ModLoader.throwException("An impossible error has occurred!", var4);
        }
        catch (InvocationTargetException var5)
        {
            ModLoader.getLogger().throwing("ModLoaderMp", "init", var5);
            ModLoader.throwException("An impossible error has occurred!", var5);
        }
        catch (NoSuchMethodException var6)
        {
            ModLoader.getLogger().throwing("ModLoaderMp", "init", var6);
            ModLoader.throwException("An impossible error has occurred!", var6);
        }
        catch (SecurityException var7)
        {
            ModLoader.getLogger().throwing("ModLoaderMp", "init", var7);
            ModLoader.throwException("An impossible error has occurred!", var7);
        }

        log("ModLoaderMP 1.0.0 Initialized");
    }

    private static void handleModCheck(Packet230ModLoader var0)
    {
        Packet230ModLoader var1 = new Packet230ModLoader();
        var1.modId = "ModLoaderMP".hashCode();
        var1.packetType = 0;
        var1.dataString = new String[ModLoader.getLoadedMods().size()];

        for (int var2 = 0; var2 < ModLoader.getLoadedMods().size(); ++var2)
        {
            var1.dataString[var2] = ((BaseMod)ModLoader.getLoadedMods().get(var2)).toString();
        }

        sendPacket(var1);
    }

    private static void handleTileEntityPacket(Packet230ModLoader var0)
    {
        if (var0.dataInt != null && var0.dataInt.length >= 5)
        {
            int var1 = var0.dataInt[0];
            int var2 = var0.dataInt[1];
            int var3 = var0.dataInt[2];
            int var4 = var0.dataInt[3];
            int var5 = var0.dataInt[4];
            int[] var6 = new int[var0.dataInt.length - 5];
            System.arraycopy(var0.dataInt, 5, var6, 0, var0.dataInt.length - 5);
            float[] var7 = var0.dataFloat;
            String[] var8 = var0.dataString;

            for (int var9 = 0; var9 < ModLoader.getLoadedMods().size(); ++var9)
            {
                BaseMod var10 = (BaseMod)ModLoader.getLoadedMods().get(var9);

                if (var10 instanceof BaseModMp)
                {
                    BaseModMp var11 = (BaseModMp)var10;

                    if (var11.getId() == var1)
                    {
                        var11.handleTileEntityPacket(var2, var3, var4, var5, var6, var7, var8);
                        break;
                    }
                }
            }
        }
        else
        {
            log("Bad TileEntityPacket received.");
        }
    }

    private static void sendPacket(Packet230ModLoader var0)
    {
        if (packet230Received && ModLoader.getMinecraftInstance().theWorld != null && ModLoader.getMinecraftInstance().theWorld.isRemote)
        {
            ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(var0);
        }
    }

    public static BaseModMp getModInstance(Class var0)
    {
        for (int var1 = 0; var1 < ModLoader.getLoadedMods().size(); ++var1)
        {
            BaseMod var2 = (BaseMod)ModLoader.getLoadedMods().get(var1);

            if (var2 instanceof BaseModMp)
            {
                BaseModMp var3 = (BaseModMp)var2;

                if (var0.isInstance(var3))
                {
                    return (BaseModMp)ModLoader.getLoadedMods().get(var1);
                }
            }
        }

        return null;
    }
}
