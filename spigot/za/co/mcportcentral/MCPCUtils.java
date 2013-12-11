package za.co.mcportcentral;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.logging.ILogAgent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class MCPCUtils {

    public static boolean isOverridingUpdateEntity(Class<? extends TileEntity> c) 
    {
        Class clazz = null;
        try {
            clazz = c.getMethod("func_70316_g").getDeclaringClass();  // updateEntity
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return clazz != TileEntity.class;
    }

    public static boolean canTileEntityUpdate(Class<? extends TileEntity> c)
    {
        boolean canUpdate = false;
        try {
            Constructor<? extends TileEntity> ctor = c.getConstructor();
            TileEntity te = ctor.newInstance();
            canUpdate = te.canUpdate();
        } catch (Throwable e) {
            // ignore
        }
        return canUpdate;
    }

    public static <T> void dumpAndSortClassList(List<Class<? extends T>> classList)
    {
        List<String> sortedClassList = new ArrayList();
        for (Class clazz : classList)
        {
            sortedClassList.add(clazz.getName());
        }
        Collections.sort(sortedClassList);
        for (int i = 0; i < sortedClassList.size(); i++)
        {
            MinecraftServer.getServer().logInfo("Detected TE " + sortedClassList.get(i) + " with canUpdate set to true and no updateEntity override!. This is NOT good, please report to mod author as this can hurt performance.");
        }
    }

    public static boolean migrateWorlds(String worldType, String oldWorldContainer, String newWorldContainer, String worldName)
    {
        boolean result = true;
        File newWorld = new File(new File(newWorldContainer), worldName);
        File oldWorld = new File(new File(oldWorldContainer), worldName);

        if ((!newWorld.isDirectory()) && (oldWorld.isDirectory()))
        {
            final ILogAgent log = MinecraftServer.getServer().getLogAgent();
            log.logInfo("---- Migration of old " + worldType + " folder required ----");
            log.logInfo("MCPC has moved back to using the Forge World structure, your " + worldType + " folder will be moved to a new location in order to operate correctly.");
            log.logInfo("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using MCPC in the future.");
            log.logInfo("Attempting to move " + oldWorld + " to " + newWorld + "...");

            if (newWorld.exists())
            {
                log.logSevere("A file or folder already exists at " + newWorld + "!");
                log.logInfo("---- Migration of old " + worldType + " folder failed ----");
                result = false;
            }
            else if (newWorld.getParentFile().mkdirs() || newWorld.getParentFile().exists())
            {
                log.logInfo("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);

                // Migrate world data
                try
                {
                    com.google.common.io.Files.move(oldWorld, newWorld);
                }
                catch (IOException exception)
                {
                    log.logSevere("Unable to move world data.");
                    exception.printStackTrace();
                    result = false;
                }
                try
                {
                    com.google.common.io.Files.copy(new File(oldWorld.getParent(), "level.dat"), new File(newWorld, "level.dat"));
                }
                catch (IOException exception)
                {
                    log.logSevere("Unable to migrate world level.dat.");
                }

                log.logInfo("---- Migration of old " + worldType + " folder complete ----");
            }
            else result = false;
        }
        return result;
    }
}
