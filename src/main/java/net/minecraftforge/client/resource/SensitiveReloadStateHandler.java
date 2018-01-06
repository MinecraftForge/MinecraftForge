/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.client.resource;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * Handles reload parameters for sensitive loaders.
 */
public enum SensitiveReloadStateHandler
{
    INSTANCE;

    private final Deque<Predicate<IResourceType>> currentRequirements = new ArrayDeque<>();

    /***
     * Pushes an inclusion predicate for the current reload based on the given types.
     * Should only be called when initiating a resource reload.
     *
     * @param resourceTypes the resource types to be included in the reload
     */
    public void pushReloadRequirements(IResourceType... resourceTypes)
    {
        this.currentRequirements.push(ReloadRequirements.include(resourceTypes));
    }

    /***
     * Pushes a resource type predicate for the current reload.
     * Should only be called when initiating a resource reload.
     *
     * @param requirements the resource requirement predicate
     */
    public void pushReloadRequirements(Predicate<IResourceType> requirements)
    {
        this.currentRequirements.push(requirements);
    }

    /**
     * Gets the current {@link ReloadRequirements} for the initiated reload.
     *
     * @return the relevant requirements, or an empty one if none in progress
     */
    @Nonnull
    public Predicate<IResourceType> get()
    {
        if (this.currentRequirements.isEmpty())
        {
            return net.minecraftforge.client.resource.ReloadRequirements.all();
        }

        return this.currentRequirements.peek();
    }

    /**
     * Pops the last added reload predicate from the stack. Should only be called after a resource reload has
     * been completed.
     *
     * @throws java.util.EmptyStackException if a resource reload is not in progress
     */
    public void popReloadRequirements()
    {
        this.currentRequirements.pop();
    }
}
