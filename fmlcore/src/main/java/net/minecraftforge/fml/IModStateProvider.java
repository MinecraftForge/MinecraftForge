/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.List;

public interface IModStateProvider {
    List<IModLoadingState> getAllStates();
}
