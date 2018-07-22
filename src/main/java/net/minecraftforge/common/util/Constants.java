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

package net.minecraftforge.common.util;

/**
 * A class containing constants for magic numbers used in the minecraft codebase.
 * Everything here should be checked each update, and have a comment relating to where to check it.
 */
public class Constants
{
    /**
     * NBT Tag type IDS, used when storing the nbt to disc, Should align with NBTBase.getId, 
     * table used in NBTBase.func_150283_g
     * 
     * Main use is checking tag type in NBTTagCompound.func_150297_b(String, int)
     *
     */
    public static class NBT
    {
        public static final int TAG_END         = 0;
        public static final int TAG_BYTE        = 1;
        public static final int TAG_SHORT       = 2;
        public static final int TAG_INT         = 3;
        public static final int TAG_LONG        = 4;
        public static final int TAG_FLOAT       = 5;
        public static final int TAG_DOUBLE      = 6;
        public static final int TAG_BYTE_ARRAY  = 7;
        public static final int TAG_STRING      = 8;
        public static final int TAG_LIST        = 9;
        public static final int TAG_COMPOUND    = 10;
        public static final int TAG_INT_ARRAY   = 11;
        public static final int TAG_LONG_ARRAY  = 12;
        public static final int TAG_ANY_NUMERIC = 99;
    }
}
