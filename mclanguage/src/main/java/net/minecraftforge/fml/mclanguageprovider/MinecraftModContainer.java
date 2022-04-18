/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.mclanguageprovider;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Objects;

public class MinecraftModContainer extends ModContainer {
    private static final String MCMODINSTANCE = "minecraft, the mod";

    public MinecraftModContainer(final IModInfo info) {
        super(info);
        contextExtension = () -> null;
    }

    @Override
    public boolean matches(final Object mod) {
        return Objects.equals(mod, MCMODINSTANCE);
    }

    @Override
    public Object getMod() {
        return MCMODINSTANCE;
    }
}
