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
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.CompositePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
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

            var root = file.findResource("");
            var supplier = new PathPackResources.PathResourcesSupplier(root);

            var modinfo = file.getModInfos().get(0);
            var name = "mod:" + modinfo.getModId();
            var info = new PackLocationInfo(name, Component.literal(file.getFileName()), PackSource.DEFAULT, Optional.empty());
            var meta = Pack.readPackMetadata(info, supplier, version);
            Pack pack = null;
            if (meta != null)
                pack = new Pack(info, supplier, meta, new PackSelectionConfig(false, Position.BOTTOM, false));

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
            var info = new PackLocationInfo("mod_resources", Component.translatable("fml.resources.modresources", hiddenPacks.size()), PackSource.DEFAULT, Optional.empty());
            @SuppressWarnings("resource")
            var delegating = new DelegatingPackResources(info,
                new PackMetadataSection(
                    Component.translatable("fml.resources.modresources", hiddenPacks.size()),
                    version,
                    Optional.empty()
                ),
                hiddenPacks
            );

            var supplier = delegating.supplier();

            // Create a resource pack merging all mod resources that should be hidden
            var wrapper = new PackLocationInfo("mod_resources", Component.literal("Mod Resources"), PackSource.DEFAULT, Optional.empty());
            var wrapperCfg = new PackSelectionConfig(true, Position.BOTTOM, false);
            var modResourcesPack = Pack.readMetaAndCreate(wrapper, supplier, type, wrapperCfg);
            packAcceptor.accept(modResourcesPack);
        }
    }
}
