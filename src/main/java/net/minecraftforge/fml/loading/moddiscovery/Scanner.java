/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.language.IModLanguageProvider;
import net.minecraftforge.fml.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraftforge.fml.Logging.SCAN;

public class Scanner {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ModFile fileToScan;

    public Scanner(final ModFile fileToScan) {
        this.fileToScan = fileToScan;
    }

    public ModFileScanData scan() {
        ModFileScanData result = new ModFileScanData();
        fileToScan.scanFile(p -> fileVisitor(p, result));
        final IModLanguageProvider loader = fileToScan.getLoader();
        LOGGER.debug(SCAN, "Scanning {} with language loader {}", fileToScan.getFilePath(), loader.name());
        loader.getFileVisitor().accept(result);
        return result;
    }

    private void fileVisitor(final Path path, final ModFileScanData result) {
        try {
            LOGGER.debug(SCAN,"Scanning {} path {}", fileToScan, path);
            ModClassVisitor mcv = new ModClassVisitor();
            ClassReader cr = new ClassReader(Files.newInputStream(path));
            cr.accept(mcv, 0);
            mcv.buildData(result.getClasses(), result.getAnnotations());
        } catch (IOException e) {
            // mark path bad
        }
    }
}
