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

package net.minecraftforge.fml.loading;

import java.util.Optional;

/**
 * Finds Version data from a package, with possible default values
 */
public class JarVersionLookupHandler {
    public static Optional<String> getImplementationVersion(final String pkgName) {
        // Note that with Java 9, you'll probably want the module's version data, hence pulling this out
        final String pkgVersion = Package.getPackage(pkgName).getImplementationVersion();
        return Optional.ofNullable(pkgVersion);
    }

    public static Optional<String> getSpecificationVersion(final String pkgName) {
        // Note that with Java 9, you'll probably want the module's version data, hence pulling this out
        final String pkgVersion = Package.getPackage(pkgName).getSpecificationVersion();
        return Optional.ofNullable(pkgVersion);
    }

    public static Optional<String> getImplementationVersion(final Class<?> clazz) {
        // With java 9 we'll use the module's version if it exists in preference.
        final String pkgVersion = clazz.getPackage().getImplementationVersion();
        return Optional.ofNullable(pkgVersion);
    }

    public static Optional<String> getImplementationTitle(final Class<?> clazz) {
        // With java 9 we'll use the module's version if it exists in preference.
        final String pkgVersion = clazz.getPackage().getImplementationTitle();
        return Optional.ofNullable(pkgVersion);
    }


    public static Optional<String> getSpecificationVersion(final Class<?> clazz) {
        // With java 9 we'll use the module's version if it exists in preference.
        final String pkgVersion = clazz.getPackage().getSpecificationVersion();
        return Optional.ofNullable(pkgVersion);
    }
}
