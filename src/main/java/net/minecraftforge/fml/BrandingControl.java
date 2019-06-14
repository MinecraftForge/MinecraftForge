/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class BrandingControl
{
    private static List<String> brandings;
    private static List<String> brandingsNoMC;

    private static void computeBranding()
    {
        if (brandings == null)
        {
            ImmutableList.Builder<String> brd = ImmutableList.builder();
            brd.add("Forge " + ForgeVersion.getVersion());
            brd.add("Minecraft " + MCPVersion.getMCVersion());
            brd.add("MCP " + MCPVersion.getMCPVersion());
            int tModCount = ModList.get().size();
            brd.add(ForgeI18n.parseMessage("fml.menu.loadingmods", tModCount));
            brandings = brd.build();
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }

    private static List<String> getBrandings(boolean includeMC, boolean reverse)
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

    public static String getClientBranding() {
        return "forge";
    }

    public static String getServerBranding() {
        return "forge";
    }

    public static void clearCaches(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (resourcePredicate.test(VanillaResourceType.LANGUAGES)) {
            brandings = null;
            brandingsNoMC = null;
        }
    }
}
