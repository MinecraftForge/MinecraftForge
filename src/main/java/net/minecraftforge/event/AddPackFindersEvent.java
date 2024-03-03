/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
