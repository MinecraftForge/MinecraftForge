/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.asm.transformers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

public class ModAccessTransformer extends AccessTransformer
{
    public static final Attributes.Name FMLAT = new Attributes.Name("FMLAT");
    private static Map<String, String> embedded = Maps.newHashMap(); //Needs to be primitive so that both classloaders get the same class.
    @SuppressWarnings("unchecked")
    public ModAccessTransformer() throws Exception
    {
        super(ModAccessTransformer.class);
        //We are in the new ClassLoader here, so we need to get the static field from the other ClassLoader.
        ClassLoader classLoader = this.getClass().getClassLoader().getClass().getClassLoader(); //Bit odd but it gets the class loader that loaded our current class loader yay java!
        Class<?> otherClazz = Class.forName(this.getClass().getName(), true, classLoader);
        Field otherField = otherClazz.getDeclaredField("embedded");
        otherField.setAccessible(true);
        embedded = (Map<String, String>)otherField.get(null);

        for (Map.Entry<String, String> e : embedded.entrySet())
        {
            int old_count = getModifiers().size();
            processATFile(CharSource.wrap(e.getValue()));
            int added = getModifiers().size() - old_count;
            if (added > 0)
            {
                FMLLog.log.debug("Loaded {} rules from AccessTransformer mod jar file {}\n", added, e.getKey());
            }
        }
    }

    public static void addJar(JarFile jar, String atList) throws IOException
    {
        for (String at : atList.split(" "))
        {
            JarEntry jarEntry = jar.getJarEntry("META-INF/"+at);
            if (jarEntry != null)
            {
                embedded.put(String.format("%s!META-INF/%s", jar.getName(), at),
                        new JarByteSource(jar,jarEntry).asCharSource(StandardCharsets.UTF_8).read());
            }
        }
    }

    private static class JarByteSource extends ByteSource
    {
        private JarFile jar;
        private JarEntry entry;
        public JarByteSource(JarFile jar, JarEntry entry)
        {
            this.jar = jar;
            this.entry = entry;
        }
        @Override
        public InputStream openStream() throws IOException
        {
            return jar.getInputStream(entry);
        }
    }
}
