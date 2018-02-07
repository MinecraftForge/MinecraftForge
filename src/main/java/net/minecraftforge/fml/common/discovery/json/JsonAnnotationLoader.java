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
package net.minecraftforge.fml.common.discovery.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.json.ASMInfo.Annotation;

public class JsonAnnotationLoader
{
    public static final String ANNOTATION_JSON = "META-INF/fml_cache_annotation.json";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Type INFO_TABLE = new TypeToken<Map<String, ASMInfo>>(){}.getType();

    public static Multimap<String, ASMData> loadJson(InputStream data, ModCandidate candidate, ASMDataTable table)
    {
        Map<String, ASMInfo> map = GSON.fromJson(new InputStreamReader(data), INFO_TABLE);
        Multimap<String, ASMData> ret = HashMultimap.create();

        for (Entry<String, ASMInfo> entry : map.entrySet())
        {
            //TODO: Java9 Multi-Release Jars, picking the correct class for the current platform. For now we just ignore them.
            if (entry.getKey().startsWith("META-INF/"))
                continue;

            ASMInfo asm_info = entry.getValue();
            if (asm_info.interfaces != null)
            {
                for (String type : asm_info.interfaces)
                {
                    table.addASMData(candidate, type, asm_info.name, null, null);
                    ret.put(type, new ASMData(candidate, type, asm_info.name, null, null));
                }
            }

            if (asm_info.annotations != null)
            {
                for (Annotation anno : asm_info.annotations)
                {
                    table.addASMData(candidate, anno.name, asm_info.name, anno.target, anno.getValues(asm_info));
                    ret.put(anno.name, new ASMData(candidate, anno.name, asm_info.name, anno.target, anno.getValues(asm_info)));
                }
            }
        }

        return ret;
    }
}
