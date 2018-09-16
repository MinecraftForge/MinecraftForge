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

package net.minecraftforge.fml.hooks;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class LanguageHook
{
    /**
     * Loads a lang file, first searching for a marker to enable the 'extended' format {escape characters}
     * If the marker is not found it simply returns and let the vanilla code load things.
     * The Marker is 'PARSE_ESCAPES' by itself on a line starting with '#' as such:
     * #PARSE_ESCAPES
     *
     * @param table The Map to load each key/value pair into.
     * @param inputstream Input stream containing the lang file.
     * @return A new InputStream that vanilla uses to load normal Lang files, Null if this is a 'enhanced' file and loading is done.
     */
    @Nullable
    public static InputStream loadLanguage(Map<String, String> table, InputStream inputstream) throws IOException
    {
        byte[] data = IOUtils.toByteArray(inputstream);

        boolean isEnhanced = false;
        for (String line : IOUtils.readLines(new ByteArrayInputStream(data), StandardCharsets.UTF_8))
        {
            if (!line.isEmpty() && line.charAt(0) == '#')
            {
                line = line.substring(1).trim();
                if (line.equals("PARSE_ESCAPES"))
                {
                    isEnhanced = true;
                    break;
                }
            }
        }

        if (!isEnhanced)
            return new ByteArrayInputStream(data);

        Properties props = new Properties();
        props.load(new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8));
        for (Map.Entry<Object, Object> e : props.entrySet())
        {
            table.put((String)e.getKey(), (String)e.getValue());
        }
        props.clear();
        return null;
    }
}
