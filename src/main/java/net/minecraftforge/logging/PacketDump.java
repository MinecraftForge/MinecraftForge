/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.logging;

import io.netty.buffer.ByteBuf;

public class PacketDump
{

    public static String getContentDump(ByteBuf buffer)
    {
        int currentLength = buffer.readableBytes();
        StringBuffer returnString = new StringBuffer((currentLength * 3) + // The
                                                                           // hex
                (currentLength) + // The ascii
                (currentLength / 4) + // The tabs/\n's
                30); // The text

        // returnString.append("Buffer contents:\n");
        int i, j; // Loop variables
        for (i = 0; i < currentLength; i++)
        {
            if ((i != 0) && (i % 16 == 0))
            {
                // If it's a multiple of 16 and i isn't null, show the ascii
                returnString.append('\t');
                for (j = i - 16; j < i; j++)
                {
                    if (buffer.getByte(j) < 0x20 || buffer.getByte(j) > 0x7F)
                        returnString.append('.');
                    else
                        returnString.append((char) buffer.getByte(j));
                }
                // Add a linefeed after the string
                returnString.append("\n");
            }

            returnString.append(Integer.toString((buffer.getByte(i) & 0xF0) >> 4, 16) + Integer.toString((buffer.getByte(i) & 0x0F) >> 0, 16));
            returnString.append(' ');
        }

        // Add padding spaces if it's not a multiple of 16
        if (i != 0 && i % 16 != 0)
        {
            for (j = 0; j < ((16 - (i % 16)) * 3); j++)
            {
                returnString.append(' ');
            }
        }
        // Add the tab for alignment
        returnString.append('\t');

        // Add final characters at right, after padding

        // If it was at the end of a line, print out the full line
        if (i > 0 && (i % 16) == 0)
        {
            j = i - 16;
        } else
        {
            j = (i - (i % 16));
        }

        for (; i >= 0 && j < i; j++)
        {
            if (buffer.getByte(j) < 0x20 || buffer.getByte(j) > 0x7F)
                returnString.append('.');
            else
                returnString.append((char) buffer.getByte(j));
        }

        // Finally, tidy it all up with a newline
        returnString.append('\n');
        returnString.append("Length: " + currentLength);

        return returnString.toString();

    }
}
