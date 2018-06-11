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

package net.minecraftforge.fml.loading;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StringSubstitutor
{
    private static final Map<String,String> globals = ImmutableMap.of(
            "mcVersion", ForgeVersion.mcVersion,
            "forgeVersion", ForgeVersion.getVersion(),
            "mcpVersion", ForgeVersion.mcpVersion);

    public static String replace(final String in, final ModFile file) {
        return new StrSubstitutor(getStringLookup(file)).replace(in);
    }

    private static StrLookup<String> getStringLookup(final ModFile file) {
        return new StrLookup<String>()
        {
            @Override
            public String lookup(String key)
            {
                final String[] parts = key.split("\\.");
                if (parts.length == 1) return key;
                final String pfx = parts[0];
                if ("global".equals(pfx))
                {
                    return globals.get(parts[1]);
                }
                else if ("file".equals(pfx))
                {
                    return String.valueOf(file.getSubstitutionMap().get().get(parts[1]));
                }
                return key;
            }
        };
    }
    private static List<String> split(final String in) {
        return Arrays.asList(in.split("."));
    }
}
