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

package net.minecraftforge.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class NightConfigWrapper implements IConfigurable {
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
        return this.config.getOptional(asList(key)).map(value -> {
            if (value instanceof UnmodifiableConfig) {
                return (T) ((UnmodifiableConfig) value).valueMap();
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
        final ArrayList<UnmodifiableConfig> nestedConfigs = this.config.getOrElse(path, ArrayList::new);
        return nestedConfigs.stream()
                .map(NightConfigWrapper::new)
                .map(cw->cw.setFile(file))
                .collect(Collectors.toList());
    }
}
