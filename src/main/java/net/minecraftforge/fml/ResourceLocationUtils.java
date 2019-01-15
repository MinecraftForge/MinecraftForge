/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml;

import com.google.common.base.Joiner;
import net.minecraft.util.ResourceLocation;

import java.nio.file.Path;

public class ResourceLocationUtils {
    public static ResourceLocation pathToResourceLocation(Path path, int maxDepth) {
        return new ResourceLocation(path.getName(0).toString(), Joiner.on("/").join(path.subpath(1, Math.min(maxDepth, path.getNameCount())).iterator()));
    }
}
