/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import cpw.mods.modlauncher.api.LamdbaExceptionUtils.Supplier_WithExceptions;
import net.minecraftforge.fml.loading.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.zip.ZipFile;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.*;

public enum InvalidModIdentifier {

    OLDFORGE(filePresent("mcmod.info")),
    FABRIC(filePresent("fabric.mod.json")),
    LITELOADER(filePresent("litemod.json")),
    OPTIFINE(filePresent("optifine/Installer.class")),
    INVALIDZIP((f,zf) -> !zf.isPresent());

    private BiPredicate<Path, Optional<ZipFile>> ident;

    InvalidModIdentifier(BiPredicate<Path, Optional<ZipFile>> identifier)
    {
        this.ident = identifier;
    }

    private String getReason()
    {
        return "fml.modloading.brokenfile." + StringUtils.toLowerCase(name());
    }

    public static Optional<String> identifyJarProblem(Path path)
    {
        Optional<ZipFile> zfo = optionalFromException(() -> new ZipFile(path.toFile()));
        Optional<String> result = Arrays.stream(values()).
                                         filter(i -> i.ident.test(path, zfo)).
                                         map(InvalidModIdentifier::getReason).
                                         findAny();
        zfo.ifPresent(rethrowConsumer(ZipFile::close));
        return result;
    }

    private static BiPredicate<Path, Optional<ZipFile>> filePresent(String filename)
    {
        return (f, zfo) -> zfo.map(zf -> zf.getEntry(filename) != null).orElse(false);
    }

    private static <T> Optional<T> optionalFromException(Supplier_WithExceptions<T, ? extends Exception> supp)
    {
        try
        {
            return Optional.of(supp.get());
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

}
