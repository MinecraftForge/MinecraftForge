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

package net.minecraftforge.fmllegacy.packs;

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
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.PackConstructor;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

public class ResourcePackLoader {
    private static Map<IModFile, ModFileResourcePack> modResourcePacks;
    private static PackRepository resourcePackList;

    public static Optional<ModFileResourcePack> getResourcePackFor(String modId) {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).
                map(IModFileInfo::getFile).map(mf->modResourcePacks.get(mf));
    }

    public static void loadResourcePacks(PackRepository resourcePacks, BiFunction<Map<IModFile, ? extends ModFileResourcePack>, BiConsumer<? super ModFileResourcePack, Pack>, IPackInfoFinder> packFinder) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream()
                .filter(mf->mf.requiredLanguageLoaders().stream().noneMatch(ls->ls.languageName().equals("minecraft")))
                .map(mf -> new ModFileResourcePack(mf.getFile()))
                .collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity(), (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },  LinkedHashMap::new));
        resourcePacks.addPackFinder(new LambdaFriendlyPackFinder(packFinder.apply(modResourcePacks, ModFileResourcePack::setPackInfo)));
    }

    public static List<String> getPackNames() {
        return ModList.get().applyForEachModFile(mf->"mod:"+mf.getModInfos().get(0).getModId()).collect(Collectors.toList());
    }

    public static <V> Comparator<Map.Entry<String,V>> getSorter() {
        List<String> order = new ArrayList<>();
        order.add("vanilla");
        order.add("mod_resources");

        ModList.get().getModFiles().stream()
                .filter(mf -> mf.requiredLanguageLoaders().stream().noneMatch(ls->ls.languageName().equals("minecraft")))
                .map(e -> e.getMods().get(0).getModId())
                .map(e -> "mod:" + e)
                .forEach(order::add);

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
        void addPackInfos(Consumer<Pack> consumer, PackConstructor factory);
    }

    // SO GROSS - DON'T @ me bro
    @SuppressWarnings("unchecked")
    private static class LambdaFriendlyPackFinder implements RepositorySource {
        private IPackInfoFinder wrapped;

        private LambdaFriendlyPackFinder(final IPackInfoFinder wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void loadPacks(Consumer<Pack> consumer, PackConstructor factory) {
            wrapped.addPackInfos(consumer, factory);
        }
    }
}
