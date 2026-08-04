/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
