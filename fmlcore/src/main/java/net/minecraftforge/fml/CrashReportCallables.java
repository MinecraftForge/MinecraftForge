/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CrashReportCallables {
    private static final List<ISystemReportExtender> crashCallables = Collections.synchronizedList(new ArrayList<>());

    public static void registerCrashCallable(ISystemReportExtender callable)
    {
        crashCallables.add(callable);
    }

    public static void registerCrashCallable(String headerName, Supplier<String> reportGenerator) {
        registerCrashCallable(new ISystemReportExtender() {
            @Override
            public String getLabel() {
                return headerName;
            }
            @Override
            public String get() {
                return reportGenerator.get();
            }
        });
    }

    public static List<ISystemReportExtender> allCrashCallables() {
        return List.copyOf(crashCallables);
    }
}
