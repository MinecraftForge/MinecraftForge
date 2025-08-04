/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.lowcodemod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.slf4j.Logger;

public class LowCodeModContainer extends ModContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Object modInstance;

    public LowCodeModContainer(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer gameLayer) {
        super(info);
        LOGGER.debug(LogMarkers.LOADING, "Creating LowCodeModContainer for {}", info.getModId());
        this.modInstance = new Object();
        this.contextExtension = () -> null;
        this.extensionPoints.remove(IExtensionPoint.DisplayTest.class);
    }

    @Override
    public boolean matches(Object mod) {
        return mod == modInstance;
    }

    @Override
    public Object getMod() {
        return modInstance;
    }
}
