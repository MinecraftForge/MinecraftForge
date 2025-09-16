/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.transformers;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ForgeModTransformers(String name) implements ITransformationService {
    public ForgeModTransformers() {
        this("forge");
    }

    @Override
    public void initialize(IEnvironment environment) { }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) { }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull List<ITransformer> transformers() {
        List<ITransformer> transformers = FieldToMethodTransformer.getAll();
        transformers.add(new MethodRedirector());
        return transformers;
    }
}
