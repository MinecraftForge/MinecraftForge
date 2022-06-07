/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.List;

/**
 * Provides a list of mod loading states which the mod loader may transition between.
 *
 * <p>There may be multiple mod state providers in a single application, where all states from each provider is
 * combined into a single list and ordered.</p>
 */
public interface IModStateProvider {
    /**
     * {@return the list of mod loading states known to this provider}
     */
    List<IModLoadingState> getAllStates();
}
