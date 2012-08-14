package net.minecraftforge.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.NBTTagCompound;
/**
 * This is just a helper that helps modders save data per world and per player
 * @author battlefield
 *
 */
public class NBTHelper {
	
	private static LinkedList<INBT> handlers = new LinkedList<INBT>();

	public static void registerNbtHandler(INBT handler)
	{
		if(!handlers.contains(handler))
		{
			handlers.add(handler);
		}
	}
	
	public static void saveNBT(File file)
    {
        File file2 = new File(file, "MinecraftForge");
        file2.mkdirs();

        try
        {
            File file1;

            for (INBT handler : handlers)
            {
                if (!handler.savePerPlayer())
                {
                    NBTTagCompound nbt1 = new NBTTagCompound();
                    file1 = new File(file2, handler.nbtName() + ".dat");

                    if (!file1.exists())
                    {
                        CompressedStreamTools.writeCompressed(nbt1, new FileOutputStream(file1));
                    }

                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                    handler.writeToNBT(nbttagcompound);
                    CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file1));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loadNBT(File file)
    {
        File file2 = new File(file, "MinecraftForge");
        file2.mkdirs();

        try
        {
            for (INBT handler : handlers)
            {
                if (!handler.savePerPlayer())
                {
                    NBTTagCompound nbt1 = new NBTTagCompound();
                    File file1 = new File(file, handler.nbtName() + ".dat");

                    if (!file1.exists())
                    {
                        CompressedStreamTools.writeCompressed(nbt1, new FileOutputStream(file1));
                    }

                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                    handler.readFromNBT(nbttagcompound);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void savePlayerNBT(File file, String s)
    {
        File file2;
        file2 = new File(file, "MinecraftForge/" + s);
        file2.mkdirs();

        try
        {
            File file1;

            for (INBT handler : handlers)
            {
                if (handler.savePerPlayer())
                {
                    NBTTagCompound nbt1 = new NBTTagCompound();
                    file1 = new File(file2, handler.nbtName() + ".dat");

                    if (!file1.exists())
                    {
                        CompressedStreamTools.writeCompressed(nbt1, new FileOutputStream(file1));
                    }

                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                    handler.writeToNBT(nbttagcompound);
                    CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file1));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loadPlayerNBT(File file, String s)
    {
        File file2;
        file2 = new File(file, "MinecraftForge/" + s);
        file2.mkdirs();

        try
        {
            for (INBT handler : handlers)
            {
                if (handler.savePerPlayer())
                {
                    NBTTagCompound nbt1 = new NBTTagCompound();
                    File file1 = new File(file2, handler.nbtName() + ".dat");

                    if (!file1.exists())
                    {
                        CompressedStreamTools.writeCompressed(nbt1, new FileOutputStream(file1));
                    }

                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                    handler.readFromNBT(nbttagcompound);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
}
