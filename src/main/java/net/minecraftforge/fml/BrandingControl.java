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

package net.minecraftforge.fml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.ModList;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BrandingControl
{
    private static List<String> brandings;
    private static List<String> brandingsNoMC;

    private static void computeBranding()
    {
        if (brandings == null)
        {
            ImmutableList.Builder<String> brd = ImmutableList.builder();
            brd.add("Minecraft " + ForgeVersion.mcVersion);
            brd.add("MCP " + ForgeVersion.mcpVersion);
            brd.add("Forge " + ForgeVersion.getVersion());
            int tModCount = ModList.get().size();

            brd.add(MessageFormat.format("{0,choice,0#No mods|1#1 mod|1<{0} mods} loaded", tModCount));
            brandings = brd.build();
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }

    public static List<String> getBrandings(boolean includeMC, boolean reverse)
    {
        computeBranding();
        if (includeMC) {
            return reverse ? Lists.reverse(brandings) : brandings;
        } else {
            return reverse ? Lists.reverse(brandingsNoMC) : brandingsNoMC;
        }
    }

    public static void forEachLine(boolean includeMC, boolean reverse, BiConsumer<Integer, String> lineConsumer) {
        final List<String> brandings = getBrandings(includeMC, reverse);
        IntStream.range(0, brandings.size()).boxed().forEachOrdered(idx -> lineConsumer.accept(idx, brandings.get(idx)));
    }
    private static final List<String> defaultClientBranding = Stream.of("fml", "forge").collect(Collectors.toList());
    public static String getClientBranding() {
        return defaultClientBranding.stream().collect(Collectors.joining(","));
    }

    public static final List<String> defaultServerBranding = Arrays.asList("fml", "forge");
    public static String getServerBranding() {
        return defaultServerBranding.stream().collect(Collectors.joining(","));
    }
}
