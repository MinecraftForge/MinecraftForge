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
import org.jetbrains.annotations.Nullable;

import static java.util.Arrays.asList;

@ApiStatus.Internal
final class NightConfigWrapper implements IConfigurable {
    private final UnmodifiableConfig config;
    private IModFileInfo file;

    public NightConfigWrapper(final UnmodifiableConfig config) {
        this.config = config;
    }

    private NightConfigWrapper(UnmodifiableConfig config, IModFileInfo file) {
        this.config = config;
        this.file = file;
    }

    void setFile(IModFileInfo file) {
        this.file = file;
    }

    @Override
    public <T> Optional<T> getConfigElement(final String... key) {
        var path = asList(key);
        return Optional.ofNullable(validate(this.config.get(path), path));
    }

    @SuppressWarnings("unchecked")
    private <T> T validate(@Nullable T value, List<String> path) {
        if (value instanceof UnmodifiableConfig cfg) {
            // New Night config doesn't implement valueMap(), so do a copy.
            var entries = cfg.entrySet();
            var builder = ImmutableMap.builderWithExpectedSize(entries.size());
            for (var e : entries)
                builder.put(e.getKey(), e.getValue());
            return (T) builder.build();
        } else if (value instanceof ArrayList<?> al && !al.isEmpty() && al.getFirst() instanceof UnmodifiableConfig) {
            throw new InvalidModFileException("The configuration path " + path + " is invalid. I wasn't expecting a multi-object list - remove one of the [[ ]]", file);
        }
        return value;
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key) {
        final List<String> path = asList(key);
        if (this.config.contains(path) && !(this.config.get(path) instanceof Collection)) {
            throw new InvalidModFileException("The configuration path " + path + " is invalid. Expecting a collection!", file);
        }
        final Collection<UnmodifiableConfig> nestedConfigs = this.config.getOrElse(path, ArrayList::new);
        return nestedConfigs.stream()
                .map(conf -> new NightConfigWrapper(conf, file))
                .collect(Collectors.toList());
    }
}
