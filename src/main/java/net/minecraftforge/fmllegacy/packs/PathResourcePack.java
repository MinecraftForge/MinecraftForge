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

package net.minecraftforge.fmllegacy.packs;

import java.nio.file.Path;

public class PathResourcePack extends BasePathResourcePack
{
    private final Path source;

    public PathResourcePack(final Path source)
    {
        super();
        this.source = source;
    }

    public Path getSource() {
        return this.source;
    }

    @Override
    public String getName()
    {
        return source.getFileName().toString();
    }

    @Override
    protected Path resolve(String... paths)
    {
        Path path = getSource();
        for(String name : paths)
            path = path.resolve(name);
        return path;
    }
}
