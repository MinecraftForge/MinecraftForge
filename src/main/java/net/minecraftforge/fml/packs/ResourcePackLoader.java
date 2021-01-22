/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.packs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public class ResourcePackLoader
{
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static ResourcePackList resourcePackList;

    public static Optional<ModFileResourcePack> getResourcePackFor(String modId)
    {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).
                map(ModFileInfo::getFile).map(mf->modResourcePacks.get(mf));
    }

    public static void loadResourcePacks(ResourcePackList resourcePacks, BiFunction<Map<ModFile, ? extends ModFileResourcePack>, BiConsumer<? super ModFileResourcePack, ResourcePackInfo>, IPackInfoFinder> packFinder) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream()
                .filter(mf->!Objects.equals(mf.getModLoader(),"minecraft"))
                .map(mf -> new ModFileResourcePack(mf.getFile()))
                .collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity(), (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },  LinkedHashMap::new));
        resourcePacks.addPackFinder(new LambdaFriendlyPackFinder(packFinder.apply(modResourcePacks, ModFileResourcePack::setPackInfo)));
    }

    public static List<String> getPackNames()
    {
        return ModList.get().applyForEachModFile(mf->"mod:"+mf.getModInfos().get(0).getModId()).filter(n->!n.equals("mod:minecraft")).collect(Collectors.toList());
    }

    public static <V> Comparator<Map.Entry<String,V>> getSorter() {
        List<String> order = new ArrayList<>();
        order.add("vanilla");
        order.add("mod_resources");

        ModList.get().getModFiles().stream().
        filter(mf -> !Objects.equals(mf.getModLoader(), "minecraft")).
        map(e -> e.getMods().get(0).getModId()).
        filter(e -> !"minecraft".equals(e)).
        map(e -> "mod:" + e).
        forEach(order::add);

        final Object2IntMap<String> order_f = new Object2IntOpenHashMap<>(order.size());
        for (int x = 0; x < order.size(); x++)
            order_f.put(order.get(x), x);

        return (e1, e2) -> {
            final String s1 = e1.getKey();
            final String s2 = e2.getKey();
            final int i1 = order_f.getOrDefault(s1, -1);
            final int i2 = order_f.getOrDefault(s2, -1);

            if (i1 == i2 && i1 == -1)
                return s1.compareTo(s2);
            if (i1 == -1) return 1;
            if (i2 == -1) return -1;
            return i2 - i1;
        };
    }

    public interface IPackInfoFinder {
        void addPackInfos(Consumer<ResourcePackInfo> consumer, IFactory factory);
    }

    // SO GROSS - DON'T @ me bro
    @SuppressWarnings("unchecked")
    private static class LambdaFriendlyPackFinder implements IPackFinder {
        private IPackInfoFinder wrapped;

        private LambdaFriendlyPackFinder(final IPackInfoFinder wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void findPacks(Consumer<ResourcePackInfo> consumer, IFactory factory)
        {
            wrapped.addPackInfos(consumer, factory);
        }
    }
}
