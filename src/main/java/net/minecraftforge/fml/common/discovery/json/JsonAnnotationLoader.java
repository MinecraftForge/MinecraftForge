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

package net.minecraftforge.fml.common.discovery.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.json.ASMInfo.Annotation;
import net.minecraftforge.fml.common.discovery.json.ASMInfo.TargetType;

public class JsonAnnotationLoader
{
    public static final String ANNOTATION_JSON = "META-INF/fml_cache_annotation.json";
    private static final Gson GSON = new GsonBuilder().create();
    private static final java.lang.reflect.Type INFO_TABLE = new TypeToken<Map<String, ASMInfo>>(){}.getType();

    public static Multimap<String, ASMData> loadJson(InputStream data, ModCandidate candidate, ASMDataTable table)
    {
        Map<String, ASMInfo> map = GSON.fromJson(new InputStreamReader(data, StandardCharsets.UTF_8), INFO_TABLE);
        Multimap<String, ASMData> ret = HashMultimap.create();

        for (Entry<String, ASMInfo> entry : map.entrySet())
        {
            //TODO: Java9 Multi-Release Jars, picking the correct class for the current platform. For now we just ignore them.
            if (entry.getKey().startsWith("META-INF/"))
                continue;
            //TODO: Remove in 1.13, some older mods have these in the entries due to FG issue. Basically filter out scala synthetic class.
            if (entry.getKey().endsWith("$"))
            	continue;

            ASMInfo asm_info = entry.getValue();
            if (asm_info.interfaces != null)
            {
                for (String type : asm_info.interfaces)
                {
                    //Interfaces use internal name, but annotations use source names. See ASMModParser.sendToTable
                    table.addASMData(candidate, type, asm_info.name, null, null);
                    ret.put(type, new ASMData(candidate, type, asm_info.name, null, null));
                }
            }


            String owner_name = asm_info.name.replace('/', '.');
            if (asm_info.annotations != null)
            {
                for (Annotation anno : asm_info.annotations)
                {
                    String name = anno.name.indexOf(';') > 0 ? Type.getType(anno.name).getClassName() : anno.name;
                    String target = anno.target != null && (anno.type == TargetType.CLASS || anno.type == TargetType.SUBTYPE) ? anno.target.replace('/', '.') : anno.target;

                    table.addASMData(candidate, name, owner_name, target, anno.getValues(asm_info));
                    ret.put(name, new ASMData(candidate, name, owner_name, target, anno.getValues(asm_info)));
                }
            }
        }

        return ret;
    }
}
