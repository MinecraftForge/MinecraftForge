/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.relauncher.libraries;

import java.io.IOException;

public class MemoryModList extends ModList
{
    MemoryModList(Repository repo)
    {
        super(repo);
    }

    @Override
    public void save() throws IOException {}

    @Override
    public String getName()
    {
        return "MEMORY";
    }
}
