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

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.IModLanguageProvider;
import net.minecraftforge.fml.loading.LanguageLoadingProvider;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.LOADING;
import static net.minecraftforge.fml.Logging.SCAN;
import static net.minecraftforge.fml.Logging.fmlLog;

public class Scanner {
    private final ModFile fileToScan;

    public Scanner(final ModFile fileToScan) {
        this.fileToScan = fileToScan;
    }

    public ScanResult scan() {
        ScanResult result = new ScanResult(fileToScan);
        fileToScan.scanFile(p -> fileVisitor(p, result));
        final Set<IModLanguageProvider> modLoaders = fileToScan.getModInfos().stream().map(ModInfo::getModLoader).map(FMLLoader.getLanguageLoadingProvider()::getLanguage).collect(Collectors.toSet());
        modLoaders.stream().peek(ml-> fmlLog.debug(SCAN, "Scanning {} with language loader {}", fileToScan.getFilePath(), ml.name()))
                .map(IModLanguageProvider::getFileVisitor).forEach(c->c.accept(result));
        return result;
    }

    private void fileVisitor(final Path path, final ScanResult result) {
        try {
            fmlLog.debug(SCAN,"Scanning {} path {}", fileToScan, path);
            ModClassVisitor mcv = new ModClassVisitor();
            ClassReader cr = new ClassReader(Files.newInputStream(path));
            cr.accept(mcv, 0);
            mcv.buildData(result.getClasses(), result.getAnnotations());
        } catch (IOException e) {
            // mark path bad
        }
    }
}
