/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgeGameTestHooks
{
    private static boolean registeredGametests = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type GAME_TEST_HOLDER = Type.getType(GameTestHolder.class);

    public static boolean isGametestEnabled()
    {
        return !FMLLoader.isProduction() && (SharedConstants.IS_RUNNING_IN_IDE || isGametestServer() || Boolean.getBoolean("forge.enableGameTest"));
    }

    public static boolean isGametestServer()
    {
        return !FMLLoader.isProduction() && Boolean.getBoolean("forge.gameTestServer");
    }

    @SuppressWarnings("deprecation")
    public static void registerGametests()
    {
        if (!registeredGametests && isGametestEnabled() && ModLoader.isLoadingStateValid())
        {
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

            for (Method gameTestMethod : gameTestMethods)
            {
                GameTestRegistry.register(gameTestMethod, enabledNamespaces);
            }

            registeredGametests = true;
        }
    }

    private static Set<String> getEnabledNamespaces()
    {
        String enabledNamespacesStr = System.getProperty("forge.enabledGameTestNamespaces");
        if (enabledNamespacesStr == null)
        {
            return Set.of();
        }

        return Arrays.stream(enabledNamespacesStr.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toUnmodifiableSet());
    }

    private static void addGameTestMethods(AnnotationData annotationData, Set<Method> gameTestMethods)
    {
        try
        {
            Class<?> clazz = Class.forName(annotationData.clazz().getClassName(), true, ForgeGameTestHooks.class.getClassLoader());

            gameTestMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        catch (ClassNotFoundException e)
        {
            // Should not be possible
            throw new RuntimeException(e);
        }
    }

    public static String getTemplateNamespace(Method method)
    {
        GameTest gameTest = method.getAnnotation(GameTest.class);

        if (gameTest != null && !gameTest.templateNamespace().isEmpty())
        {
            return gameTest.templateNamespace();
        }

        GameTestHolder gameTestHolder = method.getDeclaringClass().getAnnotation(GameTestHolder.class);

        if (gameTestHolder != null)
        {
            return gameTestHolder.value();
        }

        return "minecraft";
    }

    public static boolean prefixGameTestTemplate(Method method)
    {
        PrefixGameTestTemplate annotation = method.getAnnotation(PrefixGameTestTemplate.class);

        if (annotation == null)
        {
            annotation = method.getDeclaringClass().getAnnotation(PrefixGameTestTemplate.class);
        }

        // The vanilla game test system prefixes the classname by default, so we preserve that behavior here.
        return annotation == null || annotation.value();
    }
}
