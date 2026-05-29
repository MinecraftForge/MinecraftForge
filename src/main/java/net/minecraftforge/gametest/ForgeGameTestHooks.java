/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.gametest;

import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTestEnvironments;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Internal class used to glue mods into the game test framework.
 * Modders should use the supplied annotations and DeferredRegister
 */
@ApiStatus.Internal
public class singularityGameTestHooks {
    private static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static boolean isGametestEnabled() {
        return !FMLLoader.isProduction() && (SharedConstants.IS_RUNNING_IN_IDE || isGametestServer() || Boolean.getBoolean("singularity.enableGameTest"));
    }

    public static boolean isGametestServer() {
        return !FMLLoader.isProduction() && Boolean.getBoolean("singularity.gameTestServer");
    }


    @Nullable
    private static boolean shouldPrefix(Method method) {
        var cls = method.getDeclaringClass();
        return !method.isAnnotationPresent(GameTestDontPrefix.class) &&
               !cls.isAnnotationPresent(GameTestDontPrefix.class);
    }

    private static String snake(String input) {
        var result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!result.isEmpty())
                    result.append('_');
                result.append(Character.toLowerCase(c));
            } else
                result.append(c);
        }
        return result.toString();
    }

    private static String getPrefix(Class<?> cls) {
        var prefix = cls.getAnnotation(GameTestPrefix.class);
        if (prefix != null)
            return prefix.value() + '/';

        var mod = cls.getAnnotation(Mod.class);
        if (mod != null) {
            var holder = cls.getAnnotation(GameTestNamespace.class);
            if (holder != null && !holder.value().isEmpty())
                return mod.value() + '/';
        }


        return cls.getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    private static String getNamespace(Class<?> cls) {
        var holder = cls.getAnnotation(GameTestNamespace.class);
        if (holder != null && !holder.value().isEmpty())
            return holder.value();

        var mod = cls.getAnnotation(Mod.class);
        if (mod != null)
            return mod.value();

        throw new IllegalArgumentException("Could not find modid for " + cls.getName());
    }

    private static Identifier key(String namespace, String prefix, String path) {
        if (path.indexOf(':') != -1) {
            var rl = Identifier.parse(path);
            if (!prefix.isEmpty())
                return rl.withPrefix(prefix);
            return rl;
        }

        return Identifier.fromNamespaceAndPath(namespace, prefix + path);
    }

    public record TestReference(Consumer<GameTestHelper> consumer, TestData<Identifier> data) {}

    public static Map<Identifier, TestReference> gatherTests(Class<?> root, Object instance) {
        var ret = new HashMap<Identifier, TestReference>();
        var seen = new HashSet<String>();
        String class_prefix = getPrefix(root);
        String namespace = getNamespace(root);
        Class<?> cls = root;

        while (cls != Object.class) {
            for (var method : cls.getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers()) || (cls != root && Modifier.isStatic(method.getModifiers())))
                    continue;

                var gametest = method.getAnnotation(GameTest.class);
                if (gametest == null)
                    continue;

                var owner = method.getDeclaringClass().getName() + "." + method.getName();

                if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != GameTestHelper.class)
                    throw new IllegalStateException("Invalid @GameTest function " + owner + " incorrect arguments");

                // We don't want parent methods
                if (!seen.add(method.getName()))
                    continue;

                var prefix = shouldPrefix(method) ? class_prefix : "";
                var name = key(namespace, prefix, gametest.name().isEmpty() ? snake(method.getName()) : gametest.name());

                var structure = GameTest.DEFAULT_STRUCTURE.equals(gametest.structure())
                    ? Identifier.parse(gametest.structure())
                    : key(namespace, "", gametest.structure());

                var env = GameTestEnvironments.DEFAULT.equals(gametest.environment())
                    ? Identifier.withDefaultNamespace(gametest.environment())
                    : key(namespace, "", gametest.environment());

                var data = new TestData<Identifier>(
                    env,
                    structure,
                    gametest.maxTicks(),
                    gametest.setupTicks(),
                    gametest.required(),
                    gametest.rotation(),
                    gametest.manualOnly(),
                    gametest.maxAttempts(),
                    gametest.requiredSuccesses(),
                    gametest.skyAccess(),
                    gametest.padding()
                );

                MethodHandle handle = unreflect(method, owner);

                Consumer<GameTestHelper> func;
                if (Modifier.isStatic(method.getModifiers())) {
                    func = ctx -> {
                        try {
                            handle.invoke(ctx);
                        } catch (Throwable e) {
                            sneakyThrow(e);
                        }
                    };
                } else {
                    func = ctx -> {
                        try {
                            handle.invoke(instance, ctx);
                        } catch (Throwable e) {
                            sneakyThrow(e);
                        }
                    };
                }

                ret.put(name, new TestReference(func, data));
            }

            cls = cls.getSuperclass();
        }
        return ret;
    }

    private static MethodHandle unreflect(Method method, String owner) {
        try {
            return LOOKUP.unreflect(method);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to unreflect " + owner, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E)e;
    }
}
