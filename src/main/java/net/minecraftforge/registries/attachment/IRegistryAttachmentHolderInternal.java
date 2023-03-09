/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import net.minecraft.core.Holder;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public interface IRegistryAttachmentHolderInternal<R, A> extends IRegistryAttachmentHolder<R, A>
{
    void replaceWith(Map<Holder<R>, A> map);
    Map<Holder<R>, A> view();

    void freeze();
    void unfreeze();
}
