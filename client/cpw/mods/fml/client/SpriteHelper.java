/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package cpw.mods.fml.client;

import java.util.BitSet;
import java.util.HashMap;

import net.minecraft.src.ModLoader;
import cpw.mods.fml.common.Loader;

/**
 * @author cpw
 *
 */
public class SpriteHelper
{
    private static HashMap<String, BitSet> spriteInfo = new HashMap<String, BitSet>();

    public static int getUniqueSpriteIndex(String path)
    {
        BitSet slots = spriteInfo.get(path);
        if (slots == null)
        {
            if (path.equals("/terrain.png"))
            {
                slots = SpriteHelper.toBitSet(
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000011111100" +
                        "0000000011111111" +
                        "0000000011111000" +
                        "0000000111111100" +
                        "0000000111111000" +
                        "0000000000000000");
                spriteInfo.put("/terrain.png", slots);
            }
            else if (path.equals("/gui/items.png"))
            {
                slots = SpriteHelper.toBitSet(
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000000000000000" +
                        "0000001000000000" +
                        "0000001110000000" +
                        "0000001000000000" +
                        "1111111010000000" +
                        "1111111010100000" +
                        "1111111111111100" +
                        "1111111111111111" +
                        "1111111111111111" +
                        "1111111111111111" +
                        "0000000000000000");
                spriteInfo.put("/gui/items.png", slots);
            }
            else
            {
                Exception ex = new Exception(String.format("Invalid getUniqueSpriteIndex call for texture: %s", path));
                Loader.log.throwing("ModLoader", "getUniqueSpriteIndex", ex);
                ModLoader.throwException(ex);
                return 0;
            }
        }
        int ret = SpriteHelper.getFreeSlot(slots);
        if (ret == -1)
        {
            Exception ex = new Exception(String.format("No more sprite indicies left for: %s", path));
            Loader.log.throwing("ModLoader", "getUniqueSpriteIndex", ex);
            ModLoader.throwException(ex);
        }
        return ret;
    }

    public static BitSet toBitSet(String data)
    {
        BitSet ret = new BitSet(data.length());
        for (int x = 0; x < data.length(); x++)
        {
            ret.set(x, data.charAt(x) == '1');
        }
        return ret;
    }

    public static int getFreeSlot(BitSet slots)
    {
        return slots.nextSetBit(0);
    }

}
