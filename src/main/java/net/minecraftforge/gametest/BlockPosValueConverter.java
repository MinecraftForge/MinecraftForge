/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import joptsimple.ValueConverter;
import net.minecraft.core.BlockPos;

public class BlockPosValueConverter implements ValueConverter<BlockPos>
{
    @Override
    public BlockPos convert(String value)
    {
        String[] split = value.split(",");
        return BlockPos.containing(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    @Override
    public Class<BlockPos> valueType()
    {
        return BlockPos.class;
    }

    @Override
    public String valuePattern()
    {
        return null;
    }
}
