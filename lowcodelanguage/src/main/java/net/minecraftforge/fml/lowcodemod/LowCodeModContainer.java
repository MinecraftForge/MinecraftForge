/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.lowcodemod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.slf4j.Logger;

import java.util.Objects;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class LowCodeModContainer extends ModContainer
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ModFileScanData scanResults;
    private Object modInstance;

    public LowCodeModContainer(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer gameLayer)
    {
        super(info);
        LOGGER.debug(LOADING, "Creating LowCodeModContainer for {}", info.getModId());
        this.scanResults = modFileScanResults;
        this.modInstance = new Object();
        this.contextExtension = () -> null;
        this.extensionPoints.remove(IExtensionPoint.DisplayTest.class);
    }

    @Override
    public boolean matches(Object mod)
    {
        return mod == modInstance;
    }

    @Override
    public Object getMod()
    {
        return modInstance;
    }

    @Override
    protected <T extends Event & IModBusEvent> void acceptEvent(final T e)
    {
    }
}
