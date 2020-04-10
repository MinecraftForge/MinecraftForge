/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.repackage.com.nothome.delta;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * For debugging patch generation.
 */
public class DebugDiffWriter implements DiffWriter {

    private ByteArrayOutputStream os = new ByteArrayOutputStream();

    /**
     * Constructs a new DebugDiffWriter.
     */
    public DebugDiffWriter() {}

    @Override
    public void addCopy(long offset, int length) throws IOException {
        if (os.size() > 0)
            writeBuf();
        System.err.println("COPY off: " + offset + ", len: " + length);
    }

    @Override
    public void addData(byte b) throws IOException {
        os.write(b);
        writeBuf();
    }
    private void writeBuf() {
        System.err.print("DATA: ");
        byte[] ba = os.toByteArray();
        for (int ix = 0; ix < ba.length; ix++) {
            if (ba[ix] == '\n')
                System.err.print("\\n");
            else
                System.err.print(String.valueOf((char) ba[ix]));
            //System.err.print("0x" + Integer.toHexString(buf[ix]) + " "); // hex output
        }
        System.err.println("");
        os.reset();
    }

    @Override
    public void flush() throws IOException {
        System.err.println("FLUSH");
    }
    @Override
    public void close() throws IOException {
        System.err.println("CLOSE");
    }

}

