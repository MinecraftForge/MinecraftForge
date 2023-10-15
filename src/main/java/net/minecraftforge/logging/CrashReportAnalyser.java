/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.logging;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public final class CrashReportAnalyser {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, IModInfo> PACKAGE_MOD_CACHE = new HashMap<>();
    private static final Map<IModInfo, String[]> SUSPECTED_MODS = new HashMap<>();

    private CrashReportAnalyser() {}

    /**
     * Tries to cache, analyze and append the suspected mods for this crash to the crash report.
     * */
    public static String appendSuspectedMods(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            cacheModList();
            analyseCrashReport(throwable, uncategorizedStackTrace);
            buildSuspectedModsSection(stringBuilder);
        }
        catch (Throwable t) {
            LOGGER.error("Failed to append suspected mod(s) to crash report!", t);
        }

        return stringBuilder.toString();
    }

    /**
     * Tries to analyze the given exception and uncategorized stacktrace of the crash report.
     * It checks the stacktrace for any occurrence of the package names cached in the {@link #PACKAGE_MOD_CACHE},
     * including mixin classes.
     * **/
    private static void analyseCrashReport(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        scanThrowable(throwable);
        scanStacktrace(uncategorizedStackTrace);
    }

    /**
     * Converts the suspected mod(s) for this crash to a String to be added to the crash report,
     * including the mods name, its id, version and an issues link if available,
     * as well as the position where the exception occurred (either the stacktrace element
     * or the mixin class and its target(s))
     * **/
    private static void buildSuspectedModsSection(StringBuilder stringBuilder) {
        stringBuilder.append("Suspected Mod");
        stringBuilder.append(SUSPECTED_MODS.size() == 1 ? ": " : "s: ");
        if (SUSPECTED_MODS.isEmpty()) {
            stringBuilder.append("NONE\n");
        }
        else {
            SUSPECTED_MODS.forEach((iModInfo, position) -> {
                stringBuilder
                        .append("\n\t")
                        .append(iModInfo.getDisplayName())
                        .append(" (").append(iModInfo.getModId()).append("),")
                        .append(" Version: ").append(iModInfo.getVersion());

                iModInfo.getOwningFile().getConfig().<String>getConfigElement("issueTrackerURL").ifPresent(issuesLink -> {
                    stringBuilder
                            .append("\n\t\t")
                            .append("Issue tracker URL: ")
                            .append(issuesLink);
                });

                stringBuilder.append("\n\t\t");

                for (String s : position) {
                    stringBuilder.append(s);
                }
                stringBuilder.append("\n");
            });
            SUSPECTED_MODS.clear();
        }
    }

    /**
     * Checks the stacktrace of the given throwable and all its children for any occurrence of the package names cached in the {@link #PACKAGE_MOD_CACHE},
     * including mixin classes.
     * **/
    private static void scanThrowable(Throwable throwable) {
        scanStacktrace(throwable.getStackTrace());

        if (throwable.getCause() != null && throwable.getCause() != throwable) {
            scanThrowable(throwable.getCause());
        }
    }

    /**
     * Checks the given stacktrace for any occurrence of the  package names cached in the {@link #PACKAGE_MOD_CACHE},
     * including mixin classes.
     * **/
    private static void scanStacktrace(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            identifyByClass(stackTraceElement);
            identifyByMixins(stackTraceElement);
        }
    }


    /**
     * Iterates over all loaded mods, resolving and caching their package names with the corresponding {@link IModInfo}.
     * **/
    private static void cacheModList() {
        ModList modList = ModList.get();
        ModuleLayer gameLayer = FMLLoader.getGameLayer();

        if (modList != null) {
            modList.getMods().forEach(iModInfo -> {
                //Don't cache minecraft or forge as they will always be included in the stacktrace
                if (!iModInfo.getModId().equals("forge") && !iModInfo.getModId().equals("minecraft")) {
                    Set<String> packages = new HashSet<>();

                    gameLayer.findModule(iModInfo.getModId()).ifPresent(module -> packages.addAll(module.getPackages()));

                    packages.forEach(s ->  PACKAGE_MOD_CACHE.put(s, iModInfo));
                }
            });
        }
    }

    private static void identifyByClass(StackTraceElement stackTraceElement) {
        blameIfPresent(stackTraceElement);
    }

    private static void identifyByMixins(StackTraceElement stackTraceElement) {
        IMixinInfo mixinInfo = getMixinInfo(stackTraceElement);

        if (mixinInfo != null) {
            String elementAsString = stackTraceElement.toString();
            String mixinClassName = mixinInfo.getClassName();
            List<String> targetClasses = mixinInfo.getTargetClasses();
            blameIfPresent(mixinClassName, "Mixin class: ", mixinClassName, "\n\t\tTarget", (targetClasses.size() == 1 ? ": " + targetClasses.get(0) : "s: " + targetClasses).replaceAll("/", "."), "\n\t\tat ", elementAsString);
        }
    }

    private static void blameIfPresent(StackTraceElement stackTraceElement) {
        blameIfPresent(stackTraceElement.getClassName(), "at ", stackTraceElement.toString());
    }

    private static void blameIfPresent(String className, String... position) {
        String commonPackage = findMatchingPackage(className);

        if (commonPackage != null) {
            SUSPECTED_MODS.putIfAbsent(PACKAGE_MOD_CACHE.get(commonPackage), position);
        }
    }

    /**
     * Checks if the beginning of the given class name is contained in the {@link #PACKAGE_MOD_CACHE}
     * @return the  package name of the class or null if it is not present in the cache
     * */
    @Nullable
    private static String findMatchingPackage(String className) {
        for (String s : PACKAGE_MOD_CACHE.keySet()) {
            if (className.startsWith(s)) {
                return s;
            }
        }

        return null;
    }

    @Nullable
    private static MixinMerged findMixinMerged(StackTraceElement element) {
        try {
            Class<?> clazz = Class.forName(element.getClassName());
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(element.getMethodName())) {
                    MixinMerged mixinMerged = method.getAnnotation(MixinMerged.class);
                    if (mixinMerged != null) {
                        return mixinMerged;
                    }
                }
            }
        }
        catch (ClassNotFoundException | NoClassDefFoundError ignored) {}

        return null;
    }


    /**
     * Tries to identify any applied mixin for the class and method provided by the stacktrace element.
     * @param element the stacktrace element to check
     * @return the {@link IMixinInfo} for the class and method or null if there is no mixin applied at this position
     * **/
    @Nullable
    private static IMixinInfo getMixinInfo(StackTraceElement element) {
        MixinMerged mixinMerged = findMixinMerged(element);
        if (mixinMerged != null) {
            ClassInfo classInfo = ClassInfo.forName(mixinMerged.mixin().replace('.', '/'));
            if (classInfo != null) {
                try {
                    Field mixinField = ClassInfo.class.getDeclaredField("mixin");
                    mixinField.setAccessible(true);
                    return (IMixinInfo) mixinField.get(classInfo);
                }
                catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
