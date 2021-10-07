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

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Consumer;

/**
 * Fired on PackRepository creation to allow mods to add new pack finders.
 */
public class AddPackFindersEvent extends Event implements IModBusEvent
{
    private final PackType packType;
    private final Consumer<RepositorySource> sources;

    public AddPackFindersEvent(PackType packType, Consumer<RepositorySource> sources)
    {
        this.packType = packType;
        this.sources = sources;
    }

    /**
     * Adds a new source to the list of pack finders.
     * @param source the pack finder
     */
    public void addRepositorySource(RepositorySource source)
    {
        sources.accept(source);
    }

    /**
     * @return the {@link PackType} of the resource manager being constructed.
     */
    public PackType getPackType()
    {
        return packType;
    }
}
