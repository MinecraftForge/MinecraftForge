/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.gametest.ForgeGameTestHooks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * Game tests are registered on client or server startup.
 * It is only run once for a given instance of the game if {@link ForgeGameTestHooks#isGametestEnabled} returns true.
 * This is the preferred way to register your game tests.
 * <p>
 * Fired on the Mod bus, see {@link IModBusEvent}.
 */
public class RegisterGameTestsEvent extends Event implements IModBusEvent
{
    private final Set<Method> gameTestMethods;

    public RegisterGameTestsEvent(Set<Method> gameTestMethods)
    {
        this.gameTestMethods = gameTestMethods;
    }

    /**
     * Registers an entire class to the game test registry.
     * All methods annotated with {@link GameTest} or {@link GameTestGenerator} will be registered.
     * If the set of enabled namespaces is non-empty,
     * a method will only be registered if its {@link GameTest#templateNamespace() template namespace} is in an enabled namespace.
     *
     * @param testClass the test class to register to the game test registry
     */
    public void register(Class<?> testClass)
    {
        Arrays.stream(testClass.getDeclaredMethods()).forEach(this::register);
    }

    /**
     * Registers a single method to the game test registry.
     * The method will only be registered if it is annotated with {@link GameTest} or {@link GameTestGenerator}.
     * If the set of enabled namespaces is non-empty,
     * the method will only be registered if its {@link GameTest#templateNamespace() template namespace} is an enabled namespace.
     *
     * @param testMethod the test method to register to the game test registry
     */
    public void register(Method testMethod)
    {
        this.gameTestMethods.add(testMethod);
    }
}
