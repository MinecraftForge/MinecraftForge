/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

public class CoreModFile implements net.minecraftforge.forgespi.ICoreModFile {
    private final java.nio.file.Path internalPath;
    private final ModFile file;
    private final String name;

    CoreModFile(final String name, final java.nio.file.Path path, final ModFile file) {
        this.name = name;
        this.internalPath = path;
        this.file = file;
    }

    @Override
    public java.io.Reader readCoreMod() throws java.io.IOException {
        return java.nio.file.Files.newBufferedReader(this.internalPath);
    }

    @Override
    public java.nio.file.Path getPath() {
        return this.internalPath;
    }
}
