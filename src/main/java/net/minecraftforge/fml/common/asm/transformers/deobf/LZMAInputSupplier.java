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

package net.minecraftforge.fml.common.asm.transformers.deobf;

import java.io.IOException;
import java.io.InputStream;

import LZMA.LzmaInputStream;

import com.google.common.io.ByteSource;

public class LZMAInputSupplier extends ByteSource {
    private InputStream compressedData;

    public LZMAInputSupplier(InputStream compressedData)
    {
        this.compressedData = compressedData;
    }

    @Override
    public InputStream openStream() throws IOException
    {
        return new LzmaInputStream(this.compressedData);
    }

}
