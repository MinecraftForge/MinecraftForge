/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Level;

import com.google.common.io.Files;

/**
 * Copied from http://stackoverflow.com/questions/1399126/java-util-zip-recreating-directory-structure
 * because the code looked very tidy and neat. Thanks, McDowell!
 *
 * @author McDowell
 *
 */
public class ZipperUtil {
    public static void zip(File directory, File zipfile) throws IOException
    {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = null;
        try
        {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty())
            {
                directory = queue.pop();
                for (File kid : directory.listFiles())
                {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory())
                    {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else
                    {
                        zout.putNextEntry(new ZipEntry(name));
                        Files.copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally
        {
            res.close();
        }
    }

    public static void backupWorld() throws IOException
    {
        String dirName = FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName();

        backupWorld(dirName);
    }

    @Deprecated
    public static void backupWorld(String dirName, String saveName) throws IOException
    {
        backupWorld(dirName);
    }

    public static void backupWorld(String dirName) throws IOException
    {
        File dstFolder = FMLCommonHandler.instance().getSavesDirectory();
        File zip = new File(dstFolder, String.format("%s-%2$tY%2$tm%2$td-%2$tH%2$tM%2$tS.zip", dirName, System.currentTimeMillis()));

        try
        {
            ZipperUtil.zip(new File(dstFolder, dirName), zip);
        }
        catch (IOException e)
        {
            FMLLog.log.warn("World backup failed.", e);
            throw e;
        }

        FMLLog.log.info("World backup created at {}.", zip.getCanonicalPath());
    }
}
