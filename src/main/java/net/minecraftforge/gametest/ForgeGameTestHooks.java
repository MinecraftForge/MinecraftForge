/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Internal class used to glue mods into the game test framework.
 * Modders should use the supplied annotations and {@link RegisterGameTestsEvent}
 */
@ApiStatus.Internal
public class ForgeGameTestHooks {
    private static boolean registeredGametests = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type GAME_TEST_HOLDER = Type.getType(GameTestHolder.class);
    private static final String DEFAULT_BATCH = getDefaultBatch();

    public static boolean isGametestEnabled() {
        return !FMLLoader.isProduction() && (SharedConstants.IS_RUNNING_IN_IDE || isGametestServer() || Boolean.getBoolean("forge.enableGameTest"));
    }

    public static boolean isGametestServer() {
        return !FMLLoader.isProduction() && Boolean.getBoolean("forge.gameTestServer");
    }

    @SuppressWarnings("deprecation")
    public static void registerGametests() {
        if (!registeredGametests && isGametestEnabled() && ModLoader.isLoadingStateValid()) {
            Set<String> enabledNamespaces = getEnabledNamespaces();
            LOGGER.info("Enabled Gametest Namespaces: {}", enabledNamespaces);

            Set<Method> gameTestMethods = new HashSet<>();
            RegisterGameTestsEvent event = new RegisterGameTestsEvent(gameTestMethods);

            ModLoader.get().postEvent(event);

            ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> GAME_TEST_HOLDER.equals(a.annotationType()))
                .forEach(a -> addGameTestMethods(a, gameTestMethods));

            for (Method gameTestMethod : gameTestMethods) {
                GameTestRegistry.register(gameTestMethod, enabledNamespaces);
            }

            registeredGametests = true;
        }
    }

    private static Set<String> getEnabledNamespaces() {
        String enabledNamespacesStr = System.getProperty("forge.enabledGameTestNamespaces");
        if (enabledNamespacesStr == null)
            return Set.of();

        return Arrays.stream(enabledNamespacesStr.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toUnmodifiableSet());
    }

    private static void addGameTestMethods(AnnotationData annotationData, Set<Method> gameTestMethods) {
        try {
            Class<?> clazz = Class.forName(annotationData.clazz().getClassName(), true, ForgeGameTestHooks.class.getClassLoader());

            gameTestMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        } catch (ClassNotFoundException e) {
            // Should not be possible
            throw new RuntimeException(e);
        }
    }

    public static String getTestName(Method method, GameTest meta) {
        var name = method.getName().toLowerCase(Locale.ENGLISH);
        return getPrefixed(method, name);
    }

    private static String getPrefixed(Method method, String name) {
        var prefix = getPrefix(method);
        return prefix == null ? name : prefix + '.' + name;
    }

    @Nullable
    private static String getPrefix(Method method) {
        var cls = method.getDeclaringClass();
        var shouldPrefix = !method.isAnnotationPresent(GameTestDontPrefix.class) &&
                           !cls.isAnnotationPresent(GameTestDontPrefix.class);
        if (!shouldPrefix)
            return null;

        var prefix = cls.getAnnotation(GameTestPrefix.class);
        if (prefix != null)
            return prefix.value();

        var holder = cls.getAnnotation(GameTestHolder.class);
        if (holder != null && !holder.value().isEmpty())
            return holder.value();

        var mod = method.getDeclaringClass().getAnnotation(Mod.class);
        if (mod != null)
            return mod.value();

        return cls.getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    public static String getTestTemplate(Method method, GameTest meta, String testName) {
        // If we have a ':' assume we specified the exact template path
        if (meta.template().indexOf(':') != -1)
            return meta.template();

        String template = null;

        if (meta.template().isEmpty())
            template = testName;
        else
            template = getPrefixed(method, meta.template());

        var cls = method.getDeclaringClass();

        var holder = cls.getAnnotation(GameTestHolder.class);
        if (holder != null && !holder.namespace().isEmpty())
            return holder.namespace() + ':' + template;

        var mod = cls.getAnnotation(Mod.class);
        if (mod != null)
            return mod.value() + ':' + template;

        return template;
    }

    private static String getDefaultBatch() {
        try {
            return (String)ObfuscationReflectionHelper.findMethod(GameTest.class, "m_177043" + '_').getDefaultValue();
        } catch (Exception e) {
            e.printStackTrace(); // Should never happen, but just in case.
            return "defaultBatch";
        }
    }

    public static String getTestBatch(Method method, GameTest gametest) {
        var batch = gametest.batch();

        if (DEFAULT_BATCH.equals(batch)) {
            var prefix = getPrefix(method);
            return prefix == null ? batch : prefix;
        }

        return getPrefixed(method, batch.toLowerCase(Locale.ENGLISH));
    }

    public static void addTest(Collection<TestFunction> functions, Set<String> classes, Set<String> filters, TestFunction func) {
        boolean allowed = filters.isEmpty() || filters.stream().anyMatch(f -> f.equals(func.batchName()) || func.batchName().startsWith(f + '.'));
        if (!allowed)
            return;

        functions.add(func);

        var batch = func.batchName();
        classes.add(batch);

        int idx = batch.lastIndexOf('.');
        while (idx != -1) {
            batch = batch.substring(0, idx);
            idx = batch.lastIndexOf('.');
            classes.add(batch);
        }
    }
}
