package net.minecraftforge.logging;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public abstract class CrashReportAnalyser {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, IModInfo> BASE_PACKAGE_MOD_CACHE = new HashMap<>();
    private static final Map<IModInfo, String> SUSPECTED_MODS = new HashMap<>();

    /**
     * Appends the suspected mod(s) for this crash to the crash report,
     * including the mods name, its id, version and an issues link if available,
     * as well as the position where the exception occurred (either the stacktrace element
     * or the mixin class and its target(s))
     * **/
    public static String appendSuspectedMods() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Suspected Mod");
        stringBuilder.append(SUSPECTED_MODS.size() == 1 ? ": " : "s: ");
        if (SUSPECTED_MODS.isEmpty()) {
            stringBuilder.append("NONE");
        }
        else {
            SUSPECTED_MODS.forEach((iModInfo, position) -> {
                stringBuilder
                        .append("\n\t")
                        .append(iModInfo.getDisplayName())
                        .append(" (").append(iModInfo.getModId()).append("),")
                        .append(" Version: ").append(iModInfo.getVersion())
                        .append("\n\t\t")
                        .append(position);

                iModInfo.getOwningFile().getConfig().<String>getConfigElement("issueTrackerURL").ifPresent(issuesLink -> {
                    stringBuilder
                            .append("\n\t\t")
                            .append("Please report this crash to the mod author(s): ")
                            .append(issuesLink);
                });
                stringBuilder.append("\n");
            });
        }

        return stringBuilder.toString();
    }


    public static Map<String, IModInfo> getBasePackageModCache() {
        return BASE_PACKAGE_MOD_CACHE;
    }

    public static Map<IModInfo, String> getSuspectedMods() {
        return SUSPECTED_MODS;
    }

    /**
     * Tries to analyse the given exception and uncategorized stacktrace of the crash report.
     * It checks the stacktrace for any occurrence of the base package names cached in the basePackageModCache,
     * including mixin classes.
     * **/
    public static void analyseCrashReport(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        try {
            scanThrowable(throwable);
            scanStacktrace(uncategorizedStackTrace);
        }
        catch(Throwable t) {
            LOGGER.error("Failed to analyse crash report!", t);
        }
    }

    /**
     * Checks the stacktrace of the given throwable and all its children for any occurrence of the base package names cached in the basePackageModCache,
     * including mixin classes.
     * **/
    private static void scanThrowable(Throwable throwable) {
        scanStacktrace(throwable.getStackTrace());

        if (throwable.getCause() != null && throwable.getCause() != throwable) {
            scanThrowable(throwable.getCause());
        }
    }

    /**
     * Checks the given stacktrace for any occurrence of the base package names cached in the basePackageModCache,
     * including mixin classes.
     * **/
    private static void scanStacktrace(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            identifyByClass(stackTraceElement);
            identifyByMixins(stackTraceElement);
        }
    }


    /**
     * Iterates over all loaded mods, resolving and caching their base package names with the corresponding {@link IModInfo}.
     * <ul>
     *     <li>For forge this would be: "net.minecraftforge"</li>
     *     <li>For minecraft this would be:</li>
     *     <ul>
     *         <li>"net.minecraft"</li>
     *         <li>"com.mojang"</li>
     *         <li>"mcp.client"</li>
     *     </ul>
     * </ul>
     * **/
    public static void cacheModList() {
        ModList.get().getMods().forEach(iModInfo -> {
            //Don't cache minecraft or forge as they will always be included in the stacktrace
            if (!iModInfo.getModId().equals("forge") && !iModInfo.getModId().equals("minecraft")) {
                ModFileScanData scanData = iModInfo.getOwningFile().getFile().getScanResult();
                Collection<String> commonClassPaths = resolveCommonClassPaths(scanData.getClasses());
                commonClassPaths.forEach(s -> BASE_PACKAGE_MOD_CACHE.put(s, iModInfo));
            }
        });
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
            blameIfPresent(
                    "Mixin class: " + mixinClassName +
                            "\n\t\tTarget" + (targetClasses.size() == 1 ? ": " + targetClasses.get(0).replaceAll("/", ".") : "s: " + targetClasses.toString().replaceAll("/", ".")) +
                            "\n\t\tat " + elementAsString,
                    mixinClassName
            );
        }
    }

    private static void blameIfPresent(StackTraceElement stackTraceElement) {
        blameIfPresent("at " + stackTraceElement.toString(), stackTraceElement.getClassName());
    }
    private static void blameIfPresent(String position, String className) {
        String commonPackage = findMatchingBasePackage(className);

        if (commonPackage != null) {
            SUSPECTED_MODS.putIfAbsent(BASE_PACKAGE_MOD_CACHE.get(commonPackage), position);
        }
    }

    /**
     * Checks if the beginning of the given class name is contained in the basePackageModCache
     * @return the base package name of the class or null if it is not present in the cache
     * */
    @Nullable
    private static String findMatchingBasePackage(String className) {
        for (String s : BASE_PACKAGE_MOD_CACHE.keySet()) {
            if (className.startsWith(s)) {
                return s;
            }
        }

        return null;
    }

    /**
     * Tries to resolve the base package for the given Set of {@link ModFileScanData.ClassData}
     * @param classes the classes to resolve the base package for
     * @return a {@link Collection} of base package names
     * **/
    private static Collection<String> resolveCommonClassPaths(Collection<ModFileScanData.ClassData> classes) {
        Set<String> commonClassPaths = new HashSet<>();
        final String[] classNames = classes.stream().map(classData -> classData.clazz().getClassName()).toArray(String[]::new);

        Set<String> basePackages = new HashSet<>();
        for (String className : classNames) {
            if (className.contains(".")) {
                String firstPackage = className.substring(0, className.indexOf("."));
                basePackages.add(firstPackage);
            }
        }

        basePackages.forEach(s -> {
            String[] strings = Arrays.stream(classNames).filter(s1 -> s1.startsWith(s)).toArray(String[]::new);
            String root = findCommonClassPath(strings);

            if (!root.isBlank()) commonClassPaths.add(root);
        });

        return commonClassPaths;
    }

    /**
     * Checks the given string array of fully qualified class names for their common part.
     * @param strings the class names to resolve the common clas path for
     * @return the longest common part at the beginning of the given strings
     * **/
    public static String findCommonClassPath(String[] strings) {
        int n = strings.length;

        String reference = strings[0];
        int refLength = reference.length();

        String commonClassPath = "";

        for (int i = 0; i < refLength; i++) {
            for (int j = i + 1; j <= refLength; j++) {

                String stem = reference.substring(i, j);
                int k;
                for (k = 0; k < n; k++) {
                    if (!strings[k].startsWith(stem)) {
                        break;
                    }
                }

                if (k == n && commonClassPath.length() < stem.length()) {
                    commonClassPath = stem;
                }
            }
        }

        return commonClassPath;
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
