/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.examiner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraftforge.bootstrap.api.BootstrapClasspathModifier;

public class BootstrapFilter implements BootstrapClasspathModifier {
    @Override
    public String name() {
        return "examiner";
    }

    @Override
    public boolean process(List<Path[]> classpath) {
        var modified = false;

        for (var itr = classpath.iterator(); itr.hasNext(); ) {
            for (var path : itr.next()) {
                final var target = path.resolve(Locator.MARKER);
                if (Files.exists(target)) {
                    itr.remove();
                    modified = true;
                    break;
                }
            }
        }

        return modified;
    }

}
