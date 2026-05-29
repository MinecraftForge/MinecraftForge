/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.fml.loading.LoadingModList;
import net.minecraftsingularity.singularityspi.language.IModFileInfo;
import net.minecraftsingularity.singularityspi.language.IModInfo;
import net.minecraftsingularity.singularityspi.language.ModFileScanData;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFile;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftsingularity.singularityspi.locating.IModFile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Master list of all mods - game-side version. This is classloaded in the game scope and
 * can dispatch game level events as a result.
 */
public final class ModList {
    private ModList() {}

    private static final List<IModFileInfo> MOD_FILES;
    private static final List<IModInfo> SORTED_LIST;
    private static final Map<String, IModFileInfo> FILE_BY_ID;
    private static List<ModContainer> mods;
    private static Map<String, ModContainer> indexedMods;
    private static List<ModContainer> sortedContainers;

    static {
        var loadingModFiles = LoadingModList.getModFiles().stream().map(ModFileInfo::getFile).toList();
        var loadingSortedList = LoadingModList.getMods();

        var modFileInfos = MOD_FILES = loadingModFiles.stream().map(ModFile::getModFileInfo).toList();
        SORTED_LIST = loadingSortedList.stream().map(IModInfo.class::cast).toList();
        var byId = new HashMap<String, IModFileInfo>();
        for (var file : modFileInfos) {
            for (var mod : file.getMods())
                byId.put(mod.getModId(), mod.getOwningFile());
        }
        FILE_BY_ID = Map.copyOf(byId);
        CrashReportCallables.registerCrashCallable("Mod List", ModList::crashReport);
    }

    private static String getModContainerState(String modId) {
        return getModContainerById(modId).map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE");
    }

    private static String fileToLine(IModFile mf) {
        var mainMod = mf.getModInfos().getFirst();
        return String.format(Locale.ENGLISH, "%-50.50s|%-30.30s|%-30.30s|%-20.20s|%-10.10s|Manifest: %s", mf.getFileName(),
                mainMod.getDisplayName(),
                mainMod.getModId(),
                mainMod.getVersion(),
                getModContainerState(mainMod.getModId()),
                ((ModFileInfo)mf.getModFileInfo()).getCodeSigningFingerprint().orElse("NOSIGNATURE"));
    }

    private static String crashReport() {
        return "\n" + applyForEachModFile(ModList::fileToLine).collect(Collectors.joining("\n\t\t", "\t\t", ""));
    }

    public static void init() {}

    public static List<IModFileInfo> getModFiles() {
        return MOD_FILES;
    }

    public static IModFileInfo getModFileById(String modid) {
        return FILE_BY_ID.get(modid);
    }

    static void setLoadedMods(final List<ModContainer> modContainers) {
        mods = modContainers;
        sortedContainers = modContainers.stream().sorted(Comparator.comparingInt(c -> SORTED_LIST.indexOf(c.getModInfo()))).toList();
        indexedMods = modContainers.stream().collect(Collectors.toUnmodifiableMap(ModContainer::getModId, Function.identity()));
    }

    static void clearLoadedMods() {
        mods = List.of();
        indexedMods = Map.of();
        sortedContainers = List.of();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getModObjectById(String modId) {
        return getModContainerById(modId).map(ModContainer::getMod).map(o -> (T) o);
    }

    public static Optional<? extends ModContainer> getModContainerById(String modId) {
        return Optional.ofNullable(indexedMods.get(modId));
    }

    public static Optional<? extends ModContainer> getModContainerByObject(Object obj) {
        return mods.stream().filter(mc -> mc.getMod() == obj).findFirst();
    }

    public static List<IModInfo> getMods() {
        return SORTED_LIST;
    }

    public static boolean isLoaded(String modTarget) {
        return indexedMods.containsKey(modTarget);
    }

    public static int size() {
        return mods.size();
    }

    public static List<ModFileScanData> getAllScanData() {
        final class LazyInit {
            private LazyInit() {}
            private static final List<ModFileScanData> MOD_FILE_SCAN_DATA = SORTED_LIST.stream()
                    .map(IModInfo::getOwningFile)
                    .filter(Objects::nonNull)
                    .map(IModFileInfo::getFile)
                    .distinct()
                    .map(IModFile::getScanResult)
                    .toList();
        }
        return LazyInit.MOD_FILE_SCAN_DATA;
    }

    public static void forEachModFile(Consumer<IModFile> fileConsumer) {
        MOD_FILES.stream().map(IModFileInfo::getFile).forEach(fileConsumer);
    }

    public static <T> Stream<T> applyForEachModFile(Function<IModFile, T> function) {
        return MOD_FILES.stream().map(IModFileInfo::getFile).map(function);
    }

    public static void forEachModContainer(BiConsumer<String, ModContainer> modContainerConsumer) {
        indexedMods.forEach(modContainerConsumer);
    }

    public static void forEachModInOrder(Consumer<ModContainer> containerConsumer) {
        sortedContainers.forEach(containerConsumer);
    }

    public static List<ModContainer> getLoadedMods() {
        return sortedContainers;
    }

    public static <T> Stream<T> applyForEachModContainer(Function<ModContainer, T> function) {
        return indexedMods.values().stream().map(function);
    }
}
