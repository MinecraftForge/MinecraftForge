/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
package net.minecraftforge.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ContainerType;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.asm.ASMModParser;
import net.minecraftforge.fml.common.discovery.json.JsonAnnotationLoader;

public class TestAnnotationParser
{
    private static String TEST_JAR = "forestry_1.12.2-5.7.0.0.jar";
    public static Pattern classFile = Pattern.compile("[^\\s\\$]+(\\$[^\\s]+)?\\.class$");
    private static final int RUN_COUNT = 100;

    private File getFile()
    {
        ClassLoader cl = getClass().getClassLoader();
        URL url = cl.getResource(TEST_JAR);
        return url == null ? null : new File(url.getFile());
    }

    @Test
    public void testAnnotationLoaderASM() throws IOException
    {
        File jar = getFile();
        if (jar == null)
            return; //Skip this test if the test jar doesn't exist.

        Timer timer = new Timer();
        for( int x = 0; x < RUN_COUNT; x++)
        {
            timer.start();
            loadAnnotationsASM(jar);
            timer.end(null);
        }
        System.out.println("LoaderASM: " + timer.finish());
    }


    @Test
    public void testAnnotationLoaderJSON() throws IOException
    {
        File jar = getFile();
        if (jar == null)
            return; //Skip this test if the test jar doesn't exist.

        Timer timer = new Timer();
        for( int x = 0; x < RUN_COUNT; x++)
        {
            timer.start();
            loadAnnotationsJSON(jar);
            timer.end(null);
        }
        System.out.println("LoaderJSON: " + timer.finish());
    }

    private void loadAnnotationsASM(File jar) throws IOException
    {
        ASMDataTable dataTable = new ASMDataTable();
        ModCandidate candidate = new ModCandidate(jar, jar, ContainerType.JAR);
        try (ZipFile in = new ZipFile(jar))
        {
            for (ZipEntry e : Collections.list(in.entries()))
            {
                if (e.getName() != null && e.getName().startsWith("__MACOSX"))
                    continue;

                Matcher match = classFile.matcher(e.getName());
                if (match.matches())
                {
                    ASMModParser modParser;
                    try (InputStream inputStream = in.getInputStream(e))
                    {
                        modParser = new ASMModParser(inputStream);
                    }
                    //candidate.addClassEntry(e.getName());
                    if (modParser != null)
                        modParser.sendToTable(dataTable, candidate);
                }
            }
        }
    }

    private void loadAnnotationsJSON(File jar) throws IOException
    {
        ASMDataTable dataTable = new ASMDataTable();
        ModCandidate candidate = new ModCandidate(jar, jar, ContainerType.JAR);
        try (ZipFile in = new ZipFile(jar))
        {
            // We need to loop everything to gather a list of class files, but as we're not reading every entry we should be faster?
            for (ZipEntry e : Collections.list(in.entries()))
            {
                if (e.getName() != null && e.getName().startsWith("__MACOSX"))
                    continue;

                Matcher match = classFile.matcher(e.getName());
                if (match.matches())
                {
                    //We check for classes, make this fancier and support multi-release jars?
                    //candidate.addClassEntry(e.getName());
                }
            }

            InputStream json_input = in.getInputStream(in.getEntry(JsonAnnotationLoader.ANNOTATION_JSON));
            JsonAnnotationLoader.loadJson(json_input, candidate, dataTable);
        }
    }

    private static class Timer
    {
        private long start;
        private long min = Long.MAX_VALUE;
        private long max = Long.MIN_VALUE;
        private long total = 0;
        private int count = 0;

        public void start()
        {
            this.start = System.currentTimeMillis();
        }
        public void end(String message)
        {
            long now = System.currentTimeMillis();
            long time = now - start;
            if (message != null)
                System.out.println(String.format(message, time));
            min = Long.min(min, time);
            max = Long.max(max, time);
            total += time;
            count++;
        }

        public String finish()
        {
            return  "Runs: " + count +
                   " Min: " + min +
                   " Max: " + max +
                   " Total: " + total +
                   " Average: " + (total/count);
        }
    }
}
