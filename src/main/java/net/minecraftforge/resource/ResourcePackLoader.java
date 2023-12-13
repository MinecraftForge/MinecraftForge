/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.packs.repository.Pack.Info;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.CompositePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class ResourcePackLoader {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void loadResourcePacks(PackRepository resourcePacks, boolean client) {
        resourcePacks.addPackFinder(repo -> findPacks(repo, client));
    }

    @NotNull
    public static PathPackResources createPackForMod(IModFileInfo mf) {
        return new ModPathPackResources(mf.getFile());
    }

    private static class ModPathPackResources extends PathPackResources {
        private final IModFile mod;
        private final String[] prefix;

        private ModPathPackResources(IModFile mod, String... prefix) {
            super(mod.getFileName(), true, mod.getFilePath());
            this.mod = mod;
            this.prefix = prefix;
        }

        @NotNull
        @Override
        protected Path resolve(@NotNull String... paths) {
            if (prefix != null && prefix.length > 0) {
                var tmp = new String[prefix.length + paths.length];
                System.arraycopy(prefix, 0, tmp, 0, prefix.length);
                System.arraycopy(paths, 0, tmp, prefix.length, paths.length);
                return this.mod.findResource(tmp);
            }
            return this.mod.findResource(paths);
        }

        private static class Supplier implements Pack.ResourcesSupplier {
            private final IModFile mod;

            private Supplier(IModFile mod) {
                this.mod = mod;
            }

            @Override
            public PackResources openPrimary(String packId) {
                return new ModPathPackResources(mod);
            }

            @Override
            public PackResources openFull(String packId, Info info) {
                var primary = openPrimary(packId);
                if (info.overlays().isEmpty())
                    return primary;

                var lst = new ArrayList<PackResources>(info.overlays().size());
                for (var overlay : info.overlays())
                    lst.add(new ModPathPackResources(mod, overlay));

                return new CompositePackResources(primary, lst);
            }
        }
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

    private static void findPacks(Consumer<Pack> packAcceptor, boolean client) {
        var type = client ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;
        var version = SharedConstants.getCurrentVersion().getPackVersion(type);
        var hiddenPacks = new ArrayList<PackResources>();

        for (var mod : ModList.get().getModFiles()) {
            if (mod.requiredLanguageLoaders().stream().anyMatch(ls -> ls.languageName().equals("minecraft")))
                continue;

            var file = mod.getFile();

            var supplier = new ModPathPackResources.Supplier(file);

            var modinfo = file.getModInfos().get(0);
            var name = "mod:" + modinfo.getModId();
            var meta = Pack.readPackInfo(name, supplier, version);
            Pack pack = null;
            if (meta != null)
                pack = Pack.create(name, Component.literal(file.getFileName()), false, supplier, meta, Pack.Position.BOTTOM, false, PackSource.DEFAULT);

            if (pack == null) {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(modinfo, ModLoadingStage.ERROR, "fml.modloading.brokenresources", file));
                continue;
            }

            LOGGER.debug(Logging.CORE, "Generating PackInfo named {} for mod file {}", name, file.getFilePath());
            if (!client || mod.showAsResourcePack())
                packAcceptor.accept(pack);
            else
                hiddenPacks.add(pack.open());
        }

        if (!hiddenPacks.isEmpty()) {
            @SuppressWarnings("resource")
            var delegating = new DelegatingPackResources("mod_resources", false,
                new PackMetadataSection(
                    Component.translatable("fml.resources.modresources", hiddenPacks.size()),
                    version,
                    Optional.empty()
                ),
                hiddenPacks
            );

            var supplier = delegating.supplier();

            // Create a resource pack merging all mod resources that should be hidden
            var modResourcesPack = Pack.readMetaAndCreate("mod_resources", Component.literal("Mod Resources"), true, supplier, type, Pack.Position.BOTTOM, PackSource.DEFAULT);
            packAcceptor.accept(modResourcesPack);
        }
    }
}
