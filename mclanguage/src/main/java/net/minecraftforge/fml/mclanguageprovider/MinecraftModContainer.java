/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.mclanguageprovider;

import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.singularityspi.language.IModInfo;

public class MinecraftModContainer extends ModContainer {
    private static final String MCMODINSTANCE = "minecraft, the mod";

    public MinecraftModContainer(final IModInfo info) {
        super(info);
        contextExtension = () -> null;
    }

    @Override
    public boolean matches(final Object mod) {
        return MCMODINSTANCE.equals(mod);
    }

    @Override
    public Object getMod() {
        return MCMODINSTANCE;
    }
}
