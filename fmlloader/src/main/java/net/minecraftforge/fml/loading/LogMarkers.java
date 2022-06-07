/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LogMarkers {
    public static final Marker CORE = MarkerFactory.getMarker("CORE");
    public static final Marker LOADING = MarkerFactory.getMarker("LOADING");
    public static final Marker SCAN = MarkerFactory.getMarker("SCAN");
    public static final Marker SPLASH = MarkerFactory.getMarker("SPLASH");
}
