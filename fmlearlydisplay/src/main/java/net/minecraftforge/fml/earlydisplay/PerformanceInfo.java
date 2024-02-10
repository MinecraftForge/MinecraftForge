/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import com.sun.management.OperatingSystemMXBean;
import net.minecraftforge.fml.loading.FMLConfig;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class PerformanceInfo {

    private final boolean showCPUUsage;
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    private float memory;
    private String text;

    PerformanceInfo() {
        showCPUUsage = FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_SHOW_CPU);
        osBean = showCPUUsage ? ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class) : null;
        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    void update() {
        final MemoryUsage heapusage = memoryBean.getHeapMemoryUsage();
        memory = (float) heapusage.getUsed() / heapusage.getMax();

        if (!showCPUUsage) {
            text = String.format("Heap: %d/%d MB (%.1f%%) OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, memory * 100.0, memoryBean.getNonHeapMemoryUsage().getUsed() >> 20);
            return;
        }

        var cpuLoad = osBean.getProcessCpuLoad();
        String cpuText;
        if (cpuLoad == -1) {
            cpuText = String.format("*CPU: %.1f%%", osBean.getCpuLoad() * 100f);
        } else {
            cpuText = String.format("CPU: %.1f%%", cpuLoad * 100f);
        }

        text = String.format("Heap: %d/%d MB (%.1f%%) OffHeap: %d MB  %s", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, memory * 100.0, memoryBean.getNonHeapMemoryUsage().getUsed() >> 20, cpuText);
    }

    String text() {
        return text;
    }

    float memory() {
        return memory;
    }
}
