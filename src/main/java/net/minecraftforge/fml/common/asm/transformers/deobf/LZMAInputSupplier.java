/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
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
