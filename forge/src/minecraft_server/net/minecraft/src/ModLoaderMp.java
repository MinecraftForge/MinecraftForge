package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.DimensionManager;

public class ModLoaderMp
{
    public static final String NAME = "ModLoaderMP";
    public static final String VERSION = "1.2.3v3";
    private static boolean hasInit = false;
    private static Map entityTrackerMap = new HashMap();
    private static Map entityTrackerEntryMap = new HashMap();
    private static List bannedMods = new ArrayList();

    public static void initialize()
    {
        if (!hasInit)
        {
            initializePrivate();
        }
    }

    public static void registerEntityTracker(Class var0, int var1, int var2)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (entityTrackerMap.containsKey(var0))
        {
            System.out.println("RegisterEntityTracker error: entityClass already registered.");
        }
        else
        {
            entityTrackerMap.put(var0, new Pair(Integer.valueOf(var1), Integer.valueOf(var2)));
        }
    }

    public static void registerEntityTrackerEntry(Class var0, int var1)
    {
        registerEntityTrackerEntry(var0, false, var1);
    }

    public static void registerEntityTrackerEntry(Class var0, boolean var1, int var2)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var2 > 255)
        {
            System.out.println("RegisterEntityTrackerEntry error: entityId cannot be greater than 255.");
        }

        if (entityTrackerEntryMap.containsKey(var0))
        {
            System.out.println("RegisterEntityTrackerEntry error: entityClass already registered.");
        }
        else
        {
            entityTrackerEntryMap.put(var0, new EntityTrackerEntry2(var2, var1));
        }
    }

    public static void handleAllLogins(EntityPlayerMP var0)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        sendModCheck(var0);

        for (int var1 = 0; var1 < ModLoader.getLoadedMods().size(); ++var1)
        {
            BaseMod var2 = (BaseMod)ModLoader.getLoadedMods().get(var1);

            if (var2 instanceof BaseModMp)
            {
                ((BaseModMp)var2).handleLogin(var0);
            }
        }
    }

    public static void handleAllPackets(Packet230ModLoader var0, EntityPlayerMP var1)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var0.modId == "ModLoaderMP".hashCode())
        {
            switch (var0.packetType)
            {
                case 0:
                    handleModCheckResponse(var0, var1);
                    break;

                case 1:
                    handleSendKey(var0, var1);
            }
        }
        else
        {
            for (int var2 = 0; var2 < ModLoader.getLoadedMods().size(); ++var2)
            {
                BaseMod var3 = (BaseMod)ModLoader.getLoadedMods().get(var2);

                if (var3 instanceof BaseModMp)
                {
                    BaseModMp var4 = (BaseModMp)var3;

                    if (var4.getId() == var0.modId)
                    {
                        var4.handlePacket(var0, var1);
                        break;
                    }
                }
            }
        }
    }

    public static void handleEntityTrackers(EntityTracker var0, Entity var1)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        Iterator var2 = entityTrackerMap.entrySet().iterator();
        Entry var3;

        do
        {
            if (!var2.hasNext())
            {
                return;
            }

            var3 = (Entry)var2.next();
        }
        while (!((Class)var3.getKey()).isInstance(var1));

        var0.trackEntity(var1, ((Integer)((Pair)var3.getValue()).getLeft()).intValue(), ((Integer)((Pair)var3.getValue()).getRight()).intValue(), true);
    }

    public static EntityTrackerEntry2 handleEntityTrackerEntries(Entity var0)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        return entityTrackerEntryMap.containsKey(var0.getClass()) ? (EntityTrackerEntry2)entityTrackerEntryMap.get(var0.getClass()) : null;
    }

    public static void sendPacketToAll(BaseModMp var0, Packet230ModLoader var1)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var0 == null)
        {
            IllegalArgumentException var2 = new IllegalArgumentException("baseModMp cannot be null.");
            ModLoader.getLogger().throwing("ModLoaderMP", "SendPacketToAll", var2);
            ModLoader.throwException("baseModMp cannot be null.", var2);
        }
        else
        {
            var1.modId = var0.getId();
            sendPacketToAll(var1);
        }
    }

    private static void sendPacketToAll(Packet var0)
    {
        if (var0 != null)
        {
            for (int var1 = 0; var1 < ModLoader.getMinecraftServerInstance().configManager.playerEntities.size(); ++var1)
            {
                ((EntityPlayerMP)ModLoader.getMinecraftServerInstance().configManager.playerEntities.get(var1)).playerNetServerHandler.sendPacket(var0);
            }
        }
    }

    public static void sendPacketTo(BaseModMp var0, EntityPlayerMP var1, Packet230ModLoader var2)
    {
        if (!hasInit)
        {
            initializePrivate();
        }

        if (var0 == null)
        {
            IllegalArgumentException var3 = new IllegalArgumentException("baseModMp cannot be null.");
            ModLoader.getLogger().throwing("ModLoaderMP", "SendPacketTo", var3);
            ModLoader.throwException("baseModMp cannot be null.", var3);
        }
        else
        {
            var2.modId = var0.getId();
            sendPacketTo(var1, var2);
        }
    }

    public static void log(String var0)
    {
        MinecraftServer.logger.info(var0);
        ModLoader.getLogger().fine(var0);
        System.out.println(var0);
    }

    public static World getPlayerWorld(EntityPlayer var0)
    {
        for (World world : DimensionManager.getWorlds())
        {
            if (world.playerEntities.contains(var0))
            {
                return world;
            }
        }

        return null;
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
            catch (NoSuchMethodException var3)
            {
                var0 = Packet.class.getDeclaredMethod("addIdClassMapping", new Class[] {Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Class.class});
            }

            var0.setAccessible(true);
            var0.invoke((Object)null, new Object[] {Integer.valueOf(230), Boolean.valueOf(true), Boolean.valueOf(true), Packet230ModLoader.class});
        }
        catch (IllegalAccessException var4)
        {
            ModLoader.getLogger().throwing("ModLoaderMP", "AddCustomPacketMapping", var4);
            ModLoader.throwException("ModLoaderMP", var4);
            return;
        }
        catch (IllegalArgumentException var5)
        {
            ModLoader.getLogger().throwing("ModLoaderMP", "init", var5);
            ModLoader.throwException("ModLoaderMP", var5);
            return;
        }
        catch (InvocationTargetException var6)
        {
            ModLoader.getLogger().throwing("ModLoaderMP", "init", var6);
            ModLoader.throwException("ModLoaderMP", var6);
            return;
        }
        catch (NoSuchMethodException var7)
        {
            ModLoader.getLogger().throwing("ModLoaderMP", "init", var7);
            ModLoader.throwException("ModLoaderMP", var7);
            return;
        }
        catch (SecurityException var8)
        {
            ModLoader.getLogger().throwing("ModLoaderMP", "init", var8);
            ModLoader.throwException("ModLoaderMP", var8);
            return;
        }

        try
        {
            File var11 = ModLoader.getMinecraftServerInstance().getFile("banned-mods.txt");

            if (!var11.exists())
            {
                var11.createNewFile();
            }

            BufferedReader var1 = new BufferedReader(new InputStreamReader(new FileInputStream(var11)));
            String var2;

            while ((var2 = var1.readLine()) != null)
            {
                bannedMods.add(var2);
            }
        }
        catch (FileNotFoundException var9)
        {
            ModLoader.getLogger().throwing("ModLoader", "init", var9);
            ModLoader.throwException("ModLoaderMultiplayer", var9);
            return;
        }
        catch (IOException var10)
        {
            ModLoader.getLogger().throwing("ModLoader", "init", var10);
            ModLoader.throwException("ModLoaderMultiplayer", var10);
            return;
        }

        log("ModLoaderMP 1.2.3v3 Initialized");
    }

    private static void sendPacketTo(EntityPlayerMP var0, Packet230ModLoader var1)
    {
        var0.playerNetServerHandler.sendPacket(var1);
    }

    private static void sendModCheck(EntityPlayerMP var0)
    {
        Packet230ModLoader var1 = new Packet230ModLoader();
        var1.modId = "ModLoaderMP".hashCode();
        var1.packetType = 0;
        sendPacketTo(var0, var1);
    }

    private static void handleModCheckResponse(Packet230ModLoader var0, EntityPlayerMP var1)
    {
        StringBuilder var2 = new StringBuilder();

        if (var0.dataString.length != 0)
        {
            for (int var3 = 0; var3 < var0.dataString.length; ++var3)
            {
                if (var0.dataString[var3].lastIndexOf("mod_") != -1)
                {
                    if (var2.length() != 0)
                    {
                        var2.append(", ");
                    }

                    var2.append(var0.dataString[var3].substring(var0.dataString[var3].lastIndexOf("mod_")));
                }
            }
        }
        else
        {
            var2.append("no mods");
        }

        log(var1.username + " joined with " + var2.toString());
        ArrayList var11 = new ArrayList();
        int var5;

        for (int var4 = 0; var4 < bannedMods.size(); ++var4)
        {
            for (var5 = 0; var5 < var0.dataString.length; ++var5)
            {
                if (var0.dataString[var5].lastIndexOf("mod_") != -1 && var0.dataString[var5].substring(var0.dataString[var5].lastIndexOf("mod_")).startsWith((String)bannedMods.get(var4)))
                {
                    var11.add(var0.dataString[var5]);
                }
            }
        }

        ArrayList var12 = new ArrayList();

        for (var5 = 0; var5 < ModLoader.getLoadedMods().size(); ++var5)
        {
            if (!(ModLoader.getLoadedMods().get(var5) instanceof BaseModMp))
            {
                continue;
            }
            
            BaseModMp var6 = (BaseModMp)ModLoader.getLoadedMods().get(var5);

            if (var6.hasClientSide() && var6.toString().lastIndexOf("mod_") != -1)
            {
                String var7 = var6.toString().substring(var6.toString().lastIndexOf("mod_"));
                boolean var8 = false;

                for (int var9 = 0; var9 < var0.dataString.length; ++var9)
                {
                    if (var0.dataString[var9].lastIndexOf("mod_") != -1)
                    {
                        String var10 = var0.dataString[var9].substring(var0.dataString[var9].lastIndexOf("mod_"));

                        if (var7.equals(var10))
                        {
                            var8 = true;
                            break;
                        }
                    }
                }

                if (!var8)
                {
                    var12.add(var7);
                }
            }
        }

        int var13;
        StringBuilder var14;

        if (var11.size() != 0)
        {
            var14 = new StringBuilder();

            for (var13 = 0; var13 < var11.size(); ++var13)
            {
                if (((String)var11.get(var13)).lastIndexOf("mod_") != -1)
                {
                    if (var14.length() != 0)
                    {
                        var14.append(", ");
                    }

                    var14.append(((String)var11.get(var13)).substring(((String)var11.get(var13)).lastIndexOf("mod_")));
                }
            }

            log(var1.username + " kicked for having " + var14.toString());
            StringBuilder var16 = new StringBuilder();

            for (int var15 = 0; var15 < var11.size(); ++var15)
            {
                if (((String)var11.get(var15)).lastIndexOf("mod_") != -1)
                {
                    var16.append("\n");
                    var16.append(((String)var11.get(var15)).substring(((String)var11.get(var15)).lastIndexOf("mod_")));
                }
            }

            var1.playerNetServerHandler.kickPlayer("The following mods are banned on this server:" + var16.toString());
        }
        else if (var12.size() != 0)
        {
            var14 = new StringBuilder();

            for (var13 = 0; var13 < var12.size(); ++var13)
            {
                if (((String)var12.get(var13)).lastIndexOf("mod_") != -1)
                {
                    var14.append("\n");
                    var14.append(((String)var12.get(var13)).substring(((String)var12.get(var13)).lastIndexOf("mod_")));
                }
            }

            var1.playerNetServerHandler.kickPlayer("You are missing the following mods:" + var14.toString());
        }
    }

    private static void handleSendKey(Packet230ModLoader var0, EntityPlayerMP var1)
    {
        if (var0.dataInt.length != 2)
        {
            System.out.println("SendKey packet received with missing data.");
        }
        else
        {
            int var2 = var0.dataInt[0];
            int var3 = var0.dataInt[1];

            for (int var4 = 0; var4 < ModLoader.getLoadedMods().size(); ++var4)
            {
                BaseMod var5 = (BaseMod)ModLoader.getLoadedMods().get(var4);

                if (var5 instanceof BaseModMp)
                {
                    BaseModMp var6 = (BaseModMp)var5;

                    if (var6.getId() == var2)
                    {
                        var6.handleSendKey(var1, var3);
                        break;
                    }
                }
            }
        }
    }

    public static void getCommandInfo(ICommandListener var0)
    {
        for (int var1 = 0; var1 < ModLoader.getLoadedMods().size(); ++var1)
        {
            BaseMod var2 = (BaseMod)ModLoader.getLoadedMods().get(var1);

            if (var2 instanceof BaseModMp)
            {
                BaseModMp var3 = (BaseModMp)var2;
                var3.getCommandInfo(var0);
            }
        }
    }

    public static boolean handleCommand(String var0, String var1, Logger var2, boolean var3)
    {
        boolean var4 = false;

        for (int var5 = 0; var5 < ModLoader.getLoadedMods().size(); ++var5)
        {
            BaseMod var6 = (BaseMod)ModLoader.getLoadedMods().get(var5);

            if (var6 instanceof BaseModMp)
            {
                BaseModMp var7 = (BaseModMp)var6;

                if (var7.handleCommand(var0, var1, var2, var3))
                {
                    var4 = true;
                }
            }
        }

        return var4;
    }

    public static void sendChatToAll(String var0, String var1)
    {
        String var2 = var0 + ": " + var1;
        sendChatToAll(var2);
    }

    public static void sendChatToAll(String var0)
    {
        List var1 = ModLoader.getMinecraftServerInstance().configManager.playerEntities;

        for (int var2 = 0; var2 < var1.size(); ++var2)
        {
            EntityPlayerMP var3 = (EntityPlayerMP)var1.get(var2);
            var3.playerNetServerHandler.sendPacket(new Packet3Chat(var0));
        }

        MinecraftServer.logger.info(var0);
    }

    public static void sendChatToOps(String var0, String var1)
    {
        String var2 = "\u00a77(" + var0 + ": " + var1 + ")";
        sendChatToOps(var2);
    }

    public static void sendChatToOps(String var0)
    {
        List var1 = ModLoader.getMinecraftServerInstance().configManager.playerEntities;

        for (int var2 = 0; var2 < var1.size(); ++var2)
        {
            EntityPlayerMP var3 = (EntityPlayerMP)var1.get(var2);

            if (ModLoader.getMinecraftServerInstance().configManager.isOp(var3.username))
            {
                var3.playerNetServerHandler.sendPacket(new Packet3Chat(var0));
            }
        }

        MinecraftServer.logger.info(var0);
    }

    public static Packet getTileEntityPacket(BaseModMp var0, int var1, int var2, int var3, int var4, int[] var5, float[] var6, String[] var7)
    {
        Packet230ModLoader var8 = new Packet230ModLoader();
        var8.modId = "ModLoaderMP".hashCode();
        var8.packetType = 1;
        var8.isChunkDataPacket = true;
        int var9 = var5 != null ? var5.length : 0;
        int[] var10 = new int[var9 + 5];
        var10[0] = var0.getId();
        var10[1] = var1;
        var10[2] = var2;
        var10[3] = var3;
        var10[4] = var4;

        if (var9 != 0)
        {
            System.arraycopy(var5, 0, var10, 5, var5.length);
        }

        var8.dataInt = var10;
        var8.dataFloat = var6;
        var8.dataString = var7;
        return var8;
    }

    public static void sendTileEntityPacket(TileEntity var0)
    {
        sendPacketToAll(var0.getDescriptionPacket());
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
