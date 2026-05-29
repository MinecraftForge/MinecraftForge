/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.singularity.transformers;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record singularityModTransformers(String name) implements ITransformationService {
    public singularityModTransformers() {
        this("singularity");
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
