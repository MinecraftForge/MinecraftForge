/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.gametest.ForgeGameTestHooks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
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
    private final Set<String> enabledNamespaces;

    public RegisterGameTestsEvent(Set<String> enabledNamespaces)
    {
        this.enabledNamespaces = new HashSet<>(enabledNamespaces);
    }

    /**
     * Returns the mutable set of enabled namespaces.
     * If a game test's template does not have a namespace inside this set, it will not be registered.
     * An empty set means all namespaces are enabled.
     * By default, this can only be non-empty when running through {@link GameTestServer}.
     *
     * @return the mutable set of enabled namespaces
     */
    public Set<String> getEnabledNamespaces()
    {
        return enabledNamespaces;
    }

    /**
     * Registers an entire class to the game test registry.
     * All methods annotated with {@link GameTest} or {@link GameTestGenerator} will be registered.
     * If {@link #getEnabledNamespaces()} is non-empty,
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
     * If {@link #getEnabledNamespaces()} is non-empty,
     * the method will only be registered if its {@link GameTest#templateNamespace() template namespace} is an enabled namespace.
     *
     * @param testMethod the test method to register to the game test registry
     */
    @SuppressWarnings("deprecation")
    public void register(Method testMethod)
    {
        GameTestRegistry.register(testMethod, this.getEnabledNamespaces());
    }
}
