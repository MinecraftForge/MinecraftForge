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

package net.minecraftforge.common.util;

import io.netty.buffer.ByteBuf;

/**
 * Utility class for creating a nice human readable dump of binary data.
 *
 * It might look something like this:<BR>
 *<PRE>
 *      00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F    ................
 *      69 68 67 66 65 64 63 62 61 61 6A 6B 6C 6D 6E 00    ihgfedcbaajklmn.
 *      41 00                                              A.
 *      Length: 34
 *</PRE>
 */
public class HexDumper
{

    public static String dump(ByteBuf data)
    {
        int current = data.readerIndex();
        data.readerIndex(0);
        Instance inst = new Instance(current, data.readableBytes());
        data.forEachByte(b -> {
            inst.add(b);
            return true;
        });
        data.readerIndex(current);
        return inst.finish();
    }

    public static String dump(byte[] data)
    {
        return dump(data, -1);
    }

    public static String dump(byte[] data, int marker)
    {
        Instance inst = new Instance(marker, data.length);
        for (int x = 0; x < data.length; x++)
            inst.add(data[x]);
        return inst.finish();
    }

    private static class Instance
    {
        private static final String HEX = "0123456789ABCDEF";
        private final int marked;
        private final StringBuilder buf;
        private char[] ascii = new char[16];
        private int index = 0;

        private Instance(int marked, int size)
        {
            this.marked = marked;
            int lines = ((size + 15) / 16);
            this.buf = new StringBuilder((size * 3)          //Hex
                                        + size               // ASCII
                                        + (lines * 2)        // \t and \n per line
                                        + (marked == -1 ? 0 : lines)); // ' ' or < at the start of each line

            for (int x = 0; x < ascii.length; x++)
                ascii[x] = ' ';
        }

        public void add(byte data)
        {
            if (index == 0 && marked != -1)
                buf.append(index == marked ? '<' : ' ');

            if (index != 0 && index % 16 == 0)
            {
                buf.append('\t');
                for (int x = 0; x < 16; x++)
                {
                    buf.append(ascii[x]);
                    ascii[x] = ' ';
                }
                buf.append('\n');
                if (marked != -1)
                  buf.append(index == marked ? '<' : ' ');
            }
            ascii[index % 16] = data < ' ' || data > '~' ? '.' : (char)data;
            buf.append(HEX.charAt((data & 0xF0) >> 4));
            buf.append(HEX.charAt((data & 0x0F)     ));
            if (index + 1 == marked)
                buf.append(marked % 16 == 0 ? ' ' : '<');
            else
                buf.append(marked == index ? '>' : ' ');

            index++;
        }

        public String finish()
        {
            int padding = 16 - (index % 16);
            if (padding > 0)
            {
                for (int x = 0; x < padding * 3; x++)
                    buf.append(' ');
                buf.append('\t');
                buf.append(ascii);
            }
            buf.append('\n');
            buf.append("Length: ").append(index);
            if (marked != -1)
                buf.append(" Mark: ").append(marked);
            return buf.toString();
        }
    }
}
