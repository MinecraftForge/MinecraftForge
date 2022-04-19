/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ResourcePackLoader {
    private static Map<IModFile, PathResourcePack> modResourcePacks;

    public static Optional<PathResourcePack> getPackFor(String modId) {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).
                map(IModFileInfo::getFile).map(mf->modResourcePacks.get(mf));
    }

    @Deprecated
    public static void loadResourcePacks(PackRepository resourcePacks, BiFunction<Map<IModFile, ? extends PathResourcePack>, BiConsumer<? super PathResourcePack, Pack>, ? extends RepositorySource> packFinder) {
        loadResourcePacks(resourcePacks, (map) -> packFinder.apply(map, (rp,p) -> {}));
    }

    public static void loadResourcePacks(PackRepository resourcePacks, Function<Map<IModFile, ? extends PathResourcePack>, ? extends RepositorySource> packFinder) {
        modResourcePacks = ModList.get().getModFiles().stream()
                .filter(mf->mf.requiredLanguageLoaders().stream().noneMatch(ls->ls.languageName().equals("minecraft")))
                .map(mf -> Pair.of(mf, createPackForMod(mf)))
                .collect(Collectors.toMap(p -> p.getFirst().getFile(), Pair::getSecond, (u,v) -> { throw new IllegalStateException(String.format(Locale.ENGLISH, "Duplicate key %s", u)); },  LinkedHashMap::new));
        resourcePacks.addPackFinder(packFinder.apply(modResourcePacks));
    }

    @NotNull
    public static PathResourcePack createPackForMod(IModFileInfo mf)
    {
        return new PathResourcePack(mf.getFile().getFileName(), mf.getFile().getFilePath()){
            final IModFile modFile = mf.getFile();
            @Nonnull
            @Override
            protected Path resolve(@Nonnull String... paths)
            {
                return modFile.findResource(paths);
            }
        };
    }

    public static List<String> getPackNames() {
        return ModList.get().applyForEachModFile(mf->"mod:"+mf.getModInfos().get(0).getModId()).filter(n->!n.equals("mod:minecraft")).collect(Collectors.toList());
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

}
