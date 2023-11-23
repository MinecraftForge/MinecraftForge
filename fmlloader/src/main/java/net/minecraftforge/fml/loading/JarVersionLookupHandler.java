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
    public record Version(Optional<String> title, Optional<String> vendor, Optional<String> version) {
        @Override
        public String toString() {
            return "[" +
                "title=" + title.orElse(null) +
                ", vendor=" + vendor.orElse(null) +
                ", version=" + version.orElse(null) +
            "]";
        }
    }
    public record Info(Version spec, Version impl) {}

    @SuppressWarnings("deprecation")
    public static Info getInfo(String pkgName) {
        return getInfo(Package.getPackage(pkgName));
    }

    public static Info getInfo(Class<?> clazz) {
        return getInfo(clazz.getPackage());
    }

    public static Info getInfo(Package pkg) {
        return new Info(
            new Version(
                Optional.ofNullable(pkg.getSpecificationTitle()),
                Optional.ofNullable(pkg.getSpecificationVendor()),
                Optional.ofNullable(pkg.getSpecificationVersion())
            ),
            new Version(
                Optional.ofNullable(pkg.getImplementationTitle()),
                Optional.ofNullable(pkg.getImplementationVendor()),
                Optional.ofNullable(pkg.getImplementationVersion())
            )
        );
    }


    @Deprecated(forRemoval = true, since = "1.20.2")
    public static Optional<String> getImplementationVersion(final String pkgName) {
        // Note that with Java 9, you'll probably want the module's version data, hence pulling this out
        final String pkgVersion = Package.getPackage(pkgName).getImplementationVersion();
        return Optional.ofNullable(pkgVersion);
    }

    @Deprecated(forRemoval = true, since = "1.20.2")
    public static Optional<String> getSpecificationVersion(final String pkgName) {
        // Note that with Java 9, you'll probably want the module's version data, hence pulling this out
        final String pkgVersion = Package.getPackage(pkgName).getSpecificationVersion();
        return Optional.ofNullable(pkgVersion);
    }

    @Deprecated(forRemoval = true, since = "1.20.2")
    public static Optional<String> getImplementationVersion(final Class<?> clazz) {
        // With java 9 we'll use the module's version if it exists in preference.
        final String pkgVersion = clazz.getPackage().getImplementationVersion();
        return Optional.ofNullable(pkgVersion);
    }

    @Deprecated(forRemoval = true, since = "1.20.2")
    public static Optional<String> getImplementationTitle(final Class<?> clazz) {
        // With java 9 we'll use the module's version if it exists in preference.
        final String pkgVersion = clazz.getPackage().getImplementationTitle();
        return Optional.ofNullable(pkgVersion);
    }

    @Deprecated(forRemoval = true, since = "1.20.2")
    public static Optional<String> getSpecificationVersion(final Class<?> clazz) {
        // With java 9 we'll use the module's version if it exists in preference.
        final String pkgVersion = clazz.getPackage().getSpecificationVersion();
        return Optional.ofNullable(pkgVersion);
    }
}
