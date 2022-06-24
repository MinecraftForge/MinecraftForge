/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemDecoratorProvider implements ICapabilityProvider {

    private final LazyOptional<IItemDecoratorHandler> holder = LazyOptional.of(ItemDecoratorHandler::new);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
    {
        return CapabilityItemDecoratorHandler.ITEM_DECORATOR_HANDLER_CAPABILITY.orEmpty(capability, holder);
    }
}
