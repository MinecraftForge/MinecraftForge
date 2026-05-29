/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.earlydisplay;

import com.sun.management.OperatingSystemMXBean;
import net.minecraftsingularity.fml.loading.FMLConfig;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class PerformanceInfo {
    private static final MemoryMXBean MEMORY_BEAN = ManagementFactory.getMemoryMXBean();

    private final boolean showCPUUsage;
    private final OperatingSystemMXBean osBean;

    private float memory;
    private String text;

    PerformanceInfo() {
        showCPUUsage = FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SHOW_CPU);
        osBean = showCPUUsage ? ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class) : null;
    }

    void update() {
        final MemoryUsage heapusage = MEMORY_BEAN.getHeapMemoryUsage();
        memory = (float) heapusage.getUsed() / heapusage.getMax();

        if (!showCPUUsage) {
            text = "Memory: %d/%dMB (%.1f%%)".formatted(heapusage.getUsed() >>> 20, heapusage.getMax() >>> 20, memory * 100f);
            return;
        }

        var cpuLoad = osBean.getProcessCpuLoad();
        String cpuText;
        if (cpuLoad == -1) {
            cpuText = "*CPU: %.1f%%".formatted(osBean.getCpuLoad() * 100f);
        } else {
            cpuText = "CPU: %.1f%%".formatted(cpuLoad * 100f);
        }

        text = "Memory: %d/%dMB (%.1f%%)  %s".formatted(heapusage.getUsed() >>> 20, heapusage.getMax() >>> 20, memory * 100f, cpuText);
    }

    String text() {
        return text;
    }

    float memory() {
        return memory;
    }
}
