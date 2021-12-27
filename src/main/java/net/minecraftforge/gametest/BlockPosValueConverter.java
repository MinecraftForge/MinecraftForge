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

package net.minecraftforge.gametest;

import joptsimple.ValueConverter;
import net.minecraft.core.BlockPos;

public class BlockPosValueConverter implements ValueConverter<BlockPos> {
    @Override
    public BlockPos convert(String value) {
        String[] split = value.split(",");
        return new BlockPos(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    @Override
    public Class<BlockPos> valueType() {
        return BlockPos.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
