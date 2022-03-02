/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;

@VisibleForTesting
public interface IGeneratedBlockstate {

    JsonObject toJson();
}
