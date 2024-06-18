/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableMap;

import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus;

import static java.util.Arrays.asList;

@ApiStatus.Internal
class NightConfigWrapper implements IConfigurable {
    private final UnmodifiableConfig config;
    private IModFileInfo file;

    public NightConfigWrapper(final UnmodifiableConfig config) {
        this.config = config;
    }

    NightConfigWrapper setFile(IModFileInfo file) {
        this.file = file;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getConfigElement(final String... key) {
        var path = asList(key);
        return this.config.getOptional(path).map(value -> {
            if (value instanceof UnmodifiableConfig cfg) {
                // New Night config doesn't implement valueMap(), so do a copy.
                var builder = ImmutableMap.builder();
                for (var e: cfg.entrySet())
                    builder.put(e.getKey(), e.getValue());
                return (T)builder.build();
            } else if (value instanceof ArrayList<?> al && al.size() > 0 && al.get(0) instanceof UnmodifiableConfig) {
                throw new InvalidModFileException("The configuration path " + path + " is invalid. I wasn't expecting a multi-object list - remove one of the [[ ]]", file);
            }
            return (T) value;
        });
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key) {
        final List<String> path = asList(key);
        if (this.config.contains(path) && !(this.config.get(path) instanceof Collection)) {
            throw new InvalidModFileException("The configuration path "+path+" is invalid. Expecting a collection!", file);
        }
        final Collection<UnmodifiableConfig> nestedConfigs = this.config.getOrElse(path, ArrayList::new);
        return nestedConfigs.stream()
                .map(NightConfigWrapper::new)
                .map(cw->cw.setFile(file))
                .collect(Collectors.toList());
    }
}
