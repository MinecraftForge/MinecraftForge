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

package cpw.mods.fml.common.asm.transformers.deobf;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.io.InputSupplier;

public class ZipInputSupplier implements InputSupplier<InputStream> {
    private ZipFile zipFile;
    private ZipEntry zipEntry;

    public ZipInputSupplier(ZipFile zip, ZipEntry entry)
    {
        this.zipFile = zip;
        this.zipEntry = entry;
    }

    @Override
    public InputStream getInput() throws IOException
    {
        return zipFile.getInputStream(zipEntry);
    }

}
